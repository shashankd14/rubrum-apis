package com.steel.product.jswone.controller;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.steel.product.jswone.response.MaterialSearchPageResponse;
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
	
	@PostMapping(value = "/uploadcsv", produces = "application/json")
	@Operation
	public ResponseEntity<Object> uploadcsv(@RequestBody MaterialUploadRequest request)
			throws Exception, FileNotFoundException {
		log.info("******MaterialUploadController.uploadcsv*****");
		if (request.isUploadFlag()) {
			return materialUploadService.uploadcsv(request);
		} else {
			return new ResponseEntity<Object>("{\"status\": \"failed\", \"message\": \"Invalid Request to Uploaded a file.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/list", produces = "application/json")
	public ResponseEntity<Object> materialSearch(@RequestBody MaterialSearchPageRequest materialSearchPageRequest) {
		Map<String, Object> response = new HashMap<>();

		Page<MaterialMasterJswEntity> packetsList = materialUploadService.materialSearch(materialSearchPageRequest);
		
		List<MaterialSearchPageResponse> qirList = new ArrayList<>();
		for (MaterialMasterJswEntity result : packetsList) {
			MaterialSearchPageResponse resp = new MaterialSearchPageResponse();
			resp.setMateraiId(result.getMaterialId());
			resp.setMmId(result.getMmId());
			resp.setMmDescription(result.getMmDescription());
			resp.setCategory(""+result.getCategoryId());
			resp.setSubcategory(""+result.getSubcategoryId());
			resp.setLeafcategory(""+result.getLeafcategoryId());
			resp.setBrand(""+result.getBrandId());
			resp.setProducttype(""+result.getProducttypeId());
			resp.setGrade(""+result.getGradeId());
			resp.setSubgrade(""+result.getSubgradeId());
			resp.setForm(""+result.getFormId());
			resp.setUom(""+result.getUomId());
			resp.setSurfacetype(""+result.getSurfacetypeId());
			resp.setCoatingtype(""+result.getCoatingtypeId());
			resp.setThickness(result.getThickness());
			resp.setWidth(result.getWidth());
			resp.setLength(result.getLength());
			resp.setODiameter(result.getODiameter());
			resp.setNb(result.getNb());
			resp.setIDiameter(result.getIDiameter());
			resp.setColour(""+result.getColour());
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
	
	/*
	@PostMapping(value = "/upload", produces = "application/json")
	@Operation
	public ResponseEntity<Object> upload(@Valid @ModelAttribute MaterialUploadRequest request)
			throws Exception, FileNotFoundException {
		log.info("******MaterialUploadController.upload*****");
		return materialUploadService.uploadExcel(request);
	} 
	
	@PostMapping(value = "/listOld", produces = "application/json")
	public ResponseEntity<Object> materialSearch1(@RequestBody MaterialSearchPageRequest materialSearchPageRequest) {
		Map<String, Object> response = new HashMap<>();

		Page<Object[]> packetsList = materialUploadService.materialSearch(materialSearchPageRequest);
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
	*/

}
