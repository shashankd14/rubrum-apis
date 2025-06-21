package com.steel.product.jswone.service;

import com.steel.product.application.dto.quality.ListPageSearchRequest;
import com.steel.product.jswone.request.SalesOrderChildRequest;
import com.steel.product.jswone.request.SalesOrderMainRequest;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface SalesOrderJswService {

	ResponseEntity<Object> save(SalesOrderMainRequest salesOrderPacketsListNew, String option);

	Page<Object[]> listAllSOIDs(ListPageSearchRequest listPageSearchRequest);
	
	List<Object[]> listAllSOs(List<Integer> soIDsList);

	ResponseEntity<Object> consolidatePlanner(List<SalesOrderChildRequest> salesOrderMainRequest);

	Page<Object[]> findInventory(ListPageSearchRequest listPageSearchRequest);

}
