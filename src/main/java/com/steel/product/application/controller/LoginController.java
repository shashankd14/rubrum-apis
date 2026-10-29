package com.steel.product.application.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.steel.product.application.dto.LoginRequest;
import com.steel.product.application.exception.MockException;
import com.steel.product.application.oauth.service.UserInfoService;
import com.steel.product.application.response.LoginResponse;
import com.steel.product.application.response.LoginResponseExternal;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;

@RestController
@CrossOrigin
@Log4j2
@Tag(name = "Login Controller", description = "Login Controller")
@RequestMapping("/login")
public class LoginController {

	@Autowired
	private UserInfoService userService;

	@Autowired
	private TokenStore tokenStore;

	@PostMapping
	public LoginResponse login(@Valid @RequestBody LoginRequest loginReq) throws MockException {

		log.debug("*** Login Method Invoked ***");
		return userService.login(loginReq);
	}
	
	@RequestMapping(value = "/auth", method = { RequestMethod.GET, RequestMethod.POST })
	public LoginResponseExternal loginTally(@Valid @RequestBody LoginRequest loginReq) throws MockException {
		log.debug("*** loginTally Method Invoked ***");
		return userService.loginTally(loginReq);
	}

	@PostMapping("/logout")
	public ResponseEntity<Map<String, String>> logout(
			@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
		log.debug("*** Logout Method Invoked ***");
		Map<String, String> response = new HashMap<>();

		if (authorization == null || !authorization.toLowerCase().startsWith("bearer ")) {
			response.put("message", "Authorization bearer token is required");
			return ResponseEntity.badRequest().body(response);
		}

		String tokenValue = authorization.substring(7).trim();
		OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
		if (accessToken != null) {
			if (accessToken.getRefreshToken() != null) {
				tokenStore.removeRefreshToken(accessToken.getRefreshToken());
			}
			tokenStore.removeAccessToken(accessToken);
		}

		response.put("message", "Logged out successfully");
		return ResponseEntity.ok(response);
	}
}