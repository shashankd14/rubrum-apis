package com.steel.product.trading.service;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.steel.product.trading.entity.CategoryEntity;
import com.steel.product.trading.entity.MaterialMasterEntity;
import com.steel.product.trading.entity.SubCategoryEntity;
import com.steel.product.trading.request.CategoryRequest;
import com.steel.product.trading.request.DeleteRequest;
import com.steel.product.trading.request.MaterialMasterRequest;
import com.steel.product.trading.request.SearchRequest;
import com.steel.product.trading.request.SubCategoryRequest;

public interface MaterialMasterService {

	ResponseEntity<Object> save(MaterialMasterRequest materialRequest, MultipartFile itemImage,
			MultipartFile crossSectionalImage);

	Page<MaterialMasterEntity> getMaterialList(SearchRequest searchListPageRequest);

	MaterialMasterEntity findByItemId(Integer itemId);

	ResponseEntity<Object> categorySave(CategoryRequest categoryRequest);

	CategoryEntity findByCategoryId(Integer id);

	Page<CategoryEntity> getCategoryList(SearchRequest searchListPageRequest);

	ResponseEntity<Object> materialDelete(DeleteRequest deleteRequest);

	ResponseEntity<Object> categoryDelete(DeleteRequest deleteRequest);

	ResponseEntity<Object> subcategorySave(SubCategoryRequest categoryRequest);

	Page<SubCategoryEntity> getSubCategoryList(SearchRequest searchListPageRequest);

	SubCategoryEntity findBySubCategoryId(Integer id);

	ResponseEntity<Object> subcategoryDelete(DeleteRequest deleteRequest);

}
