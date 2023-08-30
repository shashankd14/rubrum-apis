package com.steel.product.application.controller;

import com.steel.product.application.dto.TallyBillingInvoiceListDTO;
import com.steel.product.application.dto.delivery.TallyUpdateSttsRequestDTO;
import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.service.DeliveryDetailsService;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@Tag(name = "Tally Integration", description = "Tally Integration")
public class TallyController {

	@Autowired
	private DeliveryDetailsService deliveryDetailsService;

	@RequestMapping(value = "/billingInvoiceList/{pageNo}/{pageSize}", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<Object> findAllWithPagination(@PathVariable int pageNo, @PathVariable int pageSize) {

		Map<String, Object> response = new HashMap<>();
		Page<DeliveryDetails> mainPaginationData = deliveryDetailsService.findAllDeliveriesForBillingNew(pageNo, pageSize);

		Page<DeliveryDetails> pageResult = deliveryDetailsService.billingInvoiceList(pageNo, pageSize);
		List<Integer> dcIds = new ArrayList<>();
		for (DeliveryDetails deliveryDetails : pageResult.getContent()) {
			dcIds.add(deliveryDetails.getDeliveryId());
		}
		System.out.println("dcIds " + dcIds);
		List<TallyBillingInvoiceListDTO> billingInvoiceList = deliveryDetailsService.billingDCDetails(dcIds);
		response.put("content", billingInvoiceList);
		response.put("currentPage", mainPaginationData.getNumber());
		response.put("totalItems", mainPaginationData.getTotalElements());
		response.put("totalPages", mainPaginationData.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

    @PostMapping("/update/dcstts")
	public ResponseEntity<Object> updateTallyStatus(@RequestBody TallyUpdateSttsRequestDTO deliveryDto) {
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		try {
			deliveryDetailsService.updateTallyStatus(deliveryDto);
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Tally status updated successfully!\"}", header, HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
}