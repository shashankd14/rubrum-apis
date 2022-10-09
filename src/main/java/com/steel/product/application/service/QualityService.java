package com.steel.product.application.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.steel.product.application.dto.quality.QualityPartyMappingRequest;
import com.steel.product.application.dto.quality.QualityPartyMappingResponse;
import com.steel.product.application.dto.quality.QualityTemplateMainResponse;

public interface QualityService {

	ResponseEntity<Object> save(String payloadTransaction, int userId);

	ResponseEntity<Object> deleteTemplate(int id);

	ResponseEntity<Object> delete(int id);

	QualityTemplateMainResponse getById(int id);

	List<QualityTemplateMainResponse> getAllTemplateDetails();

	ResponseEntity<Object> templateMapSave(QualityPartyMappingRequest qualityPartyMappingRequest, int userId);

	ResponseEntity<Object> deleteTemplateMap (int id);

	List<QualityPartyMappingResponse> getByPartyId(int partyId);

	List<QualityPartyMappingResponse> getAllMappings();
}
