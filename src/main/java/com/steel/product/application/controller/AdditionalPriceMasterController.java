package com.steel.product.application.controller;

import com.steel.product.application.dto.additionalpricemaster.AdditionalPriceMasterRequest;
import com.steel.product.application.dto.additionalpricemaster.AdditionalPriceMasterResponse;
import com.steel.product.application.service.AdditionalPriceMasterService;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Tag(name = "Additional Price Master", description = "Additional Price Master")
@RequestMapping({ "/additionalprice" })
public class AdditionalPriceMasterController {

	@Autowired
	private AdditionalPriceMasterService additionalPriceMasterService;

	@GetMapping(value = "/pocess/{id}", produces = "application/json")
	public ResponseEntity<Object> getByPocessId(@PathVariable("id") int id) {
		return new ResponseEntity<Object>(additionalPriceMasterService.additionalPriceStaticDetails(id), HttpStatus.OK);
	}
	
	@PostMapping(value = "/save", produces = "application/json")
	public ResponseEntity<Object> save(@RequestBody List<AdditionalPriceMasterRequest> priceMasterRequest) {

		return additionalPriceMasterService.save(priceMasterRequest);
	}
	
	@PutMapping(value = "/update", produces = "application/json")
	public ResponseEntity<Object> update(@RequestBody List<AdditionalPriceMasterRequest> priceMasterRequest) {

		return additionalPriceMasterService.save(priceMasterRequest);
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/json" )
	public ResponseEntity<Object> delete(@PathVariable("id") int id) {
		return additionalPriceMasterService.delete(id);
	}

	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Object> getById(@PathVariable("id") int id) {
		AdditionalPriceMasterResponse resp = additionalPriceMasterService.getById(id);
		return new ResponseEntity<Object>(resp, HttpStatus.OK);
	}

	@GetMapping(value = "/{partyId}/{processId}/{matGradeId}", produces = "application/json")
	public ResponseEntity<Object> getCustProcessMaterialId(@PathVariable("partyId") int partyId,
			@PathVariable("processId") int processId, @PathVariable("matGradeId") int matGradeId) {
		List<AdditionalPriceMasterResponse> list = additionalPriceMasterService.getCustProcessAdditionalPriceId(partyId,
				processId, matGradeId);
		return new ResponseEntity<Object>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/{partyId}/{processId}", produces = "application/json")
	public ResponseEntity<Object> getCustProcess(@PathVariable("partyId") int partyId,
			@PathVariable("processId") int processId) {
		List<AdditionalPriceMasterResponse> list = additionalPriceMasterService.getCustProcess(partyId, processId);
		return new ResponseEntity<Object>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/party/{partyId}", produces = "application/json")
	public ResponseEntity<Object> getCust(@PathVariable("partyId") int partyId ) {
		List<AdditionalPriceMasterResponse> list = additionalPriceMasterService.getCustPriceDetails(partyId);
		return new ResponseEntity<Object>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/process/{processId}", produces = "application/json")
	public ResponseEntity<Object> getProcessPriceDetails(@PathVariable("processId") int processId) {
		List<AdditionalPriceMasterResponse> list = additionalPriceMasterService.getProcessPriceDetails( processId);
		return new ResponseEntity<Object>(list, HttpStatus.OK);
	}

	@GetMapping(produces = "application/json")
	public List<AdditionalPriceMasterResponse> getAllPriceDetails() {
		List<AdditionalPriceMasterResponse> list = additionalPriceMasterService.getAllPriceDetails();
		return list;
	}


}
