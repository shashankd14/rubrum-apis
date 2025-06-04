package com.steel.product.jswone.controller;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.steel.product.jswone.request.SalesOrderMainRequest;
import com.steel.product.jswone.service.SalesOrderJswService;

@RestController
@CrossOrigin
@Tag(name = "Sales Order", description = "Sales Order")
@RequestMapping({ "/so" })
public class SalesOrderJswController {

	private SalesOrderJswService salesOrderService;

	@Autowired
	public SalesOrderJswController(SalesOrderJswService salesOrderService) {
		this.salesOrderService = salesOrderService;
	}

	@PostMapping(value = "/create", produces = "application/json")
	public ResponseEntity<Object> save(@RequestBody SalesOrderMainRequest salesOrderMainRequest) {
		return salesOrderService.save(salesOrderMainRequest);
	}

}
