package com.steel.product.jswone.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.steel.product.application.dto.delivery.DeliveryDto;
import com.steel.product.application.dto.quality.ListPageSearchRequest;
import com.steel.product.application.dto.salesorder.InventoryAdjustmentStatusUpdate;
import com.steel.product.application.dto.salesorder.SalesOrderListDTO;
import com.steel.product.jswone.request.MMIDReceiveMainRequest;
import com.steel.product.jswone.request.POSOIntegrationRequest;
import com.steel.product.jswone.response.InventoryAdjustmentResponse;
import com.steel.product.jswone.response.PODetailsMainResponse;
import com.steel.product.jswone.response.PoGrnMainResponse;

public interface JSWIntegrationService {

	ResponseEntity<Object> poReceive(POSOIntegrationRequest request);

	ResponseEntity<Object> mmidreceive(MMIDReceiveMainRequest request);

	List<Object[]> locationwisePOList(POSOIntegrationRequest request);

	PODetailsMainResponse podetails(POSOIntegrationRequest request);

	PoGrnMainResponse postgrn(POSOIntegrationRequest request);

	Map<String, Object> poWiseInwardList(POSOIntegrationRequest request);

	ResponseEntity<Object> soReceive(POSOIntegrationRequest request);

	List<Object[]> locationwiseSOList(POSOIntegrationRequest request);

	Map<String, Object> allpoinvlist(ListPageSearchRequest listPageSearchRequest);

	ResponseEntity<Object> coilSyncStts(List<POSOIntegrationRequest> request);

	PODetailsMainResponse uploadDocument(POSOIntegrationRequest req);

	InventoryAdjustmentResponse inventoryAdjustment(DeliveryDto request);

	InventoryAdjustmentResponse warehouseReassignment(SalesOrderListDTO request);

	ResponseEntity<Object> inventoryAdjustmentStatusUpdate(InventoryAdjustmentStatusUpdate request);

<<<<<<< HEAD
=======
	/**
	 * FG (cut-to-length) inventory adjustment for packets just moved WIP -> FG.
	 * One call = one cut = one fgReferenceId. Needs no delivery/DC id - only the coil (inwardId)
	 * and the packets (instructionIds) that were just finished.
	 */
>>>>>>> 47cee13ce1be546c35f96497a262c73beba7cfba
	InventoryAdjustmentResponse inventoryAdjustmentFG(Integer inwardId, List<Integer> instructionIds);
}
