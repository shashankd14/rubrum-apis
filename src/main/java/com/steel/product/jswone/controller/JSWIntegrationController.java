package com.steel.product.jswone.controller;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.steel.product.jswone.request.MMIDReceiveMainRequest;
import com.steel.product.jswone.request.POIntegrationRequest;
import com.steel.product.jswone.request.POListResponse;
import com.steel.product.jswone.service.JSWIntegrationService;

@RestController
@CrossOrigin
@Tag(name = "Integration APIs", description = "Integration APIs")
//@RequestMapping({ "/xternal" })
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
			resp.setWarehouseId(result[1] != null ? (String) result[1] : null);
			locationwisePOList.add(resp);
		}
		return new ResponseEntity<Object>(locationwisePOList, HttpStatus.OK);
	}

}
