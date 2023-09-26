package com.steel.product.application.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.steel.product.application.dto.LoginRequest;
import com.steel.product.application.exception.MockException;
import com.steel.product.application.oauth.service.UserInfoService;
import com.steel.product.application.response.LoginResponse;

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

	@PostMapping
	public LoginResponse login(@Valid @RequestBody LoginRequest loginReq) throws MockException {

		log.debug("*** Login Method Invoked ***");
		return userService.login(loginReq);
	}
	
	@RequestMapping(value = "/auth", method = { RequestMethod.GET, RequestMethod.POST })
	public LoginResponse loginTally(@Valid @RequestBody LoginRequest loginReq) throws MockException {

		log.debug("*** loginTally Method Invoked ***");
		return userService.loginTally(loginReq);
	}
}