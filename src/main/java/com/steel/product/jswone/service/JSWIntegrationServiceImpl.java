package com.steel.product.jswone.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steel.product.application.util.CommonUtil;
import com.steel.product.jswone.entity.MaterialMasterFileDataEntity;
import com.steel.product.jswone.entity.MaterialMasterJswEntity;
import com.steel.product.jswone.entity.POReceiveDetailsEntity;
import com.steel.product.jswone.entity.POWiseMmidDetailsEntity;
import com.steel.product.jswone.repository.MaterialMasterFiledataRepository;
import com.steel.product.jswone.repository.MaterialMasterJswRepository;
import com.steel.product.jswone.repository.POReceiveDetailsRepository;
import com.steel.product.jswone.repository.POWiseMmidDetailsRepository;
import com.steel.product.jswone.repository.PropertyRepository;
import com.steel.product.jswone.request.ApiResponse;
import com.steel.product.jswone.request.MMIDReceiveMainRequest;
import com.steel.product.jswone.request.MaterialMasterFileDataDTO;
import com.steel.product.jswone.request.POIntegrationRequest;
import com.steel.product.jswone.response.PODetailsLineItemResponse;
import com.steel.product.jswone.response.PODetailsMainResponse;
import com.steel.product.jswone.response.PoGrnCustomType;
import com.steel.product.jswone.response.PoGrnLineItem;
import com.steel.product.jswone.response.PoGrnMainRequest;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Log4j2
public class JSWIntegrationServiceImpl implements JSWIntegrationService {

	@Autowired
	private POReceiveDetailsRepository poReceiveDetailsRepository;

	@Autowired
	private MaterialMasterFiledataRepository repository;

	@Autowired
	private MaterialMasterJswRepository materialMasterRepository;

	@Autowired
	private MaterialUploadService materialUploadService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PropertyRepository propertyRepository;
	
	@Autowired
	private POWiseMmidDetailsRepository powseMmidDetailsRepository;

	@Autowired
	CommonUtil commonUtil;

