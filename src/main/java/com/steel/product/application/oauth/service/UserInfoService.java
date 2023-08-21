package com.steel.product.application.oauth.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.steel.product.application.dao.AdminMenuRepository;
import com.steel.product.application.dao.UserRepository;
import com.steel.product.application.dto.LoginRequest;
import com.steel.product.application.dto.admin.AdminMenuDto;
import com.steel.product.application.dto.admin.CreateUserRequest;
import com.steel.product.application.entity.AdminUserEntity;
import com.steel.product.application.entity.UserPartyMap;
import com.steel.product.application.entity.UserRoleMap;
import com.steel.product.application.exception.MockException;
import com.steel.product.application.response.LoginResponse;
import com.steel.product.application.response.OAuthResponse;
import lombok.extern.log4j.Log4j2;

@Service
@Transactional
@Log4j2
public class UserInfoService
{
	
	private WebClient webClient;
	
	@Value("${user.oauth.clientId}")
	private String clientId;

	@Value("${user.oauth.clientSecret}")
	private String clientPwd;

	@Value("${login.basepath}")
	private String authBasePath;

    @Autowired
    private UserRepository userDetailsRepository;

    @Autowired
	AdminMenuRepository adminMenuRepository;
    
	private static String OAUTHURL;

    /**
	 * Constructor
	 */
	@Inject
	public UserInfoService(@Value("${login.oauthUrl}") String oauthUrl) {

		log.info("login url:" + oauthUrl);

		UserInfoService.OAUTHURL = oauthUrl;

		webClient = WebClient.builder().baseUrl(oauthUrl)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				// .filter(logRequest())
				.build();
	}
	
	public AdminUserEntity getUserEntityByEmail(String email) {
		return userDetailsRepository.findByEmailId(email);
	}

    public AdminUserEntity getUserEntityByUserName( String userName )
    {
        short enabled = 1;
        return userDetailsRepository.findByUserNameAndEnabled( userName, enabled );
    }

    public List< AdminUserEntity > getAllActiveUserInfo()
    {
        return userDetailsRepository.findAllByEnabled( ( short ) 1 );
    }

    public AdminUserEntity getUserInfoById( Integer id )
    {
        return userDetailsRepository.findByUserId( id );
    }

	public AdminUserEntity addUser(CreateUserRequest userInfo) {
		AdminUserEntity adminUserEntity = new AdminUserEntity();
		
		if(userInfo.getUserId()>0) {
			adminUserEntity.setUserId(userInfo.getUserId());
		}
		adminUserEntity.setUserName(userInfo.getUserName());
		adminUserEntity.setFirstName(userInfo.getFirstName());
		adminUserEntity.setLastName(userInfo.getLastName());
		adminUserEntity.setMobileNo(userInfo.getMobileNo());
		adminUserEntity.setEmailId(userInfo.getEmailId());
		adminUserEntity.setFailLgnCounter(0);
		adminUserEntity.setEnabled((short) 1);
		adminUserEntity.setUserDataVisible(userInfo.getUserDataVisible());
		adminUserEntity.setRawPassword(userInfo.getPassword());
		adminUserEntity.setPassword(new BCryptPasswordEncoder().encode(userInfo.getPassword()));
		if(userInfo.getUserId()>0) {
			userDetailsRepository.deletePartyMapByUserId(userInfo.getUserId());
			userDetailsRepository.deleteRoleMapByUserId(userInfo.getUserId());
		}
		for (Integer partyId : userInfo.getPartyList()) {
			UserPartyMap userPartyMap = new UserPartyMap();
			userPartyMap.setPartyId(partyId);
			userPartyMap.setUserEntityid(adminUserEntity);
			adminUserEntity.getUserPartyMap().add(userPartyMap);
		}
		for (Integer roleId : userInfo.getRoleList()) {
			UserRoleMap userRoleMap = new UserRoleMap();
			userRoleMap.setRoleId(roleId);
			userRoleMap.setUserEntityid(adminUserEntity);
			adminUserEntity.getUserRoleMap().add(userRoleMap);
		}
		return userDetailsRepository.save(adminUserEntity);
	}

    public void deleteUser( Integer id )
    {
        userDetailsRepository.deleteById( id );
    }

    public AdminUserEntity updatePassword(AdminUserEntity userRecord )
    {
    	AdminUserEntity userInfo = userDetailsRepository.findByUserId( userRecord.getUserId() );
        userInfo.setPassword (new BCryptPasswordEncoder().encode( userRecord.getPassword()) );
        userInfo.setRawPassword ( userRecord.getPassword() );
        return userDetailsRepository.save( userInfo );
    }

