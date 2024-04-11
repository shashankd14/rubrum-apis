package com.steel.product.trading.service;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import com.steel.product.trading.entity.WeighbridgeEntity;
import com.steel.product.trading.request.DeleteRequest;
import com.steel.product.trading.request.SearchRequest;
import com.steel.product.trading.request.WeighbridgeRequest;

public interface WeighbridgeService {

	ResponseEntity<Object> save(WeighbridgeRequest locationRequest);

	WeighbridgeEntity findByWeighbridgeId(Integer id);

	Page<WeighbridgeEntity> getWeighbridgeList(SearchRequest searchListPageRequest);

	ResponseEntity<Object> weighbridgeDelete(DeleteRequest deleteRequest);

}
