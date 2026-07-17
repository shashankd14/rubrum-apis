package com.steel.product.application.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.steel.product.application.dto.delivery.DeliveryDto;
import com.steel.product.application.dto.delivery.DeliveryPacketsDto;
import com.steel.product.application.dto.delivery.ValidatePriceMappingDTO;
import com.steel.product.application.dto.instruction.InstructionResponseDto;
import com.steel.product.application.dto.pricemaster.PriceCalculateResponseDTO;
import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.Process;
import com.steel.product.application.entity.Status;
import com.steel.product.application.service.DeliveryDetailsService;
import com.steel.product.application.service.SalesOrderService;
import com.steel.product.application.util.CommonUtil;
import com.steel.product.jswone.service.JSWIntegrationService;
import com.steel.product.jswone.service.MaterialMasterJswService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;

@RestController
@CrossOrigin
@Tag(name = "Delivery Details", description = "Delivery Details")
@RequestMapping("/delivery")
@Log4j2
public class DeliveryDetailsController {

    @Autowired
    private DeliveryDetailsService deliveryDetailsService;
    
    @Autowired
	private SalesOrderService salesOrderService;
    
    @Autowired
	private CommonUtil commonUtil;
    
	@Autowired
	private JSWIntegrationService service;

	@Autowired
	MaterialMasterJswService materialService;
	
	/*@GetMapping("/list/{pageNo}/{pageSize}")
	public ResponseEntity<Object> findAllWithPaginationOld(@PathVariable int pageNo, @PathVariable int pageSize,
			@RequestParam(required = false, name = "searchText") String searchText,
			@RequestParam(required = false, name = "partyId") String partyId) {

		Map<String, Object> response = new HashMap<>();
		Page<DeliveryDetails> pageResult = deliveryDetailsService.deliveryListPagination(pageNo, pageSize, searchText, partyId);
		List<DeliveryPacketsDto> list = pageResult.getContent().stream().map(inw -> new DeliveryPacketsDto(inw)).collect(Collectors.toList());
		response.put("content", list);
		response.put("currentPage", pageResult.getNumber());
		response.put("totalItems", pageResult.getTotalElements());
		response.put("totalPages", pageResult.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}*/

