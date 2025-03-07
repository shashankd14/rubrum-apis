package com.steel.product.jswone.service;

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
import com.steel.product.jswone.repository.BrandMasterJswRepository;
import com.steel.product.jswone.repository.CategoryMasterJswRepository;
import com.steel.product.jswone.repository.CoatingtypeMasterJswRepository;
import com.steel.product.jswone.repository.FormMasterJswRepository;
import com.steel.product.jswone.repository.GradeMasterJswRepository;
import com.steel.product.jswone.repository.LeafCategoryJswRepository;
import com.steel.product.jswone.repository.ProductMasterJswRepository;
import com.steel.product.jswone.repository.SubCategoryJswRepository;
import com.steel.product.jswone.repository.SubGradeJswRepository;
import com.steel.product.jswone.repository.SurfacetypeMasterJswRepository;
import com.steel.product.jswone.repository.UomMasterJswRepository;
import com.steel.product.jswone.request.SearchRequest;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class MaterialMasterJswServiceImpl implements MaterialMasterJswService {

	@Autowired
	CategoryMasterJswRepository categoryRepository;

	@Autowired
	ProductMasterJswRepository productRepository;

	@Autowired
	SubCategoryJswRepository subCategoryRepository;

	@Autowired
	LeafCategoryJswRepository leafCategoryRepository;

	@Autowired
	BrandMasterJswRepository brandRepository;
	
	@Autowired
	SurfacetypeMasterJswRepository surfaceRepository;
	
	@Autowired
	CoatingtypeMasterJswRepository coatingRepository;
	
	@Autowired
	FormMasterJswRepository formRepository;
	
	@Autowired
	UomMasterJswRepository uomRepository;
	
	@Autowired
	GradeMasterJswRepository gradeRepository;
	
	@Autowired
	SubGradeJswRepository subGradeJswRepository;

	@Override
	public List<CategoryMasterJswEntity> getCategoryList(SearchRequest searchPageRequest) {
		log.info("In getCategoryList page ");
		List<CategoryMasterJswEntity> pageResult = categoryRepository.findAll();
		return pageResult;
	}

	@Override
	public List<SubCategoryJswEntity> findSubCategoryByCategoryId(SearchRequest searchListPageRequest) {
		log.info("In findSubCategoryByCategoryId page ");
		List<SubCategoryJswEntity> kk = subCategoryRepository.findByCategoryId(searchListPageRequest.getCategoryId());
		return kk;
	}

	@Override
	public List<LeafCategoryJswEntity> findLeafCategoryBySubcategoryId(SearchRequest searchPageRequest) {
		log.info("In findLeafCategoryBySubcategoryId page ");
		List<LeafCategoryJswEntity> kk = leafCategoryRepository.findBySubcategoryId(searchPageRequest.getSubcategoryId());
		return kk;
	}

	@Override
	public List<BrandMasterJswEntity> findBrandByLeafcategoryId(SearchRequest searchPageRequest) {
		log.info("In findBrandByLeafcategoryId page ");
		List<BrandMasterJswEntity> kk = brandRepository.findByLeafcategoryId(searchPageRequest.getLeafcategoryId());
		return kk;
	}

	@Override
	public List<SurfacetypeMasterJswEntity> getSurfaceList(SearchRequest searchPageRequest) {
		log.info("In getSurfaceList page ");
		List<SurfacetypeMasterJswEntity> pageResult = surfaceRepository.findByProductId(searchPageRequest.getProductId());
		return pageResult;
	}
	
	@Override
	public List<CoatingtypeMasterJswEntity> getCoatingList(SearchRequest searchPageRequest) {
		log.info("In getCoatingList page ");
		List<CoatingtypeMasterJswEntity> pageResult = coatingRepository.findByProductId(searchPageRequest.getProductId());
		return pageResult;
	}

	@Override
	public List<UomMasterJswEntity> getUomList(SearchRequest searchPageRequest) {
		log.info("In getUomList page ");
		List<UomMasterJswEntity> pageResult = uomRepository.findByProductId(searchPageRequest.getProductId());
		return pageResult;
	}

	@Override
	public List<FormMasterJswEntity> getFormList(SearchRequest searchPageRequest) {
		log.info("In getFormList page ");
		List<FormMasterJswEntity> pageResult = formRepository.findByProductId(searchPageRequest.getProductId());
		return pageResult;
	}

	@Override
	public List<GradeMasterJswEntity> getGradeList(SearchRequest searchPageRequest) {
		log.info("In getGradeList page ");
		List<GradeMasterJswEntity> pageResult = gradeRepository.findByProductId(searchPageRequest.getProductId());
		return pageResult;
	}

	@Override
	public List<SubgradeMasterJswEntity> getSubGradeList(SearchRequest searchPageRequest) {
		log.info("In getSubGradeList page ");
		List<SubgradeMasterJswEntity> kk = subGradeJswRepository.findSubgradesByGradeId(searchPageRequest.getGradeId() );
		return kk;
	}

	@Override
	public List<ProductMasterJswEntity> getProductList(SearchRequest searchPageRequest) {
		log.info("In getProductList page ");
		List<ProductMasterJswEntity> pageResult = productRepository.findByBrandId(searchPageRequest.getBrandId() );
		return pageResult;
	}
 
}
