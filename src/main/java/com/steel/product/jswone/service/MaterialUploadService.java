package com.steel.product.jswone.service;

import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.steel.product.jswone.entity.MaterialMasterJswEntity;
import com.steel.product.jswone.request.MaterialSearchPageRequest;
import com.steel.product.jswone.request.MaterialUploadRequest;

@Service
public interface MaterialUploadService {

	public ResponseEntity<Object> uploadmmidData(MaterialUploadRequest request) throws Exception, FileNotFoundException;

	public Page<Object[]> materialSearchBymmid(MaterialSearchPageRequest materialSearchPageRequest);

	public Page<MaterialMasterJswEntity> materialSearch(MaterialSearchPageRequest materialSearchPageRequest);

	public ResponseEntity<Object> uploadInwardData(MaterialUploadRequest request)
			throws Exception, FileNotFoundException;

	public List<Object[]> mmidmasterusedinward(MaterialSearchPageRequest materialSearchPageRequest);

}
