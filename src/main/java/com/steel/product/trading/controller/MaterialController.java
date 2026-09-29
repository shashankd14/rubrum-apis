package com.steel.product.trading.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steel.product.application.dto.process.ProcessDto;
import com.steel.product.application.entity.Process;
import com.steel.product.application.service.ProcessService;
import com.steel.product.trading.dto.CategoryResponse;
import com.steel.product.trading.dto.SubCategoryResponse;
import com.steel.product.trading.entity.BrandEntity;
import com.steel.product.trading.entity.CategoryEntity;
import com.steel.product.trading.entity.ItemgradeEntity;
import com.steel.product.trading.entity.ManufacturerEntity;
import com.steel.product.trading.entity.MaterialMasterEntity;
import com.steel.product.trading.entity.SubCategoryEntity;
import com.steel.product.trading.request.BrandRequest;
import com.steel.product.trading.request.CategoryRequest;
import com.steel.product.trading.request.DeleteRequest;
import com.steel.product.trading.request.ItemgradeRequest;
import com.steel.product.trading.request.ManufacturerRequest;
import com.steel.product.trading.request.MaterialMasterRequest;
import com.steel.product.trading.request.SearchRequest;
import com.steel.product.trading.request.SubCategoryRequest;
import com.steel.product.trading.service.MaterialMasterService;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@Tag(name = "Material Master", description = "Material Master")
public class MaterialController {

	@Autowired
	private MaterialMasterService materialMasterService;

	@Autowired
	private ProcessService processService;

	// Material Master Master APIs
	@GetMapping(value = "/process/options", produces = "application/json")
	public List<ProcessDto> getProcessOptions() {
		return processService.getAll().stream()
				.map(Process::valueOf)
				.collect(Collectors.toList());
	}

	@PostMapping(value = "/material/save", produces = "application/json")
	public ResponseEntity<Object> save(
			@RequestParam(value = "materialMasterRequest", required = true) String materialRequest,
			@RequestParam(value = "technicalSpecs", required = false) String technicalSpecs,
			@RequestParam(value = "unitWeights", required = false) String unitWeights,
			@RequestParam(value = "additionalParams", required = false) String additionalParams,
			@RequestParam(value = "itemImage", required = false) MultipartFile itemImage,
			@RequestParam(value = "crossSectionalImage", required = false) MultipartFile crossSectionalImage) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		MaterialMasterRequest materialMasterRequest;
		try {
			materialMasterRequest = mapper.treeToValue(
					readMultipartJsonObject(mapper, materialRequest, "materialMasterRequest"),
					MaterialMasterRequest.class);
		} catch (Exception e) {
			return invalidJsonResponse("materialMasterRequest", e);
		}

		try {
			materialMasterRequest.setAdditionalParams(normalizeAdditionalParams(mapper, additionalParams));
		} catch (Exception e) {
			return invalidJsonResponse("additionalParams", e);
		}

