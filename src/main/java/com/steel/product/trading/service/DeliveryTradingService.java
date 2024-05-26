package com.steel.product.trading.service;

import org.springframework.http.ResponseEntity;

import com.steel.product.trading.request.DeleteRequest;
import com.steel.product.trading.request.DeliveryChalanRequest;
import com.steel.product.trading.request.DeliveryOrderRequest;

public interface DeliveryTradingService {

	ResponseEntity<Object> save(DeliveryOrderRequest eqpRequest);

	ResponseEntity<Object> doDelete(DeleteRequest deleteRequest);

	ResponseEntity<Object> dcSave(DeliveryChalanRequest eqpRequest);

}
