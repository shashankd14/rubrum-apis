package com.steel.product.trading.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steel.product.trading.entity.MaterialMasterEntity;
import com.steel.product.trading.request.MaterialMasterRequest;
import com.steel.product.trading.request.MaterialMasterSearch;
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
@RequestMapping({ "/trading/material" })
public class MaterialController {

	@Autowired
	private MaterialMasterService materialMasterService;

	@PostMapping(value = "/save", produces = "application/json")
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

	@PostMapping({ "/list" })
	public ResponseEntity<Object> getMaterialList(@RequestBody MaterialMasterSearch searchListPageRequest) {
		Map<String, Object> response = new HashMap<>();

		if (searchListPageRequest.getItemId() != null && searchListPageRequest.getItemId() > 0) {
			MaterialMasterEntity kk = materialMasterService.findByItemId(searchListPageRequest.getItemId());
			return new ResponseEntity<Object>(kk, HttpStatus.OK);
		} else {
			Page<MaterialMasterEntity> pageResult = materialMasterService.getMaterialList(searchListPageRequest);
			response.put("content", pageResult.toList());
			response.put("currentPage", pageResult.getNumber());
			response.put("totalItems", pageResult.getTotalElements());
			response.put("totalPages", pageResult.getTotalPages());
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}

	}
	 
}
