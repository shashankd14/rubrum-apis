package com.steel.product.trading.service;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import com.steel.product.trading.entity.VendorEntity;
import com.steel.product.trading.request.DeleteRequest;
import com.steel.product.trading.request.SearchRequest;
import com.steel.product.trading.request.VendorRequest;

public interface VendorService {

	ResponseEntity<Object> save(VendorRequest categoryRequest);

	VendorEntity findByVendorId(Integer id);

	Page<VendorEntity> getVendorList(SearchRequest searchListPageRequest);

	ResponseEntity<Object> vendorDelete(DeleteRequest deleteRequest);

}
