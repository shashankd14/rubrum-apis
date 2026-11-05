package com.steel.product.application.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.steel.product.application.dto.delivery.DeliveryItemDetails;
import com.steel.product.application.dto.delivery.DeliveryPacketsDto;
import com.steel.product.application.dto.delivery.ValidatePriceMappingDTO;
import com.steel.product.application.dto.instruction.InstructionResponseDto;
import com.steel.product.application.dto.pdf.PdfDto;
import com.steel.product.application.dto.pricemaster.PriceCalculateResponseDTO;
import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.entity.Process;
import com.steel.product.application.entity.Status;
import com.steel.product.application.oauth.service.UserInfoService;
import com.steel.product.application.service.DeliveryDetailsService;
import com.steel.product.application.service.InwardEntryService;
import com.steel.product.application.service.PartyDetailsService;
import com.steel.product.application.service.PdfService;
import com.steel.product.application.service.StatusService;
//import com.steel.product.application.service.SalesOrderService;
import com.steel.product.application.util.CommonUtil;
import com.steel.product.jswone.entity.SalesOrderJswEntity;
import com.steel.product.jswone.service.JSWIntegrationService;
import com.steel.product.jswone.service.MaterialMasterJswService;
import com.steel.product.jswone.service.SalesOrderJswHelperService;
import com.steel.product.jswone.service.SalesOrderJswService;

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
	private CommonUtil commonUtil;
    
	@Autowired
	private JSWIntegrationService service;

	@Autowired
	private MaterialMasterJswService materialService;
	
	@Autowired
	private UserInfoService userInfoService;
	
	@Autowired
	private SalesOrderJswHelperService soHelperService;
	
	@Autowired
	private SalesOrderJswService salesOrderJswService;

    @Autowired
    private PdfService pdfService;

    @Autowired
	private PartyDetailsService partyDetailsService;
    
    @Autowired
    private StatusService statusService;

    @Autowired
    private InwardEntryService inwdEntrySvc;
	
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
		Map<Integer, String> userMap = userInfoService.getAllActiveUserNameMap();

		log.info("In findAllDeliveryList === " + deliveryIdList);
		List<DeliveryPacketsDto> deliveryDetails = deliveryDetailsService.getDeliveryDetails(deliveryIdList, userMap);
		 
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
			int userId = commonUtil.getUserId();	
			if(!(deliveryDto.getPackingRateId() !=null && deliveryDto.getPackingRateId() > 0 )) {
				deliveryDto.setPackingRateId(0);
			}		
			if(!(deliveryDto.getLaminationId() !=null && deliveryDto.getLaminationId() > 0 )) {
				deliveryDto.setLaminationId(0);
			}
			DeliveryDetails deliveryDetails = deliveryDetailsService.save(deliveryDto, userId);

			for (DeliveryItemDetails instructionslist : deliveryDto.getDeliveryItemDetails()) {
				if (instructionslist.getSoNumber() != null && instructionslist.getSoNumber().length() > 0 && instructionslist.getMmid() != null && instructionslist.getMmid().length() > 0) {
					SalesOrderJswEntity dto = salesOrderJswService.findByRefno(instructionslist.getSoNumber());
					if (dto != null && dto.getSoId() > 0) {
						soHelperService.updateCPStatus(dto.getSoId());
						soHelperService.updateSOStatus(dto.getSoId());
					}
				}
			}
			
			if (deliveryDetails != null && deliveryDetails.getDeliveryId() > 0 && "Sales Order".equals((deliveryDetails.getDeliveryType()))) {
				DeliveryDto dto = new DeliveryDto();
				dto.setDeliveryId(deliveryDetails.getDeliveryId());
				service.inventoryAdjustment(dto);
			}

			if ("Stock Transfer".equals((deliveryDto.getDeliveryType())) && deliveryDetails != null && deliveryDetails.getDeliveryId() > 0) {
				createInwardEntriesForStockTransfer(deliveryDetails, userId, deliveryDto.getLocationId());
			}
			result = new ResponseEntity<>("Delivery details saved successfully!", HttpStatus.OK);
		} catch (Exception e) {
			result = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return result;
	}

    /**
     * For a "Stock Transfer" delivery, each Instruction attached to the delivery represents
     * a piece of material leaving stock. This creates a corresponding new InwardEntry for each
     * one, so the transferred material shows up as fresh inward stock.
     *
     * NOTE: deliveryDetails.getInstructions() is FetchType.LAZY. If this method is called after
     * the session/transaction that produced deliveryDetails has already closed, this will throw
     * LazyInitializationException. Safer long-term: call this from within
     * DeliveryDetailsService.save(...) itself (same transaction), or have that service return
     * the instructions eagerly.
     *
     * TODO: Neither DeliveryDto nor DeliveryDetails carries a destination location for the
     * transfer - this currently reuses the source InwardEntry's location as a placeholder.
     * TODO: swap inwardEntryService for whatever the actual injected service/repository bean is.
     */
    private void createInwardEntriesForStockTransfer(DeliveryDetails deliveryDetails, int userId, int locationId) {
        Set<Instruction> transferredInstructions = deliveryDetails.getInstructions();
        if (transferredInstructions == null) {
            return;
        }

        int counter = 0;
        for (Instruction instruction : transferredInstructions) {

            try {
				// Each instruction traces back to the InwardEntry it was originally cut from -
				// that's where the material identity (party, material, grade, etc.) comes from.
				InwardEntry sourceInwardEntry = instruction.getInwardId();
				if (sourceInwardEntry == null) {
				    continue;
				}

				InwardEntry newInwardEntry = new InwardEntry();
				// Carry over material identity from the original inward entry
				try {
					newInwardEntry.setParty(this.partyDetailsService.getPartyById(locationId));
				} catch (Exception e) {
					newInwardEntry.setParty(sourceInwardEntry.getParty());
				}
				newInwardEntry.setMaterial(sourceInwardEntry.getMaterial());
				newInwardEntry.setMaterialGrade(sourceInwardEntry.getMaterialGrade());
				newInwardEntry.setPacketClassification(instruction.getPacketClassification());
				newInwardEntry.setEndUserTagsEntity(instruction.getEndUserTagsEntity());
				if (counter == 0) {
					counter = sourceInwardEntry.getCoilSeq() + 1;
				} else {
					counter++;
				}
				newInwardEntry.setCoilNumber(sourceInwardEntry.getCoilNumber() + "-ST_" + counter);
				newInwardEntry.setBatchNumber(sourceInwardEntry.getBatchNumber());
				newInwardEntry.setParentCoilNumber(sourceInwardEntry.getCoilNumber());
				newInwardEntry.setCoilSeq(0);

				// Transferred quantity = what was actually processed on this instruction,
				// falling back to the planned amount if actuals aren't recorded yet
				Float width = instruction.getActualWidth() != null ? instruction.getActualWidth() : instruction.getPlannedWidth();
				Float length = instruction.getActualLength() != null ? instruction.getActualLength() : instruction.getPlannedLength();
				Float weight = instruction.getActualWeight() != null ? instruction.getActualWeight() : instruction.getPlannedWeight();
				int pieces = instruction.getActualNoOfPieces() != null ? instruction.getActualNoOfPieces() : (instruction.getPlannedNoOfPieces() != null ? instruction.getPlannedNoOfPieces() : 0);
				
				newInwardEntry.setfWidth(width != null ? width : 0f);
				newInwardEntry.setfLength(length != null ? length : 0f);
				newInwardEntry.setfQuantity(weight != null ? weight : 0f);
				newInwardEntry.setGrossWeight(weight != null ? weight : 0f);
				newInwardEntry.setNoofpieces(pieces);
				newInwardEntry.setStatus(this.statusService.getStatusById(1));
				// New stock arriving = fully available stock
				newInwardEntry.setInStockWeight(weight);
				newInwardEntry.setAvailableLength(length);
				newInwardEntry.setValueOfGoods(sourceInwardEntry.getValueOfGoods());
				newInwardEntry.setTdcNo(sourceInwardEntry.getTdcNo());
				newInwardEntry.setTestCertificateNumber(sourceInwardEntry.getTestCertificateNumber());
				newInwardEntry.setTestCertificateFileUrl(sourceInwardEntry.getTestCertificateFileUrl());
				newInwardEntry.setPurposeType(sourceInwardEntry.getPurposeType());
				newInwardEntry.setIsDeleted(Boolean.valueOf(false));
				newInwardEntry.setFpresent(weight);
				newInwardEntry.setfThickness(sourceInwardEntry.getfThickness());
				newInwardEntry.setdReceivedDate(sourceInwardEntry.getdReceivedDate());
				newInwardEntry.setdInvoiceDate(sourceInwardEntry.getdInvoiceDate());
				newInwardEntry.setvLorryNo(sourceInwardEntry.getvLorryNo());
				newInwardEntry.setCustomerInvoiceNo(sourceInwardEntry.getCustomerInvoiceNo());
				newInwardEntry.setvInvoiceNo(sourceInwardEntry.getvInvoiceNo());
				newInwardEntry.setCustomerCoilId(sourceInwardEntry.getCustomerCoilId());
				newInwardEntry.setCustomerBatchId(sourceInwardEntry.getCustomerBatchId());
				newInwardEntry.setDeliveryId(deliveryDetails.getDeliveryId());
				if ("Sheet".equals(sourceInwardEntry.getInwardType())) {
					newInwardEntry.setInwardType("Sheet");
				} else {
					newInwardEntry.setInwardType("Coil");
					if (instruction.getProcess().getProcessId() < 7) {
						newInwardEntry.setInwardType("Sheet");
					}
				}
				newInwardEntry.setRemarks("Auto-created from Stock Transfer Delivery ID: " + deliveryDetails.getDeliveryId());
				newInwardEntry.setCreatedBy(userId);
				newInwardEntry = inwdEntrySvc.saveEntry(newInwardEntry);
				inwdEntrySvc.updateStockTrasferSeq(sourceInwardEntry.getInwardEntryId(), counter);

				// inward PDF generation part and inward QR Code generation part	
				PdfDto pdfDto =new PdfDto();
				pdfDto.setInwardId(newInwardEntry.getInwardEntryId());
				pdfService.generateInwardPdf(pdfDto);
			} catch (Exception e) {
				//e.printStackTrace();
				log.info("Error while creating inward {} ", e.getMessage());
			}
        }
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