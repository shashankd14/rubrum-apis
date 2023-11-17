package com.steel.product.application.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.steel.product.application.dto.pricemaster.PriceMasterResponse;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.PriceMasterEntity;
import com.steel.product.application.dto.pricemaster.PriceCalculateDTO;
import com.steel.product.application.dto.pricemaster.PriceMasterListPageRequest;
import com.steel.product.application.dto.pricemaster.PriceMasterRequest;

public interface PriceMasterService {

	ResponseEntity<Object> save(List<PriceMasterRequest> priceMasterRequestList, int userId);

	ResponseEntity<Object> delete(int id);

	PriceMasterResponse getById(int id);

	List<PriceMasterResponse> getAllPriceDetails();

	List<PriceMasterResponse> getAllPriceDetails(int partyId);

	List<PriceMasterResponse> getPartyGradeWiseDetails(int partyId, int processId, int gradeId);

	PriceCalculateDTO calculateInstructionWisePrice(int partyId, BigDecimal fThickness, int processId, int gradeId,
			Integer packingRateId, BigDecimal actualWeight, Float actualLength1, int plannedNoOfPieces1, int instrSize,
			Long partDetailsId, Integer laminationId);

	PriceCalculateDTO calculateInstructionWisePrice(Instruction ins, Integer packingRateId, Integer laminationId);

	String calculateInstructionPrice(Instruction ins, int packingRateId, Integer laminationId);

	Page<PriceMasterEntity> findAllWithPagination(PriceMasterListPageRequest request);

}
