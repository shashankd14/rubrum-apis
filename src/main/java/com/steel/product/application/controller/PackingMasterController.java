package com.steel.product.application.controller;

import com.steel.product.application.dto.packingmaster.PackingBucketRequest;
import com.steel.product.application.dto.packingmaster.PackingBucketResponse;
import com.steel.product.application.dto.packingmaster.PackingItemRequest;
import com.steel.product.application.dto.packingmaster.PackingItemResponse;
import com.steel.product.application.dto.packingmaster.PackingRateMasterRequest;
import com.steel.product.application.dto.packingmaster.PackingRateMasterResponse;
import com.steel.product.application.service.PackingMasterService;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Tag(name = "Packing Item Master", description = "Packing Item Master")
@RequestMapping({ "/packing" })
public class PackingMasterController {

	@Autowired
	private PackingMasterService packingMasterService;

	@PostMapping(value = "/item/save", produces = "application/json" )
	public ResponseEntity<Object> save(@RequestBody PackingItemRequest packingItemRequest, HttpServletRequest request) {
		int userId = (request.getHeader("userId")==null ? 1: Integer.parseInt(request.getHeader("userId")));

		return packingMasterService.save(packingItemRequest, userId);
	}
	
	@PutMapping(value = "/item/update", produces = "application/json")
	public ResponseEntity<Object> update(@RequestBody PackingItemRequest packingItemRequest, HttpServletRequest request) {
		int userId = (request.getHeader("userId")==null ? 1: Integer.parseInt(request.getHeader("userId")));
		return packingMasterService.save(packingItemRequest, userId);
	}
	
	@DeleteMapping(value = "/item/{id}", produces = "application/json" )
	public ResponseEntity<Object> delete(@PathVariable("id") int id) {
		return packingMasterService.delete(id);
	}

	@GetMapping(value = "/item/{id}", produces = "application/json")
	public ResponseEntity<Object> getById(@PathVariable("id") int id) {
		PackingItemResponse resp = packingMasterService.getById(id);
		return new ResponseEntity<Object>(resp, HttpStatus.OK);
	}

	@GetMapping(value = "/item", produces = "application/json")
	public List<PackingItemResponse> getAllItemDetails() {
		List<PackingItemResponse> list = packingMasterService.getAllItemDetails();
		return list;
	}
	
	@PostMapping(value = "/bucket/save", produces = "application/json")
	public ResponseEntity<Object> save(@RequestBody PackingBucketRequest packingItemRequest, HttpServletRequest request) {
		int userId = (request.getHeader("userId") == null ? 1 : Integer.parseInt(request.getHeader("userId")));

		return packingMasterService.saveBucket(packingItemRequest, userId);
	}
	
	@PutMapping(value = "/bucket/update", produces = "application/json")
	public ResponseEntity<Object> update(@RequestBody PackingBucketRequest packingItemRequest, HttpServletRequest request) {
		int userId = (request.getHeader("userId") == null ? 1 : Integer.parseInt(request.getHeader("userId")));

		return packingMasterService.saveBucket(packingItemRequest, userId);
	}
	
	@GetMapping(value = "/bucket/{id}", produces = "application/json")
	public ResponseEntity<Object> getByBucketId(@PathVariable("id") int id) {
		PackingBucketResponse resp = packingMasterService.getByBucketId(id);
		return new ResponseEntity<Object>(resp, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/bucket/{id}", produces = "application/json" )
	public ResponseEntity<Object> deleteBucket(@PathVariable("id") int id) {
		return packingMasterService.deleteBucket(id);
	}

	@GetMapping(value = "/bucket", produces = "application/json")
	public List<PackingBucketResponse> getAllBucketList() {
		List<PackingBucketResponse> list = packingMasterService.getAllBucketList();
		return list;
	}
	
	@PostMapping(value = "/rate/save", produces = "application/json")
	public ResponseEntity<Object> save(@RequestBody PackingRateMasterRequest packingRateMasterRequest, HttpServletRequest request) {
		int userId = (request.getHeader("userId") == null ? 1 : Integer.parseInt(request.getHeader("userId")));

		return packingMasterService.save(packingRateMasterRequest, userId);
	}
	
	@PutMapping(value = "/rate/update", produces = "application/json")
	public ResponseEntity<Object> update(@RequestBody PackingRateMasterRequest packingRateMasterRequest, HttpServletRequest request) {
		int userId = (request.getHeader("userId") == null ? 1 : Integer.parseInt(request.getHeader("userId")));

		return packingMasterService.save(packingRateMasterRequest, userId);
	}
	
	@DeleteMapping(value = "/rate/{id}", produces = "application/json" )
	public ResponseEntity<Object> deleteRate(@PathVariable("id") int id) {
		return packingMasterService.deleteRate(id);
	}
	
	@GetMapping(value = "/rate/{id}", produces = "application/json")
	public ResponseEntity<Object> getByIdRate(@PathVariable("id") int id) {
		PackingRateMasterResponse resp = packingMasterService.getByIdRate(id);
		return new ResponseEntity<Object>(resp, HttpStatus.OK);
	}

	@GetMapping(value = "/rate", produces = "application/json")
	public List<PackingRateMasterResponse> getAllRateList() {
		List<PackingRateMasterResponse> list = packingMasterService.getAllRateList();
		return list;
	}

	@GetMapping(value = "/rate/party/{partyId}", produces = "application/json")
	public List<PackingRateMasterResponse> getAllRateListPartyWise(@PathVariable("partyId") int partyId) {
		List<PackingRateMasterResponse> list = packingMasterService.getAllRateListPartyWise(partyId);
		return list;
	}
	
}
