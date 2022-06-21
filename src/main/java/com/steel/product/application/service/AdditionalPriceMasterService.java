package com.steel.product.application.service;

import java.util.List;
import org.springframework.http.ResponseEntity;

import com.steel.product.application.dto.additionalpricemaster.AdditionalPriceMasterRequest;
import com.steel.product.application.dto.additionalpricemaster.AdditionalPriceMasterResponse;
import com.steel.product.application.dto.pricemaster.PriceMasterResponse;
import com.steel.product.application.entity.AdditionalPriceStaticEntity;

public interface AdditionalPriceMasterService {

	ResponseEntity<Object> save(List<AdditionalPriceMasterRequest> priceMasterRequestList);

	ResponseEntity<Object> delete(int id);

	AdditionalPriceMasterResponse getById(int id);

	List<AdditionalPriceStaticEntity> additionalPriceStaticDetails(Integer id);

	List<AdditionalPriceMasterResponse> getCustProcessAdditionalPriceId(int partyId, int processId,
			int additionalPriceId);

	List<AdditionalPriceMasterResponse> getCustProcess(int partyId, int processId);

	List<AdditionalPriceMasterResponse> getCustPriceDetails(int partyId);

	List<AdditionalPriceMasterResponse> getProcessPriceDetails(int processId);

	List<AdditionalPriceMasterResponse> getAllPriceDetails();

}
