package com.steel.product.jswone.controller;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
import com.steel.product.jswone.service.MaterialMasterJswService;
import com.steel.product.trading.entity.LocationEntity;
import com.steel.product.trading.service.LocationService;

@RestController
@CrossOrigin
@Tag(name = "Material Master", description = "Material Master")
@RequestMapping({ "/material" })
public class MaterialMasterController {

	@Autowired
	private MaterialMasterJswService materialService;
	
	@Autowired
	private LocationService locationService;

	@PostMapping({ "/category/list" })
	public ResponseEntity<Object> getCategoryList(@RequestBody SearchRequest searchPageRequest) {
		List<CategoryMasterJswEntity> pageResult = materialService.getCategoryList(searchPageRequest);
		return new ResponseEntity<Object>(pageResult, HttpStatus.OK);
	}

	@PostMapping({ "/subcategory/list/categoryId" })
	public ResponseEntity<Object> getSubCategoryList(@RequestBody SearchRequest searchPageRequest) {
		List<SubCategoryJswEntity> resp = materialService.findSubCategoryByCategoryId(searchPageRequest);
		return new ResponseEntity<Object>(resp, HttpStatus.OK);
	}

	@PostMapping({ "/leafcategory/list/subcategoryId" })
	public ResponseEntity<Object> getLeafCategoryList(@RequestBody SearchRequest searchPageRequest) {
		List<LeafCategoryJswEntity> resp = materialService.findLeafCategoryBySubcategoryId(searchPageRequest);
		return new ResponseEntity<Object>(resp, HttpStatus.OK);
	}

	@PostMapping({ "/brand/list/leafcategoryId" })
	public ResponseEntity<Object> getBrandList(@RequestBody SearchRequest searchPageRequest) {
		List<BrandMasterJswEntity> resp = materialService.findBrandByLeafcategoryId(searchPageRequest);
		return new ResponseEntity<Object>(resp, HttpStatus.OK);
	}

	@PostMapping({ "/form/list" })
	public ResponseEntity<Object> getFormList(@RequestBody SearchRequest searchPageRequest) {
		List<FormMasterJswEntity> pageResult = materialService.getFormListByProduct(searchPageRequest);
		return new ResponseEntity<Object>(pageResult, HttpStatus.OK);
	}

	@PostMapping({ "/uom/list" })
	public ResponseEntity<Object> getUomList(@RequestBody SearchRequest searchPageRequest) {
		List<UomMasterJswEntity> pageResult = materialService.getUomListByProduct(searchPageRequest);
		return new ResponseEntity<Object>(pageResult, HttpStatus.OK);
	}

	@PostMapping({ "/surface/list" })
	public ResponseEntity<Object> getSurfaceList(@RequestBody SearchRequest searchPageRequest) {
		List<SurfacetypeMasterJswEntity> pageResult = materialService.getSurfaceListByProduct(searchPageRequest);
		return new ResponseEntity<Object>(pageResult, HttpStatus.OK);
	}

	@PostMapping({ "/coating/list" })
	public ResponseEntity<Object> getCoatingList(@RequestBody SearchRequest searchPageRequest) {
		List<CoatingtypeMasterJswEntity> pageResult = materialService.getCoatingListByProduct(searchPageRequest);
		return new ResponseEntity<Object>(pageResult, HttpStatus.OK);
	}

	@PostMapping({ "/grade/list" })
	public ResponseEntity<Object> getGradeList(@RequestBody SearchRequest searchPageRequest) {
		List<GradeMasterJswEntity> pageResult = materialService.getGradeListByProduct( searchPageRequest);
		return new ResponseEntity<Object>(pageResult, HttpStatus.OK);
	}

	@PostMapping({ "/subgrade/list/gradeId" })
	public ResponseEntity<Object> getSubGradeList(@RequestBody SearchRequest searchPageRequest) {
		List<SubgradeMasterJswEntity > resp = materialService.getSubGradeListByGrade( searchPageRequest);
		return new ResponseEntity<Object>(resp, HttpStatus.OK);
	}

	@PostMapping({ "/product/list" })
	public ResponseEntity<Object> getProductListByBrand(@RequestBody SearchRequest searchPageRequest) {
		List<ProductMasterJswEntity > pageResult = materialService.getProductListByBrand(searchPageRequest);
		return new ResponseEntity<Object>(pageResult, HttpStatus.OK);
	}
	 
	@PostMapping({ "/product" })
	public ResponseEntity<Object> getAllProductList() {
		List<ProductMasterJswEntity > pageResult = materialService.getProductList();
		return new ResponseEntity<Object>(pageResult, HttpStatus.OK);
	} 
	 
	@PostMapping({ "/grade" })
	public ResponseEntity<Object> getAllGradesList() {
		List<GradeMasterJswEntity > pageResult = materialService.getGradesList();
		return new ResponseEntity<Object>(pageResult, HttpStatus.OK);
	} 
	
	@PostMapping({ "/location" })
	public ResponseEntity<Object> list(@RequestBody com.steel.product.trading.request.SearchRequest searchRequest) {
		Map<String, Object> response = new HashMap<>();

		if (searchRequest.getId() != null && searchRequest.getId() > 0) {
			LocationEntity resp = locationService.findByLocationId( searchRequest.getId());
			return new ResponseEntity<Object>(resp, HttpStatus.OK);
		} else {
			Page<LocationEntity> pageResult = locationService.getLocationList(searchRequest);
			response.put("content", pageResult.toList());
			response.put("currentPage", pageResult.getNumber());
			response.put("totalItems", pageResult.getTotalElements());
			response.put("totalPages", pageResult.getTotalPages());
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

}
