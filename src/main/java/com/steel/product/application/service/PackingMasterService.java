package com.steel.product.application.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.steel.product.application.dto.packingmaster.PackingBucketRequest;
import com.steel.product.application.dto.packingmaster.PackingBucketResponse;
import com.steel.product.application.dto.packingmaster.PackingItemRequest;
import com.steel.product.application.dto.packingmaster.PackingItemResponse;
import com.steel.product.application.dto.packingmaster.PackingRateMasterRequest;
import com.steel.product.application.dto.packingmaster.PackingRateMasterResponse;

public interface PackingMasterService {

	ResponseEntity<Object> save(PackingItemRequest packingItemRequest, int userId);

	ResponseEntity<Object> delete(int id);

	PackingItemResponse getById(int id);

	List<PackingItemResponse> getAllItemDetails();

	ResponseEntity<Object> saveBucket(PackingBucketRequest packingItemRequest, int userId);

	PackingBucketResponse getByBucketId(int id);

	ResponseEntity<Object> deleteBucket(int id);

	List<PackingBucketResponse> getAllBucketList();

	ResponseEntity<Object> save(PackingRateMasterRequest packingRateMasterRequest, int userId);

	ResponseEntity<Object> deleteRate(int id);

	PackingRateMasterResponse getByIdRate(Integer id);

	List<PackingRateMasterResponse> getAllRateList();

	List<PackingRateMasterResponse> getAllRateListPartyWise(int partyId);

}
