package com.steel.product.application.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.steel.product.application.service.DeliveryDetailsService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin
@Tag(name = "Delivery Details", description = "Delivery Details")
@RequestMapping("/delivery")
public class DeliveryDetailsController {

    @Autowired
    private DeliveryDetailsService deliveryDetailsService;

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