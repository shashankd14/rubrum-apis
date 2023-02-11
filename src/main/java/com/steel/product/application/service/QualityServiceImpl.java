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
	
	@Autowired
	AWSS3Service awsS3Service;

	@Value("${templateFilesPath}")
	private String templateFilesPath;
	
	@Override
	public ResponseEntity<Object> save(String templateId, String templateName, String stageName, String templateDetails, 
			String userId, String processId,
			MultipartFile rustObserved, MultipartFile safetyIssues, MultipartFile waterExposure, MultipartFile wireRopeDamages, 
			MultipartFile packingIntact, MultipartFile improperStorage, MultipartFile strapping, MultipartFile weighmentSlip, 
			MultipartFile weighment, MultipartFile ackReceipt, MultipartFile unloadingImproper) {
		
		ResponseEntity<Object> response = null;
		String message="Quality Template details saved successfully..! ";
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		try {
			QualityTemplateEntity oldTemplateObj  = qualityTemplateRepository.findByTemplateNameAndStageName(templateName, stageName);
			
			log.info(" templateDetails == " + templateDetails);

			QualityTemplateEntity qualityTemplateEntity = null;
			if (templateId !=null && Integer.parseInt(templateId) > 0) {
				
				if (oldTemplateObj != null && oldTemplateObj.getTemplateId() > 0 && Integer.parseInt(templateId) != oldTemplateObj.getTemplateId()) {
					return new ResponseEntity<>( "{\"status\": \"fail\", \"message\": \"Template - Stage Name already exists\"}", header, HttpStatus.BAD_REQUEST);
				}
				
				message="Quality Template details updated successfully..! ";
				qualityTemplateEntity = qualityTemplateRepository.findByTemplateId(Integer.parseInt(templateId));
				qualityTemplateEntity.setTemplateId(Integer.parseInt(templateId));
			} else {
				
				if (oldTemplateObj != null && oldTemplateObj.getTemplateId() > 0) {
					return new ResponseEntity<>( "{\"status\": \"fail\", \"message\": \"Template - Stage Name already exists\"}", header, HttpStatus.BAD_REQUEST);
				}
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

			if (rustObserved != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, stageName, templateName, rustObserved);
				qualityTemplateEntity.setRustObserved( fileUrl);
				/*
				JSONParser parser = new JSONParser();
				JSONArray array = (JSONArray)parser.parse(templateDetails);
				JSONObject jsonObject = new JSONObject();
				for (Object dbRespObj : array) {
					jsonObject = (JSONObject) dbRespObj;
					if("PackingIntact".equals(jsonObject.get("type"))) {
						
					}
				}*/
			}
			if (safetyIssues != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, stageName, templateName, safetyIssues);
				qualityTemplateEntity.setSafetyIssues( fileUrl);
			}
			if (waterExposure != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, stageName, templateName, waterExposure);
				qualityTemplateEntity.setWaterExposure( fileUrl);
			}
			if (wireRopeDamages != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, stageName, templateName, wireRopeDamages);
				qualityTemplateEntity.setWireRopeDamages( fileUrl);
			}
			if (packingIntact != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, stageName, templateName, packingIntact);
				qualityTemplateEntity.setPackingIntact(fileUrl);
			}
			if (improperStorage != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, stageName, templateName, improperStorage);
				qualityTemplateEntity.setImproperStorage(  fileUrl);
			}
			if (strapping != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, stageName, templateName, strapping);
				qualityTemplateEntity.setStrapping(fileUrl);
			}
			if (weighmentSlip != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, stageName, templateName, weighmentSlip);
				qualityTemplateEntity.setWeighmentSlip(fileUrl);
			}
			if (weighment != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, stageName, templateName, weighment);
				qualityTemplateEntity.setWeighment( fileUrl);
			}
			if (ackReceipt != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, stageName, templateName, ackReceipt);
				qualityTemplateEntity.setAckReceipt( fileUrl);
			}
			if (unloadingImproper != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, stageName, templateName, unloadingImproper);
				qualityTemplateEntity.setUnloadingImproper( fileUrl);
			}
			
			qualityTemplateRepository.save(qualityTemplateEntity);
			
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"" + message + "}", header, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
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

		
		QualityTemplateResponse resp = QualityTemplateEntity.valueOf(qualityTemplateRepository.findByTemplateId(id));;
		
		if(resp.getRustObserved()!=null && resp.getRustObserved().length() >0 ) {
			resp.setRustObservedPreSingedURL(awsS3Service.generatePresignedUrl(resp.getRustObserved()) );
		}
		if(resp.getSafetyIssues()!=null && resp.getSafetyIssues().length() >0 ) {
			resp.setSafetyIssuesPreSingedURL(awsS3Service.generatePresignedUrl(resp.getSafetyIssues()) );		
		}
		if(resp.getWaterExposure()!=null && resp.getWaterExposure().length() >0 ) {
			resp.setWaterExposurePreSingedURL(awsS3Service.generatePresignedUrl(resp.getWaterExposure()) );		
		}
		if(resp.getWireRopeDamages()!=null && resp.getWireRopeDamages().length() >0 ) {
			resp.setWireRopeDamagesPreSingedURL(awsS3Service.generatePresignedUrl(resp.getWireRopeDamages()) );		
		}
		if(resp.getPackingIntact()!=null && resp.getPackingIntact().length() >0 ) {
			resp.setPackingIntactPreSingedURL(awsS3Service.generatePresignedUrl(resp.getPackingIntact()) );		
		}
		if(resp.getImproperStorage()!=null && resp.getImproperStorage().length() >0 ) {
			resp.setImproperStoragePreSingedURL(awsS3Service.generatePresignedUrl(resp.getImproperStorage()) );		
		}
		if(resp.getStrapping()!=null && resp.getStrapping().length() >0 ) {
			resp.setStrappingPreSingedURL(awsS3Service.generatePresignedUrl(resp.getStrapping()) );		
		}
		if(resp.getWeighmentSlip()!=null && resp.getWeighmentSlip().length() >0 ) {
			resp.setWeighmentSlipPreSingedURL(awsS3Service.generatePresignedUrl(resp.getWeighmentSlip()) );		
		}
		if(resp.getWeighment()!=null && resp.getWeighment().length() >0 ) {
			resp.setWeighmentPreSingedURL(awsS3Service.generatePresignedUrl(resp.getWeighment()) );		
		}
		if(resp.getAckReceipt()!=null && resp.getAckReceipt().length() >0 ) {
			resp.setAckReceiptPreSingedURL(awsS3Service.generatePresignedUrl(resp.getAckReceipt()) );
		}
		if(resp.getUnloadingImproper()!=null && resp.getUnloadingImproper().length() >0 ) {
			resp.setUnloadingImproperPreSingedURL(awsS3Service.generatePresignedUrl(resp.getUnloadingImproper()) );
		}
		return resp;
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
