package com.steel.product.application.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.steel.product.application.dto.pricemaster.PriceMasterResponse;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.dto.pricemaster.PriceCalculateDTO;
import com.steel.product.application.dto.pricemaster.PriceMasterRequest;

public interface PriceMasterService {

	ResponseEntity<Object> save(List<PriceMasterRequest> priceMasterRequestList, int userId);

	ResponseEntity<Object> delete(int id);

	PriceMasterResponse getById(int id);

	List<PriceMasterResponse> getAllPriceDetails();

	List<PriceMasterResponse> getAllPriceDetails(int partyId);

	List<PriceMasterResponse> getPartyGradeWiseDetails(int partyId, int processId, int gradeId);

	PriceCalculateDTO calculateInstructionWisePrice(int partyId, BigDecimal fThickness, int processId, int gradeId,
			int packingRateId, Float actualWeight, Float actualLength1, int plannedNoOfPieces1, int instrSize,
			Long partDetailsId);

	PriceCalculateDTO calculateInstructionWisePrice(Instruction ins, int packingRateId);

	String calculateInstructionPrice(Instruction ins, int packingRateId);

}
