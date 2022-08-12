package com.steel.product.application.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.steel.product.application.dto.packingmaster.PackingBucketRequest;
import com.steel.product.application.dto.packingmaster.PackingBucketResponse;
import com.steel.product.application.dto.packingmaster.PackingItemRequest;
import com.steel.product.application.dto.packingmaster.PackingItemResponse;

public interface PackingMasterService {

	ResponseEntity<Object> save(PackingItemRequest packingItemRequest, int userId);

	ResponseEntity<Object> delete(int id);

	PackingItemResponse getById(int id);

	List<PackingItemResponse> getAllItemDetails();

	ResponseEntity<Object> saveBucket(PackingBucketRequest packingItemRequest, int userId);

	PackingBucketResponse getByBucketId(int id);

	ResponseEntity<Object> deleteBucket(int id);

	List<PackingBucketResponse> getAllBucketList();

}
