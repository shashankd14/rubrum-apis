package com.steel.product.application.controller;

import com.steel.product.application.dto.yieldlossratio.YieldLossRatioRequest;
import com.steel.product.application.dto.yieldlossratio.YieldLossRatioResponse;
import com.steel.product.application.dto.yieldlossratio.YieldLossRatioSearchRequest;
import com.steel.product.application.service.YieldLossRatioMasterService;
import com.steel.product.trading.request.DeleteRequest;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Tag(name = "Yield Loss Ratio Master", description = "Yield Loss Ratio Master")
@RequestMapping({ "/yieldlossratio" })
public class YieldLossRatioMasterController {

	@Autowired
	private YieldLossRatioMasterService yieldLossRatioMasterService;

	@PostMapping(value = "/save", produces = "application/json" )
	public ResponseEntity<Object> save(@RequestBody List<YieldLossRatioRequest> yieldLossRatioRequest) {
		return yieldLossRatioMasterService.save(yieldLossRatioRequest);
	}
	
	@PutMapping(value = "/update", produces = "application/json")
	public ResponseEntity<Object> update(@RequestBody List<YieldLossRatioRequest> priceMasterRequestList) {
		return yieldLossRatioMasterService.save(priceMasterRequestList);
	}
	
	@PostMapping(value = "/delete", produces = "application/json" )
	public ResponseEntity<Object> delete(@RequestBody DeleteRequest deleteRequest) {
		return yieldLossRatioMasterService.delete(deleteRequest);
	}

	@PostMapping({ "/list" })
	public ResponseEntity<Object> getAll(@RequestBody YieldLossRatioSearchRequest request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Page<Object[]> pageResult = yieldLossRatioMasterService.getAll(request);
			List<YieldLossRatioResponse> list = new ArrayList<>();
			for (Object[] result : pageResult) {
				YieldLossRatioResponse yieldLossRatioResponse = new YieldLossRatioResponse();
				yieldLossRatioResponse.setYlrId(result[0] != null ? (Integer) result[0] : null);
				yieldLossRatioResponse.setPartyId(result[1] != null ? (Integer) result[1] : null);
				yieldLossRatioResponse.setProcessId(result[2] != null ? (Integer) result[2] : null);
				yieldLossRatioResponse.setLossRatioPercentageFrom(result[3] != null ? (BigDecimal) result[3] : null);
				yieldLossRatioResponse.setLossRatioPercentageTo(result[4] != null ? (BigDecimal) result[4] : null);
				yieldLossRatioResponse.setComments(result[5] != null ? (String) result[5] : null);
				yieldLossRatioResponse.setPartyName(result[6] != null ? (String) result[6] : null);
				yieldLossRatioResponse.setProcessName(result[7] != null ? (String) result[7] : null);
				list.add(yieldLossRatioResponse);
			}
			response.put("content", list);
			response.put("currentPage", pageResult.getNumber());
			response.put("totalItems", pageResult.getTotalElements());
			response.put("totalPages", pageResult.getTotalPages());
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
