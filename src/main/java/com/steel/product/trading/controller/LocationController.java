package com.steel.product.trading.controller;

import com.steel.product.trading.entity.LocationEntity;
import com.steel.product.trading.request.DeleteRequest;
import com.steel.product.trading.request.LocationRequest;
import com.steel.product.trading.request.SearchRequest;
import com.steel.product.trading.service.LocationService;

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
@Tag(name = "Location Master", description = "Location Master")
@RequestMapping({ "/trading" })
public class LocationController {

	@Autowired
	private LocationService locationService;

	@PostMapping(value = "/location/save", produces = "application/json" )
	public ResponseEntity<Object> save(@RequestBody LocationRequest locationRequest) {
		return locationService.save(locationRequest);
	}
	
	@PutMapping(value = "/location/update", produces = "application/json" )
	public ResponseEntity<Object> update(@RequestBody LocationRequest locationRequest) {
		return locationService.save(locationRequest);
	}

	@PostMapping({ "/location/list" })
	public ResponseEntity<Object> list(@RequestBody SearchRequest searchRequest) {
		Map<String, Object> response = new HashMap<>();

		if (searchRequest.getId() != null && searchRequest.getId() > 0) {
			LocationEntity resp = locationService.findByLocationId( searchRequest.getId());
			return new ResponseEntity<Object>(resp, HttpStatus.OK);
		} else {
			Page<LocationEntity> pageResult = locationService.getLocationList(searchRequest);
			response.put("content", pageResult.toList());
			response.put("currentPage", pageResult.getNumber());
			response.put("totalItems", pageResult.getTotalElements());
			response.put("totalPages", pageResult.getTotalPages());
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/location/delete", produces = "application/json" )
	public ResponseEntity<Object> delete(@RequestBody DeleteRequest deleteRequest) {
		return locationService.locationDelete( deleteRequest);
	}
	
}
