package com.steel.product.jswone.service;

import java.io.FileNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.steel.product.jswone.entity.MaterialMasterJswEntity;
import com.steel.product.jswone.request.MaterialSearchPageRequest;
import com.steel.product.jswone.request.MaterialUploadRequest;

@Service
public interface MaterialUploadService {

	public ResponseEntity<Object> uploadcsv(MaterialUploadRequest request) throws Exception, FileNotFoundException;

	public Page<Object[]> materialSearch1(MaterialSearchPageRequest materialSearchPageRequest);

	public Page<Object[]> materialSearchBymmid(MaterialSearchPageRequest materialSearchPageRequest);

	public Page<MaterialMasterJswEntity> materialSearch(MaterialSearchPageRequest materialSearchPageRequest);

}
