package com.steel.product.trading.controller;

import com.steel.product.trading.entity.CustomerEntity;
import com.steel.product.trading.request.CustomerRequest;
import com.steel.product.trading.request.DeleteRequest;
import com.steel.product.trading.request.SearchRequest;
import com.steel.product.trading.service.CustomerService;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Tag(name = "Customer Master", description = "Customer Master")
@RequestMapping({ "/trading" })
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@PostMapping(value = "/customer/save", produces = "application/json" )
	public ResponseEntity<Object> save(@RequestBody CustomerRequest vendorRequest) {
		return customerService.save(vendorRequest);
	}
	
	@PutMapping(value = "/customer/update", produces = "application/json" )
	public ResponseEntity<Object> update(@RequestBody CustomerRequest categoryRequest) {
		return customerService.save(categoryRequest);
	}

	@PostMapping({ "/customer/list" })
	public ResponseEntity<Object> list(@RequestBody SearchRequest searchPageRequest) {
		Map<String, Object> response = new HashMap<>();

		if (searchPageRequest.getId() != null && searchPageRequest.getId() > 0) {
			CustomerEntity resp = customerService.findByCustomerId( searchPageRequest.getId());
			return new ResponseEntity<Object>(resp, HttpStatus.OK);
		} else {
			Page<CustomerEntity> pageResult = customerService.getCustomerList(searchPageRequest);
			response.put("content", pageResult.toList());
			response.put("currentPage", pageResult.getNumber());
			response.put("totalItems", pageResult.getTotalElements());
			response.put("totalPages", pageResult.getTotalPages());
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/customer/delete", produces = "application/json" )
	public ResponseEntity<Object> delete(@RequestBody DeleteRequest deleteRequest) {
		return customerService.customerDelete(deleteRequest);
	}
	
}
