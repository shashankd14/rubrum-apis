package com.steel.product.application.controller;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.steel.product.application.dto.quality.ListPageSearchRequest;
import com.steel.product.application.dto.salesorder.SalesOrderListDTO;
import com.steel.product.application.service.SalesOrderService;

@RestController
@CrossOrigin
@Tag(name = "Sales Order", description = "Sales Order")
@RequestMapping({ "/so" })
public class SalesOrderController {

	private SalesOrderService salesOrderService;

	@Autowired
	public SalesOrderController(SalesOrderService salesOrderService ) {
		this.salesOrderService = salesOrderService;
	}

	@PostMapping(value = "/allpackets", produces = "application/json")
	public ResponseEntity<Object> listAllPackets(@RequestBody ListPageSearchRequest listPageSearchRequest) {
		Map<String, Object> response = new HashMap<>();
		Page<Object[]> packetsList = salesOrderService.listAllPackets(listPageSearchRequest);
		
		Map<Integer, SalesOrderListDTO> kk = new LinkedHashMap<>();
		for (Object[] result : packetsList) {
			SalesOrderListDTO resp = new SalesOrderListDTO();
			resp.setInstructionId(result[0] != null ? (Integer) result[0] : null);
			resp.setInwardEntryId(result[1] != null ? (Integer) result[1] : null);
			resp.setCoilNo(result[2] != null ? (String) result[2] : null);
			resp.setCustomerBatchNo(result[3] != null ? (String) result[3] : null);
			resp.setMaterialGrade(result[4] != null ? (String) result[4] : null);
			resp.setMaterialDesc(result[5] != null ? (String) result[5] : null);
			resp.setFthickness(result[6] != null ? (Float) result[6] : null);
			Double dweight = (result[7] != null ? (Double) result[7] : null);
			Double dwidth = (result[10] != null ? (Double) result[10] : null);
			Double dlength = (result[11] != null ? (Double) result[11] : null);
			resp.setFwidth(dwidth.floatValue());
			resp.setFweighth(dweight.floatValue());
			resp.setFlenghth(dlength.floatValue());
			resp.setPartyId(result[8] != null ? Integer.parseInt(result[8].toString()) : null);
			resp.setPartyName(result[9] != null ? (String) result[9] : null);
			kk.put(resp.getInstructionId(), resp);
		}
		List<SalesOrderListDTO> qirList = new ArrayList<SalesOrderListDTO>(kk.values());

		response.put("content", qirList);
		response.put("currentPage", packetsList.getNumber());
		response.put("totalItems", packetsList.getTotalElements());
		response.put("totalPages", packetsList.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}


}
