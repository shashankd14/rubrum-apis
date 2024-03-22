package com.steel.product.trading.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steel.product.trading.entity.CategoryEntity;
import com.steel.product.trading.entity.ManufacturerEntity;
import com.steel.product.trading.entity.MaterialMasterEntity;
import com.steel.product.trading.entity.SubCategoryEntity;
import com.steel.product.trading.request.CategoryRequest;
import com.steel.product.trading.request.DeleteRequest;
import com.steel.product.trading.request.ManufacturerRequest;
import com.steel.product.trading.request.MaterialMasterRequest;
import com.steel.product.trading.request.SearchRequest;
import com.steel.product.trading.request.SubCategoryRequest;
import com.steel.product.trading.service.MaterialMasterService;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@Tag(name = "Material Master", description = "Material Master")
@RequestMapping({ "/trading" })
public class MaterialController {

	@Autowired
	private MaterialMasterService materialMasterService;

	// Material Master Master APIs
	@PostMapping(value = "/material/save", produces = "application/json")
	public ResponseEntity<Object> save(
			@RequestParam(value = "materialMasterRequest", required = true) String materialRequest,
			@RequestParam(value = "additionalParams", required = true) String additionalParams,
			@RequestParam(value = "itemImage", required = false) MultipartFile itemImage,
			@RequestParam(value = "crossSectionalImage", required = false) MultipartFile crossSectionalImage) {
		ResponseEntity<Object>  resp= null;
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			MaterialMasterRequest materialMasterRequest = mapper.readValue(materialRequest, MaterialMasterRequest.class);
			materialMasterRequest.setAdditionalParams(additionalParams);
			resp = materialMasterService.save(materialMasterRequest, itemImage, crossSectionalImage);
		} catch ( Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	@PutMapping(value = "/material/update", produces = "application/json")
	public ResponseEntity<Object> update(
			@RequestParam(value = "materialMasterRequest", required = true) String materialRequest,
			@RequestParam(value = "additionalParams", required = true) String additionalParams,
			@RequestParam(value = "itemImage", required = false) MultipartFile itemImage,
			@RequestParam(value = "crossSectionalImage", required = false) MultipartFile crossSectionalImage) {
		ResponseEntity<Object>  resp= null;
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			MaterialMasterRequest materialMasterRequest = mapper.readValue(materialRequest, MaterialMasterRequest.class);
			materialMasterRequest.setAdditionalParams(additionalParams);
			resp = materialMasterService.save(materialMasterRequest, itemImage, crossSectionalImage);
		} catch ( Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	@PostMapping({ "/material/list" })
	public ResponseEntity<Object> getMaterialList(@RequestBody SearchRequest searchPageRequest) {
		Map<String, Object> response = new HashMap<>();

		if (searchPageRequest.getId() != null && searchPageRequest.getId() > 0) {
			MaterialMasterEntity resp = materialMasterService.findByItemId(searchPageRequest.getId());
			return new ResponseEntity<Object>(resp, HttpStatus.OK);
		} else {
			Page<MaterialMasterEntity> pageResult = materialMasterService.getMaterialList(searchPageRequest);
			response.put("content", pageResult.toList());
			response.put("currentPage", pageResult.getNumber());
			response.put("totalItems", pageResult.getTotalElements());
			response.put("totalPages", pageResult.getTotalPages());
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}
	
	@PostMapping(value = "/material/delete", produces = "application/json" )
	public ResponseEntity<Object> materialDelete(@RequestBody DeleteRequest deleteRequest) {
		return materialMasterService.materialDelete(deleteRequest);
	}

	// Category Master APIs
	
	@PostMapping(value = "/category/save", produces = "application/json" )
	public ResponseEntity<Object> categorySave(@RequestBody CategoryRequest categoryRequest) {
		return materialMasterService.categorySave(categoryRequest);
	}
	
	@PutMapping(value = "/category/update", produces = "application/json" )
	public ResponseEntity<Object> categoryUpdate(@RequestBody CategoryRequest categoryRequest) {
		return materialMasterService.categorySave(categoryRequest);
	}

	@PostMapping({ "/category/list" })
	public ResponseEntity<Object> getCategoryList(@RequestBody SearchRequest searchPageRequest) {
		Map<String, Object> response = new HashMap<>();

		if (searchPageRequest.getId() != null && searchPageRequest.getId() > 0) {
			CategoryEntity resp = materialMasterService.findByCategoryId( searchPageRequest.getId());
			return new ResponseEntity<Object>(resp, HttpStatus.OK);
		} else {
			Page<CategoryEntity> pageResult = materialMasterService.getCategoryList(searchPageRequest);
			response.put("content", pageResult.toList());
			response.put("currentPage", pageResult.getNumber());
			response.put("totalItems", pageResult.getTotalElements());
			response.put("totalPages", pageResult.getTotalPages());
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/category/delete", produces = "application/json" )
	public ResponseEntity<Object> categoryDelete(@RequestBody DeleteRequest deleteRequest) {
		return materialMasterService.categoryDelete(deleteRequest);
	}

	// Sub-Category Master APIs

	@PostMapping(value = "/subcategory/save", produces = "application/json" )
	public ResponseEntity<Object> subcategorySave(@RequestBody SubCategoryRequest categoryRequest) {
		return materialMasterService.subcategorySave(categoryRequest);
	}
	
	@PutMapping(value = "/subcategory/update", produces = "application/json" )
	public ResponseEntity<Object> subcategoryUpdate(@RequestBody SubCategoryRequest categoryRequest) {
		return materialMasterService.subcategorySave(categoryRequest);
	}

	@PostMapping({ "/subcategory/list" })
	public ResponseEntity<Object> getsubCategoryList(@RequestBody SearchRequest searchPageRequest) {
		Map<String, Object> response = new HashMap<>();

		if (searchPageRequest.getId() != null && searchPageRequest.getId() > 0) {
			SubCategoryEntity resp = materialMasterService.findBySubCategoryId( searchPageRequest.getId());
			return new ResponseEntity<Object>(resp, HttpStatus.OK);
		} else {
			Page<SubCategoryEntity> pageResult = materialMasterService.getSubCategoryList(searchPageRequest);
			response.put("content", pageResult.toList());
			response.put("currentPage", pageResult.getNumber());
			response.put("totalItems", pageResult.getTotalElements());
			response.put("totalPages", pageResult.getTotalPages());
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/subcategory/delete", produces = "application/json" )
	public ResponseEntity<Object> subcategoryDelete(@RequestBody DeleteRequest deleteRequest) {
		return materialMasterService.subcategoryDelete(deleteRequest);
	}
	

	// Manufacturer Master APIs
	
	@PostMapping(value = "/manufacturer/save", produces = "application/json" )
	public ResponseEntity<Object> manufacturerSave(@RequestBody ManufacturerRequest manufacturerRequest) {
		return materialMasterService.manufacturerSave(manufacturerRequest);
	}
	
	@PutMapping(value = "/manufacturer/update", produces = "application/json" )
	public ResponseEntity<Object> manufacturerUpdate(@RequestBody ManufacturerRequest manufacturerRequest) {
		return materialMasterService.manufacturerSave(manufacturerRequest);
	}

	@PostMapping({ "/manufacturer/list" })
	public ResponseEntity<Object> getManufacturerList(@RequestBody SearchRequest searchPageRequest) {
		Map<String, Object> response = new HashMap<>();

		if (searchPageRequest.getId() != null && searchPageRequest.getId() > 0) {
			ManufacturerEntity resp = materialMasterService.findByManufacturerId( searchPageRequest.getId());
			return new ResponseEntity<Object>(resp, HttpStatus.OK);
		} else {
			Page<ManufacturerEntity> pageResult = materialMasterService.getManufacturerList(searchPageRequest);
			response.put("content", pageResult.toList());
			response.put("currentPage", pageResult.getNumber());
			response.put("totalItems", pageResult.getTotalElements());
			response.put("totalPages", pageResult.getTotalPages());
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/manufacturer/delete", produces = "application/json" )
	public ResponseEntity<Object> manufacturerDelete(@RequestBody DeleteRequest deleteRequest) {
		return materialMasterService.manufacturerDelete(deleteRequest);
	}
}
