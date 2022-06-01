package com.steel.product.application.oauth.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.codec.binary.Base64;
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

import com.steel.product.application.dao.UserRepository;
import com.steel.product.application.dto.LoginRequest;
import com.steel.product.application.entity.UserEntity;
import com.steel.product.application.exception.MockException;
import com.steel.product.application.response.LoginResponse;
import com.steel.product.application.response.OAuthResponse;
import com.steel.product.application.util.AdvancedEncryptionStandard;
import com.steel.product.application.util.ApplicationConstants;

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

    public UserEntity getUserInfoByUserName( String userName )
    {
        short enabled = 1;
        return userDetailsRepository.findByUserNameAndEnabled( userName, enabled );
    }

    public List< UserEntity > getAllActiveUserInfo()
    {
        return userDetailsRepository.findAllByEnabled( ( short ) 1 );
    }

    public UserEntity getUserInfoById( Integer id )
    {
        return userDetailsRepository.findByUserId( id );
    }

    public UserEntity addUser( UserEntity userInfo )
    {
        userInfo.setPassword( new BCryptPasswordEncoder().encode( userInfo.getPassword() ) );
//    	userInfo.setPassword(  userInfo.getPassword() );
        return userDetailsRepository.save( userInfo );
    }

    public UserEntity updateUser( Integer id, UserEntity userRecord )
    {
    	UserEntity userInfo = userDetailsRepository.findByUserId( id );
        userInfo.setUserName( userRecord.getUserName() );
        userInfo.setPassword( userRecord.getPassword() );
        userInfo.setRole( userRecord.getRole() );
        userInfo.setEnabled( userRecord.getEnabled() );
        return userDetailsRepository.save( userInfo );
    }

    public void deleteUser( Integer id )
    {
        userDetailsRepository.deleteById( id );
    }

    public UserEntity updatePassword( Integer id, UserEntity userRecord )
    {
    	UserEntity userInfo = userDetailsRepository.findByUserId( id );
        userInfo.setPassword( userRecord.getPassword() );
        return userDetailsRepository.save( userInfo );
    }

    public UserEntity updateRole( Integer id, UserEntity userRecord )
    {
    	UserEntity userInfo = userDetailsRepository.findByUserId( id );
        userInfo.setRole( userRecord.getRole() );
        return userDetailsRepository.save( userInfo );
    }
    

	public LoginResponse login(LoginRequest loginReq) throws MockException {

		log.info("*** login processing started ***" + loginReq.getUserName());

		LoginResponse response = null;
		OAuthResponse oauthResp = null;
		boolean isUserAuthorized = false;
		String decodedpwd = "";

		try {

			String pwd = loginReq.getPassword();

			try {
				AdvancedEncryptionStandard advancedEncryptionStandard = new AdvancedEncryptionStandard(ApplicationConstants.AES_KEY.getBytes(StandardCharsets.UTF_8));
				byte[] rawEncryptedPassword = Base64.decodeBase64(pwd);
				byte[] decryptedCipherText = advancedEncryptionStandard.decrypt(rawEncryptedPassword);
				decodedpwd = new String(decryptedCipherText);
			} catch (Exception e) {
				log.info("AES Decrytion exception catch:--" + e);
			}

			oauthResp = webClient.post().uri(authBasePath + "/oauth/token")
					.header("Authorization", "Basic " + Base64Utils.encodeToString((clientId + ":" + clientPwd).getBytes("UTF-8")))
					.body(BodyInserters.fromFormData("username", loginReq.getUserName()).with("password", decodedpwd).with("grant_type", "password"))
					.retrieve().bodyToMono(OAuthResponse.class).block();

			log.debug("OAuth response:" + oauthResp);

		} catch (Exception e) {
			log.error("error occurred during login", e);
			if (e instanceof WebClientResponseException.Unauthorized
					|| e instanceof WebClientResponseException.BadRequest) {
				isUserAuthorized = false;
			} else {
				
				List<String> errors = new ArrayList<>();
				errors.add("Unable to connect to the authorization server, please contact Bank");
				throw new MockException("MSG-0002", errors);
			}
		}

		UserEntity user = userDetailsRepository.findByUserNameAndEnabled(loginReq.getUserName(), (short)1);

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
