package com.steel.product.trading.controller;

import com.steel.product.trading.entity.WeighbridgeEntity;
import com.steel.product.trading.request.DeleteRequest;
import com.steel.product.trading.request.SearchRequest;
import com.steel.product.trading.request.WeighbridgeRequest;
import com.steel.product.trading.service.WeighbridgeService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Tag(name = "WeighbridgeEntry Master", description = "WeighbridgeEntry Master")
@RequestMapping({ "/trading" })
public class WeighbridgeEntryController {

	@Autowired
	private WeighbridgeService weighbridgeService;

	@PostMapping(value = "/weighbridge/save", produces = "application/json" )
	public ResponseEntity<Object> save(@RequestBody WeighbridgeRequest locationRequest) {
		return weighbridgeService.save(locationRequest);
	}
	
	@PutMapping(value = "/weighbridge/update", produces = "application/json" )
	public ResponseEntity<Object> update(@RequestBody WeighbridgeRequest locationRequest) {
		return weighbridgeService.save(locationRequest);
	}

	@PostMapping({ "/weighbridge/list" })
	public ResponseEntity<Object> list(@RequestBody SearchRequest searchRequest) {
		Map<String, Object> response = new HashMap<>();

		if (searchRequest.getId() != null && searchRequest.getId() > 0) {
			WeighbridgeEntity resp = weighbridgeService.findByWeighbridgeId( searchRequest.getId());
			return new ResponseEntity<Object>(resp, HttpStatus.OK);
		} else {
			Page<WeighbridgeEntity> pageResult = weighbridgeService.getWeighbridgeList(searchRequest);
			response.put("content", pageResult.toList());
			response.put("currentPage", pageResult.getNumber());
			response.put("totalItems", pageResult.getTotalElements());
			response.put("totalPages", pageResult.getTotalPages());
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/weighbridge/delete", produces = "application/json" )
	public ResponseEntity<Object> delete(@RequestBody DeleteRequest deleteRequest) {
		return weighbridgeService.weighbridgeDelete( deleteRequest);
	}
	
}
