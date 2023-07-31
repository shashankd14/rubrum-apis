package com.steel.product.application.controller;

import com.steel.product.application.dto.TallyBillingInvoiceListDTO;
import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.service.DeliveryDetailsService;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

	@GetMapping({ "/billingInvoiceList/{pageNo}/{pageSize}" })
	public ResponseEntity<Object> findAllWithPagination(@PathVariable int pageNo, @PathVariable int pageSize) {

		Map<String, Object> response = new HashMap<>();
		Page<DeliveryDetails> pageResult = deliveryDetailsService.billingInvoiceList(pageNo, pageSize);
		List<Integer> dcIds = new ArrayList<>();
		for (DeliveryDetails deliveryDetails : pageResult.getContent()) {
			dcIds.add(deliveryDetails.getDeliveryId());
		}
		System.out.println("dcIds " + dcIds);
		List<TallyBillingInvoiceListDTO> billingInvoiceList = deliveryDetailsService.billingDCDetails(dcIds);
		response.put("content", billingInvoiceList);
		response.put("currentPage", pageResult.getNumber());
		response.put("totalItems", pageResult.getTotalElements());
		response.put("totalPages", pageResult.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

}