	@GetMapping("/list/{pageNo}/{pageSize}")
	public ResponseEntity<Object> findAllDeliveryList(@PathVariable int pageNo, @PathVariable int pageSize,
			@RequestParam(required = false, name = "searchText") String searchText,
			@RequestParam(required = false, name = "partyId") String partyId) {
		log.info("in findAllDeliveryList ");
		
		Map<String, Object> response = new HashMap<>();
		Page<Object[]> packetsList1 = deliveryDetailsService.listAllDeliveryList(pageNo, pageSize, searchText, partyId);

		List<Integer> deliveryIdList  = new ArrayList<>();
		for (Object[] result : packetsList1) {
			Integer inwardId = (result[0] != null ? (Integer) result[0] : null);
			deliveryIdList.add(inwardId);
		}
		
		log.info("In findAllDeliveryList === " + deliveryIdList);
		List<DeliveryPacketsDto> deliveryDetails = deliveryDetailsService.getDeliveryDetails(deliveryIdList);
		 
		response.put("content", deliveryDetails);
		response.put("currentPage", packetsList1.getNumber());
		response.put("totalItems", packetsList1.getTotalElements());
		response.put("totalPages", packetsList1.getTotalPages());
		
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@GetMapping("/getById/{deliveryId}")
	public ResponseEntity<Object> getById(@PathVariable("deliveryId") int deliveryId) {
		try {
			List<Instruction> deliveredInstructionsById = deliveryDetailsService.getInstructionsByDeliveryId(deliveryId);
			
			List<InstructionResponseDto> resp=new ArrayList<>();
			for (Instruction instruction : deliveredInstructionsById){
				InstructionResponseDto instructionResponseDto = new InstructionResponseDto();
				instructionResponseDto.setStatus(Status.valueOf(instruction.getStatus()));
				instructionResponseDto.setParentInstructionId(instruction.getParentInstruction() != null ? instruction.getParentInstruction().getInstructionId() : null);
				instructionResponseDto.setPacketClassification(instruction.getPacketClassification() != null ? instruction.getPacketClassification(): null);
				instructionResponseDto.setEndUserTagsentity( instruction.getEndUserTagsEntity() != null ? instruction.getEndUserTagsEntity(): null);		
				instructionResponseDto.setInstructionDate(instruction.getInstructionDate());
		        instructionResponseDto.setInstructionId(instruction.getInstructionId());
		        instructionResponseDto.setProcess(instruction.getProcess() != null ? Process.valueOf(instruction.getProcess()) : null);
		        instructionResponseDto.setPlannedWeight(instruction.getPlannedWeight());
		        instructionResponseDto.setPlannedWidth(instruction.getPlannedWidth());
		        instructionResponseDto.setPlannedLength(instruction.getPlannedLength());
		        instructionResponseDto.setPlannedNoOfPieces(instruction.getPlannedNoOfPieces());
		        instructionResponseDto.setActualWidth(instruction.getActualWidth());
		        instructionResponseDto.setActualWeight(instruction.getActualWeight());
		        instructionResponseDto.setActualLength(instruction.getActualLength());
		        instructionResponseDto.setActualNoOfPieces(instruction.getActualNoOfPieces());
		        instructionResponseDto.setInwardEntryId(instruction.getInwardId() != null ? instruction.getInwardId().getInwardEntryId() : null);
		        instructionResponseDto.setIsDeleted(instruction.getIsDeleted());
		        instructionResponseDto.setGroupId(instruction.getGroupId());
		        instructionResponseDto.setDamage(instruction.getDamage());
		        instructionResponseDto.setPackingWeight(instruction.getPackingWeight());
		        instructionResponseDto.setWastage(instruction.getWastage());
		        instructionResponseDto.setRemarks(instruction.getRemarks());
		        instructionResponseDto.setParentGroupId(instruction.getParentGroupId());
		        instructionResponseDto.setDeliveryDetails(instruction.getDeliveryDetails() != null ? DeliveryDetails.valueOf(instruction.getDeliveryDetails()) : null);
		        instructionResponseDto.setChildInstructions((instruction.getChildInstructions() != null && !instruction.getChildInstructions().isEmpty())
		                ? instruction.getChildInstructions().stream().map(ci -> Instruction.valueOf(ci)).collect(Collectors.toList()) : null);
		        instructionResponseDto.setIsSlitAndCut(instruction.getIsSlitAndCut());
				instructionResponseDto.setPartId(instruction.getPartDetails() != null ? instruction.getPartDetails().getId() : null);
				instructionResponseDto.setPartDetailsId(instruction.getPartDetails() != null ? instruction.getPartDetails().getPartDetailsId(): null);
				instructionResponseDto.setPlannedYieldLossRatio(instruction.getPartDetails() != null ? instruction.getPartDetails().getPlannedYieldLossRatio(): null);
				instructionResponseDto.setActualYieldLossRatio(instruction.getPartDetails() != null ? instruction.getPartDetails().getActualYieldLossRatio(): null);
				instructionResponseDto.setPdfS3Url(instruction.getPartDetails() != null ? instruction.getPartDetails().getPdfS3Url() : null);
				instructionResponseDto.setCoilNumber(instruction.getInwardId().getCoilNumber());
				instructionResponseDto.setCustomerBatchId(instruction.getInwardId().getCustomerBatchId());
				instructionResponseDto.setFThickness( instruction.getInwardId().getfThickness());
				instructionResponseDto.setMaterial(instruction.getInwardId().getMmId()!= null ? materialService.getProductName( instruction.getInwardId().getMmId()).getDescription() : null);
				instructionResponseDto.setMaterialGrade(instruction.getInwardId().getMmId()!= null ? materialService.getGradeName(instruction.getInwardId().getMmId()).getGradeName() : null);
				resp.add(instructionResponseDto);
			}
			return new ResponseEntity<>(resp, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

    @PostMapping("/validatePriceMapping")
	public ResponseEntity<Object> validatePriceMapping(@RequestBody DeliveryDto deliveryDto) {
		ResponseEntity<Object> result = null;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		try {
			int userId = commonUtil.getUserId();			
			if(!(deliveryDto.getPackingRateId() !=null && deliveryDto.getPackingRateId() > 0 )) {
				deliveryDto.setPackingRateId(0);
			}		
			if(!(deliveryDto.getLaminationId() !=null && deliveryDto.getLaminationId() > 0 )) {
				deliveryDto.setLaminationId(0);
			}
			PriceCalculateResponseDTO priceCalculateResponseDTO = deliveryDetailsService.validatePriceMapping(deliveryDto, deliveryDto.getPackingRateId());
			if (priceCalculateResponseDTO.isValidationStatus()) {
				result = new ResponseEntity<>(priceCalculateResponseDTO, headers, HttpStatus.OK);
			} else {
				result = new ResponseEntity<>(priceCalculateResponseDTO, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return result;
	}

    @PostMapping("/validatePriceMappingFullHandling")
	public ResponseEntity<Object> validatePriceMappingFullHandling(@RequestBody ValidatePriceMappingDTO validatePriceMappingDTO) {
		ResponseEntity<Object> result = null;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		try {
			int userId = commonUtil.getUserId();	
			if(!(validatePriceMappingDTO.getPackingRateId() !=null && validatePriceMappingDTO.getPackingRateId() > 0 )) {
				validatePriceMappingDTO.setPackingRateId(0);
			}		
			if(!(validatePriceMappingDTO.getLaminationId() !=null && validatePriceMappingDTO.getLaminationId() > 0 )) {
				validatePriceMappingDTO.setLaminationId(0);
			}
			PriceCalculateResponseDTO priceCalculateResponseDTO = deliveryDetailsService.calculateInwardWisePrice(validatePriceMappingDTO, validatePriceMappingDTO.getPackingRateId());
			if (priceCalculateResponseDTO.isValidationStatus()) {
				result = new ResponseEntity<>(priceCalculateResponseDTO, headers, HttpStatus.OK);
			} else {
				result = new ResponseEntity<>(priceCalculateResponseDTO, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return result;
	}
    
    @PostMapping("/save")
	public ResponseEntity<Object> save(@RequestBody DeliveryDto deliveryDto) {
		ResponseEntity<Object> result = null;

		try {
			if ("Sales Order".equals((deliveryDto.getDeliveryType()))) {
				int sonovalidationCNt = salesOrderService.validateSoNoAndCustCode(deliveryDto.getDeliveryItemDetails());
				if (sonovalidationCNt > 1) {
					HttpHeaders headers = new HttpHeaders();                    
					headers.set( "Content-Type", "application/json" );
					return new ResponseEntity<>(
							"{\"status\": \"failure\", \"message\": \"The selected packets don't have the same SONO and CUSTCODE\"}",
							headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			
			int userId = commonUtil.getUserId();	
			if(!(deliveryDto.getPackingRateId() !=null && deliveryDto.getPackingRateId() > 0 )) {
				deliveryDto.setPackingRateId(0);
			}		
			if(!(deliveryDto.getLaminationId() !=null && deliveryDto.getLaminationId() > 0 )) {
				deliveryDto.setLaminationId(0);
			}
			DeliveryDetails deliveryDetails = deliveryDetailsService.save(deliveryDto, userId);

			if (deliveryDetails != null && deliveryDetails.getDeliveryId() > 0 && "Sales Order".equals((deliveryDetails.getDeliveryType()))) {
				DeliveryDto dto = new DeliveryDto();
				dto.setDeliveryId(deliveryDetails.getDeliveryId());
				service.inventoryAdjustment(dto);
			}
			result = new ResponseEntity<>("Delivery details saved successfully!", HttpStatus.OK);
		} catch (Exception e) {
			result = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return result;
	}

    @DeleteMapping("/deleteById/{deliveryId}")
	public ResponseEntity<Object> deleteById(@PathVariable("deliveryId") Integer id) {
		try {
			deliveryDetailsService.deleteById(id);
			return new ResponseEntity<>("Deleted successful!", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}