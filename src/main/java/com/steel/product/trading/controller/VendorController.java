package com.steel.product.trading.controller;

import com.steel.product.trading.entity.VendorEntity;
import com.steel.product.trading.request.DeleteRequest;
import com.steel.product.trading.request.SearchRequest;
import com.steel.product.trading.request.VendorRequest;
import com.steel.product.trading.service.VendorService;

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
@Tag(name = "Vendor Master", description = "Vendor Master")
@RequestMapping({ "/trading" })
public class VendorController {

	@Autowired
	private VendorService vendorService;

	@PostMapping(value = "/vendor/save", produces = "application/json" )
	public ResponseEntity<Object> save(@RequestBody VendorRequest vendorRequest) {
		return vendorService.save(vendorRequest);
	}
	
	@PutMapping(value = "/vendor/update", produces = "application/json" )
	public ResponseEntity<Object> update(@RequestBody VendorRequest categoryRequest) {
		return vendorService.save(categoryRequest);
	}

	@PostMapping({ "/vendor/list" })
	public ResponseEntity<Object> list(@RequestBody SearchRequest searchPageRequest) {
		Map<String, Object> response = new HashMap<>();

		if (searchPageRequest.getId() != null && searchPageRequest.getId() > 0) {
			VendorEntity resp = vendorService.findByVendorId( searchPageRequest.getId());
			return new ResponseEntity<Object>(resp, HttpStatus.OK);
		} else {
			Page<VendorEntity> pageResult = vendorService.getVendorList(searchPageRequest);
			response.put("content", pageResult.toList());
			response.put("currentPage", pageResult.getNumber());
			response.put("totalItems", pageResult.getTotalElements());
			response.put("totalPages", pageResult.getTotalPages());
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/vendor/delete", produces = "application/json" )
	public ResponseEntity<Object> delete(@RequestBody DeleteRequest deleteRequest) {
		return vendorService.vendorDelete(deleteRequest);
	}
	
}
