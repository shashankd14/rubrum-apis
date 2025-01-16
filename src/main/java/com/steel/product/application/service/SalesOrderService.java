package com.steel.product.application.service;

import com.steel.product.application.dto.quality.ListPageSearchRequest;

import org.springframework.data.domain.Page;

public interface SalesOrderService {

	Page<Object[]> listAllPackets(ListPageSearchRequest listPageSearchRequest);
}
