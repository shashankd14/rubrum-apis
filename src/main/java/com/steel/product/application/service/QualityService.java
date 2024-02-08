package com.steel.product.application.service;

import java.io.File;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.steel.product.application.dto.instruction.InstructionResponseDto;
import com.steel.product.application.dto.quality.KQPPartyMappingRequest;
import com.steel.product.application.dto.quality.KQPPartyMappingResponse;
import com.steel.product.application.dto.quality.KQPRequest;
import com.steel.product.application.dto.quality.KQPResponse;
import com.steel.product.application.dto.quality.QIRSaveDataRequest;
import com.steel.product.application.dto.quality.QualityCheckRequest;
import com.steel.product.application.dto.quality.QualityCheckResponse;
import com.steel.product.application.dto.quality.QualityInspectionReportResponse;
import com.steel.product.application.dto.quality.QualityPartyMappingRequest;
import com.steel.product.application.dto.quality.QualityPartyMappingRequestNew;
import com.steel.product.application.dto.quality.QualityPartyMappingResponse;
import com.steel.product.application.dto.quality.QualityTemplateResponse;

public interface QualityService {

	ResponseEntity<Object> deleteTemplate(int id);

	ResponseEntity<Object> delete(int id);

	QualityTemplateResponse getById(int id);

	List<QualityTemplateResponse> getAllTemplateDetails();

	ResponseEntity<Object> templateMapSave(QualityPartyMappingRequest qualityPartyMappingRequest);

	ResponseEntity<Object> deleteTemplateMap(Integer templateId);

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

	List<QualityPartyMappingResponse> getByPartyIdAndStageName(int partyId, String stageName);

	ResponseEntity<Object> save(KQPRequest kcpRequest, int userId);

	KQPResponse kqpGetById(int id);

	List<KQPResponse> kqpGetByAll();

	ResponseEntity<Object> deleteKQP(int id);

	List<Float> getAllThickness();

	List<Float> getAllWidth();

	List<Float> getAllLength();

	ResponseEntity<Object> kqpPartyMapSave(KQPPartyMappingRequest qualityPartyMappingRequest);

	ResponseEntity<Object> deleteKQPPartyMap(Integer kqpId);

	List<KQPPartyMappingResponse> getByKQPId(int kqpId);

	List<KQPPartyMappingResponse> getAllKQPMappings();

	List<InstructionResponseDto> fetchpacketdtls(QIRSaveDataRequest qirSaveDataRequest);

	List<InstructionResponseDto> getDispatchDetails(QIRSaveDataRequest qirSaveDataRequest);

	ResponseEntity<Object> qirReportSave(String templateId, String stageName, String templateDetails,
			String planDetails, String userId, String processId, MultipartFile rustObserved, MultipartFile safetyIssues,
			MultipartFile waterExposure, MultipartFile wireRopeDamages, MultipartFile packingIntact,
			MultipartFile improperStorage, MultipartFile strapping, MultipartFile weighmentSlip,
			MultipartFile weighment, MultipartFile acknowledgementReceipt, MultipartFile unloadingImproper,
			String inwardId, String customerBatchNo, String planId, String deliveryChalanNo, String qirId,
			MultipartFile coilBend, MultipartFile packingDamageTransit, MultipartFile processingReport1,
			MultipartFile processingReport2, MultipartFile processingReport3, MultipartFile processingReport4,
			String comments);

	QualityInspectionReportResponse getqirById(String coilNo, String planId);

	ResponseEntity<Object> deleteQIR(int id);

	QualityInspectionReportResponse getQIRReport(String stageName, String coilNo, String planId);

	Page<Object[]> qirInwardListPage(Integer pageNo, Integer pageSize);

	QualityInspectionReportResponse getQIRReportById(int id);

	File qirPDF(Integer id);

	Page<Object[]> qirPreProcessingListPage(Integer pageNo, Integer pageSize);

	Page<Object[]> qirPreDispatchList(Integer pageNo, Integer pageSize);

	Page<Object[]> qirProcessingListPage(Integer pageNo, Integer pageSize);

	Page<Object[]> qirPostDispatchList(Integer pageNo, Integer pageSize);
}
