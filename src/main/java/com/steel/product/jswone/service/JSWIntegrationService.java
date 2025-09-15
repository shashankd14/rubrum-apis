package com.steel.product.jswone.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import com.steel.product.jswone.request.MMIDReceiveMainRequest;
import com.steel.product.jswone.request.POIntegrationRequest;

public interface JSWIntegrationService {

	ResponseEntity<Object> poReceive(POIntegrationRequest request);

	ResponseEntity<Object> mmidreceive(MMIDReceiveMainRequest request);

	List<Object[]> locationwisePOList(POIntegrationRequest request);

}
