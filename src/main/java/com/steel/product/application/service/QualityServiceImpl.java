package com.steel.product.application.service;

import com.steel.product.application.dao.QualityPartyTemplateRepository;
import com.steel.product.application.dao.QualityTemplateRepository;
import com.steel.product.application.dto.quality.QualityCheckRequest;
import com.steel.product.application.dto.quality.QualityCheckResponse;
import com.steel.product.application.dto.quality.QualityPartyMappingRequest;
import com.steel.product.application.dto.quality.QualityPartyMappingRequestNew;
import com.steel.product.application.dto.quality.QualityPartyMappingResponse;
import com.steel.product.application.dto.quality.QualityTemplateResponse;
import com.steel.product.application.entity.QualityPartyTemplateEntity;
import com.steel.product.application.entity.QualityTemplateEntity;
import com.steel.product.application.util.CommonUtil;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Log4j2
public class QualityServiceImpl implements QualityService {

	@Autowired
	QualityTemplateRepository qualityTemplateRepository;
	
	@Autowired
	QualityPartyTemplateRepository qualityPartyTemplateRepository;

	@Value("${templateFilesPath}")
	private String templateFilesPath;
	
	@Override
	public ResponseEntity<Object> save(String templateId, String templateName, String stageName, String templateDetails,
			String userId, MultipartFile file1, MultipartFile file2,
			MultipartFile file3, MultipartFile file4, MultipartFile file5, String processId) {
		
		ResponseEntity<Object> response = null;
		String message="Quality Template details saved successfully..! ";
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		try {

			log.info("  templateDetails == " + templateDetails);

			QualityTemplateEntity qualityTemplateEntity = null;
			if (templateId !=null && Integer.parseInt(templateId) > 0) {
				message="Quality Template details updated successfully..! ";
				qualityTemplateEntity = qualityTemplateRepository.findByTemplateId(Integer.parseInt(templateId));
				qualityTemplateEntity.setTemplateId(Integer.parseInt(templateId));
			} else {
				qualityTemplateEntity = new QualityTemplateEntity();
				qualityTemplateEntity.setCreatedBy(Integer.parseInt(userId) );
				qualityTemplateEntity.setCreatedOn(new Date());
			}
			qualityTemplateEntity.setStageName(stageName);
			if(processId!=null && processId.length()>0) {
				qualityTemplateEntity.setProcessId( Integer.parseInt(processId) );
			}
			
			qualityTemplateEntity.setTemplateName(templateName);
			qualityTemplateEntity.setTemplateDetails( templateDetails);
			qualityTemplateEntity.setUpdatedBy(Integer.parseInt(userId) );
			qualityTemplateEntity.setUpdatedOn(new Date());

			if (file1 != null) {
				CommonUtil.persistFiles(templateFilesPath, stageName, templateName, file1);
			}
			if (file2 != null) {
				CommonUtil.persistFiles(templateFilesPath, stageName, templateName, file2);
			}
			if (file3 != null) {
				CommonUtil.persistFiles(templateFilesPath, stageName, templateName, file3);
			}
			if (file4 != null) {
				CommonUtil.persistFiles(templateFilesPath, stageName, templateName, file4);
			}
			if (file5 != null) {
				CommonUtil.persistFiles(templateFilesPath, stageName, templateName, file5);
			}
			
			qualityTemplateRepository.save(qualityTemplateEntity);
			
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"" + message + "}", header, HttpStatus.OK);
		} catch (Exception e) {
			log.info("error is ==" + e.getMessage());
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Error Occurred\"}", header, HttpStatus.BAD_REQUEST);
		}
		
		return response;
	}

	@Override
	public ResponseEntity<Object> delete(int id) {
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		try {
			qualityTemplateRepository.deleteById(id);
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Template stage details deleted successfully..! \"}", header, HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public QualityTemplateResponse getById(int id) {

		return QualityTemplateEntity.valueOf(qualityTemplateRepository.findByTemplateId(id));
	}

	@Override
	public List<QualityTemplateResponse> getAllTemplateDetails() {

		List<QualityTemplateResponse> instructionList = qualityTemplateRepository.findAllTemplates().stream()
				.map(i -> QualityTemplateEntity.valueOf(i)).collect(Collectors.toList());
		return instructionList ;
	}

	@Override
	public ResponseEntity<Object> deleteTemplate(int id) {
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		try {
			
			qualityTemplateRepository.deleteById(id);
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Quality Template deleted successfully..! \"}", header, HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public ResponseEntity<Object> templateMapSave(QualityPartyMappingRequest qualityPartyMappingRequest) {
		
		List<QualityPartyTemplateEntity> list1 = qualityPartyTemplateRepository.findByTemplateId(qualityPartyMappingRequest.getTemplateId());
		for (QualityPartyTemplateEntity qqualityPartyTemplateEntity : list1) {
			qualityPartyTemplateRepository.deleteById(qqualityPartyTemplateEntity.getId());
		}
		
		ResponseEntity<Object> response = null;
		List<QualityPartyTemplateEntity> list=new ArrayList<QualityPartyTemplateEntity>();
		for (Integer partyId : qualityPartyMappingRequest.getPartyIdList()) {
			QualityPartyTemplateEntity qualityPartyTemplateEntity = new QualityPartyTemplateEntity();
			qualityPartyTemplateEntity.getTemplateEntity().setTemplateId(qualityPartyMappingRequest.getTemplateId());
			qualityPartyTemplateEntity.getParty().setnPartyId(partyId);
			qualityPartyTemplateEntity.setThickness(qualityPartyMappingRequest.getThickness());
			qualityPartyTemplateEntity.setEndUserTagId(qualityPartyMappingRequest.getEndUserTagId());
			qualityPartyTemplateEntity.setMatGradeId( qualityPartyMappingRequest.getMatGradeId());
			qualityPartyTemplateEntity.setCreatedBy(qualityPartyMappingRequest.getUserId());
			qualityPartyTemplateEntity.setUpdatedBy(qualityPartyMappingRequest.getUserId());
			qualityPartyTemplateEntity.setCreatedOn(new Date());
			qualityPartyTemplateEntity.setUpdatedOn(new Date());
			list.add(qualityPartyTemplateEntity);
		}
		qualityPartyTemplateRepository.saveAll(list);
		response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Party-Template mapping details saved successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		return response;
	}

	@Override
	public ResponseEntity<Object> deleteTemplateMap(int templateId) {
		ResponseEntity<Object> response = null;
		try {

			List<QualityPartyTemplateEntity> list1 = qualityPartyTemplateRepository.findByTemplateId(templateId);
			for (QualityPartyTemplateEntity qqualityPartyTemplateEntity : list1) {
				qualityPartyTemplateRepository.deleteById(qqualityPartyTemplateEntity.getId());
			}
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
	public List<QualityPartyMappingResponse> getByTemplateId(int templateId) {
		List<QualityPartyMappingResponse> instructionList = qualityPartyTemplateRepository.findByTemplateId(templateId).stream()
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
