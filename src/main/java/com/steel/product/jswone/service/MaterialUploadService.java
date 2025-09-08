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

	Integer setCategoryMaster(String categoryName);

	Integer setSubCategoryMaster(String subCategoryName, Integer categoryId);

	Integer setLeafCategoryMaster(String leafcategoryName, Integer subCategoryId);

	Integer setBrandNameMaster(String brandName, Integer leafcategoryId);

	Integer setUomMaster(String uom, Integer producttypeId);

	Integer setFormMaster(String form, Integer producttypeId);

	Integer setGradeMaster(String grade, Integer producttypeId);

	Integer setSubGradeMaster(String subgrade, Integer gradeId);

	Integer setProductMaster(String productName, MaterialMasterJswEntity mmEntity);

	Integer setSurfacetypeMaster(String surfacetype, Integer producttypeId);

	Integer setCoatingtypeMaster(String coatingtype, Integer producttypeId);

}