		return materialMasterService.save(materialMasterRequest, itemImage, crossSectionalImage);
	}

	@PutMapping(value = "/material/update", produces = "application/json")
	public ResponseEntity<Object> update(
			@RequestParam(value = "materialMasterRequest", required = true) String materialRequest,
			@RequestParam(value = "technicalSpecs", required = false) String technicalSpecs,
			@RequestParam(value = "unitWeights", required = false) String unitWeights,
			@RequestParam(value = "additionalParams", required = false) String additionalParams,
			@RequestParam(value = "itemImage", required = false) MultipartFile itemImage,
			@RequestParam(value = "crossSectionalImage", required = false) MultipartFile crossSectionalImage) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		MaterialMasterRequest materialMasterRequest;
		try {
			materialMasterRequest = mapper.treeToValue(
					readMultipartJsonObject(mapper, materialRequest, "materialMasterRequest"),
					MaterialMasterRequest.class);
		} catch (Exception e) {
			return invalidJsonResponse("materialMasterRequest", e);
		}

		try {
			materialMasterRequest.setAdditionalParams(normalizeAdditionalParams(mapper, additionalParams));
		} catch (Exception e) {
			return invalidJsonResponse("additionalParams", e);
		}

		return materialMasterService.save(materialMasterRequest, itemImage, crossSectionalImage);
	}

	private String normalizeAdditionalParams(ObjectMapper mapper, String additionalParams) throws Exception {
		JsonNode additionalParamsJson = readMultipartJsonObject(mapper, additionalParams, "additionalParams");

		JsonNode technicalSpecs = additionalParamsJson.get("technicalSpecs");
		if (technicalSpecs != null && !technicalSpecs.isObject()) {
			throw new IllegalArgumentException("technicalSpecs must be a JSON object");
		}

		if (technicalSpecs != null) {
			JsonNode customParameters = technicalSpecs.get("customParameters");
			if (customParameters != null && !customParameters.isArray()) {
				throw new IllegalArgumentException("customParameters must be a JSON array");
			}
		}

		JsonNode unitWeights = additionalParamsJson.get("unitWeights");
		if (unitWeights != null && !unitWeights.isArray()) {
			throw new IllegalArgumentException("unitWeights must be a JSON array");
		}

		return mapper.writeValueAsString(additionalParamsJson);
	}

	private JsonNode readMultipartJsonObject(ObjectMapper mapper, String value, String fieldName) throws Exception {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException(fieldName + " is required");
		}

		String json = value.trim();
		if (json.length() >= 2 && json.startsWith("'") && json.endsWith("'")) {
			json = json.substring(1, json.length() - 1).trim();
		}

		JsonNode jsonNode = mapper.readTree(json);
		if (jsonNode != null && jsonNode.isTextual()) {
			jsonNode = mapper.readTree(jsonNode.asText());
		}
		if (jsonNode == null || !jsonNode.isObject()) {
			throw new IllegalArgumentException(fieldName + " must be a JSON object");
		}
		return jsonNode;
	}

	private ResponseEntity<Object> invalidJsonResponse(String fieldName, Exception exception) {
		Map<String, String> response = new HashMap<>();
		response.put("status", "fail");
		response.put("message", "Invalid " + fieldName + ": " + exception.getMessage());
		return ResponseEntity.badRequest().body(response);
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
			if (resp == null) {
				return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<Object>(buildCategoryResponses(Collections.singletonList(resp)).get(0), HttpStatus.OK);
		} else {
			Page<CategoryEntity> pageResult = materialMasterService.getCategoryList(searchPageRequest);
			response.put("content", buildCategoryResponses(pageResult.toList()));
			response.put("currentPage", pageResult.getNumber());
			response.put("totalItems", pageResult.getTotalElements());
			response.put("totalPages", pageResult.getTotalPages());
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	private List<CategoryResponse> buildCategoryResponses(List<CategoryEntity> categories) {
		List<Integer> categoryIds = new ArrayList<>();
		Map<Integer, String> categoryNames = new HashMap<>();
		for (CategoryEntity category : categories) {
			categoryIds.add(category.getCategoryId());
			categoryNames.put(category.getCategoryId(), category.getCategoryName());
		}

		Map<Integer, List<SubCategoryResponse>> subcategoriesByCategory = new HashMap<>();
		for (SubCategoryEntity subcategory : materialMasterService.getSubCategoriesByCategoryIds(categoryIds)) {
			SubCategoryResponse subcategoryResponse = new SubCategoryResponse();
			subcategoryResponse.setSubcategoryId(subcategory.getSubcategoryId());
			subcategoryResponse.setSubcategoryName(subcategory.getSubcategoryName());
			subcategoryResponse.setSubcategoryHsnCode(subcategory.getSubcategoryHsnCode());
			subcategoryResponse.setCategoryId(subcategory.getCategoryId());
			subcategoryResponse.setCategoryName(categoryNames.get(subcategory.getCategoryId()));
			subcategoriesByCategory
					.computeIfAbsent(subcategory.getCategoryId(), id -> new ArrayList<>())
					.add(subcategoryResponse);
		}

		List<CategoryResponse> responses = new ArrayList<>();
		for (CategoryEntity category : categories) {
			CategoryResponse categoryResponse = new CategoryResponse();
			categoryResponse.setCategoryId(category.getCategoryId());
			categoryResponse.setCategoryName(category.getCategoryName());
			categoryResponse.setCategoryHsnCode(category.getCategoryHsnCode());
			categoryResponse.setIsDeleted(category.getIsDeleted());
			categoryResponse.setCreatedBy(category.getCreatedBy());
			categoryResponse.setUpdatedBy(category.getUpdatedBy());
			categoryResponse.setCreatedOn(category.getCreatedOn());
			categoryResponse.setUpdatedOn(category.getUpdatedOn());
			categoryResponse.setSubcategories(
					subcategoriesByCategory.getOrDefault(category.getCategoryId(), Collections.emptyList()));
			responses.add(categoryResponse);
		}
		return responses;
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
			Page<Object[]> pageResult = materialMasterService.getSubCategoryList(searchPageRequest);
			List<SubCategoryResponse> list = new ArrayList<>();
			for (Object[] result : pageResult) {
				SubCategoryResponse subCategoryResponse= new SubCategoryResponse();
				subCategoryResponse.setSubcategoryId(result[0] != null ? (Integer) result[0] : null);
				subCategoryResponse.setSubcategoryName(result[1] != null ? (String) result[1] : null);
				subCategoryResponse.setSubcategoryHsnCode(result[2] != null ? (String) result[2] : null);
				subCategoryResponse.setCategoryId(result[3] != null ? (Integer) result[3] : null);
				subCategoryResponse.setCategoryName(result[4] != null ? (String) result[4] : null);
				list.add(subCategoryResponse);
			}
			response.put("content", list);
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

	// Brand Master APIs
	
	@PostMapping(value = "/brand/save", produces = "application/json" )
	public ResponseEntity<Object> brandSave(@RequestBody BrandRequest brandRequest) {
		return materialMasterService.brandSave(brandRequest);
	}
	
	@PutMapping(value = "/brand/update", produces = "application/json" )
	public ResponseEntity<Object> manufacturerUpdate(@RequestBody BrandRequest brandRequest) {
		return materialMasterService.brandSave(brandRequest);
	}

	@PostMapping({ "/brand/list" })
	public ResponseEntity<Object> getBrandList(@RequestBody SearchRequest searchPageRequest) {
		Map<String, Object> response = new HashMap<>();

		if (searchPageRequest.getId() != null && searchPageRequest.getId() > 0) {
			BrandEntity resp = materialMasterService.findByBrandId( searchPageRequest.getId());
			return new ResponseEntity<Object>(resp, HttpStatus.OK);
		} else {
			Page<BrandEntity> pageResult = materialMasterService.getBrandList(searchPageRequest);
			response.put("content", pageResult.toList());
			response.put("currentPage", pageResult.getNumber());
			response.put("totalItems", pageResult.getTotalElements());
			response.put("totalPages", pageResult.getTotalPages());
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/brand/delete", produces = "application/json" )
	public ResponseEntity<Object> brandDelete(@RequestBody DeleteRequest deleteRequest) {
		return materialMasterService.brandDelete(deleteRequest);
	}
	
	// ItemGrade Master APIs
	
	@PostMapping(value = "/itemgrade/save", produces = "application/json" )
	public ResponseEntity<Object> itemgradeSave(@RequestBody ItemgradeRequest itemgradeRequest) {
		return materialMasterService.itemgradeSave(itemgradeRequest);
	}
	
	@PutMapping(value = "/itemgrade/update", produces = "application/json" )
	public ResponseEntity<Object> itemgradeUpdate(@RequestBody ItemgradeRequest itemgradeRequest) {
		return materialMasterService.itemgradeSave(itemgradeRequest);
	}

	@PostMapping({ "/itemgrade/list" })
	public ResponseEntity<Object> getitemgradeList(@RequestBody SearchRequest searchPageRequest) {
		Map<String, Object> response = new HashMap<>();

		if (searchPageRequest.getId() != null && searchPageRequest.getId() > 0) {
			ItemgradeEntity resp = materialMasterService.findByItemgradeId( searchPageRequest.getId());
			return new ResponseEntity<Object>(resp, HttpStatus.OK);
		} else {
			Page<ItemgradeEntity> pageResult = materialMasterService.getItemgradeList(searchPageRequest);
			response.put("content", pageResult.toList());
			response.put("currentPage", pageResult.getNumber());
			response.put("totalItems", pageResult.getTotalElements());
			response.put("totalPages", pageResult.getTotalPages());
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/itemgrade/delete", produces = "application/json" )
	public ResponseEntity<Object> itemgradeDelete(@RequestBody DeleteRequest deleteRequest) {
		return materialMasterService.itemgradeDelete(deleteRequest);
	}
	
	
}
