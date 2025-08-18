package com.steel.product.jswone.controller;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.steel.product.jswone.entity.MaterialMasterJswEntity;
import com.steel.product.jswone.request.MaterialSearchPageRequest;
import com.steel.product.jswone.request.MaterialUploadRequest;
import com.steel.product.jswone.response.MaterialResponseWithInwardUniqueData;
import com.steel.product.jswone.response.MaterialResponseWithUniqueData;
import com.steel.product.jswone.response.MaterialSearchPageResponse;
import com.steel.product.jswone.service.MaterialMasterJswService;
import com.steel.product.jswone.service.MaterialUploadService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("material")
@Tag(name = "Material Upload", description = "Material Upload")
public class MaterialUploadController {

	@Autowired
	MaterialUploadService materialUploadService;
	
	@Autowired
	private MaterialMasterJswService materialService;
	
	@PostMapping(value = "/importmmiddata", produces = "application/json")
	@Operation
	public ResponseEntity<Object> importmmiddata(@RequestBody MaterialUploadRequest request)
			throws Exception, FileNotFoundException {
		log.info("******MaterialUploadController.uploadmmidData*****");
		if (request.isFileData() || request.isMasterData() ) {
			return materialUploadService.uploadmmidData(request);
		} else {
			return new ResponseEntity<Object>("{\"status\": \"failed\", \"message\": \"Invalid Request to Uploaded a file.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/importinwarddata", produces = "application/json")
	@Operation
	public ResponseEntity<Object> importinwarddata(@RequestBody MaterialUploadRequest request)
			throws Exception, FileNotFoundException {
		log.info("******MaterialUploadController.uploadInwardData*****");
		if (request.isFileData() || request.isMasterData() ) {
			return materialUploadService.uploadInwardData(request);
		} else {
			return new ResponseEntity<Object>("{\"status\": \"failed\", \"message\": \"Invalid Request to Uploaded a file.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/list", produces = "application/json")
	public ResponseEntity<Object> materialSearch(@RequestBody MaterialSearchPageRequest materialSearchPageRequest) {
		Map<String, Object> response = new HashMap<>();
		Map<Integer, String> productMap = new HashMap<>();
		Map<Integer, String> categoryMap = new HashMap<>();
		Map<Integer, String> subCategoryMap = new HashMap<>();
		Map<Integer, String> leafCategoryMap = new HashMap<>();
		Map<Integer, String> brandMap = new HashMap<>();
		Map<Integer, String> gradeMap = new HashMap<>();
		Map<Integer, String> subGradeMap = new HashMap<>();
		Map<Integer, String> formMap = new HashMap<>();
		Map<Integer, String> uomMap = new HashMap<>();
		Map<Integer, String> surfaceMap = new HashMap<>();
		Map<Integer, String> coatingMap = new HashMap<>();

		Page<MaterialMasterJswEntity> packetsList = materialUploadService.materialSearch(materialSearchPageRequest);
		
		if(packetsList != null && packetsList.getSize()>0) {
			productMap = materialService.getProductsMap();
			categoryMap = materialService.getCategoryMap();
			subCategoryMap = materialService.getSubCategoryMap();
			leafCategoryMap = materialService.getLeafCategoryMap();
			brandMap = materialService.getBrandMap();
			gradeMap = materialService.getGradeMap();
			subGradeMap = materialService.getSubGradeMap();
			formMap = materialService.getFormMap();
			uomMap = materialService.getUomMap();
			surfaceMap = materialService.getSurfaceMap();
			coatingMap = materialService.getCoatingMap();
		}
		
		List<MaterialSearchPageResponse> qirList = new ArrayList<>();
		for (MaterialMasterJswEntity result : packetsList) {
			MaterialSearchPageResponse resp = new MaterialSearchPageResponse();
			resp.setMateraiId(result.getMaterialId());
			resp.setMmId(result.getMmId());
			resp.setMmDescription(result.getMmDescription());
			resp.setCategoryId(result.getCategoryId());
			resp.setSubcategoryId(result.getSubcategoryId());
			resp.setLeafcategoryId(result.getLeafcategoryId());
			resp.setBrandId(result.getBrandId());
			resp.setProducttypeId(result.getProducttypeId());
			resp.setGradeId(result.getGradeId());
			resp.setSubgradeId(result.getSubgradeId());
			resp.setFormId(result.getFormId());
			resp.setUomId( result.getUomId());
			resp.setSurfacetypeId( result.getSurfacetypeId() );
			resp.setCoatingtypeId(result.getCoatingtypeId());
			resp.setCategory(categoryMap.get(result.getCategoryId()));
			resp.setSubcategory(subCategoryMap.get(result.getSubcategoryId()));
			resp.setLeafcategory(leafCategoryMap.get(result.getLeafcategoryId()));
			resp.setBrand(brandMap.get(result.getBrandId()));
			resp.setProducttype(productMap.get(result.getProducttypeId()));
			resp.setGrade(gradeMap.get(result.getGradeId()));
			resp.setSubgrade(subGradeMap.get(result.getSubgradeId()));
			resp.setForm(formMap.get(result.getFormId()));
			resp.setUom(uomMap.get( result.getUomId()));
			resp.setSurfacetype(surfaceMap.get(result.getSurfacetypeId()));
			resp.setCoatingtype(coatingMap.get(result.getCoatingtypeId()));
			resp.setThickness(result.getThickness());
			resp.setWidth(result.getWidth());
			resp.setLength(result.getLength());
			resp.setODiameter(result.getODiameter());
			resp.setNb(result.getNb());
			resp.setIDiameter(result.getIDiameter());
			resp.setColour("" + result.getColour());
			resp.setHsn(result.getHsn());
			resp.setTax(result.getTax());
			resp.setVariantKey(result.getVariantKey());
			qirList.add(resp);
		}
		response.put("content", qirList);
		response.put("currentPage", packetsList.getNumber());
		response.put("totalItems", packetsList.getTotalElements());
		response.put("totalPages", packetsList.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	@PostMapping(value = "/listwithuniquedata", produces = "application/json")
	public ResponseEntity<Object> listwithuniquedata(@RequestBody MaterialSearchPageRequest materialSearchPageRequest) {
		Map<Integer, String> productMap = new HashMap<>();
		Map<Integer, String> categoryMap = new HashMap<>();
		Map<Integer, String> subCategoryMap = new HashMap<>();
		Map<Integer, String> leafCategoryMap = new HashMap<>();
		Map<Integer, String> brandMap = new HashMap<>();
		Map<Integer, String> gradeMap = new HashMap<>();
		Map<Integer, String> subGradeMap = new HashMap<>();
		Map<Integer, String> formMap = new HashMap<>();
		Map<Integer, String> uomMap = new HashMap<>();
		Map<Integer, String> surfaceMap = new HashMap<>();
		Map<Integer, String> coatingMap = new HashMap<>();
		
		Map<Integer, String> productMapNew = new TreeMap<>();
		Map<Integer, String> categoryMapNew = new TreeMap<>();
		Map<Integer, String> subCategoryMapNew = new TreeMap<>();
		Map<Integer, String> leafCategoryMapNew = new TreeMap<>();
		Map<Integer, String> brandMapNew = new TreeMap<>();
		Map<Integer, String> gradeMapNew = new TreeMap<>();
		Map<Integer, String> subGradeMapNew = new TreeMap<>();
		Map<Integer, String> formMapNew = new TreeMap<>();
		Map<Integer, String> uomMapNew = new TreeMap<>();
		Map<Integer, String> surfaceMapNew = new TreeMap<>();
		Map<Integer, String> coatingMapNew = new TreeMap<>();
		Map<BigDecimal, BigDecimal> thicknessMapNew = new TreeMap<>();
		Map<BigDecimal, BigDecimal> oDiameterMapNew = new TreeMap<>();
		Map<BigDecimal, BigDecimal> widthMapNew = new TreeMap<>();
		Map<BigDecimal, BigDecimal> iDiameterMapNew = new TreeMap<>();
		Map<BigDecimal, BigDecimal> nbMapNew = new TreeMap<>();
		
		materialSearchPageRequest.setPageNo(1);
		materialSearchPageRequest.setPageSize(1000000);
		
		Page<MaterialMasterJswEntity> packetsList = materialUploadService.materialSearch(materialSearchPageRequest);

		if (packetsList != null && packetsList.getSize() > 0) {
			productMap = materialService.getProductsMap();
			categoryMap = materialService.getCategoryMap();
			subCategoryMap = materialService.getSubCategoryMap();
			leafCategoryMap = materialService.getLeafCategoryMap();
			brandMap = materialService.getBrandMap();
			gradeMap = materialService.getGradeMap();
			subGradeMap = materialService.getSubGradeMap();
			formMap = materialService.getFormMap();
			uomMap = materialService.getUomMap();
			surfaceMap = materialService.getSurfaceMap();
			coatingMap = materialService.getCoatingMap();
		}
		
		MaterialResponseWithUniqueData resp = new MaterialResponseWithUniqueData();
		for (MaterialMasterJswEntity result : packetsList) {
			productMapNew.put(result.getProducttypeId(), productMap.get(result.getProducttypeId()));
			categoryMapNew.put(result.getCategoryId(), categoryMap.get(result.getCategoryId()));
			subCategoryMapNew.put(result.getSubcategoryId(), subCategoryMap.get(result.getSubcategoryId()));
			leafCategoryMapNew.put(result.getLeafcategoryId(), leafCategoryMap.get(result.getLeafcategoryId()));
			brandMapNew.put(result.getBrandId(), brandMap.get(result.getBrandId()));
			gradeMapNew.put(result.getGradeId(), gradeMap.get(result.getGradeId()));			
			subGradeMapNew.put(result.getSubgradeId(), subGradeMap.get(result.getSubgradeId()));
			formMapNew.put(result.getFormId(), formMap.get(result.getFormId()));
			uomMapNew.put(result.getUomId(), uomMap.get(result.getUomId()));
			surfaceMapNew.put(result.getSurfacetypeId(), surfaceMap.get(result.getSurfacetypeId()));
			coatingMapNew.put(result.getCoatingtypeId(), coatingMap.get(result.getCoatingtypeId()));
			thicknessMapNew.put(result.getThickness(), result.getThickness());
			widthMapNew.put(result.getWidth(), result.getWidth());
			oDiameterMapNew.put(result.getODiameter(), result.getODiameter());
			iDiameterMapNew.put(result.getIDiameter(), result.getIDiameter());
			nbMapNew.put(result.getNb(), result.getNb());
		}
		resp.setProductMap(productMapNew);
		resp.setCategoryMap(categoryMapNew);
		resp.setSubCategoryMap(subCategoryMapNew);
		resp.setLeafCategoryMap(leafCategoryMapNew);
		resp.setBrandMap(brandMapNew);
		resp.setGradeMap(gradeMapNew);
		resp.setSubGradeMap(subGradeMapNew);
		resp.setFormMap(formMapNew);
		resp.setUomMap(uomMapNew);
		resp.setSurfaceMap(surfaceMapNew);
		resp.setCoatingMap(coatingMapNew);
		resp.setThicknessMap(thicknessMapNew);
		resp.setWidthMap(widthMapNew);
		resp.setODiameterMap(oDiameterMapNew);
		resp.setIDiameterMap(iDiameterMapNew);
		resp.setNbMap(nbMapNew); 
		return new ResponseEntity<Object>(resp, HttpStatus.OK);
	}
	
	@PostMapping(value = "/mmidmasterusedinward", produces = "application/json")
	public ResponseEntity<Object> masterlistusedininward(@RequestBody MaterialSearchPageRequest request) {
		Map<Integer, String> productMapNew = new TreeMap<>();
		Map<Integer, String> brandMapNew = new TreeMap<>();
		Map<Integer, String> gradeMapNew = new TreeMap<>();
		Map<Integer, String> subGradeMapNew = new TreeMap<>();
		
		request.setPageNo(1);
		request.setPageSize(1000000);

		List<Object[]> packetsList = materialUploadService.mmidmasterusedinward(request);
		MaterialResponseWithInwardUniqueData resp = new MaterialResponseWithInwardUniqueData();

		for (Object[] result : packetsList) {
			Integer productNameId = (result[0] != null ? (Integer) result[0] : null);
			Integer gradeId = (result[1] != null ? (Integer) result[1] : null);
			Integer subGradeId = (result[2] != null ? (Integer) result[2] : null);
			Integer brandId = (result[3] != null ? (Integer) result[3] : null);

			String productName = (result[4] != null ? (String) result[4] : null);
			String gradeName = (result[5] != null ? (String) result[5] : null);
			String subGradeName = (result[6] != null ? (String) result[6] : null);
			String brandName = (result[7] != null ? (String) result[7] : null);

			productMapNew.put(productNameId, productName);
			gradeMapNew.put(gradeId, gradeName);
			subGradeMapNew.put(subGradeId, subGradeName);
			brandMapNew.put(brandId, brandName);
		}
		resp.setProductMap(productMapNew);
		resp.setBrandMap(brandMapNew);
		resp.setGradeMap(gradeMapNew);
		resp.setSubGradeMap(subGradeMapNew);
		return new ResponseEntity<Object>(resp, HttpStatus.OK);
	}
	
	@PostMapping(value = "/mmid", produces = "application/json")
	public ResponseEntity<Object> materialSearchBymmid(@RequestBody MaterialSearchPageRequest materialSearchPageRequest) {
		Map<String, Object> response = new HashMap<>();

		Page<Object[]> packetsList = materialUploadService.materialSearchBymmid(materialSearchPageRequest);
		List<MaterialSearchPageResponse> qirList = new ArrayList<>();

		for (Object[] result : packetsList) {
			MaterialSearchPageResponse resp = new MaterialSearchPageResponse();
			resp.setMateraiId(result[0] != null ? (Integer) result[0] : null);
			resp.setMmId(result[1] != null ? (String) result[1] : null);
			resp.setMmDescription(result[2] != null ? (String) result[2] : null);
			resp.setCategory( result[3] != null ? (String) result[3] : null);
			resp.setSubcategory(result[5] != null ? (String) result[5] : null);
			resp.setLeafcategory(result[7] != null ? (String) result[7] : null);
			resp.setBrand(result[9] != null ? (String) result[9] : null);
			resp.setProducttype(result[11] != null ? (String) result[11] : null);
			resp.setGrade(result[13] != null ? (String) result[13] : null);
			resp.setSubgrade(result[15] != null ? (String) result[15] : null);
			resp.setForm(result[17] != null ? (String) result[17] : null);
			resp.setUom(result[19] != null ? (String) result[19] : null);
			resp.setSurfacetype(result[21] != null ? (String) result[21] : null);
			resp.setCoatingtype(result[23] != null ? (String) result[23] : null);
			resp.setDiameter(result[24] != null ? (String) result[24] : null);
			resp.setThickness(result[25] != null ? (BigDecimal) result[25] : null);
			resp.setWidth(result[26] != null ? (BigDecimal) result[26] : null);
			resp.setLength(result[27] != null ? (BigDecimal) result[27] : null);
			resp.setODiameter( result[33] != null ? (BigDecimal) result[33] : null);
			resp.setNb(result[34] != null ? (BigDecimal) result[34] : null);
			resp.setIDiameter(result[35] != null ? (BigDecimal) result[35] : null);
			//resp.setSpangletype(result[28] != null ? (String) result[28] : null);
			resp.setColour(result[29] != null ? (String) result[29] : null);
			resp.setHsn(result[30] != null ? (String) result[30] : null);
			resp.setTax(result[31] != null ? (String) result[31] : null);
			resp.setVariantKey(result[32] != null ? (String) result[32] : null);
			qirList.add(resp);
		}
		response.put("content", qirList);
		response.put("currentPage", packetsList.getNumber());
		response.put("totalItems", packetsList.getTotalElements());
		response.put("totalPages", packetsList.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
}
