package com.steel.product.application.service;

import com.lowagie.text.DocumentException;
import com.steel.product.application.dto.delivery.DeliveryItemDetails;
import com.steel.product.application.dto.quality.ListPageSearchRequest;
import com.steel.product.application.dto.salesorder.SalesOrderCreateDTO;
import com.steel.product.application.dto.salesorder.SalesOrderListResponse;
import com.steel.product.trading.request.DeleteRequest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface SalesOrderService {

	Page<Object[]> listAllPackets(ListPageSearchRequest listPageSearchRequest);

	ResponseEntity<Object> save(List<SalesOrderCreateDTO> salesOrderPacketsListNew);

	List<Object[]> listAllSOs(List<String> soIDsList);

	ResponseEntity<Object> delete(DeleteRequest deleteRequest);

	ResponseEntity<Object> deletePackets(DeleteRequest deleteRequest);

	Page<Object[]> listAllSOIDs(ListPageSearchRequest listPageSearchRequest);

	int validateSoNoAndCustCode(List<DeliveryItemDetails> deliveryItemDetails);

	SalesOrderListResponse getSoNoAndCustCode(List<Integer> list);

	File generatePdf(ListPageSearchRequest request) throws IOException, DocumentException;

	Map<Integer, List<String>> fetchMappedSOList(List<Integer> soList);

	List<String> mmidBySO(String soNo);

}
