package com.steel.product.trading.controller;

import com.steel.product.application.dto.pricemaster.PriceMasterResponse;
import com.steel.product.application.dto.pricemaster.PriceMasterListPageRequest;
import com.steel.product.trading.entity.MaterialMasterEntity;
import com.steel.product.trading.request.MaterialMasterRequest;
import com.steel.product.trading.service.MaterialMasterService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Tag(name = "Material Master", description = "Material Master")
@RequestMapping({ "/trading/material" })
public class MaterialController {

	@Autowired
	private MaterialMasterService materialMasterService;

	@PostMapping(value = "/save", produces = "application/json" )
	public ResponseEntity<Object> save(@RequestBody List<MaterialMasterRequest> priceMasterRequestList, HttpServletRequest request) {
		int userId = (request.getHeader("userId")==null ? 1: Integer.parseInt(request.getHeader("userId")));
		return materialMasterService.save(priceMasterRequestList, userId);
	}
	
	@PutMapping(value = "/update", produces = "application/json")
	public ResponseEntity<Object> update(@RequestBody List<MaterialMasterRequest> priceMasterRequestList, HttpServletRequest request) {
		int userId = (request.getHeader("userId")==null ? 1: Integer.parseInt(request.getHeader("userId")));
		return materialMasterService.save(priceMasterRequestList, userId);
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/json" )
	public ResponseEntity<Object> delete(@PathVariable("id") int id) {
		return materialMasterService.delete(id);
	}

	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Object> getById(@PathVariable("id") int id) {
		PriceMasterResponse resp = materialMasterService.getById(id);
		return new ResponseEntity<Object>(resp, HttpStatus.OK);
	}

	@GetMapping(produces = "application/json")
	public List<PriceMasterResponse> getAllPriceDetails() {
		List<PriceMasterResponse> list = materialMasterService.getAllPriceDetails();
		return list;
	}

	@PostMapping({ "/list" })
	public ResponseEntity<Object> findAllWithPagination(@RequestBody PriceMasterListPageRequest request) {
		Map<String, Object> response = new HashMap<>();

		try {
			Page<MaterialMasterEntity> pageResult = materialMasterService.findAllWithPagination(request);
			List<Object> inwardList = null;//pageResult.stream().map(entity -> PriceMasterEntity.valueOf(entity)).collect(Collectors.toList());
			response.put("content", inwardList);
			response.put("currentPage", pageResult.getNumber());
			response.put("totalItems", pageResult.getTotalElements());
			response.put("totalPages", pageResult.getTotalPages());
			return new ResponseEntity(response, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


}
