package com.steel.product.application.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.steel.product.application.dto.party.LocationMasterDto;
import com.steel.product.application.service.LocationMasterService;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/location")
@CrossOrigin
@Log4j2
public class LocationMasterController {

	private final LocationMasterService locationMasterService;

	public LocationMasterController(LocationMasterService locationMasterService) {
		this.locationMasterService = locationMasterService;
	}

	@PostMapping("/list")
	public ResponseEntity<Object> getAllLocations() {
		log.info("POST /api/location/list called");
		try {
			List<LocationMasterDto> locations = locationMasterService.getAllLocations();
			return ResponseEntity.ok(locations);
		} catch (Exception e) {
			log.error("Error fetching location list: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to fetch locations: " + e.getMessage());
		}
	}
}