package com.steel.product.application.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lowagie.text.DocumentException;
import com.steel.product.application.dao.KQPPartyTemplateRepository;
import com.steel.product.application.dao.KQPRepository;
import com.steel.product.application.dao.QualityInspectionReportRepository;
import com.steel.product.application.dao.QualityPartyTemplateRepository;
import com.steel.product.application.dao.QualityReportRepository;
import com.steel.product.application.dao.QualityTemplateRepository;
import com.steel.product.application.dto.instruction.InstructionResponseDto;
import com.steel.product.application.dto.pdf.LabelPrintDTO;
import com.steel.product.application.dto.qrcode.QRCodeResponse;
import com.steel.product.application.dto.quality.KQPPartyMappingRequest;
import com.steel.product.application.dto.quality.KQPPartyMappingResponse;
import com.steel.product.application.dto.quality.KQPRequest;
import com.steel.product.application.dto.quality.KQPResponse;
import com.steel.product.application.dto.quality.QIRPanDetailsJsonArrayChildDTO;
import com.steel.product.application.dto.quality.QIRPanDetailsJsonArrayDTO;
import com.steel.product.application.dto.quality.QIRPanToleranceChildDTO;
import com.steel.product.application.dto.quality.QIRSaveDataRequest;
import com.steel.product.application.dto.quality.QIRTemplateDtlsJsonArrayDTO;
import com.steel.product.application.dto.quality.QualityCheckRequest;
import com.steel.product.application.dto.quality.QualityCheckResponse;
import com.steel.product.application.dto.quality.QualityInspDispatchListResponse;
import com.steel.product.application.dto.quality.QualityInspReportListPageResponse;
import com.steel.product.application.dto.quality.QualityInspectionReportResponse;
import com.steel.product.application.dto.quality.QualityPartyMappingRequest;
import com.steel.product.application.dto.quality.QualityPartyMappingRequestNew;
import com.steel.product.application.dto.quality.QualityPartyMappingResponse;
import com.steel.product.application.dto.quality.QualityTemplateResponse;
import com.steel.product.application.entity.CompanyDetails;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.entity.KQPEntity;
import com.steel.product.application.entity.KQPPartyTemplateEntity;
import com.steel.product.application.entity.QualityInspectionReportEntity;
import com.steel.product.application.entity.QualityPartyTemplateEntity;
import com.steel.product.application.entity.QualityTemplateEntity;

