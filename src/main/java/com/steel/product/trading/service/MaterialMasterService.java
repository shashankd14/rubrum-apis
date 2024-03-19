package com.steel.product.trading.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.steel.product.application.dto.pricemaster.PriceMasterResponse;
import com.steel.product.application.entity.PriceMasterEntity;
import com.steel.product.trading.entity.MaterialMasterEntity;
import com.steel.product.trading.request.MaterialMasterRequest;
import com.steel.product.application.dto.pricemaster.PriceMasterListPageRequest;

public interface MaterialMasterService {

	ResponseEntity<Object> save(List<MaterialMasterRequest> priceMasterRequestList, int userId);

	ResponseEntity<Object> delete(int id);

	PriceMasterResponse getById(int id);

	List<PriceMasterResponse> getAllPriceDetails();

	List<PriceMasterResponse> getAllPriceDetails(int partyId);

	List<PriceMasterResponse> getPartyGradeWiseDetails(int partyId, int processId, int gradeId);

	Page<MaterialMasterEntity> findAllWithPagination(PriceMasterListPageRequest request);

}
