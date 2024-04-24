package com.steel.product.trading.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.steel.product.trading.request.DeleteRequest;
import com.steel.product.trading.request.EQPRequest;
import com.steel.product.trading.request.EQPSearchRequest;
import com.steel.product.trading.request.InwardSearchRequest;

public interface EQPService {

	ResponseEntity<Object> save(EQPRequest eqpRequest);

	Map<String, Object> getEQPList(EQPSearchRequest searchPageRequest);

}
