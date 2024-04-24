package com.steel.product.trading.controller;

import com.steel.product.trading.request.EQPRequest;
import com.steel.product.trading.request.EQPSearchRequest;
import com.steel.product.trading.service.EQPService;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Tag(name = "EQP", description = "EQP")
@RequestMapping({ "/trading" })
public class EQPController {

	@Autowired
	private EQPService eqpService;

	@PostMapping(value = "/enquiry/save", produces = "application/json")
	public ResponseEntity<Object> save(@RequestBody EQPRequest eqpRequest) {
		return eqpService.save(eqpRequest);
	}

	@PostMapping(value = "/enquiry/update", produces = "application/json")
	public ResponseEntity<Object> update(@RequestBody EQPRequest eqpRequest) {
		return eqpService.save(eqpRequest);
	}

	@PostMapping({ "/enquiry/list" })
	public ResponseEntity<Object> getEQPList(@RequestBody EQPSearchRequest searchPageRequest) {
		Map<String, Object> response = eqpService.getEQPList(searchPageRequest);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
}
