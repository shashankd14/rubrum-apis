package com.steel.product.application.service;

import com.steel.product.application.dao.PriceMasterRepository;
import com.steel.product.application.dto.pricemaster.PriceMasterResponse;
import com.steel.product.application.dto.additionalpricemaster.AdditionalPriceMasterResponse;
import com.steel.product.application.dto.pricemaster.PriceMasterRequest;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.entity.PriceMasterEntity;

import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class PriceMasterServiceImpl implements PriceMasterService {

	@Autowired
	PriceMasterRepository priceMasterRepository;

	@Autowired
	InstructionService instructionService;

	@Autowired
	AdditionalPriceMasterService additionalPriceMasterService;

	@Override
	public ResponseEntity<Object> save(List<PriceMasterRequest> priceMasterRequestList, int userId) {

		ResponseEntity<Object> response = null;
		List<PriceMasterEntity> list=new ArrayList<>();
		
		for (PriceMasterRequest priceMasterRequest : priceMasterRequestList) {

			for (Integer partyId : priceMasterRequest.getPartyId()) {

				for (Integer matGradeId : priceMasterRequest.getMatGradeId()) {
					PriceMasterEntity priceMasterEntity = new PriceMasterEntity();
					if (priceMasterRequest.getId() != null && priceMasterRequest.getId() > 0) {
						priceMasterEntity.setId(priceMasterRequest.getId());
					}
					priceMasterEntity.setPartyId(partyId);
					priceMasterEntity.setProcessId(priceMasterRequest.getProcessId());
					priceMasterEntity.setMatGradeId(matGradeId);
					priceMasterEntity.setPrice(priceMasterRequest.getPrice());
					priceMasterEntity.setThicknessFrom(priceMasterRequest.getThicknessFrom());
					priceMasterEntity.setThicknessTo(priceMasterRequest.getThicknessTo());
					priceMasterEntity.setCreatedBy(userId);
					priceMasterEntity.setUpdatedBy(userId);
					priceMasterEntity.setCreatedOn(new Date());
					priceMasterEntity.setUpdatedOn(new Date());
					list.add(priceMasterEntity);
				}
			}
		}
		
		for (PriceMasterEntity entity : list) {
			
			List<PriceMasterEntity> fromList = priceMasterRepository.validateRange(entity.getPartyId(),
					entity.getProcessId(), entity.getMatGradeId(), entity.getThicknessFrom());
			
			if(fromList!=null && fromList.size()>0) {
				if(fromList.size()==1) {
					PriceMasterEntity duplicateEntity=fromList.get(0);
					if(entity.getId() != duplicateEntity.getId() ) {
						return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered From Range Already Exists.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
					}
				} else {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered From Range Already Exists.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} 
			List<PriceMasterEntity> toList = priceMasterRepository.validateRange(entity.getPartyId(),
					entity.getProcessId(), entity.getMatGradeId(), entity.getThicknessTo());
			if(toList!=null && toList.size()>0) {
				if(toList.size()==1) {
					PriceMasterEntity duplicateEntity=toList.get(0);
					if(entity.getId() != duplicateEntity.getId() ) {
						return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered To Range Already Exists.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
					}
				} else {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered To Range Already Exists.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		}
				
		priceMasterRepository.saveAll(list);
		response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Price Master details saved successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		return response;
	}

	@Override
	public ResponseEntity<Object> delete(int id) {
		ResponseEntity<Object> response = null;
		try {
			priceMasterRepository.deleteById(id);
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Price Master details deleted successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public PriceMasterResponse getById(int id) {

		List<Object[]> list = priceMasterRepository.findById1(id);
		return prepareData(list);
	}
	
	@Override
	public List<PriceMasterResponse> getAllPriceDetails() {

		List<Object[]> list = priceMasterRepository.findAll1();
		return prepareDataList(list);
	}
	
	private List<PriceMasterResponse> prepareDataList(List<Object[]> results) {
		List<PriceMasterResponse> list = new ArrayList<>();
		try {
			if (results != null && !results.isEmpty()) {
				for (Object[] result : results) {
					PriceMasterResponse priceMasterResponse = new PriceMasterResponse();
					priceMasterResponse.setId(result[0] != null ? (Integer) result[0] : null);
					priceMasterResponse.setPartyId(result[1] != null ? (Integer) result[1] : null);
					priceMasterResponse.setProcessId(result[2] != null ? (Integer) result[2] : null);
					priceMasterResponse.setMatGradeId(result[3] != null ? (Integer) result[3] : null);
					priceMasterResponse.setThicknessFrom(result[4] != null ? (BigDecimal) result[4] : null);
					priceMasterResponse.setThicknessTo(result[5] != null ? (BigDecimal) result[5] : null);
					priceMasterResponse.setPrice(result[6] != null ? (BigDecimal) result[6] : null);
					priceMasterResponse.setCreatedBy(result[7] != null ? (Integer) result[7] : null);
					priceMasterResponse.setUpdatedBy(result[8] != null ? (Integer) result[8] : null);
					priceMasterResponse.setCreatedOn(result[9] != null ? (Date) result[9] : null);
					priceMasterResponse.setUpdatedOn(result[10] != null ? (Date) result[10] : null);
					priceMasterResponse.setPartyName(result[11] != null ? (String) result[11] : null);
					priceMasterResponse.setProcessName(result[12] != null ? (String) result[12] : null);
					priceMasterResponse.setMatGradeName(result[13] != null ? (String) result[13] : null);
					priceMasterResponse.setMaterialDescription( result[14] != null ? (String) result[14] : null);
					priceMasterResponse.setMatId(result[15] != null ? (Integer) result[15] : null);
					list.add(priceMasterResponse);
				}
			}
		} catch (Exception e) {
			log.error("Unable to get services due to :: ", e);
		}
		return list;
	}
	
	private PriceMasterResponse prepareData (List<Object[]> results) {
		PriceMasterResponse priceMasterResponse = null;
		try {
			if (results != null && !results.isEmpty()) {
				for (Object[] result : results) {
					priceMasterResponse = new PriceMasterResponse();
					priceMasterResponse.setId(result[0] != null ? (Integer) result[0] : null);
					priceMasterResponse.setPartyId(result[1] != null ? (Integer) result[1] : null);
					priceMasterResponse.setProcessId(result[2] != null ? (Integer) result[2] : null);
					priceMasterResponse.setMatGradeId(result[3] != null ? (Integer) result[3] : null);
					priceMasterResponse.setThicknessFrom(result[4] != null ? (BigDecimal) result[4] : null);
					priceMasterResponse.setThicknessTo(result[5] != null ? (BigDecimal) result[5] : null);
					priceMasterResponse.setPrice(result[6] != null ? (BigDecimal) result[6] : null);
					priceMasterResponse.setCreatedBy(result[7] != null ? (Integer) result[7] : null);
					priceMasterResponse.setUpdatedBy(result[8] != null ? (Integer) result[8] : null);
					priceMasterResponse.setCreatedOn(result[9] != null ? (Date) result[9] : null);
					priceMasterResponse.setUpdatedOn(result[10] != null ? (Date) result[10] : null);
					priceMasterResponse.setPartyName(result[11] != null ? (String) result[11] : null);
					priceMasterResponse.setProcessName(result[12] != null ? (String) result[12] : null);
					priceMasterResponse.setMatGradeName(result[13] != null ? (String) result[13] : null);
					priceMasterResponse.setMaterialDescription( result[14] != null ? (String) result[14] : null);
					priceMasterResponse.setMatId(result[15] != null ? (Integer) result[15] : null);
				}
			}
		} catch (Exception e) {
			log.error("Unable to get services due to :: ", e);
		}
		return priceMasterResponse;
	}
	/*
	@Override
	public ResponseEntity<Object> calculatePrice(CalculatePriceRequest calculatePriceRequest ) {
		JSONObject obj=new JSONObject();
		JSONObject basePrice=new JSONObject();
		JSONObject additionalPrice=new JSONObject();
		BigDecimal totalPrice = new BigDecimal("0.00");
		obj.put("BasePrice", basePrice);
		obj.put("AdditionalPrice", additionalPrice);

		List<PriceMasterResponse> basePriceList = getAllPriceDetails();
		List<AdditionalPriceMasterResponse> addPriceList = additionalPriceMasterService.getAllPriceDetails();

		for (Integer instructionId : calculatePriceRequest.getInstructionIds()) {
			System.out.println("instructionId == " + instructionId);
			Instruction instruction = instructionService.getById(instructionId);
			InwardEntry inwardEntry = instruction.getInwardId();
			BigDecimal fThickness = new BigDecimal(Float.toString(inwardEntry.getfThickness()));
			BigDecimal plannedNoOfPieces = new BigDecimal(Float.toString(instruction.getPlannedNoOfPieces()));
			BigDecimal bundleWeight = new BigDecimal(Float.toString(instruction.getActualWeight()==null ? instruction.getPlannedWeight() : instruction.getActualWeight()));
			BigDecimal noofPlans = BigDecimal.valueOf( inwardEntry.getInstructions().size() );
			BigDecimal actualLength = new BigDecimal(Float.toString(instruction.getActualLength()==null ? instruction.getPlannedLength() : instruction.getActualLength()));
			
			for (PriceMasterResponse priceMasterResponse : basePriceList) {
				
				if (inwardEntry.getMaterialGrade().getGradeId() == priceMasterResponse.getMatGradeId()
					&& inwardEntry.getParty().getnPartyId() == priceMasterResponse.getPartyId()
					&& instruction.getProcess().getProcessId() == priceMasterResponse.getProcessId()
					&& fThickness.compareTo(priceMasterResponse.getThicknessFrom()) == 1
					&& priceMasterResponse.getThicknessTo().compareTo(fThickness) == 1) {

					basePrice.put(inwardEntry.getMaterial().getDescription()+" - "+inwardEntry.getMaterialGrade().getGradeName(), priceMasterResponse.getPrice());
					totalPrice = totalPrice.add(priceMasterResponse.getPrice());
				}
			}
			
			for (AdditionalPriceMasterResponse additionalPriceMasterResponse : addPriceList) {
				
				if (inwardEntry.getParty().getnPartyId() == additionalPriceMasterResponse.getPartyId()
				&& instruction.getProcess().getProcessId() == additionalPriceMasterResponse.getProcessId()) {
					
					if (additionalPriceMasterResponse.getProcessId() == 2 || additionalPriceMasterResponse.getProcessId() == 3) {
								
						int partCount = instructionService.getPartCount(instruction.getPartDetails().getId());
						
						if ((additionalPriceMasterResponse.getAdditionalPriceId() == 1 || additionalPriceMasterResponse.getAdditionalPriceId() == 11)
							&& BigDecimal.valueOf(partCount).compareTo(additionalPriceMasterResponse.getRangeFrom()) == 1
							&& additionalPriceMasterResponse.getRangeTo().compareTo(BigDecimal.valueOf(partCount)) == 1) {

							additionalPrice.put(additionalPriceMasterResponse.getAdditionalPriceDesc(), additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
						
						if ((additionalPriceMasterResponse.getAdditionalPriceId() == 2 || additionalPriceMasterResponse.getAdditionalPriceId() == 12)
							&& plannedNoOfPieces.compareTo(additionalPriceMasterResponse.getRangeFrom()) == 1
							&& additionalPriceMasterResponse.getRangeTo().compareTo(plannedNoOfPieces) == 1) {

							additionalPrice.put(additionalPriceMasterResponse.getAdditionalPriceDesc(), additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
						
						if ((additionalPriceMasterResponse.getAdditionalPriceId() == 3 || additionalPriceMasterResponse.getAdditionalPriceId() == 10)  
							&& bundleWeight.compareTo(additionalPriceMasterResponse.getRangeFrom()) == 1
							&& additionalPriceMasterResponse.getRangeTo().compareTo(bundleWeight) == 1) {

							additionalPrice.put(additionalPriceMasterResponse.getAdditionalPriceDesc(), additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
						
						if ((additionalPriceMasterResponse.getAdditionalPriceId() == 5 || additionalPriceMasterResponse.getAdditionalPriceId() == 13)
							&& noofPlans.compareTo(additionalPriceMasterResponse.getRangeFrom()) == 1
							&& additionalPriceMasterResponse.getRangeTo().compareTo(noofPlans) == 1) {

							additionalPrice.put(additionalPriceMasterResponse.getAdditionalPriceDesc(), additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
					}
					
					if (additionalPriceMasterResponse.getProcessId() == 1 || additionalPriceMasterResponse.getProcessId() == 3) {
						
						if ((additionalPriceMasterResponse.getAdditionalPriceId() == 6 || additionalPriceMasterResponse.getAdditionalPriceId() == 14)
							&& actualLength.compareTo(additionalPriceMasterResponse.getRangeFrom()) == 1
							&& additionalPriceMasterResponse.getRangeTo().compareTo(actualLength) == 1) {

							additionalPrice.put(additionalPriceMasterResponse.getAdditionalPriceDesc(), additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
						
						if (additionalPriceMasterResponse.getAdditionalPriceId() == 8  
							&& bundleWeight.compareTo(additionalPriceMasterResponse.getRangeFrom()) == 1
							&& additionalPriceMasterResponse.getRangeTo().compareTo(bundleWeight) == 1) {

							additionalPrice.put(additionalPriceMasterResponse.getAdditionalPriceDesc(), additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
					}
				}
			}
		}
		obj.put("TotalPrice", totalPrice);
		return new ResponseEntity<Object>(obj, HttpStatus.OK);
	}
	 */
	@Override
	public String calculateInstructionPrice(int instructionId ) {
		
		JSONObject obj=new JSONObject();
		
		try {
			JSONObject additionalPrice=new JSONObject();
			BigDecimal totalPrice = new BigDecimal("0.00");
			obj.put("BasePrice", "0.00");
			obj.put("AdditionalPrice", additionalPrice);

			List<PriceMasterResponse> basePriceList = getAllPriceDetails();
			List<AdditionalPriceMasterResponse> addPriceList = additionalPriceMasterService.getAllPriceDetails();

			System.out.println("instructionId == " + instructionId);
			Instruction instruction = instructionService.getById(instructionId);
			InwardEntry inwardEntry = instruction.getInwardId();
			BigDecimal fThickness = new BigDecimal(Float.toString(inwardEntry.getfThickness()));
			BigDecimal plannedNoOfPieces = new BigDecimal(Float.toString(instruction.getPlannedNoOfPieces()));
			BigDecimal bundleWeight = new BigDecimal(Float.toString(instruction.getActualWeight()==null ? instruction.getPlannedWeight() : instruction.getActualWeight()));
			BigDecimal noofPlans = BigDecimal.valueOf( inwardEntry.getInstructions().size() );
			BigDecimal actualLength = new BigDecimal(Float.toString(instruction.getActualLength()==null ? instruction.getPlannedLength() : instruction.getActualLength()));
			
			for (PriceMasterResponse priceMasterResponse : basePriceList) {
				
				if (inwardEntry.getMaterialGrade().getGradeId() == priceMasterResponse.getMatGradeId()
					&& inwardEntry.getParty().getnPartyId() == priceMasterResponse.getPartyId()
					&& instruction.getProcess().getProcessId() == priceMasterResponse.getProcessId()
					&& fThickness.compareTo(priceMasterResponse.getThicknessFrom()) == 1
					&& priceMasterResponse.getThicknessTo().compareTo(fThickness) == 1) {
					obj.put("BasePrice", priceMasterResponse.getPrice());
				}
			}
			
			for (AdditionalPriceMasterResponse additionalPriceMasterResponse : addPriceList) {
				
				if (inwardEntry.getParty().getnPartyId() == additionalPriceMasterResponse.getPartyId()
				&& instruction.getProcess().getProcessId() == additionalPriceMasterResponse.getProcessId()) {
					
					if (additionalPriceMasterResponse.getProcessId() == 2 || additionalPriceMasterResponse.getProcessId() == 3) {
								
						int partCount = instructionService.getPartCount(instruction.getPartDetails().getId());
						
						if ((additionalPriceMasterResponse.getAdditionalPriceId() == 1 || additionalPriceMasterResponse.getAdditionalPriceId() == 11)
							&& BigDecimal.valueOf(partCount).compareTo(additionalPriceMasterResponse.getRangeFrom()) == 1
							&& additionalPriceMasterResponse.getRangeTo().compareTo(BigDecimal.valueOf(partCount)) == 1) {

							additionalPrice.put(additionalPriceMasterResponse.getAdditionalPriceDesc(), additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
						
						if ((additionalPriceMasterResponse.getAdditionalPriceId() == 2 || additionalPriceMasterResponse.getAdditionalPriceId() == 12)
							&& plannedNoOfPieces.compareTo(additionalPriceMasterResponse.getRangeFrom()) == 1
							&& additionalPriceMasterResponse.getRangeTo().compareTo(plannedNoOfPieces) == 1) {

							additionalPrice.put(additionalPriceMasterResponse.getAdditionalPriceDesc(), additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
						
						if ((additionalPriceMasterResponse.getAdditionalPriceId() == 3 || additionalPriceMasterResponse.getAdditionalPriceId() == 10)  
							&& bundleWeight.compareTo(additionalPriceMasterResponse.getRangeFrom()) == 1
							&& additionalPriceMasterResponse.getRangeTo().compareTo(bundleWeight) == 1) {

							additionalPrice.put(additionalPriceMasterResponse.getAdditionalPriceDesc(), additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
						
						if ((additionalPriceMasterResponse.getAdditionalPriceId() == 5 || additionalPriceMasterResponse.getAdditionalPriceId() == 13)
							&& noofPlans.compareTo(additionalPriceMasterResponse.getRangeFrom()) == 1
							&& additionalPriceMasterResponse.getRangeTo().compareTo(noofPlans) == 1) {

							additionalPrice.put(additionalPriceMasterResponse.getAdditionalPriceDesc(), additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
					}
					
					if (additionalPriceMasterResponse.getProcessId() == 1 || additionalPriceMasterResponse.getProcessId() == 3) {
						
						if ((additionalPriceMasterResponse.getAdditionalPriceId() == 6 || additionalPriceMasterResponse.getAdditionalPriceId() == 14)
							&& actualLength.compareTo(additionalPriceMasterResponse.getRangeFrom()) == 1
							&& additionalPriceMasterResponse.getRangeTo().compareTo(actualLength) == 1) {

							additionalPrice.put(additionalPriceMasterResponse.getAdditionalPriceDesc(), additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
						
						if (additionalPriceMasterResponse.getAdditionalPriceId() == 8  
							&& bundleWeight.compareTo(additionalPriceMasterResponse.getRangeFrom()) == 1
							&& additionalPriceMasterResponse.getRangeTo().compareTo(bundleWeight) == 1) {

							additionalPrice.put(additionalPriceMasterResponse.getAdditionalPriceDesc(), additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
					}
				}
			}

			obj.put("AdditionalTotalPrice", totalPrice);
		} catch (Exception e) { 
		}
		return obj.toString();
	}
}
