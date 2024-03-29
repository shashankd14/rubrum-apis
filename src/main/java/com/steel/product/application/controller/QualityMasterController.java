package com.steel.product.application.controller;

import com.steel.product.application.dto.instruction.InstructionResponseDto;
import com.steel.product.application.dto.quality.KQPPartyMappingRequest;
import com.steel.product.application.dto.quality.KQPPartyMappingResponse;
import com.steel.product.application.dto.quality.KQPRequest;
import com.steel.product.application.dto.quality.KQPResponse;
import com.steel.product.application.dto.quality.QIRSaveDataRequest;
import com.steel.product.application.dto.quality.QualityCheckRequest;
import com.steel.product.application.dto.quality.QualityCheckResponse;
import com.steel.product.application.dto.quality.QualityInspDispatchListResponse;
import com.steel.product.application.dto.quality.QualityInspReportListPageResponse;
import com.steel.product.application.dto.quality.QualityInspectionReportResponse;
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

	@GetMapping(value = "/templatemap/getallwidth", produces = "application/json")
	public ResponseEntity<Object> getAllWidth() {

		return new ResponseEntity<Object>(qualityService.getAllWidth(), HttpStatus.OK);
	}

	@GetMapping(value = "/templatemap/getalllength", produces = "application/json")
	public ResponseEntity<Object> getAllLength() {

		return new ResponseEntity<Object>(qualityService.getAllLength(), HttpStatus.OK);
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
	
	@GetMapping({ "/reports/inwardlist/{pageNo}/{pageSize}" })
	public ResponseEntity<Object> inwardlist(@PathVariable int pageNo, @PathVariable int pageSize,
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

	@GetMapping({ "/reports/preprocessing/{pageNo}/{pageSize}" })
	public ResponseEntity<Object> preprocessing(@PathVariable int pageNo, @PathVariable int pageSize,
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

	@GetMapping({ "/reports/processing/{pageNo}/{pageSize}" })
	public ResponseEntity<Object> processing(@PathVariable int pageNo, @PathVariable int pageSize,
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

	@GetMapping({ "/reports/predispatch/{pageNo}/{pageSize}" })
	public ResponseEntity<Object> predispatch(@PathVariable int pageNo, @PathVariable int pageSize,
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

	@GetMapping({ "/reports/postdispatch/{pageNo}/{pageSize}" })
	public ResponseEntity<Object> postdispatch(@PathVariable int pageNo, @PathVariable int pageSize,
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
	
	@PostMapping(value = "/kqp/save", produces = "application/json" )
	public ResponseEntity<Object> save(@RequestBody KQPRequest kcpRequest, HttpServletRequest request) {
		int userId = (request.getHeader("userId")==null ? 1: Integer.parseInt(request.getHeader("userId")));

		return qualityService.save(kcpRequest, userId);
	}
	
	@PutMapping(value = "/kqp/update", produces = "application/json" )
	public ResponseEntity<Object> update(@RequestBody KQPRequest kcpRequest, HttpServletRequest request) {
		int userId = (request.getHeader("userId")==null ? 1: Integer.parseInt(request.getHeader("userId")));

		return qualityService.save(kcpRequest, userId);
	}

	@GetMapping(value = "/kqp/{id}", produces = "application/json")
	public KQPResponse kqpGetById (@PathVariable("id") int id) {
		return qualityService.kqpGetById(id);
	}

	@GetMapping(value = "/kqp", produces = "application/json")
	public List<KQPResponse> kqpGetByAll() {
		return qualityService.kqpGetByAll();
	}
	
	@DeleteMapping(value = "/kqp/{id}", produces = "application/json" )
	public ResponseEntity<Object> deleteKQP(@PathVariable("id") int id) {
		return qualityService.deleteKQP(id);
	}

	@PostMapping(value = "/kqppartymap/save", produces = "application/json")
	public ResponseEntity<Object> kqpPartyMapSave(@RequestBody KQPPartyMappingRequest kqpPartyMappingRequest) {
 		return qualityService.kqpPartyMapSave(kqpPartyMappingRequest);
	}
	
	@DeleteMapping(value = "/kqppartymap/{kqpId}", produces = "application/json" )
	public ResponseEntity<Object> deleteKQPPartyMap (@PathVariable("kqpId") int kqpId) {
		return qualityService.deleteKQPPartyMap(kqpId);
	}

	@GetMapping(value = "/kqppartymap/{kqpId}", produces = "application/json")
	public List<KQPPartyMappingResponse> getkqpByTemplateId(@PathVariable("kqpId") int kqpId) {
		List<KQPPartyMappingResponse> list = qualityService.getByKQPId(kqpId);
		return list;
	}

	@GetMapping(value = "/kqppartymap", produces = "application/json")
	public List<KQPPartyMappingResponse> getAllKQPMappings() {
		List<KQPPartyMappingResponse> list = qualityService.getAllKQPMappings();
		return list;
	}

	@GetMapping(value = "/qir/inward/listpage", produces = "application/json")
	public List<QualityInspReportListPageResponse> qirInwardListPage() {
		List<QualityInspReportListPageResponse> list = qualityService.qirInwardListPage();
		return list;
	}

	@GetMapping(value = "/qir/preprocessing/listpage", produces = "application/json")
	public List<QualityInspReportListPageResponse> qirPreListPage() {
		List<QualityInspReportListPageResponse> list = qualityService.qirListPage();
		return list;
	}

	@GetMapping(value = "/qir/processing/listpage", produces = "application/json")
	public List<QualityInspReportListPageResponse> qirPostListPage() {
		List<QualityInspReportListPageResponse> list = qualityService.qirListPage();
		return list;
	}

	@PostMapping(value = "/qir/fetchpacketdtls", produces = "application/json")
	public ResponseEntity<Object> getDocInstructionDetails(@RequestBody QIRSaveDataRequest qirSaveDataRequest) {
		try {
			List<InstructionResponseDto> instructionList = qualityService.fetchpacketdtls(qirSaveDataRequest);
			return new ResponseEntity<Object>(instructionList, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/qir/postdispatch/dispatchlist", produces = "application/json")
	public List<QualityInspDispatchListResponse> qirPostDispatchList() {
		List<QualityInspDispatchListResponse> list = qualityService.qirDispatchList();
		return list;
	}
			
	@GetMapping(value = "/qir/predispatch/dispatchlist", produces = "application/json")
	public List<QualityInspDispatchListResponse> qirPreDispatchList() {
		List<QualityInspDispatchListResponse> list = qualityService.qirDispatchList();
		return list;
	}

	@PostMapping(value = "/qir/dispatchdtls", produces = "application/json")
	public ResponseEntity<Object> getDispatchDetails(@RequestBody QIRSaveDataRequest qirSaveDataRequest) {
		try {
			List<InstructionResponseDto> instructionList = qualityService.getDispatchDetails(qirSaveDataRequest);
			return new ResponseEntity<Object>(instructionList, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/qir/save")
	public ResponseEntity<Object> qirReportSave(HttpServletRequest request,
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
			@RequestParam(value = "templateId", required = false) String templateId,
			@RequestParam(value = "processId", required = false) String processId,
			@RequestParam(value = "stageName", required = true) String stageName,
			@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "templateDetails", required = true) String templateDetails,
			@RequestParam(value = "coilNo", required = true) String coilNo,
			@RequestParam(value = "customerBatchNo", required = true) String customerBatchNo,
			@RequestParam(value = "planId", required = false) String planId,
			@RequestParam(value = "deliveryChalanNo", required = false) String deliveryChalanNo,
			@RequestParam(value = "qirId", required = false) String qirId) {

		return qualityService.qirReportSave(templateId, stageName, templateDetails, userId, processId, rustObserved,
				safetyIssues, waterExposure, wireRopeDamages, packingIntact, improperStorage, strapping, weighmentSlip,
				weighment, acknowledgementReceipt, unloadingImproper, coilNo, customerBatchNo, planId,
				deliveryChalanNo, qirId);
	}

	@GetMapping(value = "/qir/{stageName}/{coilNo}/{planId}", produces = "application/json")
	public QualityInspectionReportResponse getQIRREport(@PathVariable("stageName") String stageName,
			@PathVariable("coilNo") String coilNo, @PathVariable("planId") String planId) {
		return qualityService.getQIRReport(stageName, coilNo, planId);
	}
	
	@GetMapping(value = "/qir/{id}", produces = "application/json" )
	public QualityInspectionReportResponse getQIR(@PathVariable("id") int id) {
		return qualityService.getQIRReportById(id);
	}
	
	@DeleteMapping(value = "/qir/{id}", produces = "application/json" )
	public ResponseEntity<Object> deleteQIR(@PathVariable("id") int id) {
		return qualityService.deleteQIR(id);
	}


}
