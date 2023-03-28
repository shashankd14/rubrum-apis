package com.steel.product.application.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.steel.product.application.dto.quality.KQPRequest;
import com.steel.product.application.dto.quality.KQPResponse;
import com.steel.product.application.dto.quality.QualityCheckRequest;
import com.steel.product.application.dto.quality.QualityCheckResponse;
import com.steel.product.application.dto.quality.QualityPartyMappingRequest;
import com.steel.product.application.dto.quality.QualityPartyMappingRequestNew;
import com.steel.product.application.dto.quality.QualityPartyMappingResponse;
import com.steel.product.application.dto.quality.QualityReportResponse;
import com.steel.product.application.dto.quality.QualityTemplateResponse;

public interface QualityService {

	ResponseEntity<Object> deleteTemplate(int id);

	ResponseEntity<Object> delete(int id);

	QualityTemplateResponse getById(int id);

	List<QualityTemplateResponse> getAllTemplateDetails();

	ResponseEntity<Object> templateMapSave(QualityPartyMappingRequest qualityPartyMappingRequest);

	ResponseEntity<Object> deleteTemplateMap(int templateId);

	List<QualityPartyMappingResponse> getByPartyId(int partyId);

	List<QualityPartyMappingResponse> getByTemplateId(int templateId);

	List<QualityPartyMappingResponse> getAllMappings();

	QualityCheckResponse qualityCheck(QualityCheckRequest qualityCheckRequest);

	ResponseEntity<Object> templateMapSaveNew(List<QualityPartyMappingRequestNew> list1, int partyId, int userId);

	ResponseEntity<Object> save(String templateId, String templateName, String stageName, String templateDetails,
			String userId, String processId, MultipartFile rustObserved, MultipartFile safetyIssues,
			MultipartFile waterExposure, MultipartFile wireRopeDamages, MultipartFile packingIntact,
			MultipartFile improperStorage, MultipartFile strapping, MultipartFile weighmentSlip,
			MultipartFile weighment, MultipartFile acknowledgementReceipt, MultipartFile unloadingImproper);

	List<Float> getAllThickness();

	ResponseEntity<Object> reportsSave(String inspectionId, String coilNumber, String inwardId, String templateId,
			String stageName, String templateDetails, String userId, MultipartFile rustObserved,
			MultipartFile safetyIssues, MultipartFile waterExposure, MultipartFile wireRopeDamages,
			MultipartFile packingIntact, MultipartFile improperStorage, MultipartFile strapping,
			MultipartFile weighmentSlip, MultipartFile weighment, MultipartFile acknowledgementReceipt,
			MultipartFile unloadingImproper);

	QualityReportResponse inspectionreportGetById(int id);

	ResponseEntity<Object> deleteInspectionReport(int id);

	List<QualityReportResponse> inspectionreportGetAll();

	List<QualityPartyMappingResponse> getByPartyIdAndStageName(int partyId, String stageName);

	ResponseEntity<Object> save(KQPRequest kcpRequest, int userId);

	KQPResponse kqpGetById(int id);

	List<KQPResponse> kqpGetByAll();

	ResponseEntity<Object> deleteKQP(int id);

}
