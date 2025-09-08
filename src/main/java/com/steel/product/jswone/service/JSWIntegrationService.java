package com.steel.product.jswone.service;

import org.springframework.http.ResponseEntity;

import com.steel.product.jswone.request.MMIDReceiveIntegrationRequest;
import com.steel.product.jswone.request.POIntegrationRequest;

public interface JSWIntegrationService {

	ResponseEntity<Object> poReceive(POIntegrationRequest request);

	ResponseEntity<Object> mmidreceive(MMIDReceiveIntegrationRequest request);

}
