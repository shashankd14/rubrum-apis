package com.steel.product.jswone.service;

import java.io.FileNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.steel.product.jswone.request.MaterialSearchPageRequest;
import com.steel.product.jswone.request.MaterialUploadRequest;

@Service
public interface MaterialUploadService {

	public ResponseEntity<Object> upload(MaterialUploadRequest request) throws Exception, FileNotFoundException;

	public Page<Object[]> materialSearch(MaterialSearchPageRequest materialSearchPageRequest);

	public Page<Object[]> materialSearchBymmid(MaterialSearchPageRequest materialSearchPageRequest);

}
