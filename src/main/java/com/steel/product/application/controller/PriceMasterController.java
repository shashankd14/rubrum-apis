package com.steel.product.application.controller;

import com.steel.product.application.dto.pricemaster.PriceMasterResponse;
import com.steel.product.application.entity.PriceMasterEntity;
import com.steel.product.application.dto.pricemaster.CalculatePriceRequest;
import com.steel.product.application.dto.pricemaster.PriceMasterListPageRequest;
import com.steel.product.application.dto.pricemaster.PriceMasterRequest;
import com.steel.product.application.service.PriceMasterService;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Tag(name = "Price Master", description = "Price Master")
@RequestMapping({ "/pricemaster" })
public class PriceMasterController {

	@Autowired
	private PriceMasterService priceMasterService;

	@PostMapping(value = "/save", produces = "application/json" )
	public ResponseEntity<Object> save(@RequestBody List<PriceMasterRequest> priceMasterRequestList, HttpServletRequest request) {
		int userId = (request.getHeader("userId")==null ? 1: Integer.parseInt(request.getHeader("userId")));

		return priceMasterService.save(priceMasterRequestList, userId);
	}
	
	@PutMapping(value = "/update", produces = "application/json")
	public ResponseEntity<Object> update(@RequestBody List<PriceMasterRequest> priceMasterRequestList, HttpServletRequest request) {
		int userId = (request.getHeader("userId")==null ? 1: Integer.parseInt(request.getHeader("userId")));
		return priceMasterService.save(priceMasterRequestList, userId);
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/json" )
	public ResponseEntity<Object> delete(@PathVariable("id") int id) {
		return priceMasterService.delete(id);
	}

	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Object> getById(@PathVariable("id") int id) {
		PriceMasterResponse resp = priceMasterService.getById(id);
		return new ResponseEntity<Object>(resp, HttpStatus.OK);
	}

	@GetMapping(produces = "application/json")
	public List<PriceMasterResponse> getAllPriceDetails() {
		List<PriceMasterResponse> list = priceMasterService.getAllPriceDetails();
		return list;
	}

	@PostMapping({ "/list" })
	public ResponseEntity<Object> findAllWithPagination(@RequestBody PriceMasterListPageRequest request) {
		Map<String, Object> response = new HashMap<>();

		try {
			Page<PriceMasterEntity> pageResult = priceMasterService.findAllWithPagination(request);
			List<Object> inwardList = pageResult.stream().map(entity -> PriceMasterEntity.valueOf(entity)).collect(Collectors.toList());
			response.put("content", inwardList);
			response.put("currentPage", pageResult.getNumber());
			response.put("totalItems", pageResult.getTotalElements());
			response.put("totalPages", pageResult.getTotalPages());
			return new ResponseEntity(response, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PostMapping(value = "/calculatePrice", produces = "application/json")
	public String calculatePrice(@RequestBody CalculatePriceRequest calculatePriceRequest) {
		return priceMasterService.calculateInstructionPrice(null, 0, 0);
	}

}
