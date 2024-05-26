package com.steel.product.trading.controller;

import com.steel.product.trading.request.DeleteRequest;
import com.steel.product.trading.request.DeliveryChalanRequest;
import com.steel.product.trading.request.DeliveryOrderRequest;
import com.steel.product.trading.service.DeliveryTradingService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Tag(name = "Delivery Trading Details", description = "Delivery Trading Details")
@RequestMapping({ "/trading" })
public class DeliveryTradingController {

	@Autowired
	private DeliveryTradingService deliveryTradingService;

	@PostMapping(value = "/do/save", produces = "application/json")
	public ResponseEntity<Object> save(@RequestBody DeliveryOrderRequest eqpRequest) {
		return deliveryTradingService.save(eqpRequest);
	}

	@PostMapping(value = "/do/update", produces = "application/json")
	public ResponseEntity<Object> update(@RequestBody DeliveryOrderRequest eqpRequest) {
		return deliveryTradingService.save(eqpRequest);
	}

	@PostMapping(value = "/do/delete", produces = "application/json")
	public ResponseEntity<Object> doDelete(@RequestBody DeleteRequest deleteRequest) {
		return deliveryTradingService.doDelete(deleteRequest);
	}
	
	// Delivery Chalan
	@PostMapping(value = "/dc/save", produces = "application/json")
	public ResponseEntity<Object> dcSave(@RequestBody DeliveryChalanRequest eqpRequest) {
		return deliveryTradingService.dcSave(eqpRequest);
	}

	@PostMapping(value = "/dc/update", produces = "application/json")
	public ResponseEntity<Object> dcUpdate(@RequestBody DeliveryChalanRequest eqpRequest) {
		return deliveryTradingService.dcSave(eqpRequest);
	}

	@PostMapping(value = "/dc/delete", produces = "application/json")
	public ResponseEntity<Object> dcDelete(@RequestBody DeleteRequest deleteRequest) {
		return deliveryTradingService.doDelete(deleteRequest);
	}
}
