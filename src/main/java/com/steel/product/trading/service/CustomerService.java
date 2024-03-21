package com.steel.product.trading.service;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.steel.product.trading.entity.CustomerEntity;
import com.steel.product.trading.request.CustomerRequest;
import com.steel.product.trading.request.DeleteRequest;
import com.steel.product.trading.request.SearchRequest;

public interface CustomerService {

	ResponseEntity<Object> save(CustomerRequest categoryRequest);

	CustomerEntity findByCustomerId(Integer id);

	Page<CustomerEntity> getCustomerList(SearchRequest searchListPageRequest);

	ResponseEntity<Object> customerDelete(DeleteRequest deleteRequest);

}
