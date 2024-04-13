package com.steel.product.trading.controller;

import com.steel.product.trading.request.InwardTradingRequest;
import com.steel.product.trading.service.InwardTradingService;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Tag(name = "Inward Trading", description = "Inward Trading")
@RequestMapping({ "/trading" })
public class InwardTradingController {

	@Autowired
	private InwardTradingService inwardTradingService;

	@PostMapping(value = "/inward/save", produces = "application/json")
	public ResponseEntity<Object> save(@RequestBody InwardTradingRequest inwardTradingRequest) {
		return inwardTradingService.save(inwardTradingRequest);
	}
	
}
