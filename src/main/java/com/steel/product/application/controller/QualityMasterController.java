package com.steel.product.application.controller;

import com.steel.product.application.dto.quality.QualityCheckRequest;
import com.steel.product.application.dto.quality.QualityCheckResponse;
import com.steel.product.application.dto.quality.QualityPartyMappingRequest;
import com.steel.product.application.dto.quality.QualityPartyMappingResponse;
import com.steel.product.application.dto.quality.QualityTemplateMainResponse;
import com.steel.product.application.service.QualityService;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Tag(name = "Quality", description = "Quality")
@RequestMapping({ "/quality" })
public class QualityMasterController {

	@Autowired
	private QualityService qualityService;

	@PostMapping(value = "/template/save", produces = "application/json")
	public ResponseEntity<Object> save(@RequestBody Optional<String> payload, HttpServletRequest request) {
		int userId = (request.getHeader("userId") == null ? 1 : Integer.parseInt(request.getHeader("userId")));
		String payloadTransaction = "";
		if (payload.isPresent()) {
			payloadTransaction = payload.get();
		}

		return qualityService.save(payloadTransaction, userId);
	}

	@PutMapping(value = "/template/update", produces = "application/json")
	public ResponseEntity<Object> update(@RequestBody Optional<String> payload, HttpServletRequest request) {
		int userId = (request.getHeader("userId") == null ? 1 : Integer.parseInt(request.getHeader("userId")));
		String payloadTransaction = "";
		if (payload.isPresent()) {
			payloadTransaction = payload.get();
		}

		return qualityService.save(payloadTransaction, userId);
	}

	@GetMapping(value = "/template/{id}", produces = "application/json")
	public QualityTemplateMainResponse getById(@PathVariable("id") int id) {
		QualityTemplateMainResponse list = qualityService.getById(id);
		return list;
	}

	@GetMapping(value = "/template", produces = "application/json")
	public List<QualityTemplateMainResponse> getAllTemplates( ) {
		List<QualityTemplateMainResponse> list = qualityService.getAllTemplateDetails();
		return list;
	}
	
	@DeleteMapping(value = "/template/stage/{id}", produces = "application/json" )
	public ResponseEntity<Object> deleteStageDetails(@PathVariable("id") int id) {
		return qualityService.delete(id);
	}
	
	@DeleteMapping(value = "/template/templateName/{id}", produces = "application/json" )
	public ResponseEntity<Object> deleteTemplate(@PathVariable("id") int id) {
		return qualityService.deleteTemplate(id);
	}
	
	@PostMapping(value = "/templatemap/save", produces = "application/json")
	public ResponseEntity<Object> templateMapSave(@RequestBody QualityPartyMappingRequest qualityPartyMappingRequest, HttpServletRequest request) {
		int userId = (request.getHeader("userId") == null ? 1 : Integer.parseInt(request.getHeader("userId")));
		return qualityService.templateMapSave(qualityPartyMappingRequest, userId);
	}
	
	@DeleteMapping(value = "/templatemap/{id}", produces = "application/json" )
	public ResponseEntity<Object> deleteTemplateMap (@PathVariable("id") int id) {
		return qualityService.deleteTemplateMap(id);
	}

	@GetMapping(value = "/templatemap/party/{partyId}", produces = "application/json")
	public List<QualityPartyMappingResponse> getBypartyId(@PathVariable("partyId") int partyId) {
		List<QualityPartyMappingResponse> list = qualityService.getByPartyId(partyId);
		return list;
	}

	@GetMapping(value = "/templatemap/party", produces = "application/json")
	public List<QualityPartyMappingResponse> getAllMappings() {
		List<QualityPartyMappingResponse> list = qualityService.getAllMappings();
		return list;
	}
	
	@GetMapping(value = "/qualityCheck", produces = "application/json")
	public QualityCheckResponse qualityCheck(@RequestBody QualityCheckRequest qualityCheckRequest, HttpServletRequest request) {
		QualityCheckResponse list = qualityService.qualityCheck(qualityCheckRequest);
		return list;
	}
	
	
}
