package com.steel.product.application.service;

import com.steel.product.application.dao.QualityPartyTemplateRepository;
import com.steel.product.application.dao.QualityTemplateRepository;
import com.steel.product.application.dto.quality.QualityCheckRequest;
import com.steel.product.application.dto.quality.QualityCheckResponse;
import com.steel.product.application.dto.quality.QualityPartyMappingRequest;
import com.steel.product.application.dto.quality.QualityPartyMappingRequestNew;
import com.steel.product.application.dto.quality.QualityPartyMappingResponse;
import com.steel.product.application.dto.quality.QualityTemplateMainResponse;
import com.steel.product.application.dto.quality.QualityTemplateResponse;
import com.steel.product.application.entity.QualityPartyTemplateEntity;
import com.steel.product.application.entity.QualityTemplateEntity;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class QualityServiceImpl implements QualityService {

	@Autowired
	QualityTemplateRepository qualityTemplateRepository;
	
	@Autowired
	QualityPartyTemplateRepository qualityPartyTemplateRepository;

	@Override
	public ResponseEntity<Object> save(String payloadTransaction, int userId) {
		ResponseEntity<Object> response = null;

		List<QualityTemplateEntity> list=new ArrayList<>();
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject) parser.parse(payloadTransaction);

			log.info("  payloadTransaction == " + payloadTransaction);

			JSONArray jsonArr = new JSONArray(json.get("stageDetails").toString());
			for (int i = 0; i < jsonArr.length(); i++) {
				Integer templateId = 0;
				JSONObject jsonChild = (JSONObject) parser.parse(jsonArr.getJSONObject(i).toString());
				QualityTemplateEntity qualityTemplateEntity = null;
				if (jsonChild.containsKey("templateId") && jsonChild.get("templateId") != null) {
					templateId = Integer.parseInt(jsonChild.get("templateId").toString());
				}
				
				if (templateId > 0) {
					qualityTemplateEntity = qualityTemplateRepository.findByTemplateId(templateId);
					qualityTemplateEntity.setTemplateId(templateId);
				} else {
					qualityTemplateEntity = new QualityTemplateEntity();
					qualityTemplateEntity.setCreatedBy(userId);
					qualityTemplateEntity.setCreatedOn(new Date());
				}
				qualityTemplateEntity.setStageName(jsonChild.get("stageName").toString());
				qualityTemplateEntity.setTemplateName(json.get("templateName").toString());
				qualityTemplateEntity.setFieldDetails(jsonChild.get("fieldDetails").toString());

				if (jsonChild.containsKey("processId") && jsonChild.get("processId") != null && jsonChild.get("processId").toString().length()>0) {
					qualityTemplateEntity.setProcessId(Integer.parseInt(jsonChild.get("processId").toString()));
				}
				if (jsonChild.containsKey("remarks") && jsonChild.get("remarks") != null) {
					qualityTemplateEntity.setRemarks(jsonChild.get("remarks").toString());
				}
				qualityTemplateEntity.setUpdatedBy(userId);
				qualityTemplateEntity.setUpdatedOn(new Date());
				list.add(qualityTemplateEntity);
			}

			qualityTemplateRepository.saveAll(list);
			response = new ResponseEntity<>( "{\"status\": \"success\", \"message\": \"Quality Template details saved successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			log.info("error is ==" + e.getMessage());
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Error Occurred\"}", new HttpHeaders(), HttpStatus.BAD_REQUEST);
		}
		
		return response;
	}

	@Override
	public ResponseEntity<Object> delete(int id) {
		ResponseEntity<Object> response = null;
		try {
			qualityTemplateRepository.deleteById(id);
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Template stage details deleted successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public QualityTemplateMainResponse getById(int id) {
		QualityTemplateMainResponse resp = new QualityTemplateMainResponse();
		List<QualityTemplateEntity> list = qualityTemplateRepository.findByTemplateName(id);

		for (QualityTemplateEntity entity : list) {
			resp.setId(id);
			resp.setTemplateName(entity.getTemplateName());
			resp.getStageDetails().add(QualityTemplateEntity.valueOf(entity));
		}

		return resp;
	}

	@Override
	public List<QualityTemplateMainResponse> getAllTemplateDetails() {
		List<QualityTemplateMainResponse> resp = new ArrayList<QualityTemplateMainResponse>();

		List<QualityTemplateEntity> list = qualityTemplateRepository.findAllTemplates();
		Map<String, List<QualityTemplateResponse>> map = new HashMap<>();

		for (QualityTemplateEntity entity : list) {

			if (map != null && map.get(entity.getTemplateName()) != null) {
				List<QualityTemplateResponse> listChild = map.get(entity.getTemplateName());
				listChild.add(QualityTemplateEntity.valueOf(entity));
				map.put(entity.getTemplateName(), listChild);
			} else {
				List<QualityTemplateResponse> listChild = new ArrayList<>();
				listChild.add(QualityTemplateEntity.valueOf(entity));
				map.put(entity.getTemplateName(), listChild);
			}
		}

		// using for-each loop for iteration over Map.entrySet()
		for (Map.Entry<String, List<QualityTemplateResponse>> entry : map.entrySet()) {
			QualityTemplateMainResponse kk = new QualityTemplateMainResponse();
			kk.setId(entry.getValue().get(0).getTemplateId());
			kk.setTemplateName(entry.getKey() );
			kk.setStageDetails(entry.getValue());
			resp.add(kk );
		}
		return resp;
	}

	@Override
	public ResponseEntity<Object> deleteTemplate(int id) {
		ResponseEntity<Object> response = null;
		try {
			
			List<QualityTemplateEntity> list = qualityTemplateRepository.findByTemplateName(id);

			for (QualityTemplateEntity entity : list) {
				qualityTemplateRepository.deleteById(entity.getTemplateId());
			}
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Quality Template deleted successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public ResponseEntity<Object> templateMapSave(QualityPartyMappingRequest qualityPartyMappingRequest, int userId) {
		
		List<QualityPartyTemplateEntity> list1 = qualityPartyTemplateRepository.findByPartyId(qualityPartyMappingRequest.getPartyId());
		for (QualityPartyTemplateEntity qqualityPartyTemplateEntity : list1) {
			qualityPartyTemplateRepository.deleteById(qqualityPartyTemplateEntity.getId());
		}
		
		ResponseEntity<Object> response = null;
		List<QualityPartyTemplateEntity> list=new ArrayList<QualityPartyTemplateEntity>();
		for (Integer templateId : qualityPartyMappingRequest.getTemplateIdList()) {
			QualityPartyTemplateEntity qualityPartyTemplateEntity = new QualityPartyTemplateEntity();
			qualityPartyTemplateEntity.getTemplateEntity().setTemplateId(templateId);
			qualityPartyTemplateEntity.getParty().setnPartyId( qualityPartyMappingRequest.getPartyId() );
			qualityPartyTemplateEntity.setCreatedBy(userId);
			qualityPartyTemplateEntity.setUpdatedBy(userId);
			qualityPartyTemplateEntity.setCreatedOn(new Date());
			qualityPartyTemplateEntity.setUpdatedOn(new Date());
			list.add(qualityPartyTemplateEntity);
		}
		qualityPartyTemplateRepository.saveAll(list);
		response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Party-Template mapping details saved successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		return response;
	}

	@Override
	public ResponseEntity<Object> deleteTemplateMap(int id) {
		ResponseEntity<Object> response = null;
		try {

			 qualityPartyTemplateRepository.deleteById(id);

			response = new ResponseEntity<>( "{\"status\": \"success\", \"message\": \"Party Template mapping deleted successfully..! \"}",
					new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public List<QualityPartyMappingResponse> getByPartyId(int partyId) {
		List<QualityPartyMappingResponse> instructionList = qualityPartyTemplateRepository.findByPartyId(partyId).stream()
				.map(i -> QualityPartyTemplateEntity.valueOf(i)).collect(Collectors.toList());
		return instructionList;
	}

	@Override
	public List<QualityPartyMappingResponse> getAllMappings() {
		List<QualityPartyMappingResponse> instructionList = qualityPartyTemplateRepository.findAll().stream()
				.map(i -> QualityPartyTemplateEntity.valueOf(i)).collect(Collectors.toList());
		return instructionList;
	}

	@Override
	public QualityCheckResponse qualityCheck(QualityCheckRequest qualityCheckRequest) {
		
		System.out.println("getInwardId == "+qualityCheckRequest.getInwardId());
		System.out.println("getPartyId == "+qualityCheckRequest.getPartyId());
		System.out.println("getInstructionId == "+qualityCheckRequest.getInstructionId());
		System.out.println("getTemplateId == "+qualityCheckRequest.getTemplateId());
		
		return null;
	}

	@Override
	public ResponseEntity<Object> templateMapSaveNew(List<QualityPartyMappingRequestNew> listReq, int partyId, int userId) {
		
		List<QualityPartyTemplateEntity> list1 = qualityPartyTemplateRepository.findByPartyId(partyId);
		for (QualityPartyTemplateEntity qqualityPartyTemplateEntity : list1) {
			qualityPartyTemplateRepository.deleteById(qqualityPartyTemplateEntity.getId());
		}
		
		ResponseEntity<Object> response = null;
		List<QualityPartyTemplateEntity> list=new ArrayList<QualityPartyTemplateEntity>();
		for (QualityPartyMappingRequestNew qualityObjNew : listReq) {
			QualityPartyTemplateEntity qualityPartyTemplateEntity = new QualityPartyTemplateEntity();
			qualityPartyTemplateEntity.getTemplateEntity().setTemplateId(qualityObjNew.getTemplateId());
			qualityPartyTemplateEntity.getParty().setnPartyId(partyId);
			qualityPartyTemplateEntity.setCreatedBy(userId);
			qualityPartyTemplateEntity.setUpdatedBy(userId);
			qualityPartyTemplateEntity.setCreatedOn(new Date());
			qualityPartyTemplateEntity.setUpdatedOn(new Date());
			list.add(qualityPartyTemplateEntity);
		}
		qualityPartyTemplateRepository.saveAll(list);
		return response;
	}

}
