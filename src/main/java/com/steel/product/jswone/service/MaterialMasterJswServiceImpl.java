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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
	public List<SurfacetypeMasterJswEntity> getSurfaceListByProduct(SearchRequest searchPageRequest) {
		log.info("In getSurfaceListByProduct page ");
		List<SurfacetypeMasterJswEntity> pageResult = surfaceRepository.findByProductId(searchPageRequest.getProductId());
		return pageResult;
	}
	
	@Override
	public List<CoatingtypeMasterJswEntity> getCoatingListByProduct(SearchRequest searchPageRequest) {
		log.info("In getCoatingListByProduct page ");
		List<CoatingtypeMasterJswEntity> pageResult = coatingRepository.findByProductId(searchPageRequest.getProductId());
		return pageResult;
	}

	@Override
	public List<UomMasterJswEntity> getUomListByProduct(SearchRequest searchPageRequest) {
		log.info("In getUomListByProduct page ");
		List<UomMasterJswEntity> pageResult = uomRepository.findByProductId(searchPageRequest.getProductId());
		return pageResult;
	}

	@Override
	public List<FormMasterJswEntity> getFormListByProduct(SearchRequest searchPageRequest) {
		log.info("In getFormListByProduct page ");
		List<FormMasterJswEntity> pageResult = formRepository.findByProductId(searchPageRequest.getProductId());
		return pageResult;
	}

	@Override
	public List<GradeMasterJswEntity> getGradeListByProduct(SearchRequest searchPageRequest) {
		log.info("In getGradeListByProduct page ");
		List<GradeMasterJswEntity> pageResult = gradeRepository.findByProductId(searchPageRequest.getProductId());
		return pageResult;
	}

	@Override
	public List<SubgradeMasterJswEntity> getSubGradeListByGrade(SearchRequest searchPageRequest) {
		log.info("In getSubGradeListByGrade page ");
		List<SubgradeMasterJswEntity> kk = subGradeJswRepository.findSubgradesByGradeId(searchPageRequest.getGradeId() );
		return kk;
	}

	@Override
	public List<ProductMasterJswEntity> getProductListByBrand(SearchRequest searchPageRequest) {
		log.info("In getProductListByBrand page ");
		List<ProductMasterJswEntity> pageResult = productRepository.findByBrandId(searchPageRequest.getBrandId() );
		return pageResult;
	}

	@Override
	public List<ProductMasterJswEntity> getProductList(SearchRequest searchPageRequest) {
		log.info("In getAllProductList page ");
		List<ProductMasterJswEntity> pageResult = productRepository.findAll();
		return pageResult;
	}

	@Override
	public Map<Integer, String> getProductsMap() {
		log.info("In getProductsMap page ");
		List<ProductMasterJswEntity> list = productRepository.findAll();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (ProductMasterJswEntity i : list) {
			map.put(i.getProductId(), i.getProductName());
		}
		return map;
	}

	@Override
	public Map<Integer, String> getCategoryMap() {
		log.info("In getCategoryMap page ");
		List<CategoryMasterJswEntity> list = categoryRepository.findAll();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (CategoryMasterJswEntity i : list) {
			map.put(i.getCategoryId(), i.getCategoryName());
		}
		return map;
	}

	@Override
	public Map<Integer, String> getSubCategoryMap() {
		log.info("In getSubCategoryMap page ");
		List<SubCategoryJswEntity> list = subCategoryRepository.findAll();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (SubCategoryJswEntity i : list) {
			map.put(i.getSubcategoryId(), i.getSubcategoryName());
		}
		return map;
	}

	@Override
	public Map<Integer, String> getLeafCategoryMap() {
		log.info("In getLeafCategoryMap page ");
		List<LeafCategoryJswEntity> list = leafCategoryRepository.findAll();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (LeafCategoryJswEntity i : list) {
			map.put(i.getLeafcategoryId(), i.getLeafcategoryName());
		}
		return map;
	}

	@Override
	public Map<Integer, String> getCoatingMap() {
		log.info("In getCoatingMap page ");
		List<CoatingtypeMasterJswEntity> list = coatingRepository.findAll();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (CoatingtypeMasterJswEntity i : list) {
			map.put(i.getCoatingtypeId(), i.getCoatingtype());
		}
		return map;
	}

	@Override
	public Map<Integer, String> getSurfaceMap() {
		log.info("In getSurfaceMap page ");
		List<SurfacetypeMasterJswEntity> list = surfaceRepository.findAll();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (SurfacetypeMasterJswEntity i : list) {
			map.put(i.getSurfacetypeId(), i.getSurfacetypeName());
		}
		return map;
	}

	@Override
	public Map<Integer, String> getUomMap() {
		log.info("In getUomMap page ");
		List<UomMasterJswEntity> list = uomRepository.findAll();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (UomMasterJswEntity i : list) {
			map.put(i.getUomId(), i.getUomName());
		}
		return map;
	}

	@Override
	public Map<Integer, String> getGradeMap() {
		log.info("In getGradeMap page ");
		List<GradeMasterJswEntity> list = gradeRepository.findAll();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (GradeMasterJswEntity i : list) {
			map.put(i.getGradeId(), i.getGradeName());
		}
		return map;
	}

	@Override
	public Map<Integer, String> getSubGradeMap() {
		log.info("In getSubGradeMap page ");
		List<SubgradeMasterJswEntity> list = subGradeJswRepository.findAll();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (SubgradeMasterJswEntity i : list) {
			map.put(i.getSubgradeId(), i.getSubgradeName());
		}
		return map;
	}

	@Override
	public Map<Integer, String> getBrandMap() {
		log.info("In getBrandMap page ");
		List<BrandMasterJswEntity> list = brandRepository.findAll();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (BrandMasterJswEntity i : list) {
			map.put(i.getBrandId(), i.getBrandName());
		}
		return map;
	}

	@Override
	public Map<Integer, String> getFormMap() {
		log.info("In getFormMap page ");
		List<FormMasterJswEntity> list = formRepository.findAll();
		Map<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (FormMasterJswEntity i : list) {
			map.put(i.getFormId(), i.getFormName());
		}
		return map;
	}

	@Override
	public MaterialResponseDto getProductName(String mmId) {
		log.info("In getProductName page ");
		MaterialResponseDto productName = null;
		List<Object[]> productNameList = productRepository.getProductName(mmId);
		for (Object[] result : productNameList) {
			productName = new MaterialResponseDto();
			productName.setMatId(result[0] != null ? (Integer) result[0] : null);
			productName.setDescription(result[1] != null ? (String) result[1] : null);
		}
		return productName;
	}
	
	@Override
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
	public GradeMasterJswEntity getGradeById(Integer gradeId) {
		log.info("In getGradeById page ");
		Optional<GradeMasterJswEntity> list = gradeRepository.findById(gradeId);
		GradeMasterJswEntity kk = new GradeMasterJswEntity();
		if (list.isPresent()) {
			kk = list.get();
		}
		return kk;
	}
}
