package com.steel.product.trading.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.steel.product.trading.dto.InwardTradingResponse;
import com.steel.product.trading.request.InwardSearchRequest;
import com.steel.product.trading.request.InwardTradingRequest;

public interface InwardTradingService {

	ResponseEntity<Object> save(InwardTradingRequest inwardTradingRequest);

	Map<String, Object> getInwardList(InwardSearchRequest searchPageRequest);

}