import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
	
	@Autowired
	InwardEntryService inwdEntrySvc;
	
	@Autowired
    CompanyDetailsService companyDetailsService;

	@Value("${templateFilesPath}")
	private String templateFilesPath;

	@Override
	public ResponseEntity<Object> save(String templateId, String templateName, String stageName, String templateDetails,
			String userId, String processId, MultipartFile rustObserved, MultipartFile safetyIssues,
			MultipartFile waterExposure, MultipartFile wireRopeDamages, MultipartFile packingIntact,
			MultipartFile improperStorage, MultipartFile strapping, MultipartFile weighmentSlip,
			MultipartFile weighment, MultipartFile ackReceipt, MultipartFile unloadingImproper) {

		ResponseEntity<Object> response = null;
		String message = "Quality Template details saved successfully..! ";
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		try {
			QualityTemplateEntity oldTemplateObj = qualityTemplateRepository
					.findByTemplateNameAndStageName(templateName, stageName);

			log.info(" templateDetails == " + templateDetails);

			QualityTemplateEntity qualityTemplateEntity = null;
			if (templateId != null && Integer.parseInt(templateId) > 0) {

				if (oldTemplateObj != null && oldTemplateObj.getTemplateId() > 0
						&& Integer.parseInt(templateId) != oldTemplateObj.getTemplateId()) {
					return new ResponseEntity<>(
							"{\"status\": \"fail\", \"message\": \"Template - Stage Name already exists\"}", header,
							HttpStatus.BAD_REQUEST);
				}

				message = "Quality Template details updated successfully..! ";
				qualityTemplateEntity = qualityTemplateRepository.findByTemplateId(Integer.parseInt(templateId));
				qualityTemplateEntity.setTemplateId(Integer.parseInt(templateId));
			} else {

				if (oldTemplateObj != null && oldTemplateObj.getTemplateId() > 0) {
					return new ResponseEntity<>(
							"{\"status\": \"fail\", \"message\": \"Template - Stage Name already exists\"}", header,
							HttpStatus.BAD_REQUEST);
				}
				qualityTemplateEntity = new QualityTemplateEntity();
				qualityTemplateEntity.setCreatedBy(Integer.parseInt(userId));
				qualityTemplateEntity.setCreatedOn(new Date());
			}
			qualityTemplateEntity.setStageName(stageName);
			if (processId != null && processId.length() > 0) {
				qualityTemplateEntity.setProcessId(Integer.parseInt(processId));
			}

			qualityTemplateEntity.setTemplateName(templateName);
			qualityTemplateEntity.setTemplateDetails(templateDetails);
			qualityTemplateEntity.setUpdatedBy(Integer.parseInt(userId));
			qualityTemplateEntity.setUpdatedOn(new Date());

			if (rustObserved != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, stageName, templateName, rustObserved);
				qualityTemplateEntity.setRustObserved(fileUrl);
				/*
				 * JSONParser parser = new JSONParser(); JSONArray array =
				 * (JSONArray)parser.parse(templateDetails); JSONObject jsonObject = new
				 * JSONObject(); for (Object dbRespObj : array) { jsonObject = (JSONObject)
				 * dbRespObj; if("PackingIntact".equals(jsonObject.get("type"))) {
				 * 
				 * } }
				 */
			}
			if (safetyIssues != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, stageName, templateName, safetyIssues);
				qualityTemplateEntity.setSafetyIssues(fileUrl);
			}
			if (waterExposure != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, stageName, templateName, waterExposure);
				qualityTemplateEntity.setWaterExposure(fileUrl);
			}
			if (wireRopeDamages != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, stageName, templateName, wireRopeDamages);
				qualityTemplateEntity.setWireRopeDamages(fileUrl);
			}
			if (packingIntact != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, stageName, templateName, packingIntact);
				qualityTemplateEntity.setPackingIntact(fileUrl);
			}
			if (improperStorage != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, stageName, templateName, improperStorage);
				qualityTemplateEntity.setImproperStorage(fileUrl);
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
				qualityTemplateEntity.setWeighment(fileUrl);
			}
			if (ackReceipt != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, stageName, templateName, ackReceipt);
				qualityTemplateEntity.setAckReceipt(fileUrl);
			}
			if (unloadingImproper != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, stageName, templateName,unloadingImproper);
				qualityTemplateEntity.setUnloadingImproper(fileUrl);
			}

			qualityTemplateRepository.save(qualityTemplateEntity);

			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"" + message + "}", header,
					HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("error is ==" + e.getMessage());
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Error Occurred\"}", header,
					HttpStatus.BAD_REQUEST);
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
			response = new ResponseEntity<>(
					"{\"status\": \"success\", \"message\": \"Template stage details deleted successfully..! \"}",
					header, HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", header,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public QualityTemplateResponse getById(int id) {

		QualityTemplateResponse resp = QualityTemplateEntity.valueOf(qualityTemplateRepository.findByTemplateId(id));
		;

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
	public List<QualityTemplateResponse> getAllTemplateDetails() {

		List<QualityTemplateResponse> instructionList = qualityTemplateRepository.findAllTemplates().stream()
				.map(i -> QualityTemplateEntity.valueOf(i)).collect(Collectors.toList());
		return instructionList;
	}

	@Override
	public ResponseEntity<Object> deleteTemplate(int id) {
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		try {

			qualityTemplateRepository.deleteById(id);
			response = new ResponseEntity<>(
					"{\"status\": \"success\", \"message\": \"Quality Template deleted successfully..! \"}", header,
					HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", header,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public ResponseEntity<Object> templateMapSave(QualityPartyMappingRequest qualityPartyMappingRequest) {

		List<QualityPartyTemplateEntity> list1 = qualityPartyTemplateRepository
				.findByTemplateId(qualityPartyMappingRequest.getTemplateId());
		for (QualityPartyTemplateEntity qqualityPartyTemplateEntity : list1) {
			qualityPartyTemplateRepository.deleteById(qqualityPartyTemplateEntity.getId());
		}

		ResponseEntity<Object> response = null;
		List<QualityPartyTemplateEntity> list = new ArrayList<QualityPartyTemplateEntity>();
		for (Integer partyId : qualityPartyMappingRequest.getPartyIdList()) {
			QualityPartyTemplateEntity qualityPartyTemplateEntity = new QualityPartyTemplateEntity();
			qualityPartyTemplateEntity.getTemplateEntity().setTemplateId(qualityPartyMappingRequest.getTemplateId());
			qualityPartyTemplateEntity.getParty().setnPartyId(partyId);
			qualityPartyTemplateEntity.setThicknessList(qualityPartyMappingRequest.getThicknessList().toString());
			qualityPartyTemplateEntity.setEndUserTagIdList(qualityPartyMappingRequest.getEndUserTagIdList().toString());
			qualityPartyTemplateEntity.setMatGradeIdList(qualityPartyMappingRequest.getMatGradeIdList().toString());
			qualityPartyTemplateEntity.setCreatedBy(qualityPartyMappingRequest.getUserId());
			qualityPartyTemplateEntity.setUpdatedBy(qualityPartyMappingRequest.getUserId());
			qualityPartyTemplateEntity.setCreatedOn(new Date());
			qualityPartyTemplateEntity.setUpdatedOn(new Date());
			list.add(qualityPartyTemplateEntity);
		}
		qualityPartyTemplateRepository.saveAll(list);
		response = new ResponseEntity<>(
				"{\"status\": \"success\", \"message\": \"Party-Template mapping details saved successfully..! \"}",
				new HttpHeaders(), HttpStatus.OK);
		return response;
	}

	@Override
	public ResponseEntity<Object> deleteTemplateMap(Integer templateId) {
		ResponseEntity<Object> response = null;
		try {
			List<QualityPartyTemplateEntity> list1 = qualityPartyTemplateRepository.findByTemplateId(templateId);
			if (list1 != null && list1.size() > 0) {
				for (QualityPartyTemplateEntity qqualityPartyTemplateEntity : list1) {
					qualityPartyTemplateRepository.deleteById(qqualityPartyTemplateEntity.getId());
				}
				response = new ResponseEntity<>(
						"{\"status\": \"success\", \"message\": \"Party Template mapping deleted successfully..! \"}",
						new HttpHeaders(), HttpStatus.OK);
			} else {
				response = new ResponseEntity<>(
						"{\"status\": \"failure\", \"message\": \"Please enter valid data..! \"}", new HttpHeaders(),
						HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}",
					new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public List<QualityPartyMappingResponse> getByPartyId(int partyId) {
		List<QualityPartyMappingResponse> instructionList = qualityPartyTemplateRepository.findByPartyId(partyId)
				.stream().map(i -> QualityPartyTemplateEntity.valueOf(i)).collect(Collectors.toList());
		return instructionList;
	}

	@Override
	public List<QualityPartyMappingResponse> getByPartyIdAndStageName(int partyId, String stageName) {
		List<QualityPartyMappingResponse> instructionList = qualityPartyTemplateRepository
				.getByPartyIdAndStageName(partyId, stageName).stream().map(i -> QualityPartyTemplateEntity.valueOf(i))
				.collect(Collectors.toList());
		return instructionList;
	}

	@Override
	public List<QualityPartyMappingResponse> getByTemplateId(int templateId) {
		List<QualityPartyMappingResponse> instructionList = qualityPartyTemplateRepository.findByTemplateId(templateId)
				.stream().map(i -> QualityPartyTemplateEntity.valueOf(i)).collect(Collectors.toList());
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

		System.out.println("getInwardId == " + qualityCheckRequest.getInwardId());
		System.out.println("getPartyId == " + qualityCheckRequest.getPartyId());
		System.out.println("getInstructionId == " + qualityCheckRequest.getInstructionId());
		System.out.println("getTemplateId == " + qualityCheckRequest.getTemplateId());

		return null;
	}

	@Override
	public ResponseEntity<Object> templateMapSaveNew(List<QualityPartyMappingRequestNew> listReq, int partyId,
			int userId) {

		List<QualityPartyTemplateEntity> list1 = qualityPartyTemplateRepository.findByPartyId(partyId);
		for (QualityPartyTemplateEntity qqualityPartyTemplateEntity : list1) {
			qualityPartyTemplateRepository.deleteById(qqualityPartyTemplateEntity.getId());
		}

		ResponseEntity<Object> response = null;
		List<QualityPartyTemplateEntity> list = new ArrayList<QualityPartyTemplateEntity>();
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

	/*
	 * @Override public ResponseEntity<Object> reportsSave(String inspectionId,
	 * String coilNumber, String inwardId, String templateId, String stageName,
	 * String templateDetails, String userId, MultipartFile rustObserved,
	 * MultipartFile safetyIssues, MultipartFile waterExposure, MultipartFile
	 * wireRopeDamages, MultipartFile packingIntact, MultipartFile improperStorage,
	 * MultipartFile strapping, MultipartFile weighmentSlip, MultipartFile
	 * weighment, MultipartFile acknowledgementReceipt, MultipartFile
	 * unloadingImproper) {
	 * 
	 * ResponseEntity<Object> response = null; String
	 * message="Quality Inspection Report details updated successfully..! ";
	 * HttpHeaders header = new HttpHeaders(); header.set("Content-Type",
	 * "application/json"); try { QualityReportEntity qualityReportEntity = new
	 * QualityReportEntity();
	 * 
	 * QualityReportEntity checkDuplicate =
	 * qualityReportRepository.findFirstByCoilNumber( coilNumber); if
	 * (checkDuplicate != null && checkDuplicate.getCoilNumber().length() > 0) {
	 * return new ResponseEntity<>
	 * ("{\"status\": \"failure\", \"message\": \"This coil already entered..!  }",
	 * header, HttpStatus.BAD_REQUEST); }
	 * 
	 * if (inspectionId != null && Integer.parseInt(inspectionId) > 0) {
	 * qualityReportEntity =
	 * qualityReportRepository.findByInspectionId(Integer.parseInt(inspectionId)); }
	 * 
	 * if (inspectionId != null && Integer.parseInt(inspectionId) > 0) {
	 * qualityReportEntity =
	 * qualityReportRepository.findByInspectionId(Integer.parseInt(inspectionId)); }
	 * qualityReportEntity.setUpdatedBy(Integer.parseInt(userId) );
	 * qualityReportEntity.setUpdatedOn(new Date());
	 * qualityReportEntity.setTemplateDetails( templateDetails);
	 * qualityReportEntity.setInwardId(Integer.parseInt(inwardId) );
	 * qualityReportEntity.setCoilNumber(coilNumber);
	 * qualityReportEntity.setTemplateId(Integer.parseInt(templateId));
	 * qualityReportEntity.setStageName( stageName );
	 * 
	 * if (rustObserved != null) { String fileUrl =
	 * awsS3Service.persistQualityReportFiles(templateFilesPath, stageName,
	 * coilNumber, rustObserved); qualityReportEntity.setRustObserved( fileUrl); }
	 * if (safetyIssues != null) { String fileUrl =
	 * awsS3Service.persistQualityReportFiles(templateFilesPath, stageName,
	 * coilNumber, safetyIssues); qualityReportEntity.setSafetyIssues( fileUrl); }
	 * if (waterExposure != null) { String fileUrl =
	 * awsS3Service.persistQualityReportFiles(templateFilesPath, stageName,
	 * coilNumber, waterExposure); qualityReportEntity.setWaterExposure( fileUrl); }
	 * if (wireRopeDamages != null) { String fileUrl =
	 * awsS3Service.persistQualityReportFiles(templateFilesPath, stageName,
	 * coilNumber, wireRopeDamages); qualityReportEntity.setWireRopeDamages(
	 * fileUrl); } if (packingIntact != null) { String fileUrl =
	 * awsS3Service.persistQualityReportFiles(templateFilesPath, stageName,
	 * coilNumber, packingIntact); qualityReportEntity.setPackingIntact(fileUrl); }
	 * if (improperStorage != null) { String fileUrl =
	 * awsS3Service.persistQualityReportFiles(templateFilesPath, stageName,
	 * coilNumber, improperStorage); qualityReportEntity.setImproperStorage(
	 * fileUrl); } if (strapping != null) { String fileUrl =
	 * awsS3Service.persistQualityReportFiles(templateFilesPath, stageName,
	 * coilNumber, strapping); qualityReportEntity.setStrapping(fileUrl); } if
	 * (weighmentSlip != null) { String fileUrl =
	 * awsS3Service.persistQualityReportFiles(templateFilesPath, stageName,
	 * coilNumber, weighmentSlip); qualityReportEntity.setWeighmentSlip(fileUrl); }
	 * if (weighment != null) { String fileUrl =
	 * awsS3Service.persistQualityReportFiles(templateFilesPath, stageName,
	 * coilNumber, weighment); qualityReportEntity.setWeighment( fileUrl); } if
	 * (acknowledgementReceipt != null) { String fileUrl =
	 * awsS3Service.persistQualityReportFiles(templateFilesPath, stageName,
	 * coilNumber, acknowledgementReceipt); qualityReportEntity.setAckReceipt(
	 * fileUrl); } if (unloadingImproper != null) { String fileUrl =
	 * awsS3Service.persistQualityReportFiles(templateFilesPath, stageName,
	 * coilNumber, unloadingImproper); qualityReportEntity.setUnloadingImproper(
	 * fileUrl); }
	 * 
	 * qualityReportRepository.save(qualityReportEntity);
	 * 
	 * response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"" +
	 * message + "}", header, HttpStatus.OK); } catch (Exception e) {
	 * e.printStackTrace(); log.info("error is ==" + e.getMessage()); response = new
	 * ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Error Occurred\"}",
	 * header, HttpStatus.BAD_REQUEST); }
	 * 
	 * return response; }
	 * 
	 * @Override public QualityReportResponse inspectionreportGetById(int id) {
	 * 
	 * QualityReportResponse resp =
	 * QualityReportEntity.valueOf(qualityReportRepository.findByInspectionId(id));;
	 * 
	 * if(resp.getRustObserved()!=null && resp.getRustObserved().length() >0 ) {
	 * resp.setRustObservedPreSingedURL(awsS3Service.generatePresignedUrl(resp.
	 * getRustObserved()) ); } if(resp.getSafetyIssues()!=null &&
	 * resp.getSafetyIssues().length() >0 ) {
	 * resp.setSafetyIssuesPreSingedURL(awsS3Service.generatePresignedUrl(resp.
	 * getSafetyIssues()) ); } if(resp.getWaterExposure()!=null &&
	 * resp.getWaterExposure().length() >0 ) {
	 * resp.setWaterExposurePreSingedURL(awsS3Service.generatePresignedUrl(resp.
	 * getWaterExposure()) ); } if(resp.getWireRopeDamages()!=null &&
	 * resp.getWireRopeDamages().length() >0 ) {
	 * resp.setWireRopeDamagesPreSingedURL(awsS3Service.generatePresignedUrl(resp.
	 * getWireRopeDamages()) ); } if(resp.getPackingIntact()!=null &&
	 * resp.getPackingIntact().length() >0 ) {
	 * resp.setPackingIntactPreSingedURL(awsS3Service.generatePresignedUrl(resp.
	 * getPackingIntact()) ); } if(resp.getImproperStorage()!=null &&
	 * resp.getImproperStorage().length() >0 ) {
	 * resp.setImproperStoragePreSingedURL(awsS3Service.generatePresignedUrl(resp.
	 * getImproperStorage()) ); } if(resp.getStrapping()!=null &&
	 * resp.getStrapping().length() >0 ) {
	 * resp.setStrappingPreSingedURL(awsS3Service.generatePresignedUrl(resp.
	 * getStrapping()) ); } if(resp.getWeighmentSlip()!=null &&
	 * resp.getWeighmentSlip().length() >0 ) {
	 * resp.setWeighmentSlipPreSingedURL(awsS3Service.generatePresignedUrl(resp.
	 * getWeighmentSlip()) ); } if(resp.getWeighment()!=null &&
	 * resp.getWeighment().length() >0 ) {
	 * resp.setWeighmentPreSingedURL(awsS3Service.generatePresignedUrl(resp.
	 * getWeighment()) ); } if(resp.getAckReceipt()!=null &&
	 * resp.getAckReceipt().length() >0 ) {
	 * resp.setAckReceiptPreSingedURL(awsS3Service.generatePresignedUrl(resp.
	 * getAckReceipt()) ); } if(resp.getUnloadingImproper()!=null &&
	 * resp.getUnloadingImproper().length() >0 ) {
	 * resp.setUnloadingImproperPreSingedURL(awsS3Service.generatePresignedUrl(resp.
	 * getUnloadingImproper()) ); } return resp; }
	 * 
	 * @Override public List<QualityReportResponse> inspectionreportGetAll() {
	 * List<QualityReportResponse> instructionList =
	 * qualityReportRepository.findAll().stream() .map(i ->
	 * QualityReportEntity.valueOf(i)).collect(Collectors.toList()); return
	 * instructionList; }
	 * 
	 * @Override public ResponseEntity<Object> deleteInspectionReport(int id) {
	 * 
	 * ResponseEntity<Object> response = null; HttpHeaders header = new
	 * HttpHeaders(); header.set("Content-Type", "application/json"); try {
	 * 
	 * qualityReportRepository.deleteById(id); response = new ResponseEntity<>(
	 * "{\"status\": \"success\", \"message\": \"Quality Inspection Report deleted successfully..! \"}"
	 * , header, HttpStatus.OK); } catch (Exception e) { response = new
	 * ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() +
	 * "\"}", header, HttpStatus.INTERNAL_SERVER_ERROR); } return response; }
	 */

	@Override
	public ResponseEntity<Object> save(KQPRequest kcpRequest, int userId) {

		ResponseEntity<Object> response = null;

		KQPEntity checkpackingItemEntity = kqpRepository.findFirstByKqpName(kcpRequest.getKqpName());

		KQPEntity kqpEntity = new KQPEntity();
		if (checkpackingItemEntity != null && checkpackingItemEntity.getKqpId() > 0) {
			kqpEntity.setKqpId(kcpRequest.getKqpId());

			if (checkpackingItemEntity != null && kcpRequest.getKqpId() != checkpackingItemEntity.getKqpId()) {
				return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"KQP Name already exists..! \"}",
						new HttpHeaders(), HttpStatus.BAD_REQUEST);

			}
		}
		kqpEntity.setKqpName(kcpRequest.getKqpName());
		kqpEntity.setKqpDesc(kcpRequest.getKqpDesc());
		kqpEntity.setKqpSummary(kcpRequest.getKqpSummary());
		kqpEntity.setStageName(kcpRequest.getStageName());
		kqpEntity.setCreatedBy(userId);
		kqpEntity.setUpdatedBy(userId);
		kqpEntity.setCreatedOn(new Date());
		kqpEntity.setUpdatedOn(new Date());
		kqpRepository.save(kqpEntity);
		if (kcpRequest.getKqpId() != null && kcpRequest.getKqpId() > 0) {
			log.info("KQP details updated successfully");
			response = new ResponseEntity<>(
					"{\"status\": \"success\", \"message\": \"KQP details updated successfully..! \"}",
					new HttpHeaders(), HttpStatus.OK);
		} else {
			response = new ResponseEntity<>(
					"{\"status\": \"success\", \"message\": \"KQP details saved successfully..! \"}", new HttpHeaders(),
					HttpStatus.OK);
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
			response = new ResponseEntity<>(
					"{\"status\": \"success\", \"message\": \"KQP details deleted successfully..! \"}", header,
					HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", header,
					HttpStatus.INTERNAL_SERVER_ERROR);
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
		if (kqpPartyMappingRequest.getPartyIdList() != null && kqpPartyMappingRequest.getPartyIdList().size() > 0) {
			kqpPartyTemplateEntity.setPartyIdList(kqpPartyMappingRequest.getPartyIdList().toString());
		}
		if (kqpPartyMappingRequest.getThicknessList() != null && kqpPartyMappingRequest.getThicknessList().size() > 0) {
			kqpPartyTemplateEntity.setThicknessList(kqpPartyMappingRequest.getThicknessList().toString());
		}
		if (kqpPartyMappingRequest.getLengthList() != null && kqpPartyMappingRequest.getLengthList().size() > 0) {
			kqpPartyTemplateEntity.setLengthList(kqpPartyMappingRequest.getLengthList().toString());
		}
		if (kqpPartyMappingRequest.getWidthList() != null && kqpPartyMappingRequest.getWidthList().size() > 0) {
			kqpPartyTemplateEntity.setWidthList(kqpPartyMappingRequest.getWidthList().toString());
		}
		if (kqpPartyMappingRequest.getEndUserTagIdList() != null
				&& kqpPartyMappingRequest.getEndUserTagIdList().size() > 0) {
			kqpPartyTemplateEntity.setEndUserTagIdList(kqpPartyMappingRequest.getEndUserTagIdList().toString());
		}
		if (kqpPartyMappingRequest.getMatGradeIdList() != null
				&& kqpPartyMappingRequest.getMatGradeIdList().size() > 0) {
			kqpPartyTemplateEntity.setMatGradeIdList(kqpPartyMappingRequest.getMatGradeIdList().toString());
		}
		if (kqpPartyMappingRequest.getAnyPartyFlag() != null && "Y".equals(kqpPartyMappingRequest.getAnyPartyFlag())) {
			kqpPartyTemplateEntity.setAnyPartyFlag("Y");
		} else {
			kqpPartyTemplateEntity.setAnyPartyFlag("N");
		}
		if (kqpPartyMappingRequest.getAnyMatGradeFlag() != null
				&& "Y".equals(kqpPartyMappingRequest.getAnyMatGradeFlag())) {
			kqpPartyTemplateEntity.setAnyMatgradeFlag("Y");
		} else {
			kqpPartyTemplateEntity.setAnyMatgradeFlag("N");
		}
		if (kqpPartyMappingRequest.getAnyEndusertagFlag() != null
				&& "Y".equals(kqpPartyMappingRequest.getAnyEndusertagFlag())) {
			kqpPartyTemplateEntity.setAnyEndusertagFlag("Y");
		} else {
			kqpPartyTemplateEntity.setAnyEndusertagFlag("N");
		}
		if (kqpPartyMappingRequest.getAnyWidthFlag() != null && "Y".equals(kqpPartyMappingRequest.getAnyWidthFlag())) {
			kqpPartyTemplateEntity.setAnyWidthFlag("Y");
		} else {
			kqpPartyTemplateEntity.setAnyWidthFlag("N");
		}
		if (kqpPartyMappingRequest.getAnyLengthFlag() != null
				&& "Y".equals(kqpPartyMappingRequest.getAnyLengthFlag())) {
			kqpPartyTemplateEntity.setAnyLengthFlag("Y");
		} else {
			kqpPartyTemplateEntity.setAnyLengthFlag("N");
		}
		if (kqpPartyMappingRequest.getAnyThicknessFlag() != null
				&& "Y".equals(kqpPartyMappingRequest.getAnyThicknessFlag())) {
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
		response = new ResponseEntity<>(
				"{\"status\": \"success\", \"message\": \"KQP-Party mapping details saved successfully..! \"}",
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
				response = new ResponseEntity<>(
						"{\"status\": \"success\", \"message\": \"KQP-Party mapping deleted successfully..! \"}",
						new HttpHeaders(), HttpStatus.OK);
			} else {
				response = new ResponseEntity<>(
						"{\"status\": \"failure\", \"message\": \"Please enter valid data..! \"}", new HttpHeaders(),
						HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}",
					new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public List<KQPPartyMappingResponse> getByKQPId(int kqpId) {
		List<KQPPartyMappingResponse> instructionList = kqpPartyTemplateRepository.findByKqpId(kqpId).stream()
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
			if (result[8] != null && result[8].toString().length() > 0) {
				resp.setQirId(Integer.parseInt(result[8].toString()));
			} else {
				resp.setQirId(null);
			}
			resp.setPartyName(result[9] != null ? (String) result[9] : null);
			resp.setFwidth(result[10] != null ? (Float) result[10] : null);
			resp.setMaterialDesc(result[11] != null ? (String) result[11] : null);
			qirList.add(resp);
		}
		return qirList;
	}

	@Override
	public List<QualityInspReportListPageResponse> qirPreProcessingListPage() {
		List<Object[]> packetsList = kqpPartyTemplateRepository.qirPreProcessingListPage();
		Map<String, QualityInspReportListPageResponse> kk = new LinkedHashMap<>();
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
			if (result[8] != null && result[8].toString().length() > 0) {
				resp.setQirId(Integer.parseInt(result[8].toString()));
			} else {
				resp.setQirId(null);
			}
			resp.setPartyName(result[9] != null ? (String) result[9] : null);
			resp.setFwidth(result[10] != null ? (Float) result[10] : null);
			resp.setMaterialDesc(result[11] != null ? (String) result[11] : null);
			kk.put(resp.getPlanId(), resp);
			//qirList.add(resp);
		}
		List<QualityInspReportListPageResponse> qirList = new ArrayList<QualityInspReportListPageResponse>(kk.values());
		return qirList;
	}

	@Override
	public List<QualityInspReportListPageResponse> qirProcessingListPage() {
		List<Object[]> packetsList = kqpPartyTemplateRepository.qirProcessingListPage();
		Map<String, QualityInspReportListPageResponse> kk = new LinkedHashMap<>();
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
			if (result[8] != null && result[8].toString().length() > 0) {
				resp.setQirId(Integer.parseInt(result[8].toString()));
			} else {
				resp.setQirId(null);
			}
			resp.setPartyName(result[9] != null ? (String) result[9] : null);
			resp.setFwidth(result[10] != null ? (Float) result[10] : null);
			resp.setMaterialDesc(result[11] != null ? (String) result[11] : null);
			kk.put(resp.getPlanId(), resp);
			//qirList.add(resp);
		}
		
		List<QualityInspReportListPageResponse> qirList = new ArrayList<QualityInspReportListPageResponse>(kk.values());

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
	public List<QualityInspDispatchListResponse> qirPreDispatchList() {
		List<Object[]> packetsList = kqpPartyTemplateRepository.qirPreDispatchList();
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
			if (result[10] != null && result[10].toString().length() > 0) {
				resp.setQirId(Integer.parseInt(result[10].toString()));
			} else {
				resp.setQirId(null);
			}
			resp.setPartyName(result[11] != null ? (String) result[11] : null);
			resp.setFwidth(result[12] != null ? (Float) result[12] : null);
			resp.setMaterialDesc(result[13] != null ? (String) result[13] : null);
			resp.setMaterialGrade( result[14] != null ? (String) result[14] : null);
			qirList.add(resp);
		}
		return qirList;
	}

	@Override
	public List<QualityInspDispatchListResponse> qirPostDispatchList() {
		List<Object[]> packetsList = kqpPartyTemplateRepository.qirPostDispatchList();
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
			if (result[10] != null && result[10].toString().length() > 0) {
				resp.setQirId(Integer.parseInt(result[10].toString()));
			} else {
				resp.setQirId(null);
			}
			resp.setPartyName(result[11] != null ? (String) result[11] : null);
			resp.setFwidth(result[12] != null ? (Float) result[12] : null);
			resp.setMaterialDesc(result[13] != null ? (String) result[13] : null);
			resp.setMaterialGrade( result[14] != null ? (String) result[14] : null);
			qirList.add(resp);
		}
		return qirList;
	}

	@Override
	public List<InstructionResponseDto> getDispatchDetails(QIRSaveDataRequest qirSaveDataRequest) {

		List<InstructionResponseDto> instructionList = kqpPartyTemplateRepository
				.getDispatchDetails(qirSaveDataRequest.getCoilNo(),
						Integer.parseInt(qirSaveDataRequest.getPartDetailsId()))
				.stream().map(i -> Instruction.valueOf(i)).collect(Collectors.toList());

		return instructionList;
	}

	@Override
	public ResponseEntity<Object> qirReportSave(String templateId, String stageName, String templateDetails,
			String planDetails, String userId, String processId, MultipartFile rustObserved, MultipartFile safetyIssues,
			MultipartFile waterExposure, MultipartFile wireRopeDamages, MultipartFile packingIntact,
			MultipartFile improperStorage, MultipartFile strapping, MultipartFile weighmentSlip,
			MultipartFile weighment, MultipartFile ackReceipt, MultipartFile unloadingImproper, String coilNo,
			String customerBatchNo, String planId, String deliveryChalanNo, String qirId, MultipartFile coilBend,
			MultipartFile packingDamageTransit, MultipartFile processingReport1, MultipartFile processingReport2,
			MultipartFile processingReport3, MultipartFile processingReport4, String comments) {

		ResponseEntity<Object> response = null;
		String message = "Quality Inspection Report details saved successfully..! ";
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		try {

			log.info(" templateDetails == " + templateDetails);
			QualityInspectionReportEntity qualityTemplateEntity = new QualityInspectionReportEntity();

			if (qirId != null && Integer.parseInt(qirId) > 0) {
				qualityTemplateEntity = qualityInspectionReportRepository.findByQirId(Integer.parseInt(qirId));
			} else {
				qualityTemplateEntity = new QualityInspectionReportEntity();
				qualityTemplateEntity.setCreatedBy(Integer.parseInt(userId));
				qualityTemplateEntity.setCreatedOn(new Date());
			}
			log.info(" templateDetails == " + templateDetails);

			qualityTemplateEntity.setCreatedBy(Integer.parseInt(userId));
			qualityTemplateEntity.setCreatedOn(new Date());
			qualityTemplateEntity.setStageName(stageName);
			qualityTemplateEntity.setComments(comments); 
			if (processId != null && processId.length() > 0) {
				qualityTemplateEntity.setProcessId(Integer.parseInt(processId));
			}
			qualityTemplateEntity.getTemplateEntity().setTemplateId(Integer.parseInt(templateId));
			qualityTemplateEntity.setCoilNo(coilNo);
			qualityTemplateEntity.setCustomerBatchNo(customerBatchNo);
			qualityTemplateEntity.setPlanId(planId);
			qualityTemplateEntity.setDeliveryChalanNo(deliveryChalanNo);
			qualityTemplateEntity.setTemplateDetails(templateDetails);
			qualityTemplateEntity.setPlanDetails(planDetails);
			qualityTemplateEntity.setUpdatedBy(Integer.parseInt(userId));
			qualityTemplateEntity.setUpdatedOn(new Date());

			if (rustObserved != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo + "_" + stageName, templateId, rustObserved);
				qualityTemplateEntity.setRustObserved(fileUrl);
			}
			if (coilBend != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo + "_" + stageName, templateId, coilBend);
				qualityTemplateEntity.setCoilBend(fileUrl);
			}
			if (safetyIssues != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo + "_" + stageName, templateId, safetyIssues);
				qualityTemplateEntity.setSafetyIssues(fileUrl);
			}
			if (waterExposure != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo + "_" + stageName, templateId, waterExposure);
				qualityTemplateEntity.setWaterExposure(fileUrl);
			}
			if (wireRopeDamages != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo + "_" + stageName, templateId, wireRopeDamages);
				qualityTemplateEntity.setWireRopeDamages(fileUrl);
			}
			if (packingIntact != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo + "_" + stageName, templateId, packingIntact);
				qualityTemplateEntity.setPackingIntact(fileUrl);
			}
			if (improperStorage != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo + "_" + stageName, templateId, improperStorage);
				qualityTemplateEntity.setImproperStorage(fileUrl);
			}
			if (strapping != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo + "_" + stageName, templateId, strapping);
				qualityTemplateEntity.setStrapping(fileUrl);
			}
			if (weighmentSlip != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo + "_" + stageName, templateId, weighmentSlip);
				qualityTemplateEntity.setWeighmentSlip(fileUrl);
			}
			if (weighment != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo + "_" + stageName, templateId, weighment);
				qualityTemplateEntity.setWeighment(fileUrl);
			}
			if (ackReceipt != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo + "_" + stageName, templateId, ackReceipt);
				qualityTemplateEntity.setAckReceipt(fileUrl);
			}
			if (unloadingImproper != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo + "_" + stageName, templateId, unloadingImproper);
				qualityTemplateEntity.setUnloadingImproper(fileUrl);
			}
			if (packingDamageTransit != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo + "_" + stageName, templateId, packingDamageTransit);
				qualityTemplateEntity.setPackingDamageTransit( fileUrl);
			}
			if (processingReport1 != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo + "_" + stageName, templateId, processingReport1);
				qualityTemplateEntity.setProcessingReport1( fileUrl);
			}
			if (processingReport2 != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo + "_" + stageName, templateId, processingReport2);
				qualityTemplateEntity.setProcessingReport2(fileUrl);
			}
			if (processingReport3 != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo + "_" + stageName, templateId, processingReport3);
				qualityTemplateEntity.setProcessingReport3(fileUrl);
			}
			if (processingReport4 != null) {
				String fileUrl = awsS3Service.persistFiles(templateFilesPath, customerBatchNo + "_" + stageName, templateId, processingReport4);
				qualityTemplateEntity.setProcessingReport4(fileUrl);
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
		if (resp.getCoilBend() != null && resp.getCoilBend().length() > 0) {
			resp.setCoilBendPreSingedURL(awsS3Service.generatePresignedUrl(resp.getCoilBend()));
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
		if (resp.getPackingDamageTransit() != null && resp.getPackingDamageTransit().length() > 0) {
			resp.setPackingDamageTransitURL(awsS3Service.generatePresignedUrl(resp.getPackingDamageTransit()));
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
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"QIR Report deleted successfully..! \"}", header,
					HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public QualityInspectionReportResponse getQIRReport(String stageName, String coilNo, String planId) {

		QualityInspectionReportResponse resp = new QualityInspectionReportResponse();
		try {
			QualityInspectionReportEntity entity = null;// qualityInspectionReportRepository.findTop1ByCoilNoDesc(coilNo);

			if ("inward".equalsIgnoreCase(stageName)) {
				entity = qualityInspectionReportRepository.findTop1ByCoilNoOrderByQirIdDesc(coilNo);
			} else if ("processing".equalsIgnoreCase(stageName) || "postprocessing".equalsIgnoreCase(stageName)) {
				entity = qualityInspectionReportRepository.findTop1ByCoilNoAndPlanId(coilNo, planId);
			} else if ("predispatch".equalsIgnoreCase(stageName) || "postdispatch".equalsIgnoreCase(stageName)) {
				entity = qualityInspectionReportRepository.findTop1ByCoilNoAndDeliveryChalanNo(coilNo, planId);
			}

			resp = QualityInspectionReportEntity.valueOf(entity);

			if (resp.getRustObserved() != null && resp.getRustObserved().length() > 0) {
				resp.setRustObservedPreSingedURL(awsS3Service.generatePresignedUrl(resp.getRustObserved()));
			}
			if (resp.getCoilBend() != null && resp.getCoilBend().length() > 0) {
				resp.setCoilBendPreSingedURL(awsS3Service.generatePresignedUrl(resp.getCoilBend()));
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
			if (resp.getPackingDamageTransit() != null && resp.getPackingDamageTransit().length() > 0) {
				resp.setPackingDamageTransitURL(awsS3Service.generatePresignedUrl(resp.getPackingDamageTransit()));
			}
			if (resp.getProcessingReport1() != null && resp.getProcessingReport1().length() > 0) {
				resp.setProcessingReport1URL(awsS3Service.generatePresignedUrl(resp.getProcessingReport1()));
			}
			if (resp.getProcessingReport2() != null && resp.getProcessingReport2().length() > 0) {
				resp.setProcessingReport2URL(awsS3Service.generatePresignedUrl(resp.getProcessingReport2()));
			}
			if (resp.getProcessingReport3() != null && resp.getProcessingReport3().length() > 0) {
				resp.setProcessingReport3URL(awsS3Service.generatePresignedUrl(resp.getProcessingReport3()));
			}
			if (resp.getProcessingReport4() != null && resp.getProcessingReport4().length() > 0) {
				resp.setProcessingReport4URL(awsS3Service.generatePresignedUrl(resp.getProcessingReport4()));
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
				if (resp.getCoilBend() != null && resp.getCoilBend().length() > 0) {
					resp.setCoilBendPreSingedURL(awsS3Service.generatePresignedUrl(resp.getCoilBend()));
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
				if (resp.getPackingDamageTransit() != null && resp.getPackingDamageTransit().length() > 0) {
					resp.setPackingDamageTransitURL(awsS3Service.generatePresignedUrl(resp.getPackingDamageTransit()));
				}
				if (resp.getProcessingReport1() != null && resp.getProcessingReport1().length() > 0) {
					resp.setProcessingReport1URL(awsS3Service.generatePresignedUrl(resp.getProcessingReport1()));
				}
				if (resp.getProcessingReport2() != null && resp.getProcessingReport2().length() > 0) {
					resp.setProcessingReport2URL(awsS3Service.generatePresignedUrl(resp.getProcessingReport2()));
				}
				if (resp.getProcessingReport3() != null && resp.getProcessingReport3().length() > 0) {
					resp.setProcessingReport3URL(awsS3Service.generatePresignedUrl(resp.getProcessingReport3()));
				}
				if (resp.getProcessingReport4() != null && resp.getProcessingReport4().length() > 0) {
					resp.setProcessingReport4URL(awsS3Service.generatePresignedUrl(resp.getProcessingReport4()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	@Override
	public File qirPDF(Integer qirId) {

		File file = null;
		try {
			 QualityInspectionReportEntity entity = qualityInspectionReportRepository.findByQirId(qirId);
			file = renderQIRpdf(entity );
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	private File renderQIRpdf(QualityInspectionReportEntity entity) throws IOException, DocumentException {
		File file = File.createTempFile("qirpdf_" +entity.getStageName()+"_"+ entity.getQirId(), ".pdf");
		//File file = new File("E:/qirpdf_" +entity.getStageName()+"_"+ entity.getQirId()+".pdf");
		Document document = new Document();

		int fixedHeight=22;
		int tableRowHeight=16;
		int fixedHeight2=32;
		try {
			
			InwardEntry inwardEntry = inwdEntrySvc.getByCoilNumber( entity.getCoilNo());
			
	        CompanyDetails companyDetails = companyDetailsService.findById(1);
	        
			ObjectMapper objectMapper = new ObjectMapper();
			TypeFactory typeFactory = objectMapper.getTypeFactory();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			List<QIRTemplateDtlsJsonArrayDTO> templateDetailsList = objectMapper.readValue(entity.getTemplateDetails(), typeFactory.constructCollectionType(List.class, QIRTemplateDtlsJsonArrayDTO.class));
			
			FileOutputStream fos = new FileOutputStream(file);
			document = new Document(PageSize.A4);

			PdfWriter pdfWriter = PdfWriter.getInstance(document, fos);
			pdfWriter.setBoxSize("art", new Rectangle(PageSize.A4));
			document.open();

			Font font9b = FontFactory.getFont(BaseFont.WINANSI, 9f, Font.BOLD);
			Font font10b = FontFactory.getFont(BaseFont.WINANSI, 10f, Font.BOLD);
			Font font9 = FontFactory.getFont(BaseFont.WINANSI, 9f);
			Font font11 = FontFactory.getFont(BaseFont.WINANSI, 11f);
			Font font11b = FontFactory.getFont(BaseFont.WINANSI, 11f, Font.BOLD);
			Font font12b = FontFactory.getFont(BaseFont.WINANSI, 12f, Font.BOLD);
			
			Paragraph p = new Paragraph();
            p.add(new Chunk( "Quality Inspection Report", font12b));
            //p.setSpacingAfter(20f);
            p.setAlignment(Element.ALIGN_CENTER);
			document.add(p);

			/* Bill Ship Address starts */
			PdfPTable coilDetailsTab = new PdfPTable(4);
			coilDetailsTab.setWidthPercentage(95);
			coilDetailsTab.setWidths(new int[] { 50, 50 , 50, 50 });

			PdfPCell companyNameCell = new PdfPCell(new Phrase(companyDetails.getCompanyName(), font11b));
			companyNameCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell.setFixedHeight(fixedHeight);
			companyNameCell.setBorder(Rectangle.NO_BORDER);
			companyNameCell.setColspan(4);
			coilDetailsTab.addCell(companyNameCell);	

			PdfPCell officeAddressCell = new PdfPCell(new Phrase("Head Office At : "+companyDetails.getAddressOffice(), font11));
			officeAddressCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			officeAddressCell.setFixedHeight(fixedHeight2);
			officeAddressCell.setBorder(Rectangle.NO_BORDER);
			officeAddressCell.setColspan(2);
			coilDetailsTab.addCell(officeAddressCell);
			PdfPCell branchAddressCell = new PdfPCell(new Phrase("Branch At : "+companyDetails.getAddressBranch(), font11));
			branchAddressCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			branchAddressCell.setFixedHeight(fixedHeight2);
			branchAddressCell.setBorder(Rectangle.NO_BORDER);
			branchAddressCell.setColspan(2);
			coilDetailsTab.addCell(branchAddressCell);
			
			PdfPCell coilNoCell = new PdfPCell(new Phrase("Coil No :", font11));
			coilNoCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			coilNoCell.setFixedHeight(fixedHeight);
			coilNoCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(coilNoCell);			
			PdfPCell coilNoCellValue = new PdfPCell(new Phrase(entity.getCoilNo(), font11));
			coilNoCellValue.setHorizontalAlignment( Element.ALIGN_LEFT);
			coilNoCellValue.setFixedHeight(fixedHeight);
			coilNoCellValue.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(coilNoCellValue);
			
			PdfPCell custBatchNoCell = new PdfPCell(new Phrase("Customer Batch No :", font11));
			custBatchNoCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			custBatchNoCell.setFixedHeight(fixedHeight);
			custBatchNoCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(custBatchNoCell);			
			
			PdfPCell custBatchNoCellValue = new PdfPCell(new Phrase(inwardEntry.getCustomerBatchId() , font11));
			custBatchNoCellValue.setHorizontalAlignment( Element.ALIGN_LEFT);
			custBatchNoCellValue.setFixedHeight(fixedHeight);
			custBatchNoCellValue.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(custBatchNoCellValue);

			PdfPCell stageCell = new PdfPCell(new Phrase("Stage : ", font11));
			stageCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			stageCell.setFixedHeight(fixedHeight);
			stageCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(stageCell);			
			PdfPCell stageCellValue = new PdfPCell(new Phrase(entity.getStageName(), font11));
			stageCellValue.setHorizontalAlignment( Element.ALIGN_LEFT);
			stageCellValue.setFixedHeight(fixedHeight);
			stageCellValue.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(stageCellValue);
			
			PdfPCell templateNameCell = new PdfPCell(new Phrase("Template Name :", font11));
			templateNameCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			templateNameCell.setFixedHeight(fixedHeight);
			templateNameCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(templateNameCell);			
			PdfPCell templateNameCellValue = new PdfPCell(new Phrase(entity.getTemplateEntity().getTemplateName(), font11));
			templateNameCellValue.setHorizontalAlignment( Element.ALIGN_LEFT);
			templateNameCellValue.setFixedHeight(fixedHeight);
			templateNameCellValue.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(templateNameCellValue);

			PdfPCell thicknessCell = new PdfPCell(new Phrase("Thickness : ", font11));
			thicknessCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			thicknessCell.setFixedHeight(fixedHeight);
			thicknessCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(thicknessCell);			
			PdfPCell thicknessCellValue = new PdfPCell(new Phrase(""+inwardEntry.getfThickness(), font11));
			thicknessCellValue.setHorizontalAlignment( Element.ALIGN_LEFT);
			thicknessCellValue.setFixedHeight(fixedHeight);
			thicknessCellValue.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(thicknessCellValue);
			
			PdfPCell widthCell = new PdfPCell(new Phrase("Width : ", font11));
			widthCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			widthCell.setFixedHeight(fixedHeight);
			widthCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(widthCell);			
			PdfPCell widthCellValue = new PdfPCell(new Phrase(""+inwardEntry.getfWidth(), font11));
			widthCellValue.setHorizontalAlignment( Element.ALIGN_LEFT);
			widthCellValue.setFixedHeight(fixedHeight);
			widthCellValue.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(widthCellValue);
			
			PdfPCell matDescCell = new PdfPCell(new Phrase("Material Desc : ", font11));
			matDescCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			matDescCell.setFixedHeight(fixedHeight);
			matDescCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(matDescCell);			
			PdfPCell matDescCellValue = new PdfPCell(new Phrase(inwardEntry.getMaterial().getDescription(), font11));
			matDescCellValue.setHorizontalAlignment( Element.ALIGN_LEFT);
			matDescCellValue.setFixedHeight(fixedHeight);
			matDescCellValue.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(matDescCellValue);

			PdfPCell matGradeCell = new PdfPCell(new Phrase("Material Grade : ", font11));
			matGradeCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			matGradeCell.setFixedHeight(fixedHeight);
			matGradeCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(matGradeCell);			
			PdfPCell matGradeCellValue = new PdfPCell(new Phrase(inwardEntry.getMaterialGrade().getGradeName(), font11));
			matGradeCellValue.setHorizontalAlignment( Element.ALIGN_LEFT);
			matGradeCellValue.setFixedHeight(fixedHeight);
			matGradeCellValue.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(matGradeCellValue);
			
			PdfPCell customerNameCell = new PdfPCell(new Phrase("Customer Name : ", font11));
			customerNameCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			customerNameCell.setFixedHeight(fixedHeight);
			customerNameCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(customerNameCell);			
			PdfPCell customerNameCellValue = new PdfPCell(new Phrase(inwardEntry.getParty().getPartyName(), font11));
			customerNameCellValue.setHorizontalAlignment( Element.ALIGN_LEFT);
			customerNameCellValue.setFixedHeight(fixedHeight);
			customerNameCellValue.setColspan(3);
			customerNameCellValue.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(customerNameCellValue);
			document.add( Chunk.NEWLINE );
			document.add(coilDetailsTab);
			
			if ("PROCESSING".equals(entity.getStageName())) {

				List<QIRPanDetailsJsonArrayDTO> planDetails = objectMapper.readValue(entity.getPlanDetails(), typeFactory.constructCollectionType(List.class, QIRPanDetailsJsonArrayDTO.class));
				if (planDetails != null && planDetails.get(0) != null 
						&& planDetails.get(0).getSlitInspectionData() != null
						&& planDetails.get(0).getSlitInspectionData().size() > 0) {
					
					PdfPTable processDetailsTab = new PdfPTable(6);
					processDetailsTab.setWidthPercentage(98);
					processDetailsTab.setWidths(new int[] { 30, 30, 50, 50, 40, 120 });
					
					PdfPCell headingCell = new PdfPCell(new Phrase("Sliting Inspection Details", font10b));
					headingCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					headingCell.setFixedHeight(22);
					headingCell.setBorder(Rectangle.NO_BORDER);
					headingCell.setColspan(6);
					processDetailsTab.addCell(headingCell);
					
					PdfPCell headerColumn1Cell = new PdfPCell(new Phrase("Instruction Id", font9b));
					headerColumn1Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn1Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn1Cell);

					PdfPCell headerColumn2Cell = new PdfPCell(new Phrase("Slit Size", font9b));
					headerColumn2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn2Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn2Cell);

					PdfPCell headerColumn3Cell = new PdfPCell(new Phrase("Actual Slit Size", font9b));
					headerColumn3Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn3Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn3Cell);

					PdfPCell headerColumn4Cell = new PdfPCell(new Phrase("Actual Thickness", font9b));
					headerColumn4Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn4Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn4Cell);

					PdfPCell headerColumn5Cell = new PdfPCell(new Phrase("Burr Height", font9b));
					headerColumn5Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn5Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn5Cell);

					PdfPCell headerColumn6Cell = new PdfPCell(new Phrase("Remarks", font9b));
					headerColumn6Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn6Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn6Cell);

					for (QIRPanDetailsJsonArrayChildDTO obj : planDetails.get(0).getSlitInspectionData()) {
						PdfPCell headerColumn1CellValue = new PdfPCell(new Phrase("" + obj.getInstructionId(), font9));
						headerColumn1CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn1CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn1CellValue);

						PdfPCell headerColumn2CellValue = new PdfPCell(new Phrase(obj.getPlannedWidth(), font9));
						headerColumn2CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn2CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn2CellValue);

						PdfPCell headerColumn3CellValue = new PdfPCell(new Phrase(obj.getActualWidth(), font9));
						headerColumn3CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn3CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn3CellValue);

						PdfPCell headerColumn4CellValue = new PdfPCell(new Phrase(obj.getActualThickness(), font9));
						headerColumn4CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn4CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn4CellValue);

						PdfPCell headerColumn5CellValue = new PdfPCell(new Phrase(obj.getBurrHeight(), font9));
						headerColumn5CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn5CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn5CellValue);

						PdfPCell headerColumn6CellValue = new PdfPCell(new Phrase(obj.getRemarks(), font9));
						headerColumn6CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn6CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn6CellValue);
					}
					document.add(processDetailsTab);
				}
				
				if (planDetails != null && planDetails.get(0) != null 
						&& planDetails.get(0).getCutInspectionData() != null
						&& planDetails.get(0).getCutInspectionData().size() > 0) {
					PdfPTable processDetailsTab = new PdfPTable(9);
					processDetailsTab.setWidthPercentage(98);
					processDetailsTab.setWidths(new int[] { 30, 30, 50, 50, 40,  40,  40,  40, 80 });
					
					PdfPCell headingCell = new PdfPCell(new Phrase("Cutting Inspection Details", font10b));
					headingCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					headingCell.setFixedHeight(fixedHeight);
					headingCell.setBorder(Rectangle.NO_BORDER);
					headingCell.setColspan(9);
					processDetailsTab.addCell(headingCell);
					
					PdfPCell headerColumn1Cell = new PdfPCell(new Phrase("Thickness", font9b));
					headerColumn1Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn1Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn1Cell);

					PdfPCell headerColumn2Cell = new PdfPCell(new Phrase("Width", font9b));
					headerColumn2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn2Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn2Cell);

					PdfPCell headerColumn3Cell = new PdfPCell(new Phrase("Length", font9b));
					headerColumn3Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn3Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn3Cell);

					PdfPCell headerColumn4Cell = new PdfPCell(new Phrase("Actual Thickness", font9b));
					headerColumn4Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn4Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn4Cell);

					PdfPCell headerColumn4Cell1 = new PdfPCell(new Phrase("Actual Width", font9b));
					headerColumn4Cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn4Cell1.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn4Cell1);

					PdfPCell headerColumn4Cell2 = new PdfPCell(new Phrase("Actual Length", font9b));
					headerColumn4Cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn4Cell2.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn4Cell2);

					PdfPCell headerColumn5Cell = new PdfPCell(new Phrase("Burr Height", font9b));
					headerColumn5Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn5Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn5Cell);

					PdfPCell headerColumn5Cell1 = new PdfPCell(new Phrase("Diagonal Difference", font9b));
					headerColumn5Cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn5Cell1.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn5Cell1);

					PdfPCell headerColumn6Cell = new PdfPCell(new Phrase("Remarks", font9b));
					headerColumn6Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn6Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn6Cell);

					for (QIRPanDetailsJsonArrayChildDTO obj : planDetails.get(0).getCutInspectionData()) {
						PdfPCell headerColumn1CellValue = new PdfPCell(new Phrase("" + obj.getThickness(), font9));
						headerColumn1CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn1CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn1CellValue);

						PdfPCell headerColumn2CellValue = new PdfPCell(new Phrase(obj.getPlannedWidth(), font9));
						headerColumn2CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn2CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn2CellValue);

						PdfPCell headerColumn3CellValue = new PdfPCell(new Phrase(obj.getActualLength(), font9));
						headerColumn3CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn3CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn3CellValue);

						PdfPCell headerColumn3CellValue2 = new PdfPCell(new Phrase(obj.getActualThickness(), font9));
						headerColumn3CellValue2.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn3CellValue2.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn3CellValue2);

						PdfPCell headerColumn3CellValue3 = new PdfPCell(new Phrase(obj.getActualWidth(), font9));
						headerColumn3CellValue3.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn3CellValue3.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn3CellValue3);

						PdfPCell headerColumn3CellValue4 = new PdfPCell(new Phrase(obj.getActualLength(), font9));
						headerColumn3CellValue4.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn3CellValue4.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn3CellValue4);

						PdfPCell headerColumn4CellValue = new PdfPCell(new Phrase(obj.getBurrHeight(), font9));
						headerColumn4CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn4CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn4CellValue);

						PdfPCell headerColumn5CellValue = new PdfPCell(new Phrase(obj.getDiagonalDifference(), font9));
						headerColumn5CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn5CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn5CellValue);

						PdfPCell headerColumn6CellValue = new PdfPCell(new Phrase(obj.getRemarks(), font9));
						headerColumn6CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn6CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn6CellValue);
					}
					document.add(processDetailsTab);
				}
								
				if (planDetails != null && planDetails.get(0) != null 
						&& planDetails.get(0).getFinalInspectionData() != null
						&& planDetails.get(0).getFinalInspectionData().size() > 0) {
					PdfPTable processDetailsTab = new PdfPTable(6);
					processDetailsTab.setWidthPercentage(98);
					processDetailsTab.setWidths(new int[] { 30, 30, 50, 50, 40, 120 });
					
					PdfPCell headingCell = new PdfPCell(new Phrase("Final Inspection Details", font10b));
					headingCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					headingCell.setFixedHeight(22);
					headingCell.setBorder(Rectangle.NO_BORDER);
					headingCell.setColspan(6);
					processDetailsTab.addCell(headingCell);
					
					PdfPCell headerColumn1Cell = new PdfPCell(new Phrase("Instruction Id", font9b));
					headerColumn1Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn1Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn1Cell);

					PdfPCell headerColumn2Cell = new PdfPCell(new Phrase("Slit Size", font9b));
					headerColumn2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn2Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn2Cell);

					PdfPCell headerColumn3Cell = new PdfPCell(new Phrase("Actual Slit Size", font9b));
					headerColumn3Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn3Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn3Cell);

					PdfPCell headerColumn4Cell = new PdfPCell(new Phrase("Actual Thickness", font9b));
					headerColumn4Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn4Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn4Cell);

					PdfPCell headerColumn5Cell = new PdfPCell(new Phrase("Burr Height", font9b));
					headerColumn5Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn5Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn5Cell);

					PdfPCell headerColumn6Cell = new PdfPCell(new Phrase("Remarks", font9b));
					headerColumn6Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn6Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn6Cell);

					for (QIRPanDetailsJsonArrayChildDTO obj : planDetails.get(0).getSlitInspectionData()) {
						PdfPCell headerColumn1CellValue = new PdfPCell(new Phrase("" + obj.getInstructionId(), font9));
						headerColumn1CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn1CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn1CellValue);

						PdfPCell headerColumn2CellValue = new PdfPCell(new Phrase(obj.getPlannedWidth(), font9));
						headerColumn2CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn2CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn2CellValue);

						PdfPCell headerColumn3CellValue = new PdfPCell(new Phrase(obj.getActualWidth(), font9));
						headerColumn3CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn3CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn3CellValue);

						PdfPCell headerColumn4CellValue = new PdfPCell(new Phrase(obj.getActualThickness(), font9));
						headerColumn4CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn4CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn4CellValue);

						PdfPCell headerColumn5CellValue = new PdfPCell(new Phrase(obj.getBurrHeight(), font9));
						headerColumn5CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn5CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn5CellValue);

						PdfPCell headerColumn6CellValue = new PdfPCell(new Phrase(obj.getRemarks(), font9));
						headerColumn6CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn6CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn6CellValue);
					}
					document.add(processDetailsTab);
				}
				
				

				
				if (planDetails != null && planDetails.get(0) != null 
						&& planDetails.get(0).getToleranceInspectionDataSlit() != null
						&& planDetails.get(0).getToleranceInspectionDataSlit().size() > 0) {
					
					PdfPTable processDetailsTab = new PdfPTable(6);
					processDetailsTab.setWidthPercentage(98);
					processDetailsTab.setWidths(new int[] { 40, 40, 40, 40, 40, 40 });
					
					PdfPCell headingCell = new PdfPCell(new Phrase("Slitting Tolerance Data", font10b));
					headingCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					headingCell.setFixedHeight(22);
					headingCell.setBorder(Rectangle.NO_BORDER);
					headingCell.setColspan(6);
					processDetailsTab.addCell(headingCell);
					
					PdfPCell headerColumn1Cell = new PdfPCell(new Phrase("Thickness From", font9b));
					headerColumn1Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn1Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn1Cell);

					PdfPCell headerColumn2Cell = new PdfPCell(new Phrase("Thickness To", font9b));
					headerColumn2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn2Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn2Cell);

					PdfPCell headerColumn3Cell = new PdfPCell(new Phrase("Slit Size From", font9b));
					headerColumn3Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn3Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn3Cell);

					PdfPCell headerColumn4Cell = new PdfPCell(new Phrase("Slit Size To", font9b));
					headerColumn4Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn4Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn4Cell);

					PdfPCell headerColumn5Cell = new PdfPCell(new Phrase("Burr Height From", font9b));
					headerColumn5Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn5Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn5Cell);

					PdfPCell headerColumn6Cell = new PdfPCell(new Phrase("Burr Height To", font9b));
					headerColumn6Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn6Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn6Cell);

					for (QIRPanToleranceChildDTO obj : planDetails.get(0).getToleranceInspectionDataSlit()) {
						PdfPCell headerColumn1CellValue = new PdfPCell(new Phrase("" + obj.getToleranceThicknessFrom(), font9));
						headerColumn1CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn1CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn1CellValue);

						PdfPCell headerColumn2CellValue = new PdfPCell(new Phrase(obj.getToleranceThicknessFrom(), font9));
						headerColumn2CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn2CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn2CellValue);

						PdfPCell headerColumn3CellValue = new PdfPCell(new Phrase(obj.getToleranceSlitSizeFrom(), font9));
						headerColumn3CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn3CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn3CellValue);

						PdfPCell headerColumn4CellValue = new PdfPCell(new Phrase(obj.getToleranceSlitSizeTo(), font9));
						headerColumn4CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn4CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn4CellValue);

						PdfPCell headerColumn5CellValue = new PdfPCell(new Phrase(obj.getToleranceBurrHeightFrom(), font9));
						headerColumn5CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn5CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn5CellValue);

						PdfPCell headerColumn6CellValue = new PdfPCell(new Phrase(obj.getToleranceBurrHeightTo(), font9));
						headerColumn6CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn6CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn6CellValue);
					}
					document.add(processDetailsTab);
				}
				
				
				
				if (planDetails != null && planDetails.get(0) != null 
						&& planDetails.get(0).getToleranceInspectionData() != null
						&& planDetails.get(0).getToleranceInspectionData().size() > 0) {
					
					PdfPTable processDetailsTab = new PdfPTable(10);
					processDetailsTab.setWidthPercentage(98);
					processDetailsTab.setWidths(new int[] { 40, 40, 40, 40, 40, 40 , 40, 40, 40, 40 });
					
					PdfPCell headingCell = new PdfPCell(new Phrase("Cutting Tolerance Data", font10b));
					headingCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					headingCell.setFixedHeight(22);
					headingCell.setBorder(Rectangle.NO_BORDER);
					headingCell.setColspan(10);
					processDetailsTab.addCell(headingCell);
					
					PdfPCell headerColumn1Cell = new PdfPCell(new Phrase("Thickness From", font9b));
					headerColumn1Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn1Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn1Cell);

					PdfPCell headerColumn2Cell = new PdfPCell(new Phrase("Thickness To", font9b));
					headerColumn2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn2Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn2Cell);

					PdfPCell headerColumn3Cell = new PdfPCell(new Phrase("Width From", font9b));
					headerColumn3Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn3Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn3Cell);

					PdfPCell headerColumn4Cell = new PdfPCell(new Phrase("Width To", font9b));
					headerColumn4Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn4Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn4Cell);

					PdfPCell headerColumn5Cell = new PdfPCell(new Phrase("Length From", font9b));
					headerColumn5Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn5Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn5Cell);

					PdfPCell headerColumn6Cell = new PdfPCell(new Phrase("Length To", font9b));
					headerColumn6Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn6Cell.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn6Cell);

					PdfPCell headerColumn5Cell11 = new PdfPCell(new Phrase("Burr Height From", font9b));
					headerColumn5Cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn5Cell11.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn5Cell11);

					PdfPCell headerColumn6Cell22 = new PdfPCell(new Phrase("Burr Height To", font9b));
					headerColumn6Cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn6Cell22.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn6Cell22);

					PdfPCell headerColumn5Cell12 = new PdfPCell(new Phrase("Diagonal Difference From", font9b));
					headerColumn5Cell12.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn5Cell12.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn5Cell12);

					PdfPCell headerColumn6Cell23 = new PdfPCell(new Phrase("Diagonal Difference To", font9b));
					headerColumn6Cell23.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerColumn6Cell23.setFixedHeight(fixedHeight);
					processDetailsTab.addCell(headerColumn6Cell23);

					for (QIRPanToleranceChildDTO obj : planDetails.get(0).getToleranceInspectionData()) {
						
						PdfPCell headerColumn1CellValue = new PdfPCell(new Phrase("" + obj.getToleranceThicknessFrom(), font9));
						headerColumn1CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn1CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn1CellValue);

						PdfPCell headerColumn2CellValue = new PdfPCell(new Phrase(obj.getToleranceThicknessFrom(), font9));
						headerColumn2CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn2CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn2CellValue);

						PdfPCell headerColumn3CellValue = new PdfPCell(new Phrase(obj.getToleranceWidthFrom(), font9));
						headerColumn3CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn3CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn3CellValue);

						PdfPCell headerColumn4CellValue = new PdfPCell(new Phrase(obj.getToleranceWidthTo(), font9));
						headerColumn4CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn4CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn4CellValue);

						PdfPCell headerColumn5CellValue = new PdfPCell(new Phrase(obj.getToleranceLengthFrom(), font9));
						headerColumn5CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn5CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn5CellValue);

						PdfPCell headerColumn6CellValue = new PdfPCell(new Phrase(obj.getToleranceLengthTo(), font9));
						headerColumn6CellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn6CellValue.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn6CellValue);
						
						PdfPCell headerColumn3CellValue1 = new PdfPCell(new Phrase(obj.getToleranceBurrHeightFrom(), font9));
						headerColumn3CellValue1.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn3CellValue1.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn3CellValue1);

						PdfPCell headerColumn4CellValue1 = new PdfPCell(new Phrase(obj.getToleranceBurrHeightTo(), font9));
						headerColumn4CellValue1.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn4CellValue1.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn4CellValue1);

						PdfPCell headerColumn5CellValue1 = new PdfPCell(new Phrase(obj.getToleranceDiagonalDifferenceFrom(), font9));
						headerColumn5CellValue1.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn5CellValue1.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn5CellValue1);

						PdfPCell headerColumn6CellValue1 = new PdfPCell(new Phrase(obj.getToleranceDiagonalDifferenceTo(), font9));
						headerColumn6CellValue1.setHorizontalAlignment(Element.ALIGN_CENTER);
						headerColumn6CellValue1.setFixedHeight(tableRowHeight);
						processDetailsTab.addCell(headerColumn6CellValue1);
					}
					document.add(processDetailsTab);
				}
			}
			
			/* Bill Ship Address starts */
			PdfPTable processDetailsTab = new PdfPTable(4);
			processDetailsTab.setWidthPercentage(95);
			processDetailsTab.setWidths(new int[] { 50, 50 , 50, 50 });
			
			if ("INWARD".equals(entity.getStageName()) || "PRE_PROCESSING".equals(entity.getStageName())) {
				fillInwardPreProcessingStageTable(entity, processDetailsTab, templateDetailsList);
			}
			if ("PROCESSING".equals(entity.getStageName())) {
				fillProcessingStageTable(entity, processDetailsTab, templateDetailsList);
			}
			if ("POST_DISPATCH".equals(entity.getStageName())) {
				fillPostDispatchStageTable(entity, processDetailsTab, templateDetailsList);
			}			
			if ("PRE_DISPATCH".equals(entity.getStageName())) {				
				fillPreDispatchStageTable(entity, processDetailsTab, templateDetailsList);
			}
			PdfPCell commentsCell = new PdfPCell(new Phrase("Comments : ", font11));
			commentsCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			commentsCell.setFixedHeight(fixedHeight);
			commentsCell.setBorder(Rectangle.NO_BORDER);
			processDetailsTab.addCell(commentsCell);			
			PdfPCell commentsValue = new PdfPCell(new Phrase(entity.getComments(), font11));
			commentsValue.setHorizontalAlignment( Element.ALIGN_LEFT);
			commentsValue.setFixedHeight(55);
			commentsValue.setColspan(3);
			commentsValue.setBorder(Rectangle.NO_BORDER);
			processDetailsTab.addCell(commentsValue);
			document.add( Chunk.NEWLINE );
			document.add(processDetailsTab);
			
			/* footerDetailsTab starts */
			PdfPTable footerDetailsTab = new PdfPTable(4);
			footerDetailsTab.setWidthPercentage(95);
			footerDetailsTab.setWidths(new int[] { 50, 50 , 50, 50 });
			
			PdfPCell inspectedByCell  = new PdfPCell(new Phrase("Quality Inspected By ", font11));
			inspectedByCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			inspectedByCell.setVerticalAlignment( Element.ALIGN_BOTTOM);
			inspectedByCell.setFixedHeight(66);
			inspectedByCell.setColspan(2);
			inspectedByCell.setBorder(Rectangle.NO_BORDER);
			footerDetailsTab.addCell(inspectedByCell);
			
			PdfPCell approvedByCell  = new PdfPCell(new Phrase("Quality Approved By ", font11));
			approvedByCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			approvedByCell.setVerticalAlignment( Element.ALIGN_BOTTOM);
			approvedByCell.setFixedHeight(66);
			approvedByCell.setColspan(2);
			approvedByCell.setBorder(Rectangle.NO_BORDER);
			footerDetailsTab.addCell(approvedByCell);
			
			document.add( Chunk.NEWLINE );
			document.add( Chunk.NEWLINE );
			document.add( Chunk.NEWLINE );
			document.add( Chunk.NEWLINE );
			document.add( footerDetailsTab );
			document.close();
			System.out.println("QIR PDF Report generated successfully..!");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex);
		}
		file.deleteOnExit();
		return file;
	}
 
	public static String findFieldValue (List<QIRTemplateDtlsJsonArrayDTO> templateDetailsList, String fieldName) {

		String resp = "No";
		for (QIRTemplateDtlsJsonArrayDTO dto : templateDetailsList) {
			if (fieldName.equals(dto.getType())) {
				resp = ""+dto.getValue();
			}
		}
		return resp;
	}
	
	private void fillInwardPreProcessingStageTable(QualityInspectionReportEntity entity, PdfPTable coilDetailsTab,
			List<QIRTemplateDtlsJsonArrayDTO> templateDetailsList) {
		int fixedHeight=22;
		try {
			
			Font font11 = FontFactory.getFont(BaseFont.WINANSI, 11f);

			if ("PRE_PROCESSING".equals(entity.getStageName())) {
				PdfPCell exactWidthCell = new PdfPCell(new Phrase("Enter Exact Width : ", font11));
				exactWidthCell.setHorizontalAlignment( Element.ALIGN_LEFT);
				exactWidthCell.setFixedHeight(fixedHeight);
				exactWidthCell.setBorder(Rectangle.NO_BORDER);
				coilDetailsTab.addCell(exactWidthCell);
				PdfPCell imageCell = new PdfPCell(new Phrase(findFieldValue(templateDetailsList, "exactWidth"), font11));
				imageCell.setHorizontalAlignment( Element.ALIGN_LEFT);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			}
			if("INWARD".equals(entity.getStageName())) {
				PdfPCell packingIntactCell = new PdfPCell(new Phrase("Packing Intact : "+findFieldValue(templateDetailsList, "packingIntact"), font11));
				packingIntactCell.setHorizontalAlignment( Element.ALIGN_LEFT);
				//packingIntactCell.setFixedHeight(fixedHeight);
				packingIntactCell.setBorder(Rectangle.NO_BORDER);
				coilDetailsTab.addCell(packingIntactCell);
				if (entity.getPackingIntact() != null && entity.getPackingIntact().length() > 0) {
					String imageUrl = awsS3Service.generatePresignedUrl(entity.getPackingIntact());
					Image image = Image.getInstance(new URL(imageUrl));
					PdfPCell imageCell = new PdfPCell(image, true);
					imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
					imageCell.setBorder(Rectangle.NO_BORDER);
					imageCell.setFixedHeight(77);
					imageCell.setColspan(3);
					coilDetailsTab.addCell(imageCell);
				} else {
					PdfPCell imageCell = new PdfPCell(new Phrase("", font11));
					imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
					imageCell.setBorder(Rectangle.NO_BORDER);
					imageCell.setColspan(3);
					coilDetailsTab.addCell(imageCell);
				}
			}
			PdfPCell coilBendCell = new PdfPCell(new Phrase("Coil Bend : "+findFieldValue(templateDetailsList, "coilBend"), font11));
			coilBendCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			//coilBendCell.setFixedHeight(fixedHeight);
			coilBendCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(coilBendCell);
			if (entity.getCoilBend() != null && entity.getCoilBend().length() > 0) {
				String imageUrl = awsS3Service.generatePresignedUrl(entity.getCoilBend());
				Image image = Image.getInstance(new URL(imageUrl));
				PdfPCell imageCell = new PdfPCell(image, true);
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setFixedHeight(77);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			} else {
				PdfPCell imageCell = new PdfPCell(new Phrase("", font11));
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			}
			
			PdfPCell rustObservedCell = new PdfPCell(new Phrase("Rust Observed : "+findFieldValue(templateDetailsList, "rustObserved"), font11));
			rustObservedCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			//rustObservedCell.setFixedHeight(fixedHeight);
			rustObservedCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(rustObservedCell);
			if (entity.getRustObserved() != null && entity.getRustObserved().length() > 0) {
				String imageUrl = awsS3Service.generatePresignedUrl(entity.getRustObserved());
				Image image = Image.getInstance(new URL(imageUrl));
				PdfPCell imageCell = new PdfPCell(image, true);
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setFixedHeight(77);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			} else {
				PdfPCell imageCell = new PdfPCell(new Phrase("", font11));
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			}
			
			PdfPCell safetyIssuesCell = new PdfPCell(new Phrase("Safety Issues : "+findFieldValue(templateDetailsList, "safetyIssues"), font11));
			safetyIssuesCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			//safetyIssuesCell.setFixedHeight(fixedHeight);
			safetyIssuesCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(safetyIssuesCell);
			if (entity.getSafetyIssues() != null && entity.getSafetyIssues().length() > 0) {
				String imageUrl = awsS3Service.generatePresignedUrl(entity.getSafetyIssues());
				Image image = Image.getInstance(new URL(imageUrl));
				PdfPCell imageCell = new PdfPCell(image, true);
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setFixedHeight(77);
				imageCell.setPaddingBottom(1f);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			} else {
				PdfPCell imageCell = new PdfPCell(new Phrase("", font11));
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			}
			
			PdfPCell waterExposureCell = new PdfPCell(new Phrase("Water Exposure : "+findFieldValue(templateDetailsList, "waterExposure"), font11));
			waterExposureCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			//waterExposureCell.setFixedHeight(fixedHeight);
			waterExposureCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(waterExposureCell);
			if (entity.getWaterExposure() != null && entity.getWaterExposure().length() > 0) {
				String imageUrl = awsS3Service.generatePresignedUrl(entity.getWaterExposure());
				Image image = Image.getInstance(new URL(imageUrl));
				PdfPCell imageCell = new PdfPCell(image, true);
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setFixedHeight(77);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			} else {
				PdfPCell imageCell = new PdfPCell(new Phrase("", font11));
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			}

			PdfPCell wireRopeDamagesCell = new PdfPCell(new Phrase("Wire Rope Damages : "+findFieldValue(templateDetailsList, "wireRopeDamages"), font11));
			wireRopeDamagesCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			//wireRopeDamagesCell.setFixedHeight(fixedHeight);
			wireRopeDamagesCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(wireRopeDamagesCell);
			if (entity.getWireRopeDamages() != null && entity.getWireRopeDamages().length() > 0) {
				String imageUrl = awsS3Service.generatePresignedUrl(entity.getWireRopeDamages());
				Image image = Image.getInstance(new URL(imageUrl));
				PdfPCell imageCell = new PdfPCell(image, true);
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setFixedHeight(77);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			} else {
				PdfPCell imageCell = new PdfPCell(new Phrase("", font11));
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			}

			PdfPCell improperStorageCell = new PdfPCell(new Phrase("Improper Storage : "+findFieldValue(templateDetailsList, "improperStorage"), font11));
			improperStorageCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			//wireRopeDamagesCell.setFixedHeight(fixedHeight);
			improperStorageCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(improperStorageCell);
			if (entity.getImproperStorage() != null && entity.getImproperStorage().length() > 0) {
				String imageUrl = awsS3Service.generatePresignedUrl(entity.getImproperStorage());
				Image image = Image.getInstance(new URL(imageUrl));
				PdfPCell imageCell = new PdfPCell(image, true);
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setFixedHeight(77);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			} else {
				PdfPCell imageCell = new PdfPCell(new Phrase("", font11));
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void fillProcessingStageTable(QualityInspectionReportEntity entity, PdfPTable coilDetailsTab,
			List<QIRTemplateDtlsJsonArrayDTO> templateDetailsList) {
		int fixedHeight=22;
		try {
			
			Font font11 = FontFactory.getFont(BaseFont.WINANSI, 11f);

			PdfPCell wastageWeightCell = new PdfPCell(new Phrase("Enter Wastage Weight : ", font11));
			wastageWeightCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			wastageWeightCell.setFixedHeight(fixedHeight);
			wastageWeightCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(wastageWeightCell);
			PdfPCell wastageWeightCellValue = new PdfPCell(new Phrase(findFieldValue(templateDetailsList, "wastageWeight"), font11));
			wastageWeightCellValue.setHorizontalAlignment( Element.ALIGN_LEFT);
			wastageWeightCellValue.setBorder(Rectangle.NO_BORDER);
			wastageWeightCellValue.setColspan(3);
			coilDetailsTab.addCell(wastageWeightCellValue);
			
			PdfPCell packingRequirementsCell = new PdfPCell(new Phrase("Packing Requirements : ", font11));
			packingRequirementsCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			packingRequirementsCell.setFixedHeight(fixedHeight);
			packingRequirementsCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(packingRequirementsCell);
			PdfPCell packingRequirementsValue = new PdfPCell(new Phrase(findFieldValue(templateDetailsList, "packingRequirements"), font11));
			packingRequirementsValue.setHorizontalAlignment( Element.ALIGN_LEFT);
			packingRequirementsValue.setBorder(Rectangle.NO_BORDER);
			packingRequirementsValue.setColspan(3);
			coilDetailsTab.addCell(packingRequirementsValue);
			
			PdfPCell stickersCell = new PdfPCell(new Phrase("Stickers : ", font11));
			stickersCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			stickersCell.setFixedHeight(fixedHeight);
			stickersCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(stickersCell);
			PdfPCell stickersValue = new PdfPCell(new Phrase(findFieldValue(templateDetailsList, "stickers"), font11));
			stickersValue.setHorizontalAlignment( Element.ALIGN_LEFT);
			stickersValue.setBorder(Rectangle.NO_BORDER);
			stickersValue.setColspan(3);
			coilDetailsTab.addCell(stickersValue);
			
			PdfPCell customerApprovalCell = new PdfPCell(new Phrase("Customer Approval : ", font11));
			customerApprovalCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			customerApprovalCell.setFixedHeight(fixedHeight);
			customerApprovalCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(customerApprovalCell);
			PdfPCell customerApprovalValue = new PdfPCell(new Phrase(findFieldValue(templateDetailsList, "customerApproval"), font11));
			customerApprovalValue.setHorizontalAlignment( Element.ALIGN_LEFT);
			customerApprovalValue.setBorder(Rectangle.NO_BORDER);
			customerApprovalValue.setColspan(3);
			coilDetailsTab.addCell(customerApprovalValue);
			
			PdfPCell processingReport1Cell = new PdfPCell(new Phrase("Processing Report1 : ", font11));
			processingReport1Cell.setHorizontalAlignment( Element.ALIGN_LEFT);
			//packingIntactCell.setFixedHeight(fixedHeight);
			processingReport1Cell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(processingReport1Cell);
			if (entity.getProcessingReport1() != null && entity.getProcessingReport1().length() > 0) {
				String imageUrl = awsS3Service.generatePresignedUrl(entity.getProcessingReport1());
				Image image = Image.getInstance(new URL(imageUrl));
				PdfPCell imageCell = new PdfPCell(image, true);
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setFixedHeight(77);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			} else {
				PdfPCell imageCell = new PdfPCell(new Phrase("", font11));
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			}
			
			PdfPCell processingReport2Cell = new PdfPCell(new Phrase("Processing Report2 : ", font11));
			processingReport2Cell.setHorizontalAlignment( Element.ALIGN_LEFT);
			//packingIntactCell.setFixedHeight(fixedHeight);
			processingReport2Cell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(processingReport2Cell);
			if (entity.getProcessingReport2() != null && entity.getProcessingReport2().length() > 0) {
				String imageUrl = awsS3Service.generatePresignedUrl(entity.getProcessingReport2());
				Image image = Image.getInstance(new URL(imageUrl));
				PdfPCell imageCell = new PdfPCell(image, true);
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setFixedHeight(77);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			} else {
				PdfPCell imageCell = new PdfPCell(new Phrase("", font11));
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			}
			
			PdfPCell processingReport3Cell = new PdfPCell(new Phrase("Processing Report3 : ", font11));
			processingReport3Cell.setHorizontalAlignment( Element.ALIGN_LEFT);
			//packingIntactCell.setFixedHeight(fixedHeight);
			processingReport3Cell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(processingReport3Cell);
			if (entity.getProcessingReport3() != null && entity.getProcessingReport3().length() > 0) {
				String imageUrl = awsS3Service.generatePresignedUrl(entity.getProcessingReport3());
				Image image = Image.getInstance(new URL(imageUrl));
				PdfPCell imageCell = new PdfPCell(image, true);
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setFixedHeight(77);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			} else {
				PdfPCell imageCell = new PdfPCell(new Phrase("", font11));
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			}
			
			PdfPCell processingReport4Cell = new PdfPCell(new Phrase("Processing Report4 : ", font11));
			processingReport4Cell.setHorizontalAlignment( Element.ALIGN_LEFT);
			//packingIntactCell.setFixedHeight(fixedHeight);
			processingReport4Cell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(processingReport4Cell);
			if (entity.getProcessingReport4() != null && entity.getProcessingReport4().length() > 0) {
				String imageUrl = awsS3Service.generatePresignedUrl(entity.getProcessingReport4());
				Image image = Image.getInstance(new URL(imageUrl));
				PdfPCell imageCell = new PdfPCell(image, true);
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setFixedHeight(77);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			} else {
				PdfPCell imageCell = new PdfPCell(new Phrase("", font11));
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void fillPostDispatchStageTable(QualityInspectionReportEntity entity, PdfPTable coilDetailsTab,
			List<QIRTemplateDtlsJsonArrayDTO> templateDetailsList) {
		int fixedHeight=22;
		try {

			Font font11 = FontFactory.getFont(BaseFont.WINANSI, 11f);
			PdfPCell improperStorageCell = new PdfPCell(new Phrase("Unloading Improper : "+findFieldValue(templateDetailsList, "unloadingImproper"), font11));
			improperStorageCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			//wireRopeDamagesCell.setFixedHeight(fixedHeight);
			improperStorageCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(improperStorageCell);
			if (entity.getUnloadingImproper() != null && entity.getUnloadingImproper().length() > 0) {
				String imageUrl = awsS3Service.generatePresignedUrl(entity.getUnloadingImproper());
				Image image = Image.getInstance(new URL(imageUrl));
				PdfPCell imageCell = new PdfPCell(image, true);
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setFixedHeight(77);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			} else {
				PdfPCell imageCell = new PdfPCell(new Phrase("", font11));
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			}
			
			PdfPCell packingDamageTransitCell = new PdfPCell(new Phrase("Packing Damage Transit : "+findFieldValue(templateDetailsList, "packingDamageTransit"), font11));
			packingDamageTransitCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			packingDamageTransitCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(packingDamageTransitCell);
			if (entity.getPackingDamageTransit() != null && entity.getPackingDamageTransit().length() > 0) {
				String imageUrl = awsS3Service.generatePresignedUrl(entity.getPackingDamageTransit());
				Image image = Image.getInstance(new URL(imageUrl));
				PdfPCell imageCell = new PdfPCell(image, true);
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setFixedHeight(77);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			} else {
				PdfPCell imageCell = new PdfPCell(new Phrase("", font11));
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			}
			
			PdfPCell ackReceiptCell = new PdfPCell(new Phrase("Acknowledgement Receipt : "+findFieldValue(templateDetailsList, "ackReceipt"), font11));
			ackReceiptCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			ackReceiptCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(ackReceiptCell);
			if (entity.getAckReceipt() != null && entity.getAckReceipt().length() > 0) {
				String imageUrl = awsS3Service.generatePresignedUrl(entity.getAckReceipt());
				Image image = Image.getInstance(new URL(imageUrl));
				PdfPCell imageCell = new PdfPCell(image, true);
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setFixedHeight(77);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			} else {
				PdfPCell imageCell = new PdfPCell(new Phrase("", font11));
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			}
			
			PdfPCell weighmentCell = new PdfPCell(new Phrase("Weighment : "+findFieldValue(templateDetailsList, "weighment"), font11));
			weighmentCell.setHorizontalAlignment( Element.ALIGN_LEFT);
			weighmentCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(weighmentCell);
			if (entity.getWeighment() != null && entity.getWeighment().length() > 0) {
				String imageUrl = awsS3Service.generatePresignedUrl(entity.getWeighment());
				Image image = Image.getInstance(new URL(imageUrl));
				PdfPCell imageCell = new PdfPCell(image, true);
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setFixedHeight(77);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			} else {
				PdfPCell imageCell = new PdfPCell(new Phrase("", font11));
				imageCell.setHorizontalAlignment( Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void fillPreDispatchStageTable(QualityInspectionReportEntity entity, PdfPTable coilDetailsTab,
			List<QIRTemplateDtlsJsonArrayDTO> templateDetailsList) {
		int fixedHeight=22;
		try {

			Font font11 = FontFactory.getFont(BaseFont.WINANSI, 11f);

			PdfPCell packingConditionCell = new PdfPCell(new Phrase("Packing Condition : "+findFieldValue(templateDetailsList, "packingCondition"), font11));
			packingConditionCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			packingConditionCell.setFixedHeight(fixedHeight);
			packingConditionCell.setBorder(Rectangle.NO_BORDER);
			packingConditionCell.setColspan(4);
			coilDetailsTab.addCell(packingConditionCell);

			PdfPCell strappingCell = new PdfPCell(new Phrase("Strapping : "+findFieldValue(templateDetailsList, "strapping"), font11));
			strappingCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			strappingCell.setFixedHeight(fixedHeight);
			strappingCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(strappingCell);
			if (entity.getStrapping() != null && entity.getStrapping().length() > 0) {
				String imageUrl = awsS3Service.generatePresignedUrl(entity.getStrapping());
				Image image = Image.getInstance(new URL(imageUrl));
				PdfPCell imageCell = new PdfPCell(image, true);
				imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setFixedHeight(77);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			} else {
				PdfPCell imageCell = new PdfPCell(new Phrase("", font11));
				imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			}

			PdfPCell weighmentSlipCell = new PdfPCell(new Phrase("Weighment Slip : ", font11));
			weighmentSlipCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			weighmentSlipCell.setFixedHeight(fixedHeight);
			weighmentSlipCell.setBorder(Rectangle.NO_BORDER);
			coilDetailsTab.addCell(weighmentSlipCell);
			if (entity.getWeighmentSlip() != null && entity.getWeighmentSlip().length() > 0) {
				String imageUrl = awsS3Service.generatePresignedUrl(entity.getWeighmentSlip());
				Image image = Image.getInstance(new URL(imageUrl));
				PdfPCell imageCell = new PdfPCell(image, true);
				imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setFixedHeight(77);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			} else {
				PdfPCell imageCell = new PdfPCell(new Phrase("", font11));
				imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setColspan(3);
				coilDetailsTab.addCell(imageCell);
			}
			
			PdfPCell properLoadingCell = new PdfPCell(new Phrase("Proper Loading : "+findFieldValue(templateDetailsList, "properLoading"), font11));
			properLoadingCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			properLoadingCell.setFixedHeight(fixedHeight);
			properLoadingCell.setBorder(Rectangle.NO_BORDER);
			properLoadingCell.setColspan(4);
			coilDetailsTab.addCell(properLoadingCell);
			
			PdfPCell byndingTyingCell = new PdfPCell(new Phrase("Binding & Tying in vehicle : "+findFieldValue(templateDetailsList, "byndingTying"), font11));
			byndingTyingCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			byndingTyingCell.setFixedHeight(fixedHeight);
			byndingTyingCell.setBorder(Rectangle.NO_BORDER);
			byndingTyingCell.setColspan(4);
			coilDetailsTab.addCell(byndingTyingCell);
			
			PdfPCell weighmentQtyMatchCell = new PdfPCell(new Phrase("Weighment Qty Matches with Invoice : "+findFieldValue(templateDetailsList, "weighmentQtyMatch"), font11));
			weighmentQtyMatchCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			weighmentQtyMatchCell.setFixedHeight(fixedHeight);
			weighmentQtyMatchCell.setBorder(Rectangle.NO_BORDER);
			weighmentQtyMatchCell.setColspan(4);
			coilDetailsTab.addCell(weighmentQtyMatchCell); 

			PdfPCell ewayBillMatchCell = new PdfPCell(new Phrase("Weighment Qty Matches with Invoice : "+findFieldValue(templateDetailsList, "ewayBillMatch"), font11));
			ewayBillMatchCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			ewayBillMatchCell.setFixedHeight(fixedHeight);
			ewayBillMatchCell.setBorder(Rectangle.NO_BORDER);
			ewayBillMatchCell.setColspan(4);
			coilDetailsTab.addCell(ewayBillMatchCell); 
			
			PdfPCell labelsMatchCell = new PdfPCell(new Phrase("Labels & Stickers Matche with Invoice : "+findFieldValue(templateDetailsList, "labelsMatch"), font11));
			labelsMatchCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			labelsMatchCell.setFixedHeight(fixedHeight);
			labelsMatchCell.setBorder(Rectangle.NO_BORDER);
			labelsMatchCell.setColspan(4);
			coilDetailsTab.addCell(labelsMatchCell); 
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public File labelPrint(LabelPrintDTO labelPrintDTO) {

		File file = null;
		try {
			file = renderLabelPrintPDF(labelPrintDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	
	private File renderLabelPrintPDF(LabelPrintDTO labelPrintDTO) throws IOException, DocumentException {
		//File file = File.createTempFile("qirpdf_" +entity.getStageName()+"_"+ entity.getQirId(), ".pdf");
		File file = new File("E:/LabelPrint.pdf");
		Document document = new Document();

		int tableRowHeight = 20;
		int tableqrcodeRowHeight = 23;
		try {
			
			QRCodeResponse resp = inwdEntrySvc.getQRCodeDetails(Integer.parseInt(labelPrintDTO.getId()));
			
			Rectangle myPagesize = new Rectangle (284, 213);
			FileOutputStream fos = new FileOutputStream(file);
			document = new Document(myPagesize, 2f, 2f, 3f, 2f);

			PdfWriter pdfWriter = PdfWriter.getInstance(document, fos);
			pdfWriter.setBoxSize("art", myPagesize);
			document.open();

			Font font6 = FontFactory.getFont(BaseFont.WINANSI, 6f);
			Font font7b = FontFactory.getFont(BaseFont.WINANSI, 7f, Font.BOLD);
			Font font11b = FontFactory.getFont(BaseFont.WINANSI, 11f, Font.BOLD);
			Font font12b = FontFactory.getFont(BaseFont.WINANSI, 12f, Font.BOLD);
			
			PdfPTable coilDetailsTab = new PdfPTable(2);
			coilDetailsTab.setWidthPercentage(100);
			coilDetailsTab.setWidths(new int[] { 99, 99});

			PdfPCell companyNameCell = new PdfPCell(new Phrase("ASPEN STEEL PVT LTD", font12b));
			companyNameCell.setHorizontalAlignment( Element.ALIGN_CENTER );
			//companyNameCell.setFixedHeight(17);
			companyNameCell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
			companyNameCell.setColspan(2);
			coilDetailsTab.addCell(companyNameCell);	

			PdfPCell address1Cell = new PdfPCell(new Phrase("Plot No. 16E Phase 2, Sector 1, Bidadi. Ramnagar 562109", font6));
			address1Cell.setHorizontalAlignment( Element.ALIGN_CENTER);
			address1Cell.setVerticalAlignment( Element.ALIGN_TOP);
			//address1Cell.setFixedHeight(8);
			address1Cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			address1Cell.setColspan(2);
			coilDetailsTab.addCell(address1Cell);	

			PdfPCell addressCell2 = new PdfPCell(new Phrase("Email : aspen.bidadi@gmail.com", font6));
			addressCell2.setHorizontalAlignment( Element.ALIGN_CENTER);
			addressCell2.setVerticalAlignment( Element.ALIGN_TOP);
			//addressCell2.setFixedHeight(8);
			addressCell2.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
			addressCell2.setColspan(2);
			coilDetailsTab.addCell(addressCell2);	
			
			 // Generate barcode using ZXing
            BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;
            int barcodeHeight = 17;
            Barcode barcode = new Barcode128();
            barcode.setCode(labelPrintDTO.getId());
			barcode.setCodeType(Barcode.CODE128);
			barcode.setBarHeight(barcodeHeight);
			barcode.setX(1.0f); // Adjust the width of the barcode bars
 			Image barcodeImage = barcode.createImageWithBarcode(pdfWriter.getDirectContent(), null, null);
 			
            Paragraph tagNoParagraph = new Paragraph();
            tagNoParagraph.add(new Phrase(new Chunk("Tag No (Inward No):", font7b)));
			tagNoParagraph.add(new Phrase(new Chunk(labelPrintDTO.getId(), font11b)));
			PdfPCell companyNameCell1 = new PdfPCell(tagNoParagraph);	
			//PdfPCell companyNameCell1 = new PdfPCell(barcodeImage);			
			companyNameCell1.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell1.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell1.setFixedHeight(tableRowHeight);
			coilDetailsTab.addCell(companyNameCell1);	

			PdfPCell companyNameCell2 = new PdfPCell(new Phrase(resp.getPartyName(), font7b));
			companyNameCell2.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell2.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell2.setFixedHeight(tableRowHeight);
			coilDetailsTab.addCell(companyNameCell2);	

			Paragraph coilParagraph = new Paragraph();
			coilParagraph.add(new Phrase(new Chunk("A Coil No: ", font7b)));
			coilParagraph.add(new Phrase(new Chunk(resp.getCoilNo(), font11b)));
			PdfPCell companyNameCell3 = new PdfPCell(coilParagraph);
			companyNameCell3.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell3.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell3.setFixedHeight(tableRowHeight);
			coilDetailsTab.addCell(companyNameCell3);	

			Paragraph specParagraph = new Paragraph();
			specParagraph.add(new Phrase(new Chunk("SPEC:  ", font7b)));
			specParagraph.add(new Phrase(new Chunk(resp.getMaterialDesc(), font11b)));
			PdfPCell companyNameCell4 = new PdfPCell(specParagraph);
			companyNameCell4.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell4.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell4.setFixedHeight(tableRowHeight);
			coilDetailsTab.addCell(companyNameCell4);		

			Paragraph mcoilParagraph = new Paragraph();
			mcoilParagraph.add(new Phrase(new Chunk("M Coil No:  ", font7b)));
			mcoilParagraph.add(new Phrase(new Chunk(resp.getCoilNo(), font11b)));			
			PdfPCell companyNameCell5 = new PdfPCell(mcoilParagraph);
			companyNameCell5.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell5.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell5.setFixedHeight(tableRowHeight);
			coilDetailsTab.addCell(companyNameCell5);	

			Paragraph gradeParagraph = new Paragraph();
			gradeParagraph.add(new Phrase(new Chunk("GRADE:  ", font7b)));
			gradeParagraph.add(new Phrase(new Chunk(resp.getMaterialGrade(), font11b)));	
			PdfPCell companyNameCell6 = new PdfPCell(gradeParagraph);
			companyNameCell6.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell6.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell6.setFixedHeight(tableRowHeight);
			coilDetailsTab.addCell(companyNameCell6);		
			
			Paragraph measurementParagraph = new Paragraph();
			measurementParagraph.add(new Phrase(new Chunk("T:  ", font7b)));
			measurementParagraph.add(new Phrase(new Chunk(resp.getFthickness()+", ", font7b)));
			measurementParagraph.add(new Phrase(new Chunk("W:  ", font7b)));
			measurementParagraph.add(new Phrase(new Chunk(resp.getFwidth()+", ", font7b)));
			measurementParagraph.add(new Phrase(new Chunk("L:  ", font7b)));
			measurementParagraph.add(new Phrase(new Chunk(resp.getFlength(), font7b)));			
			PdfPCell companyNameCell7 = new PdfPCell(measurementParagraph);
			companyNameCell7.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell7.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell7.setFixedHeight(tableRowHeight);
			coilDetailsTab.addCell(companyNameCell7);			

			Paragraph dateParagraph = new Paragraph();
			dateParagraph.add(new Phrase(new Chunk("DATE:  ", font7b)));
			dateParagraph.add(new Phrase(new Chunk(resp.getReceivedDate(), font11b)));	
			PdfPCell companyNameCell8 = new PdfPCell(dateParagraph);
			companyNameCell8.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell8.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell8.setFixedHeight(tableRowHeight);
			coilDetailsTab.addCell(companyNameCell8);		
			
			Paragraph grosswtParagraph = new Paragraph();
			grosswtParagraph.add(new Phrase(new Chunk("Gross WT (kgs):  ", font7b)));
			grosswtParagraph.add(new Phrase(new Chunk(resp.getGrossWeight(), font11b)));
			PdfPCell companyNameCell9 = new PdfPCell(grosswtParagraph);
			companyNameCell9.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell9.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell9.setFixedHeight(tableqrcodeRowHeight);
			coilDetailsTab.addCell(companyNameCell9);	
			
			byte[] imageBytes = qrcode(resp);
			Image qrCodeImage = Image.getInstance(imageBytes);
			PdfPCell companyNameCell13 = new PdfPCell(qrCodeImage);
			companyNameCell13.setHorizontalAlignment( Element.ALIGN_CENTER);
			companyNameCell13.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell13.setRowspan(4);
			companyNameCell9.setFixedHeight(90);
			coilDetailsTab.addCell(companyNameCell13);		

			Paragraph netwtParagraph = new Paragraph();
			netwtParagraph.add(new Phrase(new Chunk("Net WT (kgs):  ", font7b)));
			netwtParagraph.add(new Phrase(new Chunk(resp.getNetWeight(), font11b)));
			PdfPCell companyNameCell10 = new PdfPCell(netwtParagraph);
			companyNameCell10.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell10.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell10.setFixedHeight(tableqrcodeRowHeight);
			coilDetailsTab.addCell(companyNameCell10);			

			Paragraph coilbatchParagraph = new Paragraph();
			coilbatchParagraph.add(new Phrase(new Chunk("COIL BATCH ID:  ", font7b)));
			coilbatchParagraph.add(new Phrase(new Chunk(resp.getCustomerBatchNo(), font11b)));
			PdfPCell companyNameCell11 = new PdfPCell(coilbatchParagraph);
			companyNameCell11.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell11.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell11.setFixedHeight(tableqrcodeRowHeight);
			coilDetailsTab.addCell(companyNameCell11);			

			Paragraph noofpcsParagraph = new Paragraph();
			noofpcsParagraph.add(new Phrase(new Chunk("NO OF PCS:  ", font7b)));
			noofpcsParagraph.add(new Phrase(new Chunk("1 COIL", font11b)));
			PdfPCell companyNameCell12 = new PdfPCell(noofpcsParagraph);
			companyNameCell12.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell1.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell12.setFixedHeight(tableqrcodeRowHeight);
			coilDetailsTab.addCell(companyNameCell12);
			document.add( coilDetailsTab );
			document.close();
			System.out.println("LAbel Print generated successfully..!");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex);
		}
		file.deleteOnExit();
		return file;
	}
 
	
	private byte[] qrcode(QRCodeResponse resp) {
		byte[] pngData = null;

		try {
			StringBuilder text = new StringBuilder();
			text.append("Coil NO : " + resp.getCoilNo());
			text.append("\nCustomer BatchNo : " + resp.getCustomerBatchNo());
			text.append("\nMaterial Type : " + resp.getMaterialDesc());
			text.append("\nMaterial Grade : " + resp.getMaterialGrade());
			text.append("\nThickness : " + resp.getFthickness());
			text.append("\nWidth : " + resp.getFwidth());
			text.append("\nNet Weight : " + resp.getNetWeight());
			text.append("\nGross Weight : " + resp.getGrossWeight());
			// pngData = pdfGenerator.getQRCode(text.toString(), 0, 0);

			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix bitMatrix = qrCodeWriter.encode(text.toString(), BarcodeFormat.QR_CODE, 50, 80);

			ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
			MatrixToImageConfig con = new MatrixToImageConfig();
			MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream, con);
			pngData = pngOutputStream.toByteArray();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return pngData;
	}
}
