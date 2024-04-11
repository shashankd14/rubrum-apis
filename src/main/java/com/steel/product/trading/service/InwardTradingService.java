package com.steel.product.trading.service;

import org.springframework.http.ResponseEntity;

import com.steel.product.trading.request.InwardTradingRequest;

public interface InwardTradingService {

	ResponseEntity<Object> save(InwardTradingRequest inwardTradingRequest);

}