    public AdminUserEntity updateRole( Integer id, AdminUserEntity userRecord )
    {
    	AdminUserEntity userInfo = userDetailsRepository.findByUserId( id );
       // userInfo.setRoleId( userRecord.getRoleId() );
        return userDetailsRepository.save( userInfo );
    }
    
	public LoginResponse login(LoginRequest loginReq) throws MockException {

		log.info("*** login processing started ***" + loginReq.getUserName());

		LoginResponse response = null;
		OAuthResponse oauthResp = null;

		try {

			oauthResp = webClient.post().uri(authBasePath + "/oauth/token")
					.header("Authorization", "Basic " + Base64Utils.encodeToString((clientId + ":" + clientPwd).getBytes("UTF-8")))
					.body(BodyInserters.fromFormData("username", loginReq.getUserName()).with("password", loginReq.getPassword()).with("grant_type", "password"))
					.retrieve().bodyToMono(OAuthResponse.class).block();

			log.debug("OAuth response:" + oauthResp);

		} catch (Exception e) {
			log.error("error occurred during login", e);
			if (e instanceof WebClientResponseException.Unauthorized
					|| e instanceof WebClientResponseException.BadRequest) {
			} else {
				
				List<String> errors = new ArrayList<>();
				errors.add("Unable to connect to the authorization server, please contact Bank");
				throw new MockException("MSG-0002", errors);
			}
		}

		AdminUserEntity user = userDetailsRepository.findByUserNameAndEnabled(loginReq.getUserName(), (short)1);

		// Login is success - check user access is enabled
		if (user != null && oauthResp != null && !StringUtils.isEmpty(oauthResp.getAccessToken())) {

			// reset user fail login counter
			Date loginDate = new Date();
			user.setLastLoginTime(user.getCurrentLoginTime());
			user.setCurrentLoginTime(loginDate);

			userDetailsRepository.save(user);
			List<AdminMenuDto> menusList = adminMenuRepository.findMenus(user.getUserId());

			response = LoginResponse.builder().userId(user.getUserId())
					.userName(user.getUserName())
					.lastLoginTime(user.getLastLoginTime())
					.access_token(oauthResp.getAccessToken())
					.refresh_token(oauthResp.getRefreshToken())
					.token_type(oauthResp.getTokenType())
					.expires_in(oauthResp.getExpiresIn())
					.menusList(menusList)
					.build();
			

			
		} else {
			
			List<String> errors = new ArrayList<>();
			errors.add("Invalid Login Attempt.");
			throw new MockException("MSG-0003", errors);
		}

		return response;
	}
    
	public LoginResponse loginTally(LoginRequest loginReq) throws MockException {

		log.info("*** login processing started ***" + loginReq.getUserName());

		LoginResponse response = null;
		OAuthResponse oauthResp = null;

		try {

			oauthResp = webClient.post().uri(authBasePath + "/oauth/token")
					.header("Authorization", "Basic " + Base64Utils.encodeToString((clientId + ":" + clientPwd).getBytes("UTF-8")))
					.body(BodyInserters.fromFormData("username", loginReq.getUserName()).with("password", loginReq.getPassword()).with("grant_type", "password"))
					.retrieve().bodyToMono(OAuthResponse.class).block();

			log.debug("OAuth response:" + oauthResp);

		} catch (Exception e) {
			log.error("error occurred during login", e);
			if (e instanceof WebClientResponseException.Unauthorized
					|| e instanceof WebClientResponseException.BadRequest) {
			} else {
				
				List<String> errors = new ArrayList<>();
				errors.add("Unable to connect to the authorization server, please contact Bank");
				throw new MockException("MSG-0002", errors);
			}
		}

		AdminUserEntity user = userDetailsRepository.findByUserNameAndEnabled(loginReq.getUserName(), (short)1);

		// Login is success - check user access is enabled
		if (user != null && oauthResp != null && !StringUtils.isEmpty(oauthResp.getAccessToken())) {

			// reset user fail login counter
			Date loginDate = new Date();
			user.setLastLoginTime(user.getCurrentLoginTime());
			user.setCurrentLoginTime(loginDate);
			userDetailsRepository.save(user);
			response = LoginResponse.builder().userId(user.getUserId())
					.userName(user.getUserName())
					.lastLoginTime(user.getLastLoginTime())
					.access_token(oauthResp.getAccessToken())
					.refresh_token(oauthResp.getRefreshToken())
					.token_type(oauthResp.getTokenType())
					.expires_in(oauthResp.getExpiresIn())
					.build();
			

			
		} else {
			
			List<String> errors = new ArrayList<>();
			errors.add("Invalid Login Attempt.");
			throw new MockException("MSG-0003", errors);
		}

		return response;
	}
	
}
