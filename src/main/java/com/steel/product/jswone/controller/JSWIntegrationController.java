package com.steel.product.jswone.controller;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.steel.product.jswone.request.MMIDReceiveMainRequest;
import com.steel.product.jswone.request.POIntegrationRequest;
import com.steel.product.jswone.response.PODetailsLineItemResponse;
import com.steel.product.jswone.response.PODetailsMainResponse;
import com.steel.product.jswone.response.POListResponse;
import com.steel.product.jswone.response.POWiseInwardListResponse;
import com.steel.product.jswone.service.JSWIntegrationService;

@RestController
@CrossOrigin
@Tag(name = "Integration APIs", description = "Integration APIs")
public class JSWIntegrationController {

	@Autowired
	private JSWIntegrationService service;

	@PostMapping(value = "/xternal/poreceive", produces = "application/json")
	public ResponseEntity<Object> poreceive(@RequestBody POIntegrationRequest request, HttpServletRequest httprequest) {
		String ipAddress = httprequest.getRemoteAddr();
		request.setIpAddress(ipAddress);
		return service.poReceive(request);
	}

	@PostMapping(value = "/xternal/mmidreceive", produces = "application/json")
	public ResponseEntity<Object> mmidreceive(@RequestBody MMIDReceiveMainRequest request,
			HttpServletRequest httprequest) {
		return service.mmidreceive(request);
	}

	@PostMapping(value = "/location/warehouse/polist", produces = "application/json")
	public ResponseEntity<Object> locationwisePOList(@RequestBody POIntegrationRequest request,
			HttpServletRequest httprequest) {
		List<Object[]> locationwisePOList1 = service.locationwisePOList(request);
		List<POListResponse> locationwisePOList = new ArrayList<POListResponse>();

		for (Object[] result : locationwisePOList1) {
			POListResponse resp = new POListResponse();
			resp.setPoReference(result[0] != null ? (String) result[0] : null);
			resp.setPoId(result[1] != null ? (String) result[1] : null);
			locationwisePOList.add(resp);
		}
		return new ResponseEntity<Object>(locationwisePOList, HttpStatus.OK);
	}

	@PostMapping(value = "/xternal/podetails", produces = "application/json")
	public ResponseEntity<Object> poDetails(@RequestBody POIntegrationRequest request,
			HttpServletRequest httprequest) {
		PODetailsMainResponse resp = service.podetails(request);
		List<String> locationwisePOList = new ArrayList<String>();
		Map<String, Object> response = new HashMap<>();
		for (PODetailsLineItemResponse result1 : resp.getPurchaseorder().getLine_items()) {
			locationwisePOList.add(result1.getSku());
		}
		response.put("code", resp.getCode());
		response.put("message", resp.getMessage());
		response.put("data", locationwisePOList);

		if ("0".equals(resp.getCode())) {
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} else {
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/xternal/postgrn", produces = "application/json")
	public ResponseEntity<Object> postgrn(@RequestBody POIntegrationRequest request, HttpServletRequest httprequest) {
		PODetailsMainResponse resp = service.postgrn(request);
		Map<String, Object> response = new HashMap<>();
		if ("0".equals(resp.getCode())) {
			response.put("code", resp.getCode());
			response.put("message", resp.getMessage());
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} else {
			response.put("code", resp.getCode());
			response.put("message", resp.getMessage());
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/xternal/powiseinwardlist", produces = "application/json")
	public ResponseEntity<Object> poWiseInwardList(@RequestBody POIntegrationRequest request, HttpServletRequest httprequest) {
		List<POWiseInwardListResponse> inwardList = service.poWiseInwardList(request);
		Map<String, Object> response = new HashMap<>();
		if (inwardList!=null && inwardList.size() > 0 ) {
			response.put("code", "success");
			response.put("message", "success");
			return new ResponseEntity<Object>(inwardList, HttpStatus.OK);
		} else {
			response.put("code", "fail");
			response.put("message","Data not available");
			return new ResponseEntity<Object>(inwardList, HttpStatus.BAD_REQUEST);
		}
	}

}
