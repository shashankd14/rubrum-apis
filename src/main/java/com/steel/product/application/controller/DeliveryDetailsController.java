package com.steel.product.application.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

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
import com.steel.product.application.dto.pricemaster.PriceCalculateResponseDTO;
import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.service.DeliveryDetailsService;
import com.steel.product.application.service.InwardEntryService; // TODO: confirm actual package/service name
import com.steel.product.application.service.LocationMasterService;
import com.steel.product.application.service.StatusService;

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
    private InwardEntryService inwdEntrySvc;

    @Autowired
    private LocationMasterService locationMasterService;

    @Autowired
    private StatusService statusService;

	@GetMapping({ "/list/{pageNo}/{pageSize}" })
	public ResponseEntity<Object> findAllWithPagination(@PathVariable int pageNo, @PathVariable int pageSize,
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
	}
	
    @GetMapping("/listold")
    public ResponseEntity<Object> getAll(){
        try{
            List<DeliveryPacketsDto> deliveryDetailsList = deliveryDetailsService.deliveryList();
            return new ResponseEntity<>(deliveryDetailsList, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getById/{deliveryId}")
	public ResponseEntity<Object> getById(@PathVariable("deliveryId") int deliveryId) {
		try {
			List<Instruction> deliveredInstructionsById = deliveryDetailsService
					.getInstructionsByDeliveryId(deliveryId);
			return new ResponseEntity<>(deliveredInstructionsById.stream().map(ins -> Instruction.valueOf(ins))
					.collect(Collectors.toList()), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

    @PostMapping("/validatePriceMapping")
	public ResponseEntity<Object> validatePriceMapping(@RequestBody DeliveryDto deliveryDto, HttpServletRequest request) {
		ResponseEntity<Object> result = null;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		try {
			int userId = (request.getHeader("userId") == null ? 1 : Integer.parseInt(request.getHeader("userId")));
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
	public ResponseEntity<Object> validatePriceMappingFullHandling(@RequestBody ValidatePriceMappingDTO validatePriceMappingDTO, HttpServletRequest request) {
		ResponseEntity<Object> result = null;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		try {
			int userId = (request.getHeader("userId") == null ? 1 : Integer.parseInt(request.getHeader("userId")));
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
	public ResponseEntity<Object> save(@RequestBody DeliveryDto deliveryDto, HttpServletRequest request) {
		ResponseEntity<Object> result = null;

		try {
			int userId = (request.getHeader("userId")==null ? 1: Integer.parseInt(request.getHeader("userId")));
			if(!(deliveryDto.getPackingRateId() !=null && deliveryDto.getPackingRateId() > 0 )) {
				deliveryDto.setPackingRateId(0);
			}		
			if(!(deliveryDto.getLaminationId() !=null && deliveryDto.getLaminationId() > 0 )) {
				deliveryDto.setLaminationId(0);
			}
			DeliveryDetails deliveryDetails = deliveryDetailsService.save(deliveryDto, userId);

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
				newInwardEntry.setParty(sourceInwardEntry.getParty());
				newInwardEntry.setLocation(sourceInwardEntry.getLocation());
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
				newInwardEntry.setCoilSeq(counter);

				// Transferred quantity = what was actually processed on this instruction,
				// falling back to the planned amount if actuals aren't recorded yet
				Float width = instruction.getActualWidth() != null ? instruction.getActualWidth() : instruction.getPlannedWidth();
				Float length = instruction.getActualLength() != null ? instruction.getActualLength() : instruction.getPlannedLength();
				Float weight = instruction.getActualWeight() != null ? instruction.getActualWeight() : instruction.getPlannedWeight();
				Integer pieces = instruction.getActualNoOfPieces() != null ? instruction.getActualNoOfPieces() : instruction.getPlannedNoOfPieces();

				newInwardEntry.setfWidth(width != null ? width : 0f);
				newInwardEntry.setfLength(length != null ? length : 0f);
				newInwardEntry.setfQuantity(weight != null ? weight : 0f);
				newInwardEntry.setGrossWeight(weight != null ? weight : 0f);
				newInwardEntry.setNoofpieces(pieces);
				newInwardEntry.setStatus(this.statusService.getStatusById(6));
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
				newInwardEntry.setCustomerInvoiceNo(sourceInwardEntry.getCustomerInvoiceNo());
				newInwardEntry.setCustomerCoilId(sourceInwardEntry.getCustomerCoilId());
				newInwardEntry.setCustomerBatchId(sourceInwardEntry.getCustomerBatchId());
				if (pieces > 0) {
					newInwardEntry.setInwardType("Sheet");
				} else {
					if (instruction.getProcess().getProcessId() < 7) {
						newInwardEntry.setInwardType("Sheet");
					}
					newInwardEntry.setInwardType("Coil");
				}
				newInwardEntry.setRemarks("Auto-created from Stock Transfer Delivery ID: " + deliveryDetails.getDeliveryId());
				newInwardEntry.setCreatedBy(userId);
				if(locationId == 0) {
					locationId=3;
				}
				newInwardEntry.setLocation(this.locationMasterService.getByLocationId(locationId));
				inwdEntrySvc.saveEntry(newInwardEntry);
			} catch (Exception e) {
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