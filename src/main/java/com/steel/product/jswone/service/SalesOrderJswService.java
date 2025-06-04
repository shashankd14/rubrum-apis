package com.steel.product.jswone.service;

import com.steel.product.jswone.request.SalesOrderMainRequest;

import org.springframework.http.ResponseEntity;

public interface SalesOrderJswService {

	ResponseEntity<Object> save(SalesOrderMainRequest salesOrderPacketsListNew);

}
