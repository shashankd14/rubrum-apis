package com.steel.product.jswone.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import com.steel.product.jswone.request.MMIDReceiveMainRequest;
import com.steel.product.jswone.request.POIntegrationRequest;
import com.steel.product.jswone.response.PODetailsMainResponse;

public interface JSWIntegrationService {

	ResponseEntity<Object> poReceive(POIntegrationRequest request);

	ResponseEntity<Object> mmidreceive(MMIDReceiveMainRequest request);

	List<Object[]> locationwisePOList(POIntegrationRequest request);

	PODetailsMainResponse podetails(POIntegrationRequest request);

	PODetailsMainResponse postgrn(POIntegrationRequest request);

}
