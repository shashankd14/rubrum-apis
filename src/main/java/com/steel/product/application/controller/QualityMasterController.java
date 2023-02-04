package com.steel.product.application.controller;

import com.steel.product.application.dto.quality.QualityCheckRequest;
import com.steel.product.application.dto.quality.QualityCheckResponse;
import com.steel.product.application.dto.quality.QualityPartyMappingRequest;
import com.steel.product.application.dto.quality.QualityPartyMappingResponse;
import com.steel.product.application.dto.quality.QualityTemplateResponse;
import com.steel.product.application.service.QualityService;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@Tag(name = "Quality", description = "Quality")
@RequestMapping({ "/quality" })
public class QualityMasterController {

	@Autowired
	private QualityService qualityService;

	@PostMapping(value = "/template/save")
	public ResponseEntity<Object> save(HttpServletRequest request,
			@RequestParam(value = "file1", required = false) MultipartFile file1,
			@RequestParam(value = "file2", required = false) MultipartFile file2,
			@RequestParam(value = "file3", required = false) MultipartFile file3,
			@RequestParam(value = "file4", required = false) MultipartFile file4,
			@RequestParam(value = "file5", required = false) MultipartFile file5,
			@RequestParam(value = "templateName", required = true) String templateName,
			@RequestParam(value = "processId", required = false) String processId,
			@RequestParam(value = "stageName", required = true) String stageName,
			@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "templateDetails", required = true) String templateDetails) {

		return qualityService.save(null, templateName, stageName, templateDetails, userId, file1, file2, file3, file4,
				file5, processId);
	}

	@PutMapping(value = "/template/update")
	public ResponseEntity<Object> update(HttpServletRequest request,
			@RequestParam(value = "file1", required = false) MultipartFile file1,
			@RequestParam(value = "file2", required = false) MultipartFile file2,
			@RequestParam(value = "file3", required = false) MultipartFile file3,
			@RequestParam(value = "file4", required = false) MultipartFile file4,
			@RequestParam(value = "file5", required = false) MultipartFile file5,
			@RequestParam(value = "templateName", required = true) String templateName,
			@RequestParam(value = "templateId", required = true) String templateId,
			@RequestParam(value = "processId", required = false) String processId,
			@RequestParam(value = "stageName", required = true) String stageName,
			@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "templateDetails", required = true) String templateDetails) {
		
		return qualityService.save(templateId, templateName, stageName, templateDetails, userId, file1, file2, file3, file4,
				file5, processId);
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
	public List<QualityPartyMappingResponse> getBypartyId(@PathVariable("templateId") int templateId) {
		List<QualityPartyMappingResponse> list = qualityService.getByTemplateId(templateId);
		return list;
	}

	@GetMapping(value = "/templatemap", produces = "application/json")
	public List<QualityPartyMappingResponse> getAllMappings() {
		List<QualityPartyMappingResponse> list = qualityService.getAllMappings();
		return list;
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
	
}
