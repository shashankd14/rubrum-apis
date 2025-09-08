package com.steel.product.jswone.controller;

import io.swagger.v3.oas.annotations.tags.Tag;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.steel.product.jswone.request.MMIDReceiveIntegrationRequest;
import com.steel.product.jswone.request.POIntegrationRequest;
import com.steel.product.jswone.service.JSWIntegrationService;

@RestController
@CrossOrigin
@Tag(name = "Integration APIs", description = "Integration APIs")
@RequestMapping({ "/xternal" })
public class JSWIntegrationController {

	@Autowired
	private JSWIntegrationService service;

	@PostMapping(value = "/poreceive", produces = "application/json")
	public ResponseEntity<Object> poreceive(@RequestBody POIntegrationRequest request, HttpServletRequest httprequest) {
		String ipAddress = httprequest.getRemoteAddr();
		request.setIpAddress(ipAddress);
		return service.poReceive(request);
	}
	
	@PostMapping(value = "/mmidreceive", produces = "application/json")
	public ResponseEntity<Object> mmidreceive(@RequestBody MMIDReceiveIntegrationRequest request, HttpServletRequest httprequest) {
		String ipAddress = httprequest.getRemoteAddr();
		request.setIpAddress(ipAddress);
		return service.mmidreceive(request);
	}

}
