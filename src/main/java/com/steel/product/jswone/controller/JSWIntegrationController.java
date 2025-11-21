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

import com.steel.product.application.dto.quality.ListPageSearchRequest;
import com.steel.product.jswone.request.MMIDReceiveMainRequest;
import com.steel.product.jswone.request.POSOIntegrationRequest;
import com.steel.product.jswone.response.PODetailsLineItemResponse;
import com.steel.product.jswone.response.PODetailsMainResponse;
import com.steel.product.jswone.response.POListResponse;
import com.steel.product.jswone.response.PoGrnMainResponse;
import com.steel.product.jswone.response.SOListResponse;
import com.steel.product.jswone.service.JSWIntegrationService;

@RestController
@CrossOrigin
@Tag(name = "Integration APIs", description = "Integration APIs")
public class JSWIntegrationController {

	@Autowired
	private JSWIntegrationService service;

	@PostMapping(value = "/xternal/poreceive", produces = "application/json")
	public ResponseEntity<Object> poreceive(@RequestBody POSOIntegrationRequest request, HttpServletRequest httprequest) {
		String ipAddress = httprequest.getRemoteAddr();
		request.setIpAddress(ipAddress);
		return service.poReceive(request);
	}

	@PostMapping(value = "/xternal/mmidreceive", produces = "application/json")
	public ResponseEntity<Object> mmidreceive(@RequestBody MMIDReceiveMainRequest request,
			HttpServletRequest httprequest) {
		return service.mmidreceive(request);
	}

	@PostMapping(value = "/xternal/soreceive", produces = "application/json")
	public ResponseEntity<Object> soreceive(@RequestBody POSOIntegrationRequest request, HttpServletRequest httprequest) {
		String ipAddress = httprequest.getRemoteAddr();
		request.setIpAddress(ipAddress);
		return service.soReceive(request);
	}

	@PostMapping(value = "/location/warehouse/polist", produces = "application/json")
	public ResponseEntity<Object> locationwisePOList(@RequestBody POSOIntegrationRequest request,
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

	@PostMapping(value = "/location/warehouse/solist", produces = "application/json")
	public ResponseEntity<Object> locationwiseSOList(@RequestBody POSOIntegrationRequest request,
			HttpServletRequest httprequest) {
		List<Object[]> locationwisePOList1 = service.locationwiseSOList(request);
		List<SOListResponse> locationwisePOList = new ArrayList<SOListResponse>();

		for (Object[] result : locationwisePOList1) {
			SOListResponse resp = new SOListResponse();
			resp.setSoNo(result[0] != null ? (String) result[0] : null);
			resp.setSoId(result[1] != null ? (String) result[1] : null);
			locationwisePOList.add(resp);
		}
		return new ResponseEntity<Object>(locationwisePOList, HttpStatus.OK);
	}

	@PostMapping(value = "/xternal/podetails", produces = "application/json")
	public ResponseEntity<Object> poDetails(@RequestBody POSOIntegrationRequest request,
			HttpServletRequest httprequest) {
		PODetailsMainResponse resp;
		Map<String, Object> response = new HashMap<>();
		try {
			resp = service.podetails(request);
			List<String> locationwisePOList = new ArrayList<String>();
			for (PODetailsLineItemResponse result1 : resp.getPurchaseorder().getLine_items()) {
				locationwisePOList.add(result1.getSku());
			}
			response.put("code", resp.getCode());
			response.put("message", resp.getMessage());
			response.put("data", locationwisePOList);

			if ("0".equals(resp.getCode())) {
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			} else {
				return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			response.put("code", "57");
			response.put("message", "JSW Connector API Not Working, Please Contact JSW Admin Team ");
			response.put("data", "");
			return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
		}
		
	}

	@PostMapping(value = "/xternal/postgrn", produces = "application/json")
	public ResponseEntity<Object> postgrn(@RequestBody POSOIntegrationRequest request, HttpServletRequest httprequest) {
		Map<String, Object> response = new HashMap<>();
		try {
			PoGrnMainResponse resp = service.postgrn(request);
			if (resp != null && resp.getCode() !=null && "0".equals(resp.getCode())) {
				response.put("code", resp.getCode());
				response.put("message", resp.getMessage());
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			} else if (resp != null && resp.getCode() !=null) {
				response.put("code", resp.getCode());
				response.put("message", resp.getMessage());
				return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
			} else {
				response.put("code", "57");
				response.put("message", "JSW Connector API Not Working, Please Contact JSW Admin Team ");
				response.put("data", "");
				return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			response.put("code", "57");
			response.put("message", "JSW Connector API Not Working, Please Contact JSW Admin Team ");
			response.put("data", "");
			return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/xternal/uploaddoc", produces = "application/json")
	public ResponseEntity<Object> uploaddoc(@RequestBody POSOIntegrationRequest request, HttpServletRequest httprequest) {
		Map<String, Object> response = new HashMap<>();
		try {
			PODetailsMainResponse resp = service.uploadDocument(request);
			if (resp != null && resp.getCode() !=null && "0".equals(resp.getCode())) {
				response.put("code", resp.getCode());
				response.put("message", resp.getMessage());
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			} else if (resp != null && resp.getCode() !=null) {
				response.put("code", resp.getCode());
				response.put("message", resp.getMessage());
				return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
			} else {
				response.put("code", "57");
				response.put("message", "JSW Connector API Not Working, Please Contact JSW Admin Team ");
				response.put("data", "");
				return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			response.put("code", "57");
			response.put("message", "JSW Connector API Not Working, Please Contact JSW Admin Team ");
			response.put("data", "");
			return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/xternal/powiseinwardlist", produces = "application/json")
	public ResponseEntity<Object> poWiseInwardList(@RequestBody POSOIntegrationRequest request,
			HttpServletRequest httprequest) {
		Map<String, Object> response = service.poWiseInwardList(request);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/xternal/allpoinvlist", produces = "application/json")
	public ResponseEntity<Object> allpoinvlist(@RequestBody ListPageSearchRequest listPageSearchRequest) {
		Map<String, Object> response = service.allpoinvlist(listPageSearchRequest);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/xternal/coilsyncstts", produces = "application/json")
	public ResponseEntity<Object> coilSyncStts(@RequestBody List<POSOIntegrationRequest> request, HttpServletRequest httprequest) {
		String ipAddress = httprequest.getRemoteAddr();
		return service.coilSyncStts(request);
	}

}
