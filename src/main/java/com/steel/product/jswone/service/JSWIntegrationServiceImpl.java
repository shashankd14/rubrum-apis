package com.steel.product.jswone.service;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steel.product.application.dao.DeliveryDetailsRepository;
import com.steel.product.application.dao.InstructionRepository;
import com.steel.product.application.dao.InwardEntryRepository;
import com.steel.product.application.dto.delivery.DeliveryDto;
import com.steel.product.application.dto.quality.ListPageSearchRequest;
import com.steel.product.application.service.AWSS3Service;
import com.steel.product.application.util.CommonUtil;
import com.steel.product.jswone.entity.JswoneAuditTrailEntity;
import com.steel.product.jswone.entity.MaterialMasterFileDataEntity;
import com.steel.product.jswone.entity.MaterialMasterJswEntity;
import com.steel.product.jswone.entity.POReceiveDetailsEntity;
import com.steel.product.jswone.entity.POWiseMmidDetailsEntity;
import com.steel.product.jswone.entity.SOReceiveDetailsEntity;
import com.steel.product.jswone.entity.WarehouseMasterJswEntity;
import com.steel.product.jswone.repository.JswoneAuditTrailRepository;
import com.steel.product.jswone.repository.MaterialMasterFiledataRepository;
import com.steel.product.jswone.repository.MaterialMasterJswRepository;
import com.steel.product.jswone.repository.POReceiveDetailsRepository;
import com.steel.product.jswone.repository.POWiseMmidDetailsRepository;
import com.steel.product.jswone.repository.SOReceiveDetailsRepository;
import com.steel.product.jswone.repository.WarehouseMasterRepository;
import com.steel.product.jswone.request.ApiResponse;
import com.steel.product.jswone.request.MMIDReceiveMainRequest;
import com.steel.product.jswone.request.MaterialMasterFileDataDTO;
import com.steel.product.jswone.request.POSOIntegrationRequest;
import com.steel.product.jswone.response.DC_InventoryAdjustmentLineItem;
import com.steel.product.jswone.response.DC_InventoryAdjustmentMainRequest;
import com.steel.product.jswone.response.FromSku;
import com.steel.product.jswone.response.InventoryAdjustmentBatch;
import com.steel.product.jswone.response.InventoryAdjustmentResponse;
import com.steel.product.jswone.response.PODetailsLineItemResponse;
import com.steel.product.jswone.response.PODetailsMainResponse;
import com.steel.product.jswone.response.POInvoiceListChildResponse;
import com.steel.product.jswone.response.POInvoiceListResponse;
import com.steel.product.jswone.response.POWiseInwardListMainResponse;
import com.steel.product.jswone.response.POWiseInwardListResponse;
import com.steel.product.jswone.response.PoGrnCustomType;
import com.steel.product.jswone.response.PoGrnLineItem;
import com.steel.product.jswone.response.PoGrnLineItemBatches;
import com.steel.product.jswone.response.PoGrnMainRequest;
import com.steel.product.jswone.response.PoGrnMainResponse;
import com.steel.product.jswone.response.PtQuantity;
import com.steel.product.jswone.response.ToSku;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class JSWIntegrationServiceImpl implements JSWIntegrationService {

	@Autowired
	private POReceiveDetailsRepository poReceiveDetailsRepository;
	
	@Autowired
	private JswoneAuditTrailRepository jswoneAuditTrailRepository;

	@Autowired
	private SOReceiveDetailsRepository soReceiveDetailsRepository;

	@Autowired
	private WarehouseMasterRepository warehouseMasterRepository;

	@Autowired
	private MaterialMasterFiledataRepository repository;

	@Autowired
	private MaterialMasterJswRepository materialMasterRepository;

	@Autowired
	private MaterialUploadService materialUploadService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private POWiseMmidDetailsRepository powseMmidDetailsRepository;

	@Autowired
	private InwardEntryRepository inwardEntryRepository;

	@Autowired
	private DeliveryDetailsRepository deliveryDetailsRepository;
	
	@Autowired
	private InstructionRepository instructionRepository;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	AWSS3Service awsS3Service;

	@Value("${email.gcpreportspath}")
	private String gcpReportsPath;
	
	@Override
	public ResponseEntity<Object> poReceive(POSOIntegrationRequest request) {
		log.info("******JSWIntegrationServiceImpl.poReceive*****");
		POReceiveDetailsEntity entity = new POReceiveDetailsEntity();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		log.info("obj.getReqObj() == " + request);
		String message = "PO details saved successfully";
		List<String> errorList = new ArrayList<>();
		try {
			if (!(request.getPoReference() != null && request.getPoReference().length() > 0)) {
				errorList.add("PoReference");
			}
			if (request.getWarehouseId() != null && request.getWarehouseId().length() > 0) {
				List<WarehouseMasterJswEntity> duplentity = warehouseMasterRepository
						.findByWareHouseId(request.getWarehouseId());
				if (!(duplentity != null && duplentity.size() > 0)) {
					errorList.add("WarehouseId details not available");
				}
			} else {
				errorList.add("WarehouseId");
			}
			if (!(request.getStatus() != null && request.getStatus().length() > 0)) {
				errorList.add("Status");
			}
			if (!(request.getPoId() != null && request.getPoId().length() > 0)) {
				errorList.add("PoId");
			}
			if (errorList != null && errorList.size() > 0) {
				return new ResponseEntity<Object>(
						"{\"code\": \"6024\",\"message\":\" Invalid Params\", \"error_info\":\""
								+ String.join(", ", errorList) + "\"}",
						headers, HttpStatus.BAD_REQUEST);
			}

			POReceiveDetailsEntity duplentity = poReceiveDetailsRepository.findByPoReference(request.getPoReference());
			if (duplentity != null && duplentity.getId() > 0) {
				entity.setId(duplentity.getId());
				message = "PO details updated successfully";
				entity.setUpdatedOn(new Date());
				entity.setCreatedOn(duplentity.getCreatedOn());
			} else {
				entity.setCreatedOn(new Date());
			}
			entity.setPoReference(request.getPoReference());
			entity.setWarehouseId(request.getWarehouseId());
			entity.setPoId(request.getPoId());
			entity.setPoStatus(request.getStatus());
			entity.setIpAddress(request.getIpAddress());
			entity = poReceiveDetailsRepository.save(entity);
			return new ResponseEntity<Object>(
					"{\"code\": \"0\",\"message\":\"" + message + "\", \"referenceNo\":\"" + entity.getId() + "\"}",
					headers, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(
					"{\"status\": \"fail\",\"message\":\"" + e.getMessage() + "\", \"referenceNo\":\"\"}",
					HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<Object> soReceive(POSOIntegrationRequest request) {
		log.info("******JSWIntegrationServiceImpl.soReceive*****");
		SOReceiveDetailsEntity entity = new SOReceiveDetailsEntity();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		log.info("obj.getReqObj() == " + request);
		String message = "SO details received successfully";
		List<String> errorList = new ArrayList<>();
		try {
			if (!(request.getSoNo() != null && request.getSoNo().length() > 0)) {
				errorList.add("SoNo");
			}
			if (request.getWarehouseId() != null && request.getWarehouseId().length() > 0) {
				List<WarehouseMasterJswEntity> duplentity = warehouseMasterRepository
						.findByWareHouseId(request.getWarehouseId());
				if (!(duplentity != null && duplentity.size() > 0)) {
					errorList.add("WarehouseId details not available");
				}
			} else {
				errorList.add("WarehouseId");
			}
			if (!(request.getStatus() != null && request.getStatus().length() > 0)) {
				errorList.add("Status");
			}
			if (!(request.getSoId() != null && request.getSoId().length() > 0)) {
				errorList.add("SoId");
			}
			if (errorList != null && errorList.size() > 0) {
				return new ResponseEntity<Object>(
						"{\"code\": \"6024\",\"message\":\" Invalid Params\", \"error_info\":\""
								+ String.join(", ", errorList) + "\"}",
						headers, HttpStatus.BAD_REQUEST);
			}

			SOReceiveDetailsEntity duplentity = soReceiveDetailsRepository.findBySoNo(request.getPoReference());
			if (duplentity != null && duplentity.getId() > 0) {
				entity.setId(duplentity.getId());
				message = "SO details received successfully";
				entity.setUpdatedOn(new Date());
				entity.setCreatedOn(duplentity.getCreatedOn());
			} else {
				entity.setCreatedOn(new Date());
			}
			entity.setSoNo(request.getSoNo());
			entity.setWarehouseId(request.getWarehouseId());
			entity.setSoId(request.getSoId());
			entity.setSoStatus(request.getStatus());
			entity.setIpAddress(request.getIpAddress());
			entity = soReceiveDetailsRepository.save(entity);
			return new ResponseEntity<Object>(
					"{\"code\": \"0\",\"message\":\"" + message + "\", \"referenceNo\":\"" + entity.getId() + "\"}",
					headers, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(
					"{\"status\": \"fail\",\"message\":\"" + e.getMessage() + "\", \"referenceNo\":\"\"}",
					HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	@Transactional
	public ResponseEntity<Object> mmidreceive(MMIDReceiveMainRequest request) {

		log.info("******JSWIntegrationServiceImpl.mmidreceive*****");
		String message = "MMID details saved successfully.";
		try {

			String jsonReq = objectMapper.writeValueAsString(request);
			log.info("full req is : " + jsonReq);

			MaterialMasterJswEntity destEntity = new MaterialMasterJswEntity();
			if (request.getData().getMmid() != null && request.getData().getMmid().length() > 0) {

				MaterialMasterFileDataDTO dummy = new MaterialMasterFileDataDTO();
				dummy.setMmId(request.getData().getMmid());
				dummy.setMmDescription(request.getData().getVariant().getMaterial_info());
				dummy.setCategory(request.getData().getCategory().getMaster_category());
				dummy.setSubcategory(request.getData().getCategory().getSub_category());
				dummy.setBrand(request.getData().getProduct().getBrand());
				dummy.setLeafcategory(request.getData().getCategory().getLeaf_category());
				dummy.setForm(request.getData().getCategory().getForm());
				dummy.setProducttype(request.getData().getCategory().getProduct_type());
				dummy.setGrade(request.getData().getProduct().getGrade());
				dummy.setSubgrade(request.getData().getProduct().getSub_grade());
				dummy.setThickness(request.getData().getVariant().getThickness());
				dummy.setWidth(request.getData().getVariant().getWidth());
				dummy.setLength(request.getData().getVariant().getLength());
				dummy.setCoatingtype(request.getData().getVariant().getCoating_grade_gsm());
				dummy.setUom(request.getData().getCategory().getUom().get(0).getName());
				dummy.setHsn(request.getData().getVariant().getHsn());
				dummy.setVariantKey(request.getData().getVariant().getVariant_key());
				dummy.setTax(request.getData().getVariant().getTax());
				MaterialMasterFileDataEntity sourceEntity = new MaterialMasterFileDataEntity();
				BeanUtils.copyProperties(dummy, sourceEntity);
				sourceEntity.setFilename("Zoho_Integration");
				sourceEntity.setCreatedOn(new Date());
				sourceEntity.setMmidStatus(request.getData().getVariant().getStatus());

				MaterialMasterFileDataEntity dummyEntity = repository.findFirstByMmId(sourceEntity.getMmId());
				if (dummyEntity != null && dummyEntity.getMateraiId() > 0) {
					sourceEntity.setMateraiId(dummyEntity.getMateraiId());
				}
				repository.save(sourceEntity);
				log.info("MMID saved into jsw_material_file_data table ");

				BeanUtils.copyProperties(sourceEntity, destEntity);
				MaterialMasterJswEntity oldEntity = materialMasterRepository.findFirstByMmId(sourceEntity.getMmId());

				if (oldEntity != null && oldEntity.getMaterialId() > 0) {
					destEntity.setMaterialId(oldEntity.getMaterialId());
					message = "MMID details updated successfully.";
				}
				if (sourceEntity.getLength() != null && sourceEntity.getLength().length() > 0) {
					destEntity.setLength(new BigDecimal(sourceEntity.getLength()));
				} else {
					destEntity.setLength(BigDecimal.ZERO);
				}
				if (sourceEntity.getWidth() != null && sourceEntity.getWidth().length() > 0) {
					destEntity.setWidth(new BigDecimal(sourceEntity.getWidth()));
				} else {
					destEntity.setWidth(BigDecimal.ZERO);
				}
				if (sourceEntity.getThickness() != null && sourceEntity.getThickness().length() > 0) {
					destEntity.setThickness(new BigDecimal(sourceEntity.getThickness()));
				} else {
					destEntity.setThickness(BigDecimal.ZERO);
				}
				if (sourceEntity.getODiameter() != null && sourceEntity.getODiameter().length() > 0) {
					destEntity.setODiameter(new BigDecimal(sourceEntity.getODiameter()));
				} else {
					destEntity.setODiameter(BigDecimal.ZERO);
				}
				if (sourceEntity.getNb() != null && sourceEntity.getNb().length() > 0) {
					destEntity.setNb(new BigDecimal(sourceEntity.getNb()));
				} else {
					destEntity.setNb(BigDecimal.ZERO);
				}
				if (sourceEntity.getIDiameter() != null && sourceEntity.getIDiameter().length() > 0) {
					destEntity.setIDiameter(new BigDecimal(sourceEntity.getIDiameter()));
				} else {
					destEntity.setIDiameter(BigDecimal.ZERO);
				}

				if (!(sourceEntity.getBrand() != null && sourceEntity.getBrand().length() > 0)) {
					sourceEntity.setBrand("UnBrand");
				}
				// Brand Master
				destEntity.setCategoryId(materialUploadService.setCategoryMaster(sourceEntity.getCategory()));
				destEntity.setSubcategoryId(materialUploadService.setSubCategoryMaster(sourceEntity.getSubcategory(),
						destEntity.getCategoryId()));
				destEntity.setLeafcategoryId(materialUploadService.setLeafCategoryMaster(sourceEntity.getLeafcategory(),
						destEntity.getSubcategoryId()));
				destEntity.setBrandId(materialUploadService.setBrandNameMaster(sourceEntity.getBrand(),
						destEntity.getLeafcategoryId()));
				// Product Master
				destEntity.setProducttypeId(
						materialUploadService.setProductMaster(sourceEntity.getProducttype(), destEntity));
				destEntity.setGradeId(
						materialUploadService.setGradeMaster(sourceEntity.getGrade(), destEntity.getProducttypeId()));
				destEntity.setSubgradeId(
						materialUploadService.setSubGradeMaster(sourceEntity.getSubgrade(), destEntity.getGradeId()));
				destEntity.setCoatingtypeId(materialUploadService.setCoatingtypeMaster(sourceEntity.getCoatingtype(),
						destEntity.getProducttypeId()));
				destEntity.setSurfacetypeId(materialUploadService.setSurfacetypeMaster(sourceEntity.getSurfacetype(),
						destEntity.getProducttypeId()));
				destEntity.setUomId(
						materialUploadService.setUomMaster(sourceEntity.getUom(), destEntity.getProducttypeId()));
				destEntity.setFormId(
						materialUploadService.setFormMaster(sourceEntity.getForm(), destEntity.getProducttypeId()));
				try {
					destEntity = materialMasterRepository.save(destEntity);
				} catch (Exception e) {
					log.info("error while save --  " + e.getMessage());
				}
			} else {
				return new ResponseEntity<Object>("{\"code\": \"6024\", \"message\": \"Please enter valid MMID\"}",
						new HttpHeaders(), HttpStatus.BAD_REQUEST);
			}

			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			String json = objectMapper.writeValueAsString(destEntity);
			ApiResponse response = new ApiResponse("0", message, objectMapper.readValue(json, Map.class));
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			// e.printStackTrace();
			return new ResponseEntity<Object>("{\"code\": \"404\", \"message\": \"Failed to save the MMID details\"}",
					new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public List<Object[]> locationwisePOList(POSOIntegrationRequest request) {
		List<Object[]> locationwisePOList = poReceiveDetailsRepository.locationwisePOList(request.getLocationId());
		return locationwisePOList;
	}

	@Override
	public List<Object[]> locationwiseSOList(POSOIntegrationRequest request) {
		List<Object[]> locationwisePOList = soReceiveDetailsRepository.locationwiseSOList(request.getLocationId());
		return locationwisePOList;
	}

	@Override
	public PODetailsMainResponse podetails(POSOIntegrationRequest requ) {
		PODetailsMainResponse response = new PODetailsMainResponse();
		JswoneAuditTrailEntity kk =new JswoneAuditTrailEntity();
		ObjectMapper mapper = new ObjectMapper();
		try {
			RestTemplate restTemplate = new RestTemplate();
			Map<String, String> propertyMap = commonUtil.getAllProperties();

			kk.setPoId(requ.getPoId());
			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");
			headers.set("Authorization", propertyMap.get("podetails_Authorization"));
			HttpEntity<String> request = new HttpEntity<>("{}", headers);
			String url = propertyMap.get("podetails_url") + "?purchaseorder_id=" + requ.getPoId();
			log.info("request is  == " + request + ", url - " + url);
			kk.setRequestObj("");
			kk.setProcessType("PO_DETAILS");
			kk.setRequestUrl(url);
			ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
			log.info("response is == " + res);
			kk.setDestinationResponse(res.getBody().toString());
			if (res.getBody() != null) {
				ObjectMapper om = new ObjectMapper();
				om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				response = om.readValue(res.getBody().toString(), PODetailsMainResponse.class);
			}
			if (response != null && response.getPurchaseorder() != null && response.getPurchaseorder().getLine_items() != null) {
				createPODetails(response);
				response.setCode("0");
				response.setMessage("Success");
				kk.setStatusCode("200");
			} else {
				response.setCode("1002");
				response.setMessage("Resource does not exist.");
			}
			jswoneAuditTrailRepository.save(kk);
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			String error = ex.getResponseBodyAsString();
			kk.setDestinationResponse( error);
			try {
				JsonNode outer = mapper.readTree(error);
				String outerMessage = outer.path("message").asText();
				int start = outerMessage.indexOf("{");
				int end = outerMessage.lastIndexOf("}");
				if (start != -1 && end != -1 && end > start) {
					String innerJson = outerMessage.substring(start, end + 1);
					// Parse the inner JSON
					JsonNode inner = mapper.readTree(innerJson);
					String code = inner.path("code").asText();
					String message = inner.path("message").asText();
					response.setCode(code);
					response.setMessage(message);
					kk.setSourceRespone( mapper.writeValueAsString(response));
				} else {
					log.info("No inner JSON found in message");
				}
			} catch (Exception w) {
				
			}
			kk.setStatusCode("" + ex.getStatusCode().value());
		} catch (Exception e) {
			log.info("Error response is == " + e.getMessage());
            kk.setDestinationResponse( e.getMessage());
			if (e.getMessage().contains("404")) {
				kk.setStatusCode("404");
				response.setCode("1002");
				response.setMessage("Resource does not exist.");
			}
			if (e.getMessage().contains("400")) {
				kk.setStatusCode("400");
				response.setCode("4198");
				response.setMessage("Invalid Params");
			}
			if (e.getMessage().contains("401")) {
				kk.setStatusCode("401");
				response.setCode("57");
				response.setMessage("You are not authorized to perform this operation");
			}
			if (e.getMessage().contains("500")) {
				kk.setStatusCode(""+500);
				response.setCode("57");
				response.setMessage("JSW Connector API Not Working, Please Contact JSW Admin Team ");
			}
		}
		try {
			kk.setSourceRespone( mapper.writeValueAsString(response));
		} catch (Exception e) {
			kk.setStatusCode(""+500);
			response.setCode("57");
			response.setMessage("JSW Connector API Not Working, Please Contact JSW Admin Team ");
		}
		jswoneAuditTrailRepository.save(kk);
		return response;
	}

	private void createPODetails(PODetailsMainResponse resp) {

		List<String> locationwisePOList = new ArrayList<String>();
		ObjectMapper mapper = new ObjectMapper();

		if (resp != null && resp.getPurchaseorder() != null) {
			for (PODetailsLineItemResponse result1 : resp.getPurchaseorder().getLine_items()) {
				locationwisePOList.add(result1.getSku());
				POWiseMmidDetailsEntity kk = new POWiseMmidDetailsEntity();
				kk.setMmId(result1.getSku());
				kk.setPoId(resp.getPurchaseorder().getPurchaseorder_id());
				kk.setPoReference(resp.getPurchaseorder().getPurchaseorder_number());
				String jsonString = "";
				try {
					jsonString = mapper.writeValueAsString(result1);
					kk.setMmidDetailsObject(jsonString);
				} catch (JsonProcessingException e) {
				}

				log.info("Hi lineItem == " + jsonString);
				if (result1.getSku() != null) {
					POWiseMmidDetailsEntity existingEntity = powseMmidDetailsRepository.findByMmIdAndPoId(result1.getSku(), resp.getPurchaseorder().getPurchaseorder_id());
					if (existingEntity != null && existingEntity.getId() > 0) {
						kk.setId(existingEntity.getId());
						kk.setCreatedOn(existingEntity.getCreatedOn());
						kk.setUpdatedOn(new Date());
					} else {
						kk.setCreatedOn(new Date());
						kk.setUpdatedOn(new Date());
					}
				}
				powseMmidDetailsRepository.save(kk);
			}
		}
	}

	@Override
	public PoGrnMainResponse postgrn(POSOIntegrationRequest req) {
		PoGrnMainResponse response = new PoGrnMainResponse();
		ResponseEntity<String> res = null;
		JswoneAuditTrailEntity kk =new JswoneAuditTrailEntity();
		ObjectMapper mapper = new ObjectMapper();
		String billId="";
		try {
			RestTemplate restTemplate = new RestTemplate();
			Map<String, String> propertyMap = commonUtil.getAllProperties();
			kk.setPoInvoiceNo(req.getPoInvoiceNo());
			kk.setProcessType("GRN_POST");
			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");
			headers.set(propertyMap.get("post_grn_headerkey"), propertyMap.get("post_grn_headervalue"));
			PoGrnMainRequest postGRN = prepareGRNRequest(req.getPoInvoiceNo());
			String postGRNReq = objectMapper.writeValueAsString(postGRN);
			kk.setRequestObj(postGRNReq );
			HttpEntity<String> request = new HttpEntity<>(postGRNReq, headers);
			String url = propertyMap.get("post_grn_url");
			kk.setRequestUrl(url);
			log.info("url  is  == " + url + ", postGRNReq - " + request);
			res = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
			log.info("response is == " + res);
			if (res.getBody() != null) {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				response = mapper.readValue(res.getBody().toString(), PoGrnMainResponse.class);
				if (response != null && response.getBill() != null && response.getBill().getBill_id() != null) {
					kk.setBillid(response.getBill().getBill_id());
					billId = response.getBill().getBill_id();
					req.setBillId(billId);
				}

				try {
					JsonNode root = mapper.readTree(res.getBody().toString());
					JsonNode bill = root.path("bill");
					JsonNode lineItems = bill.path("line_items");

					if (lineItems.isArray()) {
						for (JsonNode lineItem : lineItems) {
							JsonNode batches = lineItem.path("batches");
							if (batches.isArray()) {
								for (JsonNode batch : batches) {
									String batchId = batch.path("batch_id").asText(null);
									String coilNumber = batch.path("batch_number").asText(null);
									log.info("Batch ID: " + batchId+", Coil Number: " + coilNumber);
									inwardEntryRepository.updateBatchIdByPoInvNo(req.getPoInvoiceNo(), coilNumber, batchId);
								}
							}
						}
					}
				} catch (Exception e) {
				}
			}
			
			kk.setDestinationResponse(res.getBody().toString());
			jswoneAuditTrailRepository.save(kk);
			if (response != null && "0".equals( response.getCode()) ) {
				inwardEntryRepository.updateZohoSyncStatusByPoInvNo(req.getPoInvoiceNo(), "SUCCESS", billId);
				kk.setStatusCode(""+res.getStatusCode());
				response.setCode("0");
				response.setMessage( response.getMessage());
			} else {
				kk.setStatusCode(""+res.getStatusCode());
				response.setCode( response.getCode());
				response.setMessage( response.getMessage());
			}
			kk.setSourceRespone( mapper.writeValueAsString(response));
			inwardEntryRepository.updateZohoSyncRemarks(req.getPoInvoiceNo(), response.getMessage(), billId);
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			String error = ex.getResponseBodyAsString();
			kk.setStatusCode("" + ex.getStatusCode().value());
			kk.setDestinationResponse( error);
			try {
				JsonNode outer = mapper.readTree(error);
				String outerMessage = outer.path("message").asText();
				int start = outerMessage.indexOf("{");
				int end = outerMessage.lastIndexOf("}");
				if (start != -1 && end != -1 && end > start) {
					String innerJson = outerMessage.substring(start, end + 1);
					// Parse the inner JSON
					JsonNode inner = mapper.readTree(innerJson);
					String code = inner.path("code").asText();
					String message = inner.path("message").asText();
					response.setCode(code);
					response.setMessage(message);
					kk.setSourceRespone( mapper.writeValueAsString(response));
					inwardEntryRepository.updateZohoSyncRemarks(req.getPoInvoiceNo(), outerMessage, billId);
				} else {
					log.info("No inner JSON found in message");
				}
			} catch (Exception w) {
				
			}
		} catch (Exception e) {
            kk.setDestinationResponse( e.getMessage());
			if (e.getMessage().contains("404")) {
				kk.setStatusCode("404");
				response.setCode("1002");
				response.setMessage("Resource does not exist.");
			}
			if (e.getMessage().contains("400")) {
				kk.setStatusCode("400");
				response.setCode("4198");
				response.setMessage("Invalid Params");
			}
			if (e.getMessage().contains("401")) {
				kk.setStatusCode("401");
				response.setCode("57");
				response.setMessage("You are not authorized to perform this operation");
			}
			if (e.getMessage().contains("500")) {
				kk.setStatusCode("400");
				response.setCode("57");
				response.setMessage("JSW Connector API Not Working, Please Contact JSW Admin Team ");
			}
		}
		try {
			response.setUploadDocStatus("Document upload Failed");
			kk.setSourceRespone( mapper.writeValueAsString(response));
		} catch (Exception e) {
			kk.setStatusCode(""+500);
			response.setCode("57");
			response.setMessage("JSW Connector API Not Working, Please Contact JSW Admin Team ");
		}
 		if (billId != null && billId.length() > 0) {
			PODetailsMainResponse uploadDocStatusResponse = uploadDocument(req);
			if (uploadDocStatusResponse != null && "0".equals( uploadDocStatusResponse.getCode()) ) {
				response.setUploadDocStatus("Document uploaded successfully");
			}
		}
		try {
			kk.setSourceRespone( mapper.writeValueAsString(response));
			jswoneAuditTrailRepository.save(kk);
		} catch (JsonProcessingException e) {
		}
		return response;
	}

	private PoGrnMainRequest prepareGRNRequest(String poId) {
		PoGrnMainRequest req = new PoGrnMainRequest();
		List<PoGrnCustomType> customTypeList = new ArrayList<>();
		List<PoGrnLineItem> line_items = new ArrayList<>();

		List<Object[]> poDetails = powseMmidDetailsRepository.getInwardDetailsByPoId(poId);
		BigDecimal totalValueofgods = new BigDecimal("0.00"); 
		for (Object[] result : poDetails) {
			String po_reference = (result[0] != null ? result[0].toString() : null);
			String mmid_details_object = (result[1] != null ? result[1].toString() : null);
			String poinvno = (result[2] != null ? result[2].toString() : null);
			String coilNumber = ""; 
			String postdate = (result[3] != null ? result[3].toString() : null);
			BigDecimal fquantity = (result[4] != null ? new BigDecimal(result[4].toString()) : null);
			BigDecimal valueofgods = (result[5] != null ? new BigDecimal(result[5].toString()) : null);
			totalValueofgods=totalValueofgods.add(valueofgods);
			PODetailsLineItemResponse lineItems = new PODetailsLineItemResponse();
			try {
				lineItems = objectMapper.readValue(mmid_details_object, PODetailsLineItemResponse.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			req.setPo_number(po_reference);
			req.setBill_number(poinvno);
			req.setReference_number(po_reference);
			req.setDate(postdate);

			BigDecimal availQty = lineItems.getQuantity().subtract(lineItems.getQuantity_billed());
			BigDecimal extraQty = new BigDecimal("0.00");
			if (fquantity.compareTo(availQty) > 0) {
				extraQty = fquantity.subtract(availQty);
				fquantity = availQty;
			}
			PoGrnLineItem lineItem = new PoGrnLineItem();
			lineItem.setItem_id(lineItems.getItem_id());
			lineItem.setPurchase_order_line_item_id(lineItems.getLine_item_id());
			lineItem.setSku(lineItems.getSku());
			lineItem.setRate(lineItems.getRate());
			lineItem.setQuantity(fquantity);
			lineItem.setHsn_or_sac(lineItems.getHsn_or_sac());
			lineItem.setTax_id(lineItems.getTax_id());
			
			List<Object[]> poDetails1 = powseMmidDetailsRepository.getInwardDetailsByPoIdBatch(poId, lineItems.getSku());
			int coilCount = poDetails1.size();
			int counter=0;
			List<PoGrnLineItemBatches> batchesList = new ArrayList<>();
			for (Object[] resultbatch : poDetails1) {
				counter++;
				coilNumber = (resultbatch[0] != null ? resultbatch[0].toString() : null);
				BigDecimal fquantitycoil = (resultbatch[1] != null ? new BigDecimal(resultbatch[1].toString()) : null);
				
				PoGrnLineItemBatches batchObj = new PoGrnLineItemBatches();
				batchObj.setBatch_number(coilNumber);
				if (counter == coilCount) {
					batchObj.setIn_quantity(fquantitycoil.subtract(extraQty));
				} else {
					batchObj.setIn_quantity(fquantitycoil);
				}
				if(batchObj.getIn_quantity()!=null && batchObj.getIn_quantity().compareTo(BigDecimal.ZERO) > 0 ) {
					batchesList.add(batchObj);
				}
			}
			lineItem.setBatches(batchesList);
			line_items.add(lineItem);

			if (extraQty.compareTo(BigDecimal.ZERO) > 0) {
				List<PoGrnLineItemBatches> batches_pt = new ArrayList<>();
				PoGrnLineItem lineItem_pt = new PoGrnLineItem();
				lineItem_pt.setItem_id(lineItems.getItem_id());
				lineItem_pt.setPurchase_order_line_item_id(lineItems.getLine_item_id());
				lineItem_pt.setSku(lineItems.getSku());
				lineItem_pt.setRate(lineItems.getRate());
				lineItem_pt.setQuantity(extraQty);
				lineItem_pt.setHsn_or_sac(lineItems.getHsn_or_sac());
				lineItem_pt.setTax_id(lineItems.getTax_id());
				PoGrnLineItemBatches batchObj_pt = new PoGrnLineItemBatches();
				batchObj_pt.setBatch_number(coilNumber);
				batchObj_pt.setIn_quantity(extraQty);
				batches_pt.add(batchObj_pt);
				lineItem_pt.setBatches(batches_pt);
				line_items.add(lineItem_pt);
			}
		}
		req.setLine_items(line_items);

		PoGrnCustomType customParam = new PoGrnCustomType();
		customParam.setApi_name("cf_refrence_no");
		customParam.setLabel("Refrence No");
		customParam.setData_type("Text Box (Single Line)");
		customParam.setValue("");
		customTypeList.add(customParam);

		PoGrnCustomType customParam2 = new PoGrnCustomType();
		customParam2.setApi_name("cf_total_value_of_goods");
		customParam2.setLabel("Total Value Of Goods");
		customParam2.setData_type("Text Box (Single Line)");
		customParam2.setValue(totalValueofgods.toString());
		customTypeList.add(customParam2);
		req.setCustom_fields(customTypeList);
		return req;
	}
	
	@Override
	public Map<String, Object> poWiseInwardList(POSOIntegrationRequest request) {
		List<POWiseInwardListResponse> inwardList = new ArrayList<>();
		POWiseInwardListMainResponse mainReq = new POWiseInwardListMainResponse();
		List<Object[]> poDetails = powseMmidDetailsRepository.poWiseInwardList(request.getPoInvoiceNo());
		for (Object[] result : poDetails) {
			POWiseInwardListResponse kk = new POWiseInwardListResponse();
			kk.setPoReference(result[0] != null ? result[0].toString() : null);
			kk.setPoId(result[1] != null ? result[1].toString() : null);
			kk.setMmId(result[2] != null ? result[2].toString() : null);
			kk.setCoilNumber(result[3] != null ? result[3].toString() : null);
			kk.setCustomerBatchId(result[4] != null ? result[4].toString() : null);
			kk.setPostingDate(result[5] != null ? result[5].toString() : null);
			kk.setMmDesc(result[6] != null ? result[6].toString() : null);
			kk.setQty(result[7] != null ? result[7].toString() : null);
			kk.setValueOfGoods(result[8] != null ? result[8].toString() : null);
			mainReq.setTotalValueOfGoods( result[9] != null ? result[9].toString() : null);
			mainReq.setPoReference(result[0] != null ? result[0].toString() : null);
			mainReq.setPoId(result[1] != null ? result[1].toString() : null);
			inwardList.add(kk);
		}
		mainReq.setInwardList(inwardList);
		Map<String, Object> response = new HashMap<>();
		response.put("content", mainReq);
		response.put("currentPage", 1);
		response.put("totalItems", inwardList.size());
		response.put("totalPages", 1);
		return response;
	}

	@Override
	public Map<String, Object> allpoinvlist(ListPageSearchRequest request) {

		Pageable pageable = PageRequest.of((request.getPageNo() - 1), request.getPageSize());

		Page<Object[]> poDetails = powseMmidDetailsRepository.allpoinvlist(request.getSearchText(), (request.getPartyId() == null ? 0 : request.getPartyId()), pageable);
		
		Map<String, POInvoiceListResponse> soMap = new LinkedHashMap<>();

		for (Object[] result : poDetails) {
			POInvoiceListResponse kk = new POInvoiceListResponse();
			POInvoiceListChildResponse child = new POInvoiceListChildResponse();

			kk.setPoInvoiceNo(result[0] != null ? result[0].toString() : null);
			kk.setPoInvSyncStatus(result[1] != null ? result[1].toString() : "PENDING");
			kk.setPoInvSyncRemarks( result[2] != null ? result[2].toString() : "");
			kk.setManualPoFlag( result[3] != null ? result[3].toString() : "Y");
			kk.setBillId( result[4] != null ? result[4].toString() : "");
			kk.setZohoDocumentUploadStts(result[5] != null ? result[5].toString() : "PENDING");
			kk.setZohoDocumentUploadRemarks(result[6] != null ? result[6].toString() : "");
			kk.setInwardDate((result[11] != null ? result[11].toString() : null));
			kk.setLocationName((result[12] != null ? result[12].toString() : null));
			kk.setPoNumber( (result[13] != null ? result[13].toString() : null));
			
			child.setCoilNumber(result[7] != null ? (String) result[7] : null);
			child.setCustomerBatchId(result[8] != null ? (String) result[8] : null);
			child.setCoilStatus(result[9] != null ? (String) result[9] : null);
			child.setInvoiceDate((result[10] != null ? result[10].toString() : null));

			if (soMap != null && soMap.get(kk.getPoInvoiceNo()) != null) {
				POInvoiceListResponse addEntity = soMap.get(kk.getPoInvoiceNo());
				addEntity.getCoilList().add(child);
				soMap.put(kk.getPoInvoiceNo(), addEntity);
			} else {
				List<POInvoiceListChildResponse> coilList = new ArrayList<>();
				coilList.add(child);
				kk.setCoilList(coilList);
				soMap.put(kk.getPoInvoiceNo(), kk);
			}
		}
		List<POInvoiceListResponse> list = new ArrayList<>(soMap.values());

		Map<String, Object> response = new HashMap<>();
		response.put("content", list);
		response.put("currentPage", poDetails.getNumber());
		response.put("totalItems", poDetails.getTotalElements());
		response.put("totalPages", poDetails.getTotalPages());
		return response;
	}

	@Override
	public ResponseEntity<Object> coilSyncStts(List<POSOIntegrationRequest> request) {
		log.info("******JSWIntegrationServiceImpl.coilSyncStts*****");
		Map<String, String> sttsMap = new HashMap<>();
		try {
			for (POSOIntegrationRequest obj : request) {
				String batchNo = obj.getBatchNo();
				if (batchNo != null) {
					String value = inwardEntryRepository.isBatchNoPresent(batchNo);
					if (value != null && !value.isEmpty()) {
						int cnt = inwardEntryRepository.updateZohoSyncStatus(batchNo, obj.getZohoSyncStatus());
						if (cnt > 0) {
							sttsMap.put(batchNo, "Status Updated Successfully.");
						} else {
							sttsMap.put(batchNo, "Failed to update.");
						}
					} else {
						sttsMap.put(batchNo, "Invalid Batch No");
					}
				}
			}
			// Ensure no null keys sneak in
			sttsMap = sttsMap.entrySet().stream().filter(e -> e.getKey() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
			return ResponseEntity.ok(sttsMap);
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("status", "fail");
			error.put("message", e.getMessage());
			error.put("referenceNo", "");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}
	}

	@Override
	public PODetailsMainResponse uploadDocument(POSOIntegrationRequest req) {
		PODetailsMainResponse response = new PODetailsMainResponse();
		ResponseEntity<String> res = null;
		JswoneAuditTrailEntity kk =new JswoneAuditTrailEntity();
		ObjectMapper mapper = new ObjectMapper();
		try {
			RestTemplate restTemplate = new RestTemplate();
			Map<String, String> propertyMap = commonUtil.getAllProperties();
			kk.setPoInvoiceNo(req.getPoInvoiceNo());
			kk.setProcessType("UPLOAD_DOC");
			kk.setBillid(req.getBillId());
			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			headers.set(propertyMap.get("upload_doc_headerkey"), propertyMap.get("upload_doc_headervalue"));
			String url = propertyMap.get("upload_doc_url")+"/"+req.getBillId();
			kk.setRequestUrl(url);
			MultiValueMap<String, Object> docList = prepareDocumentUploadRequest(req.getBillId());
			docList.forEach((key, values) -> values.removeIf(Objects::isNull));
			HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(docList, headers);
			log.info("url  is  == " + url + ", request is  - " + request);
			kk.setRequestObj( request.toString());
			res = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
			log.info("response is == " + res);
			if (res.getBody() != null) {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				response = mapper.readValue(res.getBody().toString(), PODetailsMainResponse.class);
			}
			kk.setDestinationResponse(res.getBody().toString());
			jswoneAuditTrailRepository.save(kk);
			if (response != null && "0".equals( response.getCode()) ) {
				response.setCode("0");
				kk.setStatusCode(""+res.getStatusCode());
				inwardEntryRepository.updateZohoDocUploadStatus(req.getBillId());
				response.setMessage( response.getMessage());
			} else {
				response.setCode( response.getCode());
				response.setMessage( response.getMessage());
			}
			kk.setSourceRespone( mapper.writeValueAsString(response));
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			String error = ex.getResponseBodyAsString();
			kk.setDestinationResponse( error);
			try {
				JsonNode outer = mapper.readTree(error);
				String outerMessage = outer.path("message").asText();
				int start = outerMessage.indexOf("{");
				int end = outerMessage.lastIndexOf("}");
				if (start != -1 && end != -1 && end > start) {
					String innerJson = outerMessage.substring(start, end + 1);
					// Parse the inner JSON
					JsonNode inner = mapper.readTree(innerJson);
					String code = inner.path("code").asText();
					String message = inner.path("message").asText();
					response.setCode(code);
					response.setMessage(message);
					kk.setSourceRespone( mapper.writeValueAsString(response));
				} else {
					log.info("No inner JSON found in message");
				}
			} catch (Exception w) {
				
			}
			kk.setStatusCode("" + ex.getStatusCode().value());
		} catch (Exception e) {
            kk.setDestinationResponse( e.getMessage());
			if (e.getMessage().contains("404")) {
				kk.setStatusCode("404");
				response.setCode("1002");
				response.setMessage("Resource does not exist.");
			}
			if (e.getMessage().contains("400")) {
				kk.setStatusCode("400");
				response.setCode("4198");
				response.setMessage("Invalid Params");
			}
			if (e.getMessage().contains("401")) {
				kk.setStatusCode("401");
				response.setCode("57");
				response.setMessage("You are not authorized to perform this operation");
			}
			if (e.getMessage().contains("500")) {
				kk.setStatusCode("400");
				response.setCode("57");
				response.setMessage("JSW Connector API Not Working, Please Contact JSW Admin Team ");
			}
		}
		try {
			kk.setSourceRespone( mapper.writeValueAsString(response));
		} catch (Exception e) {
			kk.setStatusCode(""+500);
			response.setCode("57");
			response.setMessage("JSW Connector API Not Working, Please Contact JSW Admin Team ");
		}
		jswoneAuditTrailRepository.save(kk);
		try {
			kk.setSourceRespone( mapper.writeValueAsString(response));
			jswoneAuditTrailRepository.save(kk);
			String responseStr = objectMapper.writeValueAsString(response);
			inwardEntryRepository.uploadDocumentZohoSyncRemarks(responseStr, req.getBillId());
		} catch (JsonProcessingException e) {
		}
		return response;
	}

	private MultiValueMap<String, Object> prepareDocumentUploadRequest(String billId) {
		MultiValueMap<String, Object> docList = new LinkedMultiValueMap<>();
		List<Object[]> poDetails = powseMmidDetailsRepository.getDocDetailsbyPoId(billId);
		for (Object[] result : poDetails) {
			String testcertificatefileurl = (result[1] != null ? result[1].toString() : null);
			String invoicecopy_fileur = (result[2] != null ? result[2].toString() : null);
			
			if(invoicecopy_fileur!=null && invoicecopy_fileur.length()>0) {
			    String testcertificatefileNamecc = invoicecopy_fileur.split("\\?")[0];
			    String testCertificateFileName = testcertificatefileNamecc.substring(testcertificatefileNamecc.lastIndexOf('/') + 1);
				try {
					String filaPath = awsS3Service.downloadS3toLocalFile(testCertificateFileName, gcpReportsPath+File.separator+"jswone_upload_temp"+File.separator+testCertificateFileName);
				    File file = new File(filaPath);
				    if (file.exists() && file.isFile()) {
				    	docList.add("Attachment", new FileSystemResource(file));
				    }
				} catch (Exception e) {
					log.info("File Available "+testcertificatefileNamecc);
				}
			}
			if(testcertificatefileurl!=null && testcertificatefileurl.length()>0) {
			    String testcertificatefileNamecc = testcertificatefileurl.split("\\?")[0];
			    String testCertificateFileName = testcertificatefileNamecc.substring(testcertificatefileNamecc.lastIndexOf('/') + 1);
				try {
					String filaPath = awsS3Service.downloadS3toLocalFile(testCertificateFileName, gcpReportsPath+File.separator+"jswone_upload_temp"+File.separator+testCertificateFileName);
				    File file = new File(filaPath);
				    if (file.exists() && file.isFile()) {
				    	docList.add("Attachment", new FileSystemResource(file));
				    }
				} catch (Exception e) {
					log.info("File Available "+testcertificatefileNamecc);
				}
			}
		}
		return docList;
	}

	@Override
	public InventoryAdjustmentResponse inventoryAdjustment(DeliveryDto request) {
		InventoryAdjustmentResponse response = new InventoryAdjustmentResponse();
		ResponseEntity<String> res = null;
		JswoneAuditTrailEntity audit =new JswoneAuditTrailEntity();
		ObjectMapper mapper = new ObjectMapper();
		try {
			RestTemplate restTemplate = new RestTemplate();
			Map<String, String> propertyMap = commonUtil.getAllProperties();
			audit.setPoId(""+request.getDeliveryId());
			audit.setProcessType("INVENTORY_ADJUSTEMNT");
			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");
			headers.set(propertyMap.get("inventoryAdjustment_headerkey"), propertyMap.get("inventoryAdjustment_headervalue"));
			DC_InventoryAdjustmentMainRequest postGRN = prepareInvAdjustmentRequest(request.getDeliveryId());
			String inventoryAdjustmentReq = objectMapper.writeValueAsString(postGRN);
			audit.setRequestObj(inventoryAdjustmentReq);
			HttpEntity<String> extRequest = new HttpEntity<>(inventoryAdjustmentReq, headers);
			String url = propertyMap.get("inventoryAdjustment_url");
			audit.setRequestUrl(url);
			log.info("url  is  == " + url + ", inventoryAdjustment - " + extRequest);
			res = restTemplate.exchange(url, HttpMethod.POST, extRequest, String.class);
			log.info("response is == " + res);
			if (res.getBody() != null) {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				response = mapper.readValue(res.getBody().toString(), InventoryAdjustmentResponse.class);
			}
			
			audit.setDestinationResponse(res.getBody().toString());
			jswoneAuditTrailRepository.save(audit);
			if (response != null && "0".equals( response.getCode()) ) {
				audit.setStatusCode(""+res.getStatusCode());
				response.setCode("0");
				response.setMessage( response.getMessage());
			} else {
				audit.setStatusCode(""+res.getStatusCode());
				response.setCode( response.getCode());
				response.setMessage( response.getMessage());
			}
			audit.setSourceRespone( mapper.writeValueAsString(response));
			deliveryDetailsRepository.updateZohoSyncRemarks(request.getDeliveryId(), response.getMessage(), "SUCCESS", response.getData() );
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			String error = ex.getResponseBodyAsString();
			audit.setStatusCode("" + ex.getStatusCode().value());
			audit.setDestinationResponse( error);
			try {
				JsonNode outer = mapper.readTree(error);
				String code = outer.path("code").asText();
				String message = outer.path("message").asText();
				response.setCode(code);
				response.setMessage(message);
				deliveryDetailsRepository.updateZohoSyncRemarks(request.getDeliveryId(), message, "FAIL", "");
			} catch (Exception w) {
				
			}
		} catch (Exception e) {
            audit.setDestinationResponse( e.getMessage());
			if (e.getMessage().contains("404")) {
				audit.setStatusCode("404");
				response.setCode("1002");
				response.setMessage("Resource does not exist.");
			}
			if (e.getMessage().contains("400")) {
				audit.setStatusCode("400");
				response.setCode("4198");
				response.setMessage("Invalid Params");
			}
			if (e.getMessage().contains("401")) {
				audit.setStatusCode("401");
				response.setCode("57");
				response.setMessage("You are not authorized to perform this operation");
			}
			if (e.getMessage().contains("500")) {
				audit.setStatusCode("400");
				response.setCode("57");
				response.setMessage("JSW Connector API Not Working, Please Contact JSW Admin Team ");
			}
 		}
		try {
			audit.setSourceRespone( mapper.writeValueAsString(response));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		jswoneAuditTrailRepository.save(audit);
		return response;
	}

	private DC_InventoryAdjustmentMainRequest prepareInvAdjustmentRequest(Integer dcId) {
		
		List<Object[]> poDetails = instructionRepository.prepareInvAdjustmentRequest(dcId);
		
		DC_InventoryAdjustmentMainRequest req = new DC_InventoryAdjustmentMainRequest();
		List<DC_InventoryAdjustmentLineItem> lineItems = new ArrayList<>();

		for (Object[] result : poDetails) {
			DC_InventoryAdjustmentLineItem lineitem = new DC_InventoryAdjustmentLineItem();
			req.setSalesOrderNumber(result[8] != null ? result[8].toString() : null);
			req.setDate(result[1] != null ? result[1].toString() : null);
			req.setEwaybillVehicleNumber(result[2] != null ? result[2].toString() : null);
			String mmid = result[3] != null ? result[3].toString() : null;
			BigDecimal totalWeight = (result[4] == null ? null : new BigDecimal(String.valueOf(result[4])));
			req.setWarehouseId(result[5] != null ? result[5].toString() : null);
			req.setBranchID(result[6] != null ? result[6].toString() : null);
			BigDecimal totalPTWeight = (result[7] == null ? BigDecimal.ZERO : new BigDecimal(String.valueOf(result[7])));
			String fileName = (result[9] != null ? result[9].toString() : null);

			req.setReason("Stock conversion");
			req.setAdjustmentType("quantity");
			req.setAccount("Cost of goods sold");
			req.setShipmentReferenceNo("NA");
			req.setMotorVehicleNumber("NA");

			//req.setFileName(fileName);
			//req.setPdf(awsS3Service.downloadPdfAsBase64(fileName));
			//req.setDcNumber(""+dcId);

			ToSku toSku = new ToSku();
			toSku.setSkuId(mmid);
			toSku.setQuantity_adjusted(totalWeight.add(totalPTWeight));
			toSku.setUom("MT");
			lineitem.setToSku(toSku);

			List<Object[]> fromSkuDetails = instructionRepository.prepareInvAdjustmentFrom(dcId, mmid);
			for (Object[] resultfrom : fromSkuDetails) {
				BigDecimal totalWeight1 = (resultfrom[0] == null ? null : new BigDecimal(String.valueOf(resultfrom[0])));
				String mmid1 = resultfrom[1] != null ? resultfrom[1].toString() : null;
				String coilNumber = resultfrom[2] != null ? resultfrom[2].toString() : null;
				String batchId = resultfrom[3] != null ? resultfrom[3].toString() : null;
				BigDecimal ptWeight = (resultfrom[4] == null ? BigDecimal.ZERO : new BigDecimal(String.valueOf(resultfrom[4])));

				FromSku fromSku = new FromSku();
				fromSku.setSkuId(mmid1);
				//fromSku.setQuantity_adjusted(totalWeight1); 
				fromSku.setQuantity_adjusted(totalWeight1.abs().negate());
				fromSku.setUom("MT"); 
				lineitem.getFromSkus().add(fromSku);

				InventoryAdjustmentBatch batchesFrom=new InventoryAdjustmentBatch();
				batchesFrom.setBatch_number( coilNumber);
				batchesFrom.setBatch_id(batchId);
				batchesFrom.setIn_quantity(totalWeight1);
				fromSku.getBatches().add(batchesFrom); 
								
				if (ptWeight !=null && ptWeight.compareTo(BigDecimal.ZERO) > 0) {
					PtQuantity ptQuantity = new PtQuantity();
					ptQuantity.setSkuId(mmid1);
					ptQuantity.setQuantity(ptWeight);
					ptQuantity.setUom("MT");

					InventoryAdjustmentBatch batches = new InventoryAdjustmentBatch();
					batches.setBatch_number(coilNumber);
					batches.setBatch_id(batchId);
					batches.setIn_quantity(ptWeight);
					ptQuantity.getBatches().add(batches);
					lineitem.getPtQuantities().add(ptQuantity);
				}
			}
			lineItems.add(lineitem);
		}
		req.setLine_items(lineItems);
		return req;
	}

}
