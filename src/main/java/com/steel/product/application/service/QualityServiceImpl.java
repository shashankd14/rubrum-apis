package com.steel.product.application.service;

import com.steel.product.application.dao.KQPPartyTemplateRepository;
import com.steel.product.application.dao.KQPRepository;
import com.steel.product.application.dao.QualityInspectionReportRepository;
import com.steel.product.application.dao.QualityPartyTemplateRepository;
import com.steel.product.application.dao.QualityReportRepository;
import com.steel.product.application.dao.QualityTemplateRepository;
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
import com.steel.product.application.dto.quality.QualityPartyMappingRequestNew;
import com.steel.product.application.dto.quality.QualityPartyMappingResponse;
import com.steel.product.application.dto.quality.QualityReportResponse;
import com.steel.product.application.dto.quality.QualityTemplateResponse;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.KQPEntity;
import com.steel.product.application.entity.KQPPartyTemplateEntity;
import com.steel.product.application.entity.QualityInspectionReportEntity;
import com.steel.product.application.entity.QualityPartyTemplateEntity;
import com.steel.product.application.entity.QualityReportEntity;
import com.steel.product.application.entity.QualityTemplateEntity;

import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
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
	QualityInspectionReportRepository qualityInspectionReportRepository;
	
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
			qualityPartyTemplateEntity.setThicknessList(qualityPartyMappingRequest.getThicknessList().toString() );
			qualityPartyTemplateEntity.setEndUserTagIdList(qualityPartyMappingRequest.getEndUserTagIdList().toString()  );
			qualityPartyTemplateEntity.setMatGradeIdList( qualityPartyMappingRequest.getMatGradeIdList().toString()  );
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
	public ResponseEntity<Object> deleteTemplateMap(Integer templateId) {
		ResponseEntity<Object> response = null;
		try {
			List<QualityPartyTemplateEntity> list1 = qualityPartyTemplateRepository.findByTemplateId(templateId);
			if(list1!=null && list1.size()>0) {
				for (QualityPartyTemplateEntity qqualityPartyTemplateEntity : list1) {
					qualityPartyTemplateRepository.deleteById(qqualityPartyTemplateEntity.getId());
				}
				response = new ResponseEntity<>( "{\"status\": \"success\", \"message\": \"Party Template mapping deleted successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
			} else {
				response = new ResponseEntity<>( "{\"status\": \"failure\", \"message\": \"Please enter valid data..! \"}", new HttpHeaders(), HttpStatus.BAD_REQUEST);
			}
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
		KQPPartyTemplateEntity kqpPartyTemplateEntity = new KQPPartyTemplateEntity();
		kqpPartyTemplateEntity.getKqpEntity().setKqpId(kqpPartyMappingRequest.getKqpId());
		if(kqpPartyMappingRequest.getPartyIdList()!=null && kqpPartyMappingRequest.getPartyIdList().size()>0) {
			kqpPartyTemplateEntity.setPartyIdList(kqpPartyMappingRequest.getPartyIdList().toString());
		}
		if(kqpPartyMappingRequest.getThicknessList()!=null && kqpPartyMappingRequest.getThicknessList().size()>0) {
			kqpPartyTemplateEntity.setThicknessList(kqpPartyMappingRequest.getThicknessList().toString());
		}
		if(kqpPartyMappingRequest.getLengthList()!=null && kqpPartyMappingRequest.getLengthList().size()>0) {
			kqpPartyTemplateEntity.setLengthList(kqpPartyMappingRequest.getLengthList().toString());
		}
		if(kqpPartyMappingRequest.getWidthList()!=null && kqpPartyMappingRequest.getWidthList().size()>0) {
			kqpPartyTemplateEntity.setWidthList(kqpPartyMappingRequest.getWidthList().toString());
		}
		if(kqpPartyMappingRequest.getEndUserTagIdList()!=null && kqpPartyMappingRequest.getEndUserTagIdList().size()>0) {
			kqpPartyTemplateEntity.setEndUserTagIdList(kqpPartyMappingRequest.getEndUserTagIdList().toString());
		}
		if(kqpPartyMappingRequest.getMatGradeIdList()!=null && kqpPartyMappingRequest.getMatGradeIdList().size()>0) {
			kqpPartyTemplateEntity.setMatGradeIdList(kqpPartyMappingRequest.getMatGradeIdList().toString());
		}
		if (kqpPartyMappingRequest.getAnyPartyFlag() != null && "Y".equals(kqpPartyMappingRequest.getAnyPartyFlag())) {
			kqpPartyTemplateEntity.setAnyPartyFlag("Y");
		} else {
			kqpPartyTemplateEntity.setAnyPartyFlag("N");
		}
		if (kqpPartyMappingRequest.getAnyMatgradeFlag() != null && "Y".equals(kqpPartyMappingRequest.getAnyMatgradeFlag())) {
			kqpPartyTemplateEntity.setAnyMatgradeFlag("Y");
		} else {
			kqpPartyTemplateEntity.setAnyMatgradeFlag("N");
		}
		if (kqpPartyMappingRequest.getAnyEndusertagFlag() != null && "Y".equals(kqpPartyMappingRequest.getAnyEndusertagFlag())) {
			kqpPartyTemplateEntity.setAnyEndusertagFlag("Y");
		} else {
			kqpPartyTemplateEntity.setAnyEndusertagFlag("N");
		}
		if (kqpPartyMappingRequest.getAnyWidthFlag() != null && "Y".equals(kqpPartyMappingRequest.getAnyWidthFlag())) {
			kqpPartyTemplateEntity.setAnyWidthFlag("Y");
		} else {
			kqpPartyTemplateEntity.setAnyWidthFlag("N");
		}
		if (kqpPartyMappingRequest.getAnyLengthFlag() != null && "Y".equals(kqpPartyMappingRequest.getAnyLengthFlag())) {
			kqpPartyTemplateEntity.setAnyLengthFlag("Y");
		} else {
			kqpPartyTemplateEntity.setAnyLengthFlag("N");
		}
		if (kqpPartyMappingRequest.getAnyThicknessFlag() != null && "Y".equals(kqpPartyMappingRequest.getAnyThicknessFlag())) {
			kqpPartyTemplateEntity.setAnyThicknessFlag("Y");
		} else {
			kqpPartyTemplateEntity.setAnyThicknessFlag("N");
		}
		kqpPartyTemplateEntity.setCreatedBy(kqpPartyMappingRequest.getUserId());
		kqpPartyTemplateEntity.setUpdatedBy(kqpPartyMappingRequest.getUserId());
		kqpPartyTemplateEntity.setCreatedOn(new Date());
		kqpPartyTemplateEntity.setUpdatedOn(new Date());
		list.add(kqpPartyTemplateEntity);
		kqpPartyTemplateRepository.saveAll(list);
		response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"KQP-Party mapping details saved successfully..! \"}",
				new HttpHeaders(), HttpStatus.OK);
		return response;
	}

	@Override
	public ResponseEntity<Object> deleteKQPPartyMap(Integer kqpId) {
		ResponseEntity<Object> response = null;
		try {
			List<KQPPartyTemplateEntity> list1 = kqpPartyTemplateRepository.findByKqpId(kqpId);
			if (list1 != null && list1.size() > 0) {
				for (KQPPartyTemplateEntity kqpPartyTemplateEntity : list1) {
					kqpPartyTemplateRepository.deleteById(kqpPartyTemplateEntity.getId());
				}
				response = new ResponseEntity<>( "{\"status\": \"success\", \"message\": \"KQP-Party mapping deleted successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
			} else {
				response = new ResponseEntity<>( "{\"status\": \"failure\", \"message\": \"Please enter valid data..! \"}", new HttpHeaders(), HttpStatus.BAD_REQUEST);
			}
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
		List<KQPPartyMappingResponse> instructionList = kqpPartyTemplateRepository.findByAll2().stream()
				.map(i -> KQPPartyTemplateEntity.valueOf(i)).collect(Collectors.toList());
		return instructionList;
	}

	@Override
	public List<QualityInspReportListPageResponse> qirInwardListPage() {
		List<Object[]> packetsList = kqpPartyTemplateRepository.qirInwardListPage();
		List<QualityInspReportListPageResponse> qirList = new ArrayList<>();

		for (Object[] result : packetsList) {
			QualityInspReportListPageResponse resp = new QualityInspReportListPageResponse();
			resp.setInwardEntryId(result[0] != null ? (Integer) result[0] : null);
			resp.setCoilNo(result[1] != null ? (String) result[1] : null);
			resp.setCustomerBatchNo(result[2] != null ? (String) result[2] : null);
			resp.setPlanDate(result[3] != null ? (String) result[3] : null);
			resp.setMaterialGrade(result[4] != null ? (String) result[4] : null);
			resp.setFthickness(result[5] != null ? (Float) result[5] : null);
			resp.setTargetWeight(result[6] != null ? (Float) result[6] : null);
			resp.setNPartyId(result[7] != null ? Integer.parseInt(result[7].toString()) : null);
			resp.setPartyName(result[9] != null ? (String) result[9] : null);
			if(result[8] != null && result[8].toString().length() >0 ) {
				resp.setQirId(Integer.parseInt(result[8].toString()));
			} else {
				resp.setQirId(null);
			}
			qirList.add(resp);
		}
		return qirList;
	}


	@Override
	public List<QualityInspReportListPageResponse> qirListPage() {
		List<Object[]> packetsList = kqpPartyTemplateRepository.qirListPage();
		List<QualityInspReportListPageResponse> qirList = new ArrayList<>();

		for (Object[] result : packetsList) {
			QualityInspReportListPageResponse resp = new QualityInspReportListPageResponse();
			resp.setPlanId(result[0] != null ? (String) result[0] : null);
			resp.setCoilNo(result[1] != null ? (String) result[1] : null);
			resp.setCustomerBatchNo(result[2] != null ? (String) result[2] : null);
			resp.setPlanDate(result[3] != null ? (String) result[3] : null);
			resp.setMaterialGrade(result[4] != null ? (String) result[4] : null);
			resp.setFthickness(result[5] != null ? (Float) result[5] : null);
			resp.setTargetWeight(result[6] != null ? (Float) result[6] : null);
			resp.setNPartyId(result[7] != null ? Integer.parseInt(result[7].toString()) : null);
			if(result[8] != null && result[8].toString().length() >0 ) {
				resp.setQirId(Integer.parseInt(result[8].toString()));
			} else {
				resp.setQirId(null);
			}
			qirList.add(resp);
		}
		return qirList;
	}

	@Override
	public List<InstructionResponseDto> fetchpacketdtls(QIRSaveDataRequest qirSaveDataRequest) {

		List<InstructionResponseDto> instructionList = kqpPartyTemplateRepository
				.fetchpacketdtls(qirSaveDataRequest.getCoilNo(), qirSaveDataRequest.getPartDetailsId()).stream()
				.map(i -> Instruction.valueOf(i)).collect(Collectors.toList());

		return instructionList;
	}

	@Override
	public List<QualityInspDispatchListResponse> qirDispatchList() {
		List<Object[]> packetsList = kqpPartyTemplateRepository.qirDispatchList();
		List<QualityInspDispatchListResponse> qirList = new ArrayList<>();

		for (Object[] result : packetsList) {
			QualityInspDispatchListResponse resp = new QualityInspDispatchListResponse();
			resp.setCoilNo(result[0] != null ? (String) result[0] : null);
			resp.setDeliveryDate(result[1] != null ? (String) result[1] : null);
			resp.setDeliveryChalanNo(result[2] != null ? (Integer) result[2] : null);
			resp.setCustomerBatchNo(result[3] != null ? (String) result[3] : null);
			resp.setQtyDelivered(result[4] != null ? (new BigDecimal(result[4].toString())) : null);
			resp.setVehicleNo(result[5] != null ? (String) result[5] : null);
			resp.setCustomerInvoiceNo(result[6] != null ? (String) result[6] : null);
			resp.setCustomerInvoiceDate(result[7] != null ? (String) result[7] : null);
			resp.setEndUserTags(result[8] != null ? (String) result[8] : null);
			resp.setNPartyId(result[9] != null ? Integer.parseInt(result[9].toString()) : null);
			if(result[10] != null && result[10].toString().length() >0 ) {
				resp.setQirId( Integer.parseInt(result[10].toString()));
			} else {
				resp.setQirId(null);
			}
			resp.setPartyName(result[11] != null ? (String) result[11] : null);
			qirList.add(resp);
		}
		return qirList;
	}

	@Override
	public List<InstructionResponseDto> getDispatchDetails(QIRSaveDataRequest qirSaveDataRequest) {

		List<InstructionResponseDto> instructionList = kqpPartyTemplateRepository
				.getDispatchDetails(qirSaveDataRequest.getCoilNo(), Integer.parseInt(qirSaveDataRequest.getPartDetailsId())).stream()
				.map(i -> Instruction.valueOf(i)).collect(Collectors.toList());

		return instructionList;
	}

	@Override
	public ResponseEntity<Object> qirReportSave(String templateId, String stageName, String templateDetails,
			String userId, String processId, MultipartFile rustObserved, MultipartFile safetyIssues,
			MultipartFile waterExposure, MultipartFile wireRopeDamages, MultipartFile packingIntact,
			MultipartFile improperStorage, MultipartFile strapping, MultipartFile weighmentSlip,
			MultipartFile weighment, MultipartFile ackReceipt, MultipartFile unloadingImproper, String coilNo,
			String customerBatchNo, String planId, String deliveryChalanNo, String qirId) {
		
		ResponseEntity<Object> response = null;
		String message="Quality Inspection Report details saved successfully..! ";
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		try {
			
			log.info(" templateDetails == " + templateDetails);
			QualityInspectionReportEntity qualityTemplateEntity = new QualityInspectionReportEntity();

			if (qirId !=null && Integer.parseInt(qirId) > 0) {
				qualityTemplateEntity = qualityInspectionReportRepository.findByQirId(Integer.parseInt(qirId));
			} else {
				qualityTemplateEntity = new QualityInspectionReportEntity();
				qualityTemplateEntity.setCreatedBy(Integer.parseInt(userId) );
				qualityTemplateEntity.setCreatedOn(new Date());
			}
			log.info(" templateDetails == " + templateDetails);
			
			qualityTemplateEntity.setCreatedBy(Integer.parseInt(userId));
			qualityTemplateEntity.setCreatedOn(new Date());
			qualityTemplateEntity.setStageName(stageName);
			if(processId!=null && processId.length()>0) {
				qualityTemplateEntity.setProcessId( Integer.parseInt(processId) );
			}
			qualityTemplateEntity.setTemplateId(Integer.parseInt(templateId));
			qualityTemplateEntity.setCoilNo(coilNo);
			qualityTemplateEntity.setCustomerBatchNo( customerBatchNo);
			qualityTemplateEntity.setPlanId( planId);
			qualityTemplateEntity.setDeliveryChalanNo( deliveryChalanNo);
			qualityTemplateEntity.setTemplateDetails( templateDetails);
			qualityTemplateEntity.setUpdatedBy(Integer.parseInt(userId) );
			qualityTemplateEntity.setUpdatedOn(new Date());

			if (rustObserved != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo+"_"+stageName, templateId, rustObserved);
				qualityTemplateEntity.setRustObserved( fileUrl);
			}
			if (safetyIssues != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo+"_"+stageName, templateId, safetyIssues);
				qualityTemplateEntity.setSafetyIssues( fileUrl);
			}
			if (waterExposure != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo+"_"+stageName, templateId, waterExposure);
				qualityTemplateEntity.setWaterExposure( fileUrl);
			}
			if (wireRopeDamages != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo+"_"+stageName, templateId, wireRopeDamages);
				qualityTemplateEntity.setWireRopeDamages( fileUrl);
			}
			if (packingIntact != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo+"_"+stageName, templateId, packingIntact);
				qualityTemplateEntity.setPackingIntact(fileUrl);
			}
			if (improperStorage != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo+"_"+stageName, templateId, improperStorage);
				qualityTemplateEntity.setImproperStorage(  fileUrl);
			}
			if (strapping != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo+"_"+stageName, templateId, strapping);
				qualityTemplateEntity.setStrapping(fileUrl);
			}
			if (weighmentSlip != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo+"_"+stageName, templateId, weighmentSlip);
				qualityTemplateEntity.setWeighmentSlip(fileUrl);
			}
			if (weighment != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo+"_"+stageName, templateId, weighment);
				qualityTemplateEntity.setWeighment( fileUrl);
			}
			if (ackReceipt != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo+"_"+stageName, templateId, ackReceipt);
				qualityTemplateEntity.setAckReceipt( fileUrl);
			}
			if (unloadingImproper != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo+"_"+stageName, templateId, unloadingImproper);
				qualityTemplateEntity.setUnloadingImproper( fileUrl);
			}
			qualityInspectionReportRepository.save(qualityTemplateEntity);
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"" + message + "}", header, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("error is ==" + e.getMessage());
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Error Occurred\"}", header, HttpStatus.BAD_REQUEST);
		}
		
		return response;
	}

	@Override
	public QualityInspectionReportResponse getqirById(String coilNo, String planId) {

		QualityInspectionReportEntity entity = qualityInspectionReportRepository.findTop1ByCoilNoOrderByQirIdDesc(coilNo);
		QualityInspectionReportResponse resp = QualityInspectionReportEntity.valueOf(entity);

		if (resp.getRustObserved() != null && resp.getRustObserved().length() > 0) {
			resp.setRustObservedPreSingedURL(awsS3Service.generatePresignedUrl(resp.getRustObserved()));
		}
		if (resp.getSafetyIssues() != null && resp.getSafetyIssues().length() > 0) {
			resp.setSafetyIssuesPreSingedURL(awsS3Service.generatePresignedUrl(resp.getSafetyIssues()));
		}
		if (resp.getWaterExposure() != null && resp.getWaterExposure().length() > 0) {
			resp.setWaterExposurePreSingedURL(awsS3Service.generatePresignedUrl(resp.getWaterExposure()));
		}
		if (resp.getWireRopeDamages() != null && resp.getWireRopeDamages().length() > 0) {
			resp.setWireRopeDamagesPreSingedURL(awsS3Service.generatePresignedUrl(resp.getWireRopeDamages()));
		}
		if (resp.getPackingIntact() != null && resp.getPackingIntact().length() > 0) {
			resp.setPackingIntactPreSingedURL(awsS3Service.generatePresignedUrl(resp.getPackingIntact()));
		}
		if (resp.getImproperStorage() != null && resp.getImproperStorage().length() > 0) {
			resp.setImproperStoragePreSingedURL(awsS3Service.generatePresignedUrl(resp.getImproperStorage()));
		}
		if (resp.getStrapping() != null && resp.getStrapping().length() > 0) {
			resp.setStrappingPreSingedURL(awsS3Service.generatePresignedUrl(resp.getStrapping()));
		}
		if (resp.getWeighmentSlip() != null && resp.getWeighmentSlip().length() > 0) {
			resp.setWeighmentSlipPreSingedURL(awsS3Service.generatePresignedUrl(resp.getWeighmentSlip()));
		}
		if (resp.getWeighment() != null && resp.getWeighment().length() > 0) {
			resp.setWeighmentPreSingedURL(awsS3Service.generatePresignedUrl(resp.getWeighment()));
		}
		if (resp.getAckReceipt() != null && resp.getAckReceipt().length() > 0) {
			resp.setAckReceiptPreSingedURL(awsS3Service.generatePresignedUrl(resp.getAckReceipt()));
		}
		if (resp.getUnloadingImproper() != null && resp.getUnloadingImproper().length() > 0) {
			resp.setUnloadingImproperPreSingedURL(awsS3Service.generatePresignedUrl(resp.getUnloadingImproper()));
		}
		return resp;
	}

	@Override
	public ResponseEntity<Object> deleteQIR(int id) {
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		try {
			qualityInspectionReportRepository.deleteById(id);
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"QIR Report deleted successfully..! \"}", header, HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public QualityInspectionReportResponse getQIRReport(String stageName, String coilNo, String planId) {
		
		QualityInspectionReportResponse resp = new QualityInspectionReportResponse();
		try {
			QualityInspectionReportEntity entity = null;//qualityInspectionReportRepository.findTop1ByCoilNoDesc(coilNo);
			
			if ("inward".equalsIgnoreCase(stageName)) {
				entity = qualityInspectionReportRepository.findTop1ByCoilNoOrderByQirIdDesc(coilNo);
			} else if ("processing".equalsIgnoreCase(stageName) || "postprocessing".equalsIgnoreCase(stageName)) {
				entity = qualityInspectionReportRepository.findTop1ByCoilNoAndPlanId(coilNo, planId);
			} else if ("predispatch".equalsIgnoreCase(stageName) || "postdispatch".equalsIgnoreCase(stageName)) {
				entity = qualityInspectionReportRepository.findTop1ByCoilNoAndDeliveryChalanNo(coilNo, planId);
			}
			
			resp = QualityInspectionReportEntity.valueOf(entity);
			
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	@Override
	public QualityInspectionReportResponse getQIRReportById(int id) {
		
		QualityInspectionReportResponse resp = new QualityInspectionReportResponse();
		try {
			QualityInspectionReportEntity entity = qualityInspectionReportRepository.findByQirId(id);
			if (entity != null) {

				resp = QualityInspectionReportEntity.valueOf(entity);

				if (resp.getRustObserved() != null && resp.getRustObserved().length() > 0) {
					resp.setRustObservedPreSingedURL(awsS3Service.generatePresignedUrl(resp.getRustObserved()));
				}
				if (resp.getSafetyIssues() != null && resp.getSafetyIssues().length() > 0) {
					resp.setSafetyIssuesPreSingedURL(awsS3Service.generatePresignedUrl(resp.getSafetyIssues()));
				}
				if (resp.getWaterExposure() != null && resp.getWaterExposure().length() > 0) {
					resp.setWaterExposurePreSingedURL(awsS3Service.generatePresignedUrl(resp.getWaterExposure()));
				}
				if (resp.getWireRopeDamages() != null && resp.getWireRopeDamages().length() > 0) {
					resp.setWireRopeDamagesPreSingedURL(awsS3Service.generatePresignedUrl(resp.getWireRopeDamages()));
				}
				if (resp.getPackingIntact() != null && resp.getPackingIntact().length() > 0) {
					resp.setPackingIntactPreSingedURL(awsS3Service.generatePresignedUrl(resp.getPackingIntact()));
				}
				if (resp.getImproperStorage() != null && resp.getImproperStorage().length() > 0) {
					resp.setImproperStoragePreSingedURL(awsS3Service.generatePresignedUrl(resp.getImproperStorage()));
				}
				if (resp.getStrapping() != null && resp.getStrapping().length() > 0) {
					resp.setStrappingPreSingedURL(awsS3Service.generatePresignedUrl(resp.getStrapping()));
				}
				if (resp.getWeighmentSlip() != null && resp.getWeighmentSlip().length() > 0) {
					resp.setWeighmentSlipPreSingedURL(awsS3Service.generatePresignedUrl(resp.getWeighmentSlip()));
				}
				if (resp.getWeighment() != null && resp.getWeighment().length() > 0) {
					resp.setWeighmentPreSingedURL(awsS3Service.generatePresignedUrl(resp.getWeighment()));
				}
				if (resp.getAckReceipt() != null && resp.getAckReceipt().length() > 0) {
					resp.setAckReceiptPreSingedURL(awsS3Service.generatePresignedUrl(resp.getAckReceipt()));
				}
				if (resp.getUnloadingImproper() != null && resp.getUnloadingImproper().length() > 0) {
					resp.setUnloadingImproperPreSingedURL(awsS3Service.generatePresignedUrl(resp.getUnloadingImproper()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

}