	@Override
	public ResponseEntity<Object> poReceive(POIntegrationRequest request) {
		log.info("******JSWIntegrationServiceImpl.poReceive*****");
		POReceiveDetailsEntity entity = new POReceiveDetailsEntity();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		log.info("obj.getReqObj() == " + request);
		String message = "PO details saved successfully";

		try {
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
			poReceiveDetailsRepository.save(entity);
			return new ResponseEntity<Object>("{\"status\": \"success\",\"message\":\"" + message
					+ "\", \"referenceNo\":\"" + entity.getId() + "\"}", headers, HttpStatus.OK);
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
			System.out.println("full req is : " + jsonReq);

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
					System.out.println("error while save --  " + e.getMessage());
				}
			} else {
				return new ResponseEntity<Object>("{\"status\": \"failed\", \"message\": \"Please enter valid MMID\"}",
						new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
			}

			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			String json = objectMapper.writeValueAsString(destEntity);
			ApiResponse response = new ApiResponse("success", message, objectMapper.readValue(json, Map.class));
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			// e.printStackTrace();
			return new ResponseEntity<Object>(
					"{\"status\": \"failed\", \"message\": \"Failed to save the MMID details\"}", new HttpHeaders(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public List<Object[]> locationwisePOList(POIntegrationRequest request) {
		List<Object[]> locationwisePOList = poReceiveDetailsRepository.locationwisePOList(request.getLocationId());
		return locationwisePOList;
	}

	@Override
	public PODetailsMainResponse podetails(POIntegrationRequest requ) {
		PODetailsMainResponse response = new PODetailsMainResponse();
		try {
			RestTemplate restTemplate = new RestTemplate();
			Map<String, String> propertyMap = commonUtil.getAllProperties();

			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");
			headers.set("Authorization", propertyMap.get("podetails_Authorization"));
			HttpEntity<String> request = new HttpEntity<>("{}", headers);
			String url = propertyMap.get("podetails_url") + "?purchaseorder_id=" + requ.getPoId();
			//String url = "https://tigios.techurate.com/mockapi/jsontoxml/execute/jswone/podetails/1.1";
			System.out.println("request is  == " + request + ", url - " + url);
			System.out.println(" url - " + url);
			ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
			System.out.println("response is == " + res);
			if (res.getBody() != null) {
				ObjectMapper om = new ObjectMapper();
				om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				response = om.readValue(res.getBody().toString(), PODetailsMainResponse.class);
			}
			if(response!=null && response.getPurchaseorder()!=null &&  response.getPurchaseorder ().getLine_items() != null ) {
				System.out.println("Hi kanak  "+ response.getPurchaseorder ().getLine_items().size());
				createPODetails(response);
				response.setCode("0");
				response.setMessage("Success");
			}else {
				response.setCode("1002");
				response.setMessage("Resource does not exist.");
			}
		} catch (Exception e) {
			if(e.getMessage().contains("404")) {
				response.setCode("1002");
				response.setMessage("Resource does not exist.");
			}
			if(e.getMessage().contains("400")) {
				response.setCode("6024");
				response.setMessage("Invalid Params");
			}
			if(e.getMessage().contains("401")) {
				response.setCode("57");
				response.setMessage("You are not authorized to perform this operation");
			}
		}
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

				System.out.println("HI lineItem == "+jsonString);
				if (result1.getSku() != null) {
					POWiseMmidDetailsEntity existingEntity = powseMmidDetailsRepository.findByMmId(result1.getSku());
					if (existingEntity != null && existingEntity.getId() > 0) {
						kk.setId(existingEntity.getId());
						kk.setCreatedOn(existingEntity.getCreatedOn());
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
	public PODetailsMainResponse postgrn(POIntegrationRequest req ) {
		PODetailsMainResponse response = new PODetailsMainResponse();
		ResponseEntity<String> res =null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			Map<String, String> propertyMap = commonUtil.getAllProperties();

			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");
			headers.set(propertyMap.get("post_grn_headerkey"), propertyMap.get("post_grn_headervalue"));
			PoGrnMainRequest postGRN = prepareGRNRequest(req.getPoId()); 
			String postGRNReq = objectMapper.writeValueAsString(postGRN);
			
			HttpEntity<String> request = new HttpEntity<>(postGRNReq, headers);
			String url = propertyMap.get("post_grn_url");
			System.out.println("url  is  == " + url  + ", postGRNReq - " + request);
			res = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
			System.out.println("response is == " + res);
			if (res.getBody() != null) {
				ObjectMapper om = new ObjectMapper();
				om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				response = om.readValue(res.getBody().toString(), PODetailsMainResponse.class);
			}
			if(response!=null && response.getPurchaseorder()!=null &&  response.getPurchaseorder ().getLine_items() != null ) {
				createPODetails(response);
				response.setCode("0");
				response.setMessage("Success");
			}else {
				response.setCode("1002");
				response.setMessage("Resource does not exist.");
			}
		} catch (Exception e) {
			System.out.println("Error response is == " + e.getMessage());
			if(e.getMessage().contains("404")) {
				response.setCode("1002");
				response.setMessage("Resource does not exist.");
			}
			if(e.getMessage().contains("400")) {
				response.setCode("4198");
				response.setMessage("Invalid Params");
			}
			if(e.getMessage().contains("401")) {
				response.setCode("57");
				response.setMessage("You are not authorized to perform this operation");
			}
		}
		return response;
	}

	private PoGrnMainRequest prepareGRNRequest(String poId) {

		PoGrnMainRequest req = new PoGrnMainRequest();
		List<PoGrnCustomType> customTypeList = new ArrayList<>();
		List<PoGrnLineItem> line_items = new ArrayList<>();

		List<POWiseMmidDetailsEntity> poDetails = powseMmidDetailsRepository.findByPoId(poId);
		for (POWiseMmidDetailsEntity entity : poDetails) {
			PoGrnCustomType customParam = new PoGrnCustomType();
			try {
				ObjectMapper om = new ObjectMapper();
				om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				PODetailsLineItemResponse lineItems = objectMapper.readValue(entity.getMmidDetailsObject(), PODetailsLineItemResponse.class);

				Date fdate = new Date(); // example
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				req.setPo_number(entity.getPoReference());
				req.setBill_number(entity.getPoReference());
				req.setReference_number(entity.getPoReference());
				req.setDate(sdf.format(fdate));

				customParam.setApi_name("cf_refrence_no");
				customParam.setLabel(entity.getPoReference());
				customParam.setData_type("Text Box (Single Line)");
				customParam.setValue(entity.getPoReference());
				customTypeList.add(customParam);

				PoGrnLineItem lineItem = new PoGrnLineItem();
				lineItem.setItem_id(lineItems.getLine_item_id());
				lineItem.setPurchase_order_line_item_id("");
				lineItem.setSku(lineItems.getSku());
				lineItem.setRate(lineItems.getRate());
				lineItem.setQuantity(lineItems.getQuantity());
				lineItem.setHsn_or_sac(lineItems.getHsn_or_sac());
				lineItem.setTax_id(lineItems.getTax_id());
				line_items.add(lineItem);
				req.setLine_items(line_items);
				req.setCustom_type(customTypeList);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			req.setLine_items( line_items);
			req.setCustom_type(customTypeList);
		}
		return req;
	}

}
