package com.steel.product.application.service;

import com.steel.product.application.dto.quality.ListPageSearchRequest;
import com.steel.product.application.dto.salesorder.SalesOrderCreateDTO;
import com.steel.product.trading.request.DeleteRequest;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface SalesOrderService {

	Page<Object[]> listAllPackets(ListPageSearchRequest listPageSearchRequest);

	ResponseEntity<Object> save(List<SalesOrderCreateDTO> salesOrderPacketsListNew);

	Page<Object[]> listAllSOs(ListPageSearchRequest listPageSearchRequest);

	ResponseEntity<Object> delete(DeleteRequest deleteRequest);

	ResponseEntity<Object> deletePackets(DeleteRequest deleteRequest);
}
