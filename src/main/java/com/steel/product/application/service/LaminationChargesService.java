package com.steel.product.application.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.steel.product.application.dto.lamination.LaminationChargesRequest;
import com.steel.product.application.dto.lamination.LaminationChargesResponse;
import com.steel.product.application.dto.pricemaster.PriceMasterResponse;
import com.steel.product.application.entity.LaminationStaticDataEntity;

public interface LaminationChargesService {

	ResponseEntity<Object> save(List<LaminationChargesRequest> packingItemRequest, int userId);

	ResponseEntity<Object> delete(int id);

	LaminationChargesResponse getById(Integer id);

	List<LaminationChargesResponse> getByPartyId(Integer partyId);

	List<LaminationStaticDataEntity> getLaminationDetails();

	List<LaminationChargesResponse> getAllLaminationDetails();

}