package com.steel.product.application.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.steel.product.application.dto.quality.QualityCheckRequest;
import com.steel.product.application.dto.quality.QualityCheckResponse;
import com.steel.product.application.dto.quality.QualityPartyMappingRequest;
import com.steel.product.application.dto.quality.QualityPartyMappingRequestNew;
import com.steel.product.application.dto.quality.QualityPartyMappingResponse;
import com.steel.product.application.dto.quality.QualityTemplateMainResponse;
import com.steel.product.application.dto.quality.QualityTemplateResponse;
import com.steel.product.application.entity.QualityTemplateEntity;

public interface QualityService {

	ResponseEntity<Object> deleteTemplate(int id);

	ResponseEntity<Object> delete(int id);

	QualityTemplateResponse getById(int id);

	List<QualityTemplateResponse> getAllTemplateDetails();

	ResponseEntity<Object> templateMapSave(QualityPartyMappingRequest qualityPartyMappingRequest, int userId);

	ResponseEntity<Object> deleteTemplateMap (int id);

	List<QualityPartyMappingResponse> getByPartyId(int partyId);

	List<QualityPartyMappingResponse> getAllMappings();

	QualityCheckResponse qualityCheck(QualityCheckRequest qualityCheckRequest);

	ResponseEntity<Object> templateMapSaveNew(List<QualityPartyMappingRequestNew> list1, int partyId, int userId);

	ResponseEntity<Object> save(String templateId, String templateName, String stageName, String templateDetails, String userId,
			MultipartFile file1, MultipartFile file2, MultipartFile file3,
			MultipartFile file4, MultipartFile file5, String processId);

}
