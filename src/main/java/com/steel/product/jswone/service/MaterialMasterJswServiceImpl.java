package com.steel.product.jswone.service;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
	@Cacheable(value = "categoryList")
	public List<CategoryMasterJswEntity> getCategoryList(SearchRequest searchPageRequest) {
		log.info("In getCategoryList page ");
		List<CategoryMasterJswEntity> pageResult = new ArrayList<>();
		List<Object[]> list = categoryRepository.distinctValues();
		for (Object[] result : list) {
			CategoryMasterJswEntity kk =new CategoryMasterJswEntity();
			kk.setCategoryId(Integer.parseInt(result[0].toString()));
			kk.setCategoryName( result[1].toString());
			pageResult.add(kk);
		}
		return pageResult;
	}

	@Override
	@Cacheable(value = "findSubCategoryByCategoryId")
	public List<SubCategoryJswEntity> findSubCategoryByCategoryId(SearchRequest searchListPageRequest) {
		log.info("In findSubCategoryByCategoryId page ");
		List<SubCategoryJswEntity> kk = subCategoryRepository.findByCategoryId(searchListPageRequest.getCategoryId());
		return kk;
	}

	@Override
	@Cacheable(value = "findLeafCategoryBySubcategoryId")
	public List<LeafCategoryJswEntity> findLeafCategoryBySubcategoryId(SearchRequest searchPageRequest) {
		log.info("In findLeafCategoryBySubcategoryId page ");
		List<LeafCategoryJswEntity> kk = leafCategoryRepository.findBySubcategoryId(searchPageRequest.getSubcategoryId());
		return kk;
	}

	@Override
	@Cacheable(value = "findBrandByLeafcategoryId")
	public List<BrandMasterJswEntity> findBrandByLeafcategoryId(SearchRequest searchPageRequest) {
		log.info("In findBrandByLeafcategoryId page ");
		List<BrandMasterJswEntity> kk = brandRepository.findByLeafcategoryId(searchPageRequest.getLeafcategoryId());
		return kk;
	}

	@Override
	@Cacheable(value = "surfaceListByProduct")
	public List<SurfacetypeMasterJswEntity> getSurfaceListByProduct(SearchRequest searchPageRequest) {
		log.info("In getSurfaceListByProduct page ");
		List<SurfacetypeMasterJswEntity> pageResult = surfaceRepository.findByProductId(searchPageRequest.getProductId());
		return pageResult;
	}
	
	@Override
	@Cacheable(value = "coatingListByProduct")
	public List<CoatingtypeMasterJswEntity> getCoatingListByProduct(SearchRequest searchPageRequest) {
		log.info("In getCoatingListByProduct page ");
		List<CoatingtypeMasterJswEntity> pageResult = coatingRepository.findByProductId(searchPageRequest.getProductId());
		return pageResult;
	}

	@Override
	@Cacheable(value = "uomListByProduct")
	public List<UomMasterJswEntity> getUomListByProduct(SearchRequest searchPageRequest) {
		log.info("In getUomListByProduct page ");
		List<UomMasterJswEntity> pageResult = uomRepository.findByProductId(searchPageRequest.getProductId());
		return pageResult;
	}

	@Override
	@Cacheable(value = "formListByProduct")
	public List<FormMasterJswEntity> getFormListByProduct(SearchRequest searchPageRequest) {
		log.info("In getFormListByProduct page ");
		List<FormMasterJswEntity> pageResult = formRepository.findByProductId(searchPageRequest.getProductId());
		return pageResult;
	}

	@Override
	@Cacheable(value = "gradeListByProduct")
	public List<GradeMasterJswEntity> getGradeListByProduct(SearchRequest searchPageRequest) {
		log.info("In getGradeListByProduct page ");
		List<GradeMasterJswEntity> pageResult = gradeRepository.findByProductId(searchPageRequest.getProductId());
		return pageResult;
	}

	@Override
	@Cacheable(value = "subGradeListByGrade")
	public List<SubgradeMasterJswEntity> getSubGradeListByGrade(SearchRequest searchPageRequest) {
		log.info("In getSubGradeListByGrade page ");
		List<SubgradeMasterJswEntity> kk = subGradeJswRepository.findSubgradesByGradeId(searchPageRequest.getGradeId() );
		return kk;
	}

	@Override
	@Cacheable(value = "productListByBrand")
	public List<ProductMasterJswEntity> getProductListByBrand(SearchRequest searchPageRequest) {
		log.info("In getProductListByBrand page ");
		List<ProductMasterJswEntity> pageResult = productRepository.findByBrandId(searchPageRequest.getBrandId() );
		return pageResult;
	}

	@Override
	@Cacheable(value = "productList")
	public List<ProductMasterJswEntity> getProductList() {
		log.info("In getAllProductList page ");
		List<ProductMasterJswEntity> pageResult = new ArrayList<>();
		List<Object[]> list = productRepository.distinctValues();
		for (Object[] result : list) {
			ProductMasterJswEntity kk =new ProductMasterJswEntity();
			kk.setProductId( Integer.parseInt(result[0].toString()));
			kk.setProductName( result[1].toString());
			pageResult.add(kk);
		}
		return pageResult;
	}

	@Override
	@Cacheable(value = "gradesList")
	public List<GradeMasterJswEntity> getGradesList() {
		log.info("In getGradesList page ");
		List<GradeMasterJswEntity> pageResult = new ArrayList<>();
		List<Object[]> list = gradeRepository.distinctValues();
		for (Object[] result : list) {
			GradeMasterJswEntity kk =new GradeMasterJswEntity();
			kk.setGradeId( Integer.parseInt(result[0].toString()));
			kk.setGradeName(result[1].toString());
			pageResult.add(kk);
		}
		return pageResult;
	}

	@Override
	@Cacheable(value = "productsMap")
	public Map<Integer, String> getProductsMap() {
		log.info("In getProductsMap page ");
		List<Object[]> list = productRepository.distinctValues();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (Object[] result : list) {
			map.put(Integer.parseInt(result[0].toString()), result[1].toString());
		}
		return map;
	}

	@Override
	@Cacheable(value = "categoryMap")
	public Map<Integer, String> getCategoryMap() {
		log.info("In getCategoryMap page ");
		List<Object[]> list = categoryRepository.distinctValues();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (Object[] result : list) {
			map.put(Integer.parseInt(result[0].toString()), result[1].toString());
		}
		return map;
	}

	@Override
	@Cacheable(value = "subCategoryMap")
	public Map<Integer, String> getSubCategoryMap() {
		log.info("In getSubCategoryMap page ");
		List<Object[]> list = subCategoryRepository.distinctValues();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (Object[] result : list) {
			map.put(Integer.parseInt(result[0].toString()), result[1].toString());
		}
		return map;
	}

	@Override
	@Cacheable(value = "leafCategoryMap")
	public Map<Integer, String> getLeafCategoryMap() {
		log.info("In getLeafCategoryMap page ");
		List<Object[]> list = leafCategoryRepository.distinctValues();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (Object[] result : list) {
			map.put(Integer.parseInt(result[0].toString()), result[1].toString());
		}
		return map;
	}

	@Override
	@Cacheable(value = "coatingMap")
	public Map<Integer, String> getCoatingMap() {
		log.info("In getCoatingMap page ");
		List<Object[]> list = coatingRepository.distinctValues();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (Object[] result : list) {
			map.put(Integer.parseInt(result[0].toString()), result[1].toString());
		}
		return map;
	}

	@Override
	@Cacheable(value = "surfaceMap")
	public Map<Integer, String> getSurfaceMap() {
		log.info("In getSurfaceMap page ");
		List<Object[]> list = surfaceRepository.distinctValues();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (Object[] result : list) {
			map.put(Integer.parseInt(result[0].toString()), result[1].toString());
		}
		return map;
	}

	@Override
	@Cacheable(value = "uomMap")
	public Map<Integer, String> getUomMap() {
		log.info("In getUomMap page ");
		List<Object[]> list = uomRepository.distinctValues();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (Object[] result : list) {
			map.put(Integer.parseInt(result[0].toString()), result[1].toString());
		}
		return map;
	}

	@Override
	@Cacheable(value = "gradeMap")
	public Map<Integer, String> getGradeMap() {
		log.info("In getGradeMap page ");
		List<Object[]> list = gradeRepository.distinctValues();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (Object[] result : list) {
			map.put(Integer.parseInt(result[0].toString()), result[1].toString());
		}
		return map;
	}

	@Override
	@Cacheable(value = "subGradeMap")
	public Map<Integer, String> getSubGradeMap() {
		log.info("In getSubGradeMap page ");
		List<Object[]> list = subGradeJswRepository.distinctValues();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (Object[] result : list) {
			map.put(Integer.parseInt(result[0].toString()), result[1].toString());
		}
		return map;
	}

	@Override
	@Cacheable(value = "brandMap")
	public Map<Integer, String> getBrandMap() {
		log.info("In getBrandMap page ");
		List<Object[]> list = brandRepository.distinctValues();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (Object[] result : list) {
			map.put(Integer.parseInt(result[0].toString()), result[1].toString());
		}
		return map;
	}

	@Override
	@Cacheable(value = "formMap")
	public Map<Integer, String> getFormMap() {
		log.info("In getFormMap page ");
		List<Object[]> list = formRepository.distinctValues();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (Object[] result : list) {
			map.put(Integer.parseInt(result[0].toString()), result[1].toString());
		}
		return map;
	}

	@Override
	@Cacheable(value = "productName")
	public MaterialResponseDto getProductName(String mmId) {
		log.info("In getProductName page ");
		MaterialResponseDto productName = null;
		List<Object[]> productNameList = productRepository.getProductName(mmId);
		for (Object[] result : productNameList) {
			productName = new MaterialResponseDto();
			productName.setMatId(result[0] != null ? (Integer) result[0] : null);
			productName.setDescription(result[1] != null ? (String) result[1] : null);
			productName.setMmDescConcatenated(result[2] != null ? (String) result[2] : null);
		}
		return productName;
	}
	
	@Override
	@Cacheable(value = "subGradeName")
	public MaterialGradeDto getSubGradeName(String mmId) {
		log.info("In getSubGradeName page ");
		MaterialGradeDto productName = null;
		List<Object[]> productNameList = gradeRepository.getSubGradeName(mmId) ;
		for (Object[] result : productNameList) {
			productName = new MaterialGradeDto();
			productName.setGradeId( result[0] != null ? (Integer) result[0] : null);
			productName.setSubGradeName( result[1] != null ? (String) result[1] : null);
		}
		return productName;
	}
	
	@Override
	@Cacheable(value = "gradeName")
	public MaterialGradeDto getGradeName(String mmId) {
		log.info("In getGradeName page ");
		MaterialGradeDto productName = null;
		List<Object[]> productNameList = gradeRepository.getGradeName(mmId) ;
		for (Object[] result : productNameList) {
			productName = new MaterialGradeDto();
			productName.setGradeId( result[0] != null ? (Integer) result[0] : null);
			productName.setGradeName( result[1] != null ? (String) result[1] : null);
		}
		return productName;
	}

	@Override
	@Cacheable(value = "gradeProductName")
	public MaterialResponseDto getGradeProductName(String mmId) {
		log.info("In getGradeProductName page ");
		MaterialResponseDto materialResponseDto = null;
		List<Object[]> productNameList = gradeRepository.getGradeProductName(mmId);
		for (Object[] result : productNameList) {
			materialResponseDto = new MaterialResponseDto();
			materialResponseDto.setDescription(result[0] != null ? (String) result[0] : null);
			materialResponseDto.setMatId(result[2] != null ? (Integer) result[2] : null);
		    MaterialGradeDto materialGrade=new MaterialGradeDto();
		    materialGrade.setGradeName(result[1] != null ? (String) result[1] : null);
		    materialGrade.setGradeId(result[3] != null ? (Integer) result[3] : null);
		    materialResponseDto.setMaterialGrade(materialGrade);
		}
		return materialResponseDto;
	}

	@Override
	@Cacheable(value = "getGradeById")
	public GradeMasterJswEntity getGradeById(Integer gradeId) {
		log.info("In getGradeById page ");
		Optional<GradeMasterJswEntity> list = gradeRepository.findById(gradeId);
		GradeMasterJswEntity kk = new GradeMasterJswEntity();
		if (list.isPresent()) {
			kk = list.get();
		}
		return kk;
	}

	@Override
	@Cacheable(value = "getProductById")
	public ProductMasterJswEntity getProductById(Integer gradeId) {
		log.info("In getProductById page ");
		Optional<ProductMasterJswEntity> list = productRepository.findById(gradeId);
		ProductMasterJswEntity kk = new ProductMasterJswEntity();
		if (list.isPresent()) {
			kk = list.get();
		}
		return kk;
	}
}
