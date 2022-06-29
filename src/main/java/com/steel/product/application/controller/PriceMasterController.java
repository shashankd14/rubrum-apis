package com.steel.product.application.controller;

import com.steel.product.application.dto.pricemaster.PriceMasterResponse;
import com.steel.product.application.dto.pricemaster.PriceMasterRequest;
import com.steel.product.application.service.PriceMasterService;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
	/*
	
	@GetMapping(value = "/{partyId}/{processId}/{matGradeId}", produces = "application/json")
	public ResponseEntity<Object> getCustProcessMaterialId(@PathVariable("partyId") int partyId,
			@PathVariable("processId") int processId, @PathVariable("matGradeId") int matGradeId) {
		List<PriceMasterResponse> list = priceMasterService.getCustProcessMaterialId(partyId, processId, matGradeId);
		return new ResponseEntity<Object>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/{partyId}/{processId}", produces = "application/json")
	public ResponseEntity<Object> getCustProcess(@PathVariable("partyId") int partyId,
			@PathVariable("processId") int processId) {
		List<PriceMasterResponse> list = priceMasterService.getCustProcess(partyId, processId);
		return new ResponseEntity<Object>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/party/{partyId}", produces = "application/json")
	public ResponseEntity<Object> getCust(@PathVariable("partyId") int partyId ) {
		List<PriceMasterResponse> list = priceMasterService.getCustPriceDetails(partyId);
		return new ResponseEntity<Object>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/process/{processId}", produces = "application/json")
	public ResponseEntity<Object> getProcessPriceDetails(@PathVariable("processId") int processId) {
		List<PriceMasterResponse> list = priceMasterService.getProcessPriceDetails( processId);
		return new ResponseEntity<Object>(list, HttpStatus.OK);
	}

	@PostMapping(value = "/copyCustomerDetails", produces = "application/json")
	public List<PriceMasterEntity> copyCustomerDetails(@RequestBody PriceMasterRequest priceMasterRequest) {
		return priceMasterService.copyCustomerDetails(priceMasterRequest);
	}

	@PostMapping(value = "/copyProcessDetails", produces = "application/json")
	public List<PriceMasterEntity> copyCustProcessDetails(@RequestBody PriceMasterRequest priceMasterRequest) {
		return priceMasterService.copyCustProcessDetails(priceMasterRequest);
	}

	@PostMapping(value = "/copyMatGradeDetails", produces = "application/json")
	public List<PriceMasterEntity> copyMatGradeDetails(@RequestBody PriceMasterRequest priceMasterRequest) {
		return priceMasterService.copyMatGradeDetails( priceMasterRequest);
	}
  */
}
