package com.steel.product.jswone.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.steel.product.application.dto.inward.SearchListPageRequest;
import com.steel.product.application.dto.quality.ListPageSearchRequest;
import com.steel.product.jswone.request.CPSplitRequest;
import com.steel.product.jswone.request.SalesOrderBulkRequest;
import com.steel.product.jswone.request.SalesOrderChildRequest;
import com.steel.product.jswone.request.SalesOrderExternalRequest;
import com.steel.product.jswone.request.SalesOrderMainRequest;
import com.steel.product.jswone.response.CoilAllocationDTO;
import com.steel.product.jswone.response.SalesOrderChildAllocationResponse;
import com.steel.product.jswone.response.SalesOrderSheetResponse;

public interface SalesOrderJswService {

	Page<Object[]> listAllSOIDs(ListPageSearchRequest listPageSearchRequest);

	List<Object[]> listAllSOs(List<Integer> soIDsList);

	ResponseEntity<Object> save(SalesOrderMainRequest salesOrderPacketsListNew, String option);

	Page<Object[]> listAllSOIDsCP(ListPageSearchRequest listPageSearchRequest);

	List<Object[]> listAllSOsCP(List<Integer> soIDsList, boolean warehouseFlag, List<String> warehouseList);

	ResponseEntity<Object> consolidatePlanner(List<SalesOrderChildRequest> salesOrderMainRequest);

	ResponseEntity<Object> post(SalesOrderExternalRequest salesOrderExternalRequest, String option);

	ResponseEntity<Object> update(SalesOrderExternalRequest salesOrderPacketsListNew);

	ResponseEntity<Object> bulkUpdate(SalesOrderBulkRequest salesOrderPacketsListNew);

	ResponseEntity<Object> consolidateSplit(CPSplitRequest cpSplitRequest);

	ResponseEntity<Object> unAllocate(SalesOrderChildAllocationResponse req);

	List<CoilAllocationDTO> coilAllocationDetails(SalesOrderChildRequest request);

	List<Object[]> dashboard(SearchListPageRequest req);

	SalesOrderSheetResponse fetchMappedSheetSONo(int inwardId);

	Page<Object[]> findInventory(ListPageSearchRequest request);

}
