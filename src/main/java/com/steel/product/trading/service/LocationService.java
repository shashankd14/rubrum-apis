package com.steel.product.trading.service;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import com.steel.product.trading.entity.LocationEntity;
import com.steel.product.trading.request.BaseRequest;
import com.steel.product.trading.request.DeleteRequest;
import com.steel.product.trading.request.LocationRequest;
import com.steel.product.trading.request.SearchRequest;

public interface LocationService {

	ResponseEntity<Object> save(LocationRequest locationRequest);

	LocationEntity findByLocationId(Integer id);

	Page<LocationEntity> getLocationList(SearchRequest searchListPageRequest);

	ResponseEntity<Object> locationDelete(DeleteRequest deleteRequest);

	ResponseEntity<Object> statesList(BaseRequest baseRequest);

}
