package com.steel.product.application.service;

import com.steel.product.application.dao.KQPPartyTemplateRepository;
import com.steel.product.application.dao.KQPRepository;
import com.steel.product.application.dao.QualityPartyTemplateRepository;
import com.steel.product.application.dao.QualityReportRepository;
import com.steel.product.application.dao.QualityTemplateRepository;
import com.steel.product.application.dto.quality.KQPPartyMappingRequest;
import com.steel.product.application.dto.quality.KQPPartyMappingResponse;
import com.steel.product.application.dto.quality.KQPRequest;
import com.steel.product.application.dto.quality.KQPResponse;
import com.steel.product.application.dto.quality.QualityCheckRequest;
import com.steel.product.application.dto.quality.QualityCheckResponse;
import com.steel.product.application.dto.quality.QualityPartyMappingRequest;
import com.steel.product.application.dto.quality.QualityPartyMappingRequestNew;
import com.steel.product.application.dto.quality.QualityPartyMappingResponse;
import com.steel.product.application.dto.quality.QualityReportResponse;
import com.steel.product.application.dto.quality.QualityTemplateResponse;
import com.steel.product.application.entity.KQPEntity;
import com.steel.product.application.entity.KQPPartyTemplateEntity;
import com.steel.product.application.entity.QualityPartyTemplateEntity;
import com.steel.product.application.entity.QualityReportEntity;
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
	KQPRepository kqpRepository;

	@Autowired
	QualityReportRepository qualityReportRepository;

	@Autowired
	KQPPartyTemplateRepository kqpPartyTemplateRepository;
	
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
	public List<QualityPartyMappingResponse> getByPartyIdAndStageName(int partyId, String stageName) {
		List<QualityPartyMappingResponse> instructionList = qualityPartyTemplateRepository.getByPartyIdAndStageName(partyId, stageName).stream()
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

	@Override
	public List<Float> getAllThickness() {

		List<Object[]> ll = qualityTemplateRepository.getAllThickness();
		List<Float> thicknessList = new ArrayList<>();

		for (Object[] obj2 : ll) {
			thicknessList.add((Float) obj2[0]);
		}
		return thicknessList;
	}

	@Override
	public List<Float> getAllWidth() {

		List<Object[]> ll = qualityTemplateRepository.getAllWidth();
		List<Float> widthList = new ArrayList<>();

		for (Object[] obj2 : ll) {
			widthList.add((Float) obj2[0]);
		}
		return widthList;
	}

	@Override
	public List<Float> getAllLength() {

		List<Object[]> ll = qualityTemplateRepository.getAllLength();
		List<Float> lengthList = new ArrayList<>();

		for (Object[] obj2 : ll) {
			lengthList.add((Float) obj2[0]);
		}
		return lengthList;
	}
	

	@Override
	public ResponseEntity<Object> reportsSave(String inspectionId, String coilNumber, String inwardId, String templateId, String stageName,
			String templateDetails, String userId, MultipartFile rustObserved, MultipartFile safetyIssues,
			MultipartFile waterExposure, MultipartFile wireRopeDamages, MultipartFile packingIntact,
			MultipartFile improperStorage, MultipartFile strapping, MultipartFile weighmentSlip,
			MultipartFile weighment, MultipartFile acknowledgementReceipt, MultipartFile unloadingImproper) {
		
		ResponseEntity<Object> response = null;
		String message="Quality Inspection Report details updated successfully..! ";
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		try {
			QualityReportEntity qualityReportEntity = new QualityReportEntity();
			
			QualityReportEntity checkDuplicate = qualityReportRepository.findFirstByCoilNumber( coilNumber);
			if (checkDuplicate != null && checkDuplicate.getCoilNumber().length() > 0) {
				return new ResponseEntity<>("{\"status\": \"failure\", \"message\": \"This coil already entered..!  }", header, HttpStatus.BAD_REQUEST);
			}			
			
			if (inspectionId != null && Integer.parseInt(inspectionId) > 0) {
				qualityReportEntity = qualityReportRepository.findByInspectionId(Integer.parseInt(inspectionId));
			}
			
			if (inspectionId != null && Integer.parseInt(inspectionId) > 0) {
				qualityReportEntity = qualityReportRepository.findByInspectionId(Integer.parseInt(inspectionId));
			}
			qualityReportEntity.setUpdatedBy(Integer.parseInt(userId) );
			qualityReportEntity.setUpdatedOn(new Date());
			qualityReportEntity.setTemplateDetails( templateDetails);
			qualityReportEntity.setInwardId(Integer.parseInt(inwardId)  );
			qualityReportEntity.setCoilNumber(coilNumber); 
			qualityReportEntity.setTemplateId(Integer.parseInt(templateId)); 
			qualityReportEntity.setStageName( stageName ); 

			if (rustObserved != null) {
				String fileUrl = awsS3Service.persistQualityReportFiles(templateFilesPath, stageName, coilNumber, rustObserved);
				qualityReportEntity.setRustObserved( fileUrl);
			}
			if (safetyIssues != null) {
				String fileUrl = awsS3Service.persistQualityReportFiles(templateFilesPath, stageName, coilNumber, safetyIssues);
				qualityReportEntity.setSafetyIssues( fileUrl);
			}
			if (waterExposure != null) {
				String fileUrl = awsS3Service.persistQualityReportFiles(templateFilesPath, stageName, coilNumber, waterExposure);
				qualityReportEntity.setWaterExposure( fileUrl);
			}
			if (wireRopeDamages != null) {
				String fileUrl = awsS3Service.persistQualityReportFiles(templateFilesPath, stageName, coilNumber, wireRopeDamages);
				qualityReportEntity.setWireRopeDamages( fileUrl);
			}
			if (packingIntact != null) {
				String fileUrl = awsS3Service.persistQualityReportFiles(templateFilesPath, stageName, coilNumber, packingIntact);
				qualityReportEntity.setPackingIntact(fileUrl);
			}
			if (improperStorage != null) {
				String fileUrl = awsS3Service.persistQualityReportFiles(templateFilesPath, stageName, coilNumber, improperStorage);
				qualityReportEntity.setImproperStorage(  fileUrl);
			}
			if (strapping != null) {
				String fileUrl = awsS3Service.persistQualityReportFiles(templateFilesPath, stageName, coilNumber, strapping);
				qualityReportEntity.setStrapping(fileUrl);
			}
			if (weighmentSlip != null) {
				String fileUrl = awsS3Service.persistQualityReportFiles(templateFilesPath, stageName, coilNumber, weighmentSlip);
				qualityReportEntity.setWeighmentSlip(fileUrl);
			}
			if (weighment != null) {
				String fileUrl = awsS3Service.persistQualityReportFiles(templateFilesPath, stageName, coilNumber, weighment);
				qualityReportEntity.setWeighment( fileUrl);
			}
			if (acknowledgementReceipt != null) {
				String fileUrl = awsS3Service.persistQualityReportFiles(templateFilesPath, stageName, coilNumber, acknowledgementReceipt);
				qualityReportEntity.setAckReceipt( fileUrl);
			}
			if (unloadingImproper != null) {
				String fileUrl = awsS3Service.persistQualityReportFiles(templateFilesPath, stageName, coilNumber, unloadingImproper);
				qualityReportEntity.setUnloadingImproper( fileUrl);
			}
			
			qualityReportRepository.save(qualityReportEntity);
			
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"" + message + "}", header, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("error is ==" + e.getMessage());
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Error Occurred\"}", header, HttpStatus.BAD_REQUEST);
		}
		
		return response;
	}

	@Override
	public QualityReportResponse inspectionreportGetById(int id) {

		QualityReportResponse resp = QualityReportEntity.valueOf(qualityReportRepository.findByInspectionId(id));;
		
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
	public List<QualityReportResponse> inspectionreportGetAll() {
		List<QualityReportResponse> instructionList = qualityReportRepository.findAll().stream()
				.map(i -> QualityReportEntity.valueOf(i)).collect(Collectors.toList());
		return instructionList;
	}
	 
	@Override
	public ResponseEntity<Object> deleteInspectionReport(int id) {

		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		try {

			qualityReportRepository.deleteById(id);
			response = new ResponseEntity<>( "{\"status\": \"success\", \"message\": \"Quality Inspection Report deleted successfully..! \"}", header, HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public ResponseEntity<Object> save(KQPRequest kcpRequest, int userId) {

		ResponseEntity<Object> response = null;

		KQPEntity checkpackingItemEntity = kqpRepository.findFirstByKqpName( kcpRequest.getKqpName());
		
		KQPEntity kqpEntity = new KQPEntity();
		if (checkpackingItemEntity != null && checkpackingItemEntity.getKqpId() > 0) {
			kqpEntity.setKqpId(kcpRequest.getKqpId());
			
			if (checkpackingItemEntity != null && kcpRequest.getKqpId() != checkpackingItemEntity.getKqpId()) {
				return new ResponseEntity<>( 	"{\"status\": \"fail\", \"message\": \"KQP Name already exists..! \"}",
						new HttpHeaders(), HttpStatus.BAD_REQUEST);

			}
		}  
		kqpEntity.setKqpName( kcpRequest.getKqpName() );
		kqpEntity.setKqpDesc( kcpRequest.getKqpDesc()  );
		kqpEntity.setKqpSummary( kcpRequest.getKqpSummary() );
		kqpEntity.setStageName( kcpRequest.getStageName());
		kqpEntity.setCreatedBy(userId);
		kqpEntity.setUpdatedBy(userId);
		kqpEntity.setCreatedOn(new Date());
		kqpEntity.setUpdatedOn(new Date());
		kqpRepository.save (kqpEntity);
		if (kcpRequest.getKqpId() != null && kcpRequest.getKqpId() > 0) {
			log.info("KQP details updated successfully");
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"KQP details updated successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} else {
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"KQP details saved successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		}
		return response;
	}
	
	@Override
	public KQPResponse kqpGetById(int kqpId) {

		KQPResponse resp = KQPEntity.valueOf(kqpRepository.findByKqpId(kqpId));
		return resp;
	}

	@Override
	public List<KQPResponse> kqpGetByAll() {

		List<KQPResponse> instructionList = kqpRepository.findAll().stream().map(i -> KQPEntity.valueOf(i))
				.collect(Collectors.toList());

		return instructionList;
	}

	@Override
	public ResponseEntity<Object> deleteKQP(int id) {
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		try {

			kqpRepository.deleteById(id);
			response = new ResponseEntity<>( "{\"status\": \"success\", \"message\": \"KQP details deleted successfully..! \"}", header, HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public ResponseEntity<Object> kqpPartyMapSave(KQPPartyMappingRequest kqpPartyMappingRequest) {

		List<KQPPartyTemplateEntity> list1 = kqpPartyTemplateRepository.findByKqpId(kqpPartyMappingRequest.getKqpId());
		for (KQPPartyTemplateEntity kqpPartyTemplateEntity : list1) {
			kqpPartyTemplateRepository.deleteById(kqpPartyTemplateEntity.getId());
		}

		ResponseEntity<Object> response = null;
		List<KQPPartyTemplateEntity> list = new ArrayList<>();
		for (Integer partyId : kqpPartyMappingRequest.getPartyIdList()) {
			KQPPartyTemplateEntity kqpPartyTemplateEntity = new KQPPartyTemplateEntity();
			kqpPartyTemplateEntity.getKqpEntity().setKqpId(kqpPartyMappingRequest.getKqpId());
			kqpPartyTemplateEntity.getParty().setnPartyId(partyId);
			kqpPartyTemplateEntity.setThickness(kqpPartyMappingRequest.getThickness());
			kqpPartyTemplateEntity.setLength(kqpPartyMappingRequest.getLength());
			kqpPartyTemplateEntity.setWidth(kqpPartyMappingRequest.getWidth());
			kqpPartyTemplateEntity.setEndUserTagId(kqpPartyMappingRequest.getEndUserTagId());
			kqpPartyTemplateEntity.setMatGradeId(kqpPartyMappingRequest.getMatGradeId());
			kqpPartyTemplateEntity.setCreatedBy(kqpPartyMappingRequest.getUserId());
			kqpPartyTemplateEntity.setUpdatedBy(kqpPartyMappingRequest.getUserId());
			kqpPartyTemplateEntity.setCreatedOn(new Date());
			kqpPartyTemplateEntity.setUpdatedOn(new Date());
			list.add(kqpPartyTemplateEntity);
		}
		kqpPartyTemplateRepository.saveAll(list);
		response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"KQP-Party mapping details saved successfully..! \"}",
				new HttpHeaders(), HttpStatus.OK);
		return response;
	}

	@Override
	public ResponseEntity<Object> deleteKQPPartyMap(int kqpId) {
		ResponseEntity<Object> response = null;
		try {

			List<KQPPartyTemplateEntity> list1 = kqpPartyTemplateRepository.findByKqpId(kqpId);
			for (KQPPartyTemplateEntity kqpPartyTemplateEntity : list1) {
				kqpPartyTemplateRepository.deleteById(kqpPartyTemplateEntity.getId());
			}
			response = new ResponseEntity<>( "{\"status\": \"success\", \"message\": \"KQP-Party mapping deleted successfully..! \"}",
					new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	@Override
	public List<KQPPartyMappingResponse> getByKQPId(int kqpId) {
		List<KQPPartyMappingResponse> instructionList = kqpPartyTemplateRepository.findByKqpId( kqpId).stream()
				.map(i -> KQPPartyTemplateEntity.valueOf(i)).collect(Collectors.toList());
		return instructionList;
	}

	@Override
	public List<KQPPartyMappingResponse> getAllKQPMappings() {
		List<KQPPartyMappingResponse> instructionList = kqpPartyTemplateRepository.findAll().stream()
				.map(i -> KQPPartyTemplateEntity.valueOf(i)).collect(Collectors.toList());
		return instructionList;
	}
}
