package com.steel.product.jswone.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.steel.product.jswone.entity.MaterialMasterFileDataEntity;
import com.steel.product.jswone.entity.MaterialMasterJswEntity;
import com.steel.product.jswone.entity.POReceiveDetailsEntity;
import com.steel.product.jswone.repository.MaterialMasterFiledataRepository;
import com.steel.product.jswone.repository.MaterialMasterJswRepository;
import com.steel.product.jswone.repository.POReceiveDetailsRepository;
import com.steel.product.jswone.request.ApiResponse;
import com.steel.product.jswone.request.MMIDReceiveIntegrationRequest;
import com.steel.product.jswone.request.MaterialMasterFileDataDTO;
import com.steel.product.jswone.request.POIntegrationRequest;

import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class JSWIntegrationServiceImpl implements JSWIntegrationService {

	@Autowired
	private POReceiveDetailsRepository poReceiveDetailsRepository;

	@Autowired
	MaterialMasterFiledataRepository repository;

	@Autowired
	MaterialMasterJswRepository materialMasterRepository;
	
	@Autowired
	MaterialUploadService materialUploadService;
	
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
			entity.setStatus(request.getStatus());
			entity.setIpAddress(request.getIpAddress());
			poReceiveDetailsRepository.save(entity);
			return new ResponseEntity<Object>("{\"status\": \"success\",\"message\":\"" + message+ "\", \"referenceNo\":\"" + entity.getId() + "\"}", headers, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>("{\"status\": \"fail\",\"message\":\"" + e.getMessage() + "\", \"referenceNo\":\"\"}", HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	@Transactional
	public ResponseEntity<Object> mmidreceive(MMIDReceiveIntegrationRequest request) {

		log.info("******JSWIntegrationServiceImpl.mmidreceive*****");
		String message="MMID details saved successfully.";
		try {
			ObjectMapper mapper = new ObjectMapper();
			MaterialMasterJswEntity destEntity = new MaterialMasterJswEntity();
			if (request.getMmid() != null && request.getMmid().length() > 0) {

				MaterialMasterFileDataDTO dummy = new MaterialMasterFileDataDTO();
				dummy.setMmId(request.getMmid());
				dummy.setMmDescription(request.getMmdesc());
				dummy.setCategory(request.getMasterCategory());
				dummy.setSubcategory(request.getSubCategory());
				dummy.setBrand(request.getBrand());
				dummy.setLeafcategory(request.getLeafCategory());
				dummy.setForm(request.getForm());
				dummy.setProducttype(request.getProductType());
				dummy.setGrade(request.getGrade());
				dummy.setSubgrade(request.getSubGrade());
				dummy.setDiameter(request.getDiameter());
				dummy.setThickness(request.getThickness());
				dummy.setWidth(request.getWidth());
				dummy.setLength(request.getLength());
				dummy.setCoatingtype(request.getCoating_grade_gsm());
				dummy.setSpangletype(request.getSpangleType());
				dummy.setColour(request.getColour());
				dummy.setUom(request.getUom());
				MaterialMasterFileDataEntity sourceEntity = new MaterialMasterFileDataEntity();
				BeanUtils.copyProperties(dummy, sourceEntity);
				sourceEntity.setFilename("Zoho_Integration");
				sourceEntity.setCreatedOn(new Date());
				
				MaterialMasterFileDataEntity dummyEntity= repository.findFirstByMmId(sourceEntity.getMmId());
				if (dummyEntity != null && dummyEntity.getMateraiId() > 0) {
					sourceEntity.setMateraiId( dummyEntity.getMateraiId() );
				}
				repository.save(sourceEntity);
				log.info("MMID saved into jsw_material_file_data table ");

				BeanUtils.copyProperties(sourceEntity, destEntity);
				MaterialMasterJswEntity oldEntity= materialMasterRepository.findFirstByMmId(sourceEntity.getMmId());

				if (oldEntity != null && oldEntity.getMaterialId() > 0) {
					destEntity.setMaterialId(oldEntity.getMaterialId());
					message = "MMID details updated successfully.";
				}
				if(sourceEntity.getLength()!=null && sourceEntity.getLength().length() >0 ) {
					destEntity.setLength(new BigDecimal(sourceEntity.getLength()));
				} else {
					destEntity.setLength(BigDecimal.ZERO);
				}
				if(sourceEntity.getWidth() !=null && sourceEntity.getWidth().length() >0 ) {
					destEntity.setWidth(new BigDecimal(sourceEntity.getWidth()));
				} else {
					destEntity.setWidth(BigDecimal.ZERO);
				}
				if(sourceEntity.getThickness() !=null && sourceEntity.getThickness().length() >0 ) {
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
				
				if(!(sourceEntity.getBrand()!=null && sourceEntity.getBrand().length()>0)) {
					sourceEntity.setBrand("UnBrand");
				} 
				// Brand Master 
				destEntity.setCategoryId(materialUploadService.setCategoryMaster(sourceEntity.getCategory()));
				destEntity.setSubcategoryId(materialUploadService.setSubCategoryMaster(sourceEntity.getSubcategory(), destEntity.getCategoryId()));
				destEntity.setLeafcategoryId(materialUploadService.setLeafCategoryMaster(sourceEntity.getLeafcategory(), destEntity.getSubcategoryId()));
				destEntity.setBrandId(materialUploadService.setBrandNameMaster(sourceEntity.getBrand(), destEntity.getLeafcategoryId()));
				// Product Master 
				destEntity.setProducttypeId(materialUploadService.setProductMaster(sourceEntity.getProducttype(), destEntity));
				destEntity.setGradeId(materialUploadService.setGradeMaster(sourceEntity.getGrade(), destEntity.getProducttypeId()));
				destEntity.setSubgradeId(materialUploadService.setSubGradeMaster(sourceEntity.getSubgrade(), destEntity.getGradeId()));
				destEntity.setCoatingtypeId(materialUploadService.setCoatingtypeMaster( sourceEntity.getCoatingtype(), destEntity.getProducttypeId() ));
				destEntity.setSurfacetypeId(materialUploadService.setSurfacetypeMaster(sourceEntity.getSurfacetype(), destEntity.getProducttypeId()));
				destEntity.setUomId(materialUploadService.setUomMaster(sourceEntity.getUom(), destEntity.getProducttypeId()));
				destEntity.setFormId(materialUploadService.setFormMaster(sourceEntity.getForm(), destEntity.getProducttypeId()));
				try {
					destEntity = materialMasterRepository.save(destEntity);
				} catch (Exception e) {
					System.out.println("error while save --  "+e.getMessage());
				}
			}        
			
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			String json = mapper.writeValueAsString(destEntity);
			ApiResponse response = new ApiResponse("success", message, mapper.readValue(json, Map.class));
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch( Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>("{\"status\": \"failed\", \"message\": \"Failed to save the MMID details\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
}
