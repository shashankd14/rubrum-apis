package com.steel.product.trading.service;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.steel.product.trading.entity.MaterialMasterEntity;
import com.steel.product.trading.request.MaterialMasterRequest;
import com.steel.product.trading.request.MaterialMasterSearch;

public interface MaterialMasterService {

	ResponseEntity<Object> save(MaterialMasterRequest materialRequest, MultipartFile itemImage,
			MultipartFile crossSectionalImage);

	Page<MaterialMasterEntity> getMaterialList(MaterialMasterSearch searchListPageRequest);

	MaterialMasterEntity findByItemId(Integer itemId);

}
