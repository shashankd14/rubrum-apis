package com.steel.product.application.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.steel.product.application.dto.pricemaster.PriceMasterResponse;
import com.steel.product.application.dto.pricemaster.CalculatePriceRequest;
import com.steel.product.application.dto.pricemaster.PriceMasterRequest;

public interface PriceMasterService {

	ResponseEntity<Object> save(List<PriceMasterRequest> priceMasterRequestList, int userId);

	ResponseEntity<Object> delete(int id);

	PriceMasterResponse getById(int id);

	List<PriceMasterResponse> getAllPriceDetails();

	ResponseEntity<Object> calculatePrice(CalculatePriceRequest calculatePriceRequest);


}
