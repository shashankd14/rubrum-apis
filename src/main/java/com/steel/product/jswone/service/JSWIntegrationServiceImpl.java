package com.steel.product.jswone.service;

import com.steel.product.jswone.entity.MaterialMasterFileDataEntity;
import com.steel.product.jswone.entity.MaterialMasterJswEntity;
import com.steel.product.jswone.entity.POReceiveDetailsEntity;
import com.steel.product.jswone.repository.MaterialMasterFiledataRepository;
import com.steel.product.jswone.repository.MaterialMasterJswRepository;
import com.steel.product.jswone.repository.POReceiveDetailsRepository;
import com.steel.product.jswone.request.MMIDReceiveIntegrationRequest;
import com.steel.product.jswone.request.MaterialMasterFileDataDTO;
import com.steel.product.jswone.request.POIntegrationRequest;

import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
			return new ResponseEntity<Object>("{\"status\": \"success\",\"message\":\"" + message
					+ "\", \"referenceNo\":\"" + entity.getId() + "\"}", headers, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(
					"{\"status\": \"fail\",\"message\":\"" + e.getMessage() + "\", \"referenceNo\":\"\"}",
					HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<Object> mmidreceive(MMIDReceiveIntegrationRequest request) {

		log.info("******JSWIntegrationServiceImpl.mmidreceive*****");
		 
		try {
			
			if (request.getMmid()!=null && request.getMmid().length()>0 ) {

				List<MaterialMasterFileDataDTO> products = new ArrayList<MaterialMasterFileDataDTO>();
				MaterialMasterFileDataDTO dummy = new MaterialMasterFileDataDTO();
				dummy.setMmId(request.getMmid());
				dummy.setMmDescription( request.getMmdesc() );
				dummy.setCategory( request.getMasterCategory() );
				dummy.setSubcategory( request.getSubCategory() );
				dummy.setBrand(request.getBrand());
				dummy.setLeafcategory( request.getLeafCategory() );
				dummy.setForm( request.getForm() );
				dummy.setProducttype( request.getProductType());
				dummy.setGrade( request.getGrade() );
				dummy.setSubgrade( request.getSubGrade());
				dummy.setDiameter(request.getDiameter());
				dummy.setThickness( request.getThickness() );
				dummy.setWidth(request.getWidth());
				dummy.setLength( request.getLength());
				dummy.setCoatingtype(request.getCoating_grade_gsm() );
				dummy.setSpangletype( request.getSpangleType());
				dummy.setColour( request.getColour());
				dummy.setUom(request.getUom());
				products.add(dummy);
				List<MaterialMasterFileDataEntity> productList = new ArrayList<>();

				System.out.println("Hi size " + products.size());
				for (MaterialMasterFileDataDTO dto : products) {
					MaterialMasterFileDataEntity dest = new MaterialMasterFileDataEntity();
					BeanUtils.copyProperties(dto, dest);
					try {
						//dest.setFilename(newFileName);
						productList.add(dest);
						repository.save(dest);
					} catch (Exception e) {
						System.out.println("error while save --  " + e.getMessage());
					}
				}
				log.info("File Uploaded Successfully. Count is == " + products.size());
			}
				List<MaterialMasterFileDataEntity> listFileData =repository.findAll();
				log.info("listFileData is == " + listFileData.size());
				List<MaterialMasterJswEntity> materialMasterList = new ArrayList<>();
	
				for (MaterialMasterFileDataEntity sourceEntity : listFileData) {
					MaterialMasterJswEntity destEntity = new MaterialMasterJswEntity();
					BeanUtils.copyProperties(sourceEntity, destEntity);
					log.info("getMmId is == " + sourceEntity.getMmId());

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
					materialMasterList.add(destEntity);
					try {
						materialMasterRepository.save(destEntity);
					} catch (Exception e) {
						System.out.println("error while save --  "+e.getMessage());
					}
				}
			return new ResponseEntity<Object>("{\"status\": \"success\", \"message\": \"File Uploaded Successfully.\"}", new HttpHeaders(), HttpStatus.OK);
		} catch( Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>("{\"status\": \"failed\", \"message\": \"Failed to Uploaded a file.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
}
