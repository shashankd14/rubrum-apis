package com.steel.product.application.controller;

import com.steel.product.application.dto.delivery.DeliveryDto;
import com.steel.product.application.dto.delivery.DeliveryPacketsDto;
import com.steel.product.application.dto.pricemaster.PriceCalculateResponseDTO;
import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.service.DeliveryDetailsService;
import com.steel.product.application.service.PackingMasterService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@Tag(name = "Delivery Details", description = "Delivery Details")
@RequestMapping("/delivery")
public class DeliveryDetailsController {

    @Autowired
    private DeliveryDetailsService deliveryDetailsService;

    @Autowired
    private PackingMasterService packingMasterService;

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
			if(deliveryDto.getPackingRateId() == null ) {
				deliveryDto.setPackingRateId(0);
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

    @PostMapping("/save")
	public ResponseEntity<Object> save(@RequestBody DeliveryDto deliveryDto, HttpServletRequest request) {
		ResponseEntity<Object> result = null;

		DeliveryDetails deliveryDetails = new DeliveryDetails();
		try {
			int userId = (request.getHeader("userId")==null ? 1: Integer.parseInt(request.getHeader("userId")));
			if(deliveryDto.getPackingRateId() == null ) {
				deliveryDto.setPackingRateId(0);
			}
			deliveryDetails = deliveryDetailsService.save(deliveryDto, userId);
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
			return new ResponseEntity<>("Delete successful!", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}