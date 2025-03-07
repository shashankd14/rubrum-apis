package com.steel.product.jswone.service;

import java.util.List;

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

public interface MaterialMasterJswService {

	List<CategoryMasterJswEntity> getCategoryList(SearchRequest searchListPageRequest);

	List<SubCategoryJswEntity> findSubCategoryByCategoryId(SearchRequest searchListPageRequest);

	List<LeafCategoryJswEntity> findLeafCategoryBySubcategoryId(SearchRequest searchPageRequest);

	List<BrandMasterJswEntity> findBrandByLeafcategoryId(SearchRequest searchPageRequest);

	List<CoatingtypeMasterJswEntity> getCoatingList(SearchRequest searchPageRequest);

	List<SurfacetypeMasterJswEntity> getSurfaceList(SearchRequest searchPageRequest);

	List<UomMasterJswEntity> getUomList(SearchRequest searchPageRequest);

	List<FormMasterJswEntity> getFormList(SearchRequest searchPageRequest);

	List<GradeMasterJswEntity> getGradeList(SearchRequest searchPageRequest);

	List<SubgradeMasterJswEntity> getSubGradeList(SearchRequest searchPageRequest);

	List<ProductMasterJswEntity> getProductList(SearchRequest searchPageRequest);

}
