package com.steel.product.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.steel.product.application.dao.PriceMasterRepository;
import com.steel.product.application.dto.pricemaster.PriceMasterResponse;
import com.steel.product.application.dto.additionalpricemaster.AdditionalPriceMasterResponse;
import com.steel.product.application.dto.packingmaster.PackingRateMasterResponse;
import com.steel.product.application.dto.pricemaster.PriceCalculateDTO;
import com.steel.product.application.dto.pricemaster.PriceMasterListPageRequest;
import com.steel.product.application.dto.pricemaster.PriceMasterRequest;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.PriceMasterEntity;
import lombok.extern.log4j.Log4j2;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	PackingMasterService packingMasterService;	
	
	@Autowired
	AdditionalPriceMasterService additionalPriceMasterService;
	
	@Autowired
	PartyDetailsService partyDetailsService;
	
	@Autowired
	MaterialGradeService materialGradeService;
	
	@Autowired
	ProcessService processService;

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
					priceMasterEntity.setParty(partyDetailsService.getPartyById(partyId));
					priceMasterEntity.setMatGrade( materialGradeService.getById(matGradeId));
					priceMasterEntity.setProcess( processService.getById(priceMasterRequest.getProcessId()));
					priceMasterEntity.setPrice(priceMasterRequest.getPrice());
					priceMasterEntity.setThicknessFrom(priceMasterRequest.getThicknessFrom());
					priceMasterEntity.setThicknessTo(priceMasterRequest.getThicknessTo());
					priceMasterEntity.setCreatedBy(userId);
					priceMasterEntity.setUpdatedBy(userId);
					priceMasterEntity.setCreatedOn(new Date());
					priceMasterEntity.setUpdatedOn(new Date());
					if(priceMasterRequest.getPrice()!=null) {
						list.add(priceMasterEntity);
					}
				}
			}
		}
		
		for (PriceMasterEntity entity : list) {
			
			List<PriceMasterEntity> fromList = priceMasterRepository.validateRange(entity.getParty().getnPartyId(),
					entity.getProcess().getProcessId(), entity.getMatGrade().getGradeId(), entity.getThicknessFrom());
			
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
			List<PriceMasterEntity> toList = priceMasterRepository.validateRange(entity.getParty().getnPartyId(),
					entity.getProcess().getProcessId(), entity.getMatGrade().getGradeId(), entity.getThicknessTo());
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
	public List<PriceMasterResponse> getAllPriceDetails(int partyId) {

		List<Object[]> list = priceMasterRepository.findAllDetails(partyId);
		return prepareDataList(list);
	}
	
	@Override
	public List<PriceMasterResponse> getPartyGradeWiseDetails(int partyId, int processId, int gradeId) {

		List<Object[]> list = priceMasterRepository.findByPartyIdAndProcessIdAndMatGradeIdss(partyId, processId, gradeId);
		return prepareDataList(list);
	}
	
	@Override
	public List<PriceMasterResponse> getAllPriceDetails( ) {

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
	public String calculateInstructionPrice(Instruction ins, int packingRateId) {
		String jsonStr = "";
		PriceCalculateDTO priceCalculateDTO = calculateInstructionWisePrice(ins, packingRateId);
		ObjectMapper Obj = new ObjectMapper();
		try {
			
			if(priceCalculateDTO != null && priceCalculateDTO.getBasePrice() !=null 
					&& priceCalculateDTO.getBasePrice().compareTo(BigDecimal.ZERO) > 0) {
				
				priceCalculateDTO.setInstructionId( ins.getInstructionId());
				priceCalculateDTO.setCoilNo(ins.getInwardId().getCoilNumber());
				priceCalculateDTO.setCustomerBatchNo( ins.getInwardId().getCustomerBatchId());
				priceCalculateDTO.setMatGradeName( ins.getInwardId().getMaterialGrade().getGradeName());
				priceCalculateDTO.setThickness(BigDecimal.valueOf( ins.getInwardId().getfThickness()));
				priceCalculateDTO.setActualWeight( ins.getActualWeight());
				
				BigDecimal amount =new BigDecimal("0.00");
				
				if(priceCalculateDTO!=null && priceCalculateDTO.getBasePrice() !=null) {
					amount=amount.add(priceCalculateDTO.getBasePrice());
				}
				if(priceCalculateDTO!=null && priceCalculateDTO.getAdditionalPrice() != null) {
					amount=amount.add(priceCalculateDTO.getAdditionalPrice());
				}
				if(priceCalculateDTO!=null && priceCalculateDTO.getPackingPrice() != null) {
					amount=amount.add(priceCalculateDTO.getPackingPrice() );
				}
				if(amount!=null ) {
					priceCalculateDTO.setRate(amount.setScale(3, RoundingMode.HALF_EVEN));
					BigDecimal totalAmount = new BigDecimal(BigInteger.ZERO,  2);
					totalAmount = (amount.multiply(BigDecimal.valueOf(priceCalculateDTO.getActualWeight())));
					totalAmount = totalAmount.divide(BigDecimal.valueOf(1000));
					priceCalculateDTO.setTotalPrice(totalAmount.setScale(3, RoundingMode.HALF_EVEN));
				}
			}
			 
			jsonStr = Obj.writeValueAsString(priceCalculateDTO);
			System.out.println(jsonStr);
		} catch (IOException e) {
		}
		return jsonStr;
	}
	
	@Override
	public PriceCalculateDTO calculateInstructionWisePrice(int partyId, BigDecimal fThickness, int processId,
			int gradeId, Integer packingRateId, BigDecimal bundleWeight, Float actualLength1, int plannedNoOfPieces1,
			int instrSize, Long partDetailsId) {

		PriceCalculateDTO priceCalculateDTO=new PriceCalculateDTO();
		
		try {
			BigDecimal totalPrice = new BigDecimal(BigInteger.ZERO,  2);
			BigDecimal additionalPrice = new BigDecimal(BigInteger.ZERO,  2);
			List<PriceMasterResponse> basePriceList = getPartyGradeWiseDetails(partyId, processId, gradeId);
						
			for (PriceMasterResponse priceMasterResponse : basePriceList) {
				if(processId==8 || processId==7) {
					if (gradeId == priceMasterResponse.getMatGradeId()
						&& partyId == priceMasterResponse.getPartyId()
						&& processId == priceMasterResponse.getProcessId() ) {
						
						priceCalculateDTO.setBasePrice(priceMasterResponse.getPrice());
						totalPrice = totalPrice.add(priceCalculateDTO.getBasePrice()); 
					}
				} else {
					if (gradeId == priceMasterResponse.getMatGradeId()
						&& partyId == priceMasterResponse.getPartyId()
						&& processId == priceMasterResponse.getProcessId()
						&& fThickness.compareTo(priceMasterResponse.getThicknessFrom()) >= 0
						&& priceMasterResponse.getThicknessTo().compareTo(fThickness) >= 0) {
						
						priceCalculateDTO.setBasePrice(priceMasterResponse.getPrice());
						totalPrice = totalPrice.add(priceCalculateDTO.getBasePrice());
					}
				}
			}
			
			PackingRateMasterResponse packrate = packingMasterService.getByIdRate(packingRateId);
			if(packrate != null && packrate.getPackingRate() !=null && packrate.getPackingRate().compareTo(BigDecimal.ZERO) > 0) {
				//BigDecimal packingRate = new BigDecimal(BigInteger.ZERO,  2);
				//packingRate = (packrate.getPackingRate().multiply(BigDecimal.valueOf(actualWeight)));
				//packingRate = packingRate.divide(BigDecimal.valueOf(1000));
				priceCalculateDTO.setPackingPrice(packrate.getPackingRate());
				totalPrice = totalPrice.add(priceCalculateDTO.getPackingPrice());
			} else {
				priceCalculateDTO.setPackingPrice(BigDecimal.ZERO);
			}
			
			List<AdditionalPriceMasterResponse> addPriceList = additionalPriceMasterService.getAllPriceDetails();
			BigDecimal plannedNoOfPieces = new BigDecimal(Float.toString(plannedNoOfPieces1));
			//BigDecimal bundleWeight = actualWeight; //new BigDecimal(Float.toString(actualWeight));
			BigDecimal noofPlans = BigDecimal.valueOf(instrSize);
			BigDecimal actualLength = new BigDecimal(Float.toString(actualLength1));

			for (AdditionalPriceMasterResponse additionalPriceMasterResponse : addPriceList) {
				
				if (partyId == additionalPriceMasterResponse.getPartyId()
				&& processId == additionalPriceMasterResponse.getProcessId()) {
					
					if (additionalPriceMasterResponse.getProcessId() == 2 || additionalPriceMasterResponse.getProcessId() == 3) {
								
						int partCount = instructionService.getPartCount(partDetailsId);
						
						if ((additionalPriceMasterResponse.getAdditionalPriceId() == 1 || additionalPriceMasterResponse.getAdditionalPriceId() == 11)
							&& BigDecimal.valueOf(partCount).compareTo(additionalPriceMasterResponse.getRangeFrom()) >= 0
							&& additionalPriceMasterResponse.getRangeTo().compareTo(BigDecimal.valueOf(partCount)) >= 0) {
							
							additionalPrice = additionalPrice.add(additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
						
						if ((additionalPriceMasterResponse.getAdditionalPriceId() == 2 || additionalPriceMasterResponse.getAdditionalPriceId() == 12)
							&& plannedNoOfPieces.compareTo(additionalPriceMasterResponse.getRangeFrom()) >= 0
							&& additionalPriceMasterResponse.getRangeTo().compareTo(plannedNoOfPieces) >= 0) {

							additionalPrice = additionalPrice.add(additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
						
						if ((additionalPriceMasterResponse.getAdditionalPriceId() == 3 || additionalPriceMasterResponse.getAdditionalPriceId() == 10)  
							&& bundleWeight.compareTo(additionalPriceMasterResponse.getRangeFrom()) >= 0
							&& additionalPriceMasterResponse.getRangeTo().compareTo(bundleWeight) >= 0) {

							additionalPrice = additionalPrice.add(additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
						
						if ((additionalPriceMasterResponse.getAdditionalPriceId() == 5 || additionalPriceMasterResponse.getAdditionalPriceId() == 13)
							&& noofPlans.compareTo(additionalPriceMasterResponse.getRangeFrom()) >= 0
							&& additionalPriceMasterResponse.getRangeTo().compareTo(noofPlans) >= 0) {

							additionalPrice = additionalPrice.add(additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
					}
					
					if (additionalPriceMasterResponse.getProcessId() == 1 || additionalPriceMasterResponse.getProcessId() == 3) {
						
						if ((additionalPriceMasterResponse.getAdditionalPriceId() == 6 || additionalPriceMasterResponse.getAdditionalPriceId() == 14)
							&& actualLength.compareTo(additionalPriceMasterResponse.getRangeFrom()) >= 0
							&& additionalPriceMasterResponse.getRangeTo().compareTo(actualLength) >= 0) {

							additionalPrice = additionalPrice.add(additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
						
						if (additionalPriceMasterResponse.getAdditionalPriceId() == 8  
							&& bundleWeight.compareTo(additionalPriceMasterResponse.getRangeFrom()) >= 0
							&& additionalPriceMasterResponse.getRangeTo().compareTo(bundleWeight) >= 0) {

							additionalPrice = additionalPrice.add(additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
					}
				}
			}
			priceCalculateDTO.setAdditionalPrice(additionalPrice);
			priceCalculateDTO.setTotalPrice(totalPrice);
		} catch (Exception e) { 
			e.printStackTrace();
		}
		return priceCalculateDTO;
	}
	
	@Override
	public PriceCalculateDTO calculateInstructionWisePrice(Instruction ins, Integer packingRateId) {

		PriceCalculateDTO priceCalculateDTO=new PriceCalculateDTO();
		
		try {
			BigDecimal totalPrice = new BigDecimal(BigInteger.ZERO,  2);
			BigDecimal additionalPrice = new BigDecimal(BigInteger.ZERO,  2);
			
			int processId=ins.getProcess().getProcessId();
			
			List<PriceMasterResponse> basePriceList = getPartyGradeWiseDetails(ins.getInwardId().getParty().getnPartyId(), processId, ins.getInwardId().getMaterialGrade().getGradeId());
						
			for (PriceMasterResponse priceMasterResponse : basePriceList) {
				if(processId==8 || processId==7) {
					if (ins.getInwardId().getMaterialGrade().getGradeId() == priceMasterResponse.getMatGradeId()
							&& ins.getInwardId().getParty().getnPartyId() == priceMasterResponse.getPartyId()
							&& processId == priceMasterResponse.getProcessId()) {

						priceCalculateDTO.setBasePrice(priceMasterResponse.getPrice());
						totalPrice = totalPrice.add(priceCalculateDTO.getBasePrice()); 
					} 
				} else {
					if (ins.getInwardId().getMaterialGrade().getGradeId() == priceMasterResponse.getMatGradeId()
							&& ins.getInwardId().getParty().getnPartyId() == priceMasterResponse.getPartyId()
							&& processId == priceMasterResponse.getProcessId()
							&& BigDecimal.valueOf(ins.getInwardId().getfThickness()).compareTo(priceMasterResponse.getThicknessFrom()) >= 0
							&& priceMasterResponse.getThicknessTo().compareTo(BigDecimal.valueOf(ins.getInwardId().getfThickness())) >= 0) {

						priceCalculateDTO.setBasePrice(priceMasterResponse.getPrice());
						totalPrice = totalPrice.add(priceCalculateDTO.getBasePrice()); 
					}
				}
			}
			
			PackingRateMasterResponse packrate = packingMasterService.getByIdRate(packingRateId);
			if(packrate != null && packrate.getPackingRate() !=null && packrate.getPackingRate().compareTo(BigDecimal.ZERO) > 0) {
				//BigDecimal packingRate = new BigDecimal(BigInteger.ZERO,  2);
				//packingRate = (packrate.getPackingRate().multiply(BigDecimal.valueOf(ins.getActualWeight())));
				//packingRate = packingRate.divide(BigDecimal.valueOf(1000));
				priceCalculateDTO.setPackingPrice(packrate.getPackingRate());
				totalPrice = totalPrice.add(priceCalculateDTO.getPackingPrice());
			}
			
			List<AdditionalPriceMasterResponse> addPriceList = additionalPriceMasterService.getAllPriceDetails();
			//BigDecimal fThickness = new BigDecimal(Float.toString(ins.getInwardId().getfThickness()));
			BigDecimal plannedNoOfPieces =new BigDecimal("1.00");
			if(ins.getPlannedNoOfPieces()!=null && ins.getPlannedNoOfPieces() >0) {
				plannedNoOfPieces = new BigDecimal(Float.toString(ins.getPlannedNoOfPieces()));
			}
			BigDecimal bundleWeight = new BigDecimal(Float.toString(ins.getActualWeight()==null ? ins.getPlannedWeight() : ins.getActualWeight()));
			BigDecimal noofPlans = BigDecimal.valueOf( ins.getInwardId().getInstructions().size() );
			BigDecimal actualLength = new BigDecimal(Float.toString(ins.getActualLength()==null ? ins.getPlannedLength() : ins.getActualLength()));

			for (AdditionalPriceMasterResponse additionalPriceMasterResponse : addPriceList) {
				
				if (ins.getInwardId().getParty().getnPartyId() == additionalPriceMasterResponse.getPartyId()
				&& processId == additionalPriceMasterResponse.getProcessId()) {
					
					if (additionalPriceMasterResponse.getProcessId() == 2 || additionalPriceMasterResponse.getProcessId() == 3) {
								
						int partCount = instructionService.getPartCount(ins.getPartDetails().getId());
						
						if ((additionalPriceMasterResponse.getAdditionalPriceId() == 1 || additionalPriceMasterResponse.getAdditionalPriceId() == 11)
							&& BigDecimal.valueOf(partCount).compareTo(additionalPriceMasterResponse.getRangeFrom()) >= 0
							&& additionalPriceMasterResponse.getRangeTo().compareTo(BigDecimal.valueOf(partCount)) >= 0) {
							
							additionalPrice = additionalPrice.add(additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
						
						if ((additionalPriceMasterResponse.getAdditionalPriceId() == 2 || additionalPriceMasterResponse.getAdditionalPriceId() == 12)
							&& plannedNoOfPieces.compareTo(additionalPriceMasterResponse.getRangeFrom()) >= 0
							&& additionalPriceMasterResponse.getRangeTo().compareTo(plannedNoOfPieces) >= 0) {

							additionalPrice = additionalPrice.add(additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
						
						if ((additionalPriceMasterResponse.getAdditionalPriceId() == 3 || additionalPriceMasterResponse.getAdditionalPriceId() == 10)  
							&& bundleWeight.compareTo(additionalPriceMasterResponse.getRangeFrom()) >= 0
							&& additionalPriceMasterResponse.getRangeTo().compareTo(bundleWeight) >= 0) {

							additionalPrice = additionalPrice.add(additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
						
						if ((additionalPriceMasterResponse.getAdditionalPriceId() == 5 || additionalPriceMasterResponse.getAdditionalPriceId() == 13)
							&& noofPlans.compareTo(additionalPriceMasterResponse.getRangeFrom()) >= 0
							&& additionalPriceMasterResponse.getRangeTo().compareTo(noofPlans) >= 0) {

							additionalPrice = additionalPrice.add(additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
					}
					
					if (additionalPriceMasterResponse.getProcessId() == 1 || additionalPriceMasterResponse.getProcessId() == 3) {
						
						if ((additionalPriceMasterResponse.getAdditionalPriceId() == 6 || additionalPriceMasterResponse.getAdditionalPriceId() == 14)
							&& actualLength.compareTo(additionalPriceMasterResponse.getRangeFrom()) >= 0
							&& additionalPriceMasterResponse.getRangeTo().compareTo(actualLength) >= 0) {

							additionalPrice = additionalPrice.add(additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
						
						if (additionalPriceMasterResponse.getAdditionalPriceId() == 8  
							&& bundleWeight.compareTo(additionalPriceMasterResponse.getRangeFrom()) >= 0
							&& additionalPriceMasterResponse.getRangeTo().compareTo(bundleWeight) >= 0) {

							additionalPrice = additionalPrice.add(additionalPriceMasterResponse.getPrice());
							totalPrice = totalPrice.add(additionalPriceMasterResponse.getPrice());
						}
					}
				}
			}
			priceCalculateDTO.setAdditionalPrice(additionalPrice);
			priceCalculateDTO.setTotalPrice(totalPrice);
		} catch (Exception e) { 
			e.printStackTrace();
		}
		return priceCalculateDTO;
	}

	@Override
	public Page<PriceMasterEntity> findAllWithPagination(PriceMasterListPageRequest request) {
		Pageable pageable = PageRequest.of((request.getPageNo() - 1), request.getPageSize());
		Page<PriceMasterEntity> pageResult = priceMasterRepository.findAll(request.getSearchText(),
				request.getThicknessRange(), pageable);
		return pageResult;
	}

}
