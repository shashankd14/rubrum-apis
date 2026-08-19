package com.steel.product.jswone.service;

import java.util.List;
import java.util.Map;

import com.steel.product.application.dto.material.MaterialResponseDto;
import com.steel.product.application.dto.materialGradeDto.MaterialGradeDto;
import com.steel.product.jswone.entity.BrandMasterJswEntity;
import com.steel.product.jswone.entity.CategoryMasterJswEntity;
import com.steel.product.jswone.entity.CoatingtypeMasterJswEntity;
import com.steel.product.jswone.entity.FormMasterJswEntity;
import com.steel.product.jswone.entity.GradeMasterJswEntity;
import com.steel.product.jswone.entity.LeafCategoryJswEntity;
import com.steel.product.jswone.entity.ProductMasterJswEntity;
import com.steel.product.jswone.entity.SubCategoryJswEntity;
import com.steel.product.jswone.entity.SubgradeMasterJswEntity;
import com.steel.product.jswone.entity.SurfacetypeMasterJswEntity;
import com.steel.product.jswone.entity.UomMasterJswEntity;
import com.steel.product.jswone.request.SearchRequest;
import com.steel.product.jswone.response.SubGradeDTO;

public interface MaterialMasterJswService {

	List<CategoryMasterJswEntity> getCategoryList(SearchRequest searchListPageRequest);

	List<SubCategoryJswEntity> findSubCategoryByCategoryId(SearchRequest searchListPageRequest);

	List<LeafCategoryJswEntity> findLeafCategoryBySubcategoryId(SearchRequest searchPageRequest);

	List<BrandMasterJswEntity> findBrandByLeafcategoryId(SearchRequest searchPageRequest);

	List<CoatingtypeMasterJswEntity> getCoatingListByProduct(SearchRequest searchPageRequest);

	List<SurfacetypeMasterJswEntity> getSurfaceListByProduct(SearchRequest searchPageRequest);

	List<UomMasterJswEntity> getUomListByProduct(SearchRequest searchPageRequest);

	List<FormMasterJswEntity> getFormListByProduct(SearchRequest searchPageRequest);

	List<GradeMasterJswEntity> getGradeListByProduct(SearchRequest searchPageRequest);

	GradeMasterJswEntity getGradeById(Integer gradeId);
	
	ProductMasterJswEntity getProductById(Integer productId);

	List<SubgradeMasterJswEntity> getSubGradeListByGrade(SearchRequest searchPageRequest);

	List<ProductMasterJswEntity> getProductListByBrand(SearchRequest searchPageRequest);

	Map<Integer, String> getProductsMap();

	Map<Integer, String> getCategoryMap();

	Map<Integer, String> getSubCategoryMap();

	Map<Integer, String> getLeafCategoryMap();

	Map<Integer, String> getCoatingMap();

	Map<Integer, String> getSurfaceMap();

	Map<Integer, String> getUomMap();

	Map<Integer, String> getGradeMap();

	Map<Integer, String> getSubGradeMap();

	Map<Integer, String> getBrandMap();

	Map<Integer, String> getFormMap();

	MaterialResponseDto getProductName(String mmId);

	MaterialGradeDto getGradeName(String mmId);

	MaterialResponseDto getGradeProductName(String mmId);

	List<ProductMasterJswEntity> getProductList();

	List<GradeMasterJswEntity> getGradesList();

	MaterialGradeDto getSubGradeName(String mmId);

	List<SubGradeDTO> subGradeListByBrand(int productId);

}
