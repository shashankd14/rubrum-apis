package com.steel.product.application.controller;

import com.steel.product.application.dto.quality.QualityCheckRequest;
import com.steel.product.application.dto.quality.QualityCheckResponse;
import com.steel.product.application.dto.quality.QualityPartyMappingRequest;
import com.steel.product.application.dto.quality.QualityPartyMappingResponse;
import com.steel.product.application.dto.quality.QualityReportResponse;
import com.steel.product.application.dto.quality.QualityTemplateResponse;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.service.InwardEntryService;
import com.steel.product.application.service.QualityService;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@Tag(name = "Quality", description = "Quality")
@RequestMapping({ "/quality" })
public class QualityMasterController {
	
	@Autowired
	private InwardEntryService inwdEntrySvc;

	@Autowired
	private QualityService qualityService;

	@PostMapping(value = "/template/save")
	public ResponseEntity<Object> save(HttpServletRequest request,
			@RequestParam(value = "rustObserved", required = false) MultipartFile rustObserved,
			@RequestParam(value = "safetyIssues", required = false) MultipartFile safetyIssues,
			@RequestParam(value = "waterExposure", required = false) MultipartFile waterExposure,
			@RequestParam(value = "wireRopeDamages", required = false) MultipartFile wireRopeDamages,
			@RequestParam(value = "packingIntact", required = false) MultipartFile packingIntact,
			@RequestParam(value = "improperStorage", required = false) MultipartFile improperStorage,
			@RequestParam(value = "strapping", required = false) MultipartFile strapping,
			@RequestParam(value = "weighmentSlip", required = false) MultipartFile weighmentSlip,
			@RequestParam(value = "weighment", required = false) MultipartFile weighment,
			@RequestParam(value = "acknowledgementReceipt", required = false) MultipartFile acknowledgementReceipt,
			@RequestParam(value = "unloadingImproper", required = false) MultipartFile unloadingImproper,
			@RequestParam(value = "templateName", required = true) String templateName,
			@RequestParam(value = "processId", required = false) String processId,
			@RequestParam(value = "stageName", required = true) String stageName,
			@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "templateDetails", required = true) String templateDetails) {
		
		return qualityService.save(null, templateName, stageName, templateDetails, userId, processId,
				rustObserved, safetyIssues, waterExposure, wireRopeDamages, packingIntact, improperStorage, 
				strapping, weighmentSlip, weighment, acknowledgementReceipt, unloadingImproper );
	}

	@PutMapping(value = "/template/update")
	public ResponseEntity<Object> update(HttpServletRequest request,
			@RequestParam(value = "rustObserved", required = false) MultipartFile rustObserved,
			@RequestParam(value = "safetyIssues", required = false) MultipartFile safetyIssues,
			@RequestParam(value = "waterExposure", required = false) MultipartFile waterExposure,
			@RequestParam(value = "wireRopeDamages", required = false) MultipartFile wireRopeDamages,
			@RequestParam(value = "packingIntact", required = false) MultipartFile packingIntact,
			@RequestParam(value = "improperStorage", required = false) MultipartFile improperStorage,
			@RequestParam(value = "strapping", required = false) MultipartFile strapping,
			@RequestParam(value = "weighmentSlip", required = false) MultipartFile weighmentSlip,
			@RequestParam(value = "weighment", required = false) MultipartFile weighment,
			@RequestParam(value = "acknowledgementReceipt", required = false) MultipartFile acknowledgementReceipt,
			@RequestParam(value = "unloadingImproper", required = false) MultipartFile unloadingImproper,
			@RequestParam(value = "templateName", required = true) String templateName,
			@RequestParam(value = "templateId", required = true) String templateId,
			@RequestParam(value = "processId", required = false) String processId,
			@RequestParam(value = "stageName", required = true) String stageName,
			@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "templateDetails", required = true) String templateDetails) {
		
		return qualityService.save(templateId, templateName, stageName, templateDetails, userId, processId,
				rustObserved, safetyIssues, waterExposure, wireRopeDamages, packingIntact, improperStorage, 
				strapping, weighmentSlip, weighment, acknowledgementReceipt, unloadingImproper );
	}
	
	@DeleteMapping(value = "/template/{id}", produces = "application/json" )
	public ResponseEntity<Object> deleteTemplate(@PathVariable("id") int id) {
		return qualityService.deleteTemplate(id);
	}

	@GetMapping(value = "/template", produces = "application/json")
	public List<QualityTemplateResponse> getAllTemplates( ) {
		return qualityService.getAllTemplateDetails();
	}

	@GetMapping(value = "/template/{id}", produces = "application/json")
	public QualityTemplateResponse getById(@PathVariable("id") int id) {
		return qualityService.getById(id);
	}
	
	@PostMapping(value = "/templatemap/save", produces = "application/json")
	public ResponseEntity<Object> templateMapSave(@RequestBody QualityPartyMappingRequest qualityPartyMappingRequest, HttpServletRequest request) {
 		return qualityService.templateMapSave(qualityPartyMappingRequest);
	}
	
	@DeleteMapping(value = "/templatemap/{templateId}", produces = "application/json" )
	public ResponseEntity<Object> deleteTemplateMap (@PathVariable("templateId") int templateId) {
		return qualityService.deleteTemplateMap(templateId);
	}

	@GetMapping(value = "/templatemap/{templateId}", produces = "application/json")
	public List<QualityPartyMappingResponse> getByTemplateId(@PathVariable("templateId") int templateId) {
		List<QualityPartyMappingResponse> list = qualityService.getByTemplateId(templateId);
		return list;
	}

	@GetMapping(value = "/templatemap/party/{partyId}", produces = "application/json")
	public List<QualityPartyMappingResponse> getBypartyId(@PathVariable("partyId") int partyId) {
		List<QualityPartyMappingResponse> list = qualityService.getByPartyId(partyId);
		return list;
	}
	@GetMapping(value = "/templatemap/{partyId}/{stageName}", produces = "application/json")
	public List<QualityPartyMappingResponse> getBypartyId(@PathVariable("partyId") int partyId,
			@PathVariable("stageName") String stageName) {
		List<QualityPartyMappingResponse> list = qualityService.getByPartyIdAndStageName(partyId, stageName);
		return list;
	}
 

	@GetMapping(value = "/templatemap", produces = "application/json")
	public List<QualityPartyMappingResponse> getAllMappings() {
		List<QualityPartyMappingResponse> list = qualityService.getAllMappings();
		return list;
	}

	@GetMapping(value = "/templatemap/getallthickness", produces = "application/json")
	public ResponseEntity<Object> getAllThickness() {

		return new ResponseEntity<Object>(qualityService.getAllThickness(), HttpStatus.OK);
	}
	
	@GetMapping(value = "/qualityCheck", produces = "application/json")
	public QualityCheckResponse qualityCheck(@RequestBody QualityCheckRequest qualityCheckRequest, HttpServletRequest request) {
		QualityCheckResponse list = qualityService.qualityCheck(qualityCheckRequest);
		return list;
	}

	@DeleteMapping(value = "/template/stage/{id}", produces = "application/json" )
	public ResponseEntity<Object> deleteStageDetails(@PathVariable("id") int id) {
		return qualityService.delete(id);
	}

	@GetMapping({ "/reports/inwardlist/{pageNo}/{pageSize}" })
	public ResponseEntity<Object> findAllWithPagination(@PathVariable int pageNo, @PathVariable int pageSize,
			@RequestParam(required = false, name = "searchText") String searchText,
			@RequestParam(required = false, name = "partyId") String partyId) {
		Map<String, Object> response = new HashMap<>();
		Page<InwardEntry> pageResult = inwdEntrySvc.findAllWithPagination(pageNo, pageSize, searchText, partyId);
		List<Object> inwardList = pageResult.stream().map(inw -> InwardEntry.valueOfResponse(inw)).collect(Collectors.toList());
		response.put("content", inwardList);
		response.put("currentPage", pageResult.getNumber());
		response.put("totalItems", pageResult.getTotalElements());
		response.put("totalPages", pageResult.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/inspectionreport/save")
	public ResponseEntity<Object> reportsSave(HttpServletRequest request,
			@RequestParam(value = "rustObserved", required = false) MultipartFile rustObserved,
			@RequestParam(value = "safetyIssues", required = false) MultipartFile safetyIssues,
			@RequestParam(value = "waterExposure", required = false) MultipartFile waterExposure,
			@RequestParam(value = "wireRopeDamages", required = false) MultipartFile wireRopeDamages,
			@RequestParam(value = "packingIntact", required = false) MultipartFile packingIntact,
			@RequestParam(value = "improperStorage", required = false) MultipartFile improperStorage,
			@RequestParam(value = "strapping", required = false) MultipartFile strapping,
			@RequestParam(value = "weighmentSlip", required = false) MultipartFile weighmentSlip,
			@RequestParam(value = "weighment", required = false) MultipartFile weighment,
			@RequestParam(value = "acknowledgementReceipt", required = false) MultipartFile acknowledgementReceipt,
			@RequestParam(value = "unloadingImproper", required = false) MultipartFile unloadingImproper,
			@RequestParam(value = "coilNumber", required = true) String coilNumber,
			@RequestParam(value = "inwardId", required = true) String inwardId,
			@RequestParam(value = "templateId", required = true) String templateId,
			@RequestParam(value = "stageName", required = true) String stageName,
			@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "templateDetails", required = true) String templateDetails) {
		
		return qualityService.reportsSave(null, coilNumber, inwardId, templateId, stageName, templateDetails, userId, 
				rustObserved, safetyIssues, waterExposure, wireRopeDamages, packingIntact, improperStorage, 
				strapping, weighmentSlip, weighment, acknowledgementReceipt, unloadingImproper );
	}

	@PutMapping(value = "/inspectionreport/update")
	public ResponseEntity<Object> reportsUpdate(HttpServletRequest request,
			@RequestParam(value = "rustObserved", required = false) MultipartFile rustObserved,
			@RequestParam(value = "safetyIssues", required = false) MultipartFile safetyIssues,
			@RequestParam(value = "waterExposure", required = false) MultipartFile waterExposure,
			@RequestParam(value = "wireRopeDamages", required = false) MultipartFile wireRopeDamages,
			@RequestParam(value = "packingIntact", required = false) MultipartFile packingIntact,
			@RequestParam(value = "improperStorage", required = false) MultipartFile improperStorage,
			@RequestParam(value = "strapping", required = false) MultipartFile strapping,
			@RequestParam(value = "weighmentSlip", required = false) MultipartFile weighmentSlip,
			@RequestParam(value = "weighment", required = false) MultipartFile weighment,
			@RequestParam(value = "acknowledgementReceipt", required = false) MultipartFile acknowledgementReceipt,
			@RequestParam(value = "unloadingImproper", required = false) MultipartFile unloadingImproper,
			@RequestParam(value = "inspectionId", required = true) String inspectionId,
			@RequestParam(value = "coilNumber", required = true) String coilNumber,
			@RequestParam(value = "inwardId", required = true) String inwardId,
			@RequestParam(value = "templateId", required = true) String templateId,
			@RequestParam(value = "stageName", required = true) String stageName,
			@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "templateDetails", required = true) String templateDetails) {

		return qualityService.reportsSave(inspectionId, coilNumber, inwardId, templateId, stageName, templateDetails,
				userId, rustObserved, safetyIssues, waterExposure, wireRopeDamages, packingIntact, improperStorage,
				strapping, weighmentSlip, weighment, acknowledgementReceipt, unloadingImproper);
	}

	@GetMapping(value = "/inspectionreport/{id}", produces = "application/json")
	public QualityReportResponse inspectionreportGetById (@PathVariable("id") int id) {
		return qualityService.inspectionreportGetById(id);
	}

	@GetMapping(value = "/inspectionreport", produces = "application/json")
	public List<QualityReportResponse> inspectionreportGetAll() {
		return qualityService.inspectionreportGetAll();
	}
	
	@DeleteMapping(value = "/inspectionreport/{id}", produces = "application/json" )
	public ResponseEntity<Object> deleteInspectionReport(@PathVariable("id") int id) {
		return qualityService.deleteInspectionReport(id);
	}
}