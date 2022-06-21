package com.steel.product.application.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.steel.product.application.dto.pricemaster.PriceMasterRequest;
import com.steel.product.application.dto.pricemaster.PriceMasterResponse;
import com.steel.product.application.entity.PriceMasterEntity;

public interface PriceMasterService {

	ResponseEntity<Object> save(List<PriceMasterRequest> priceMasterRequestList);

	ResponseEntity<Object> delete(int id);

	PriceMasterResponse getById(int id);

	List<PriceMasterResponse> getAllPriceDetails();

	List<PriceMasterResponse> getCustProcessMaterialId(int partyId, int processId, int matGradeId);

	List<PriceMasterResponse> getProcessPriceDetails(int processId);

	List<PriceMasterResponse> getCustProcess(int partyId, int processId);

	List<PriceMasterResponse> getCustPriceDetails(int partyId);

	List<PriceMasterEntity> copyCustomerDetails(PriceMasterRequest priceMasterRequest);

	List<PriceMasterEntity> copyCustProcessDetails(PriceMasterRequest priceMasterRequest);

	List<PriceMasterEntity> copyMatGradeDetails(PriceMasterRequest priceMasterRequest);

}
