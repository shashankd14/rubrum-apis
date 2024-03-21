package com.steel.product.trading.service;

import com.steel.product.application.service.AWSS3Service;
import com.steel.product.trading.entity.CategoryEntity;
import com.steel.product.trading.entity.MaterialMasterEntity;
import com.steel.product.trading.entity.SubCategoryEntity;
import com.steel.product.trading.repository.CategoryRepository;
import com.steel.product.trading.repository.MaterialMasterRepository;
import com.steel.product.trading.repository.SubCategoryRepository;
import com.steel.product.trading.request.CategoryRequest;
import com.steel.product.trading.request.DeleteRequest;
import com.steel.product.trading.request.MaterialMasterRequest;
import com.steel.product.trading.request.SearchRequest;
import com.steel.product.trading.request.SubCategoryRequest;

import lombok.extern.log4j.Log4j2;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Log4j2
public class MaterialMasterServiceImpl implements MaterialMasterService {

	@Autowired
	SubCategoryRepository subCategoryRepository;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	MaterialMasterRepository materialMasterRepository;

	@Autowired
	AWSS3Service awsS3Service;

	@Value("${templateFilesPath}")
	private String templateFilesPath;
	
	// Material Master Master APIs

	@Override
	public ResponseEntity<Object> save(MaterialMasterRequest materialMasterRequest, MultipartFile itemImage,
			MultipartFile crossSectionalImage) {
		log.info("In save page ");

		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		String message="Materal details saved successfully..! ";
		try {
			MaterialMasterEntity materialMasterEntity = new MaterialMasterEntity();
			BeanUtils.copyProperties(materialMasterRequest, materialMasterEntity);

			if(materialMasterRequest.getSubCategoryId()!=null && materialMasterRequest.getSubCategoryId() >0 ) {
				Optional<SubCategoryEntity> subCat = subCategoryRepository.findById(materialMasterRequest.getSubCategoryId());
				if (subCat.isPresent()) {
					materialMasterEntity.setSubCategoryEntity(subCat.get());
				}
			}
			if(materialMasterRequest.getCategoryId()!=null && materialMasterRequest.getCategoryId() >0 ) {
				Optional<CategoryEntity> cate = categoryRepository.findById(materialMasterRequest.getCategoryId());
				if (cate.isPresent()) {
					materialMasterEntity.setCategoryEntity(cate.get());
				}
			}
			if(materialMasterEntity.getItemId()!=null && materialMasterEntity.getItemId()>0) {
				MaterialMasterEntity oldEntity = null;

				Optional<MaterialMasterEntity> kk = materialMasterRepository.findById(materialMasterEntity.getItemId());
				if (kk.isPresent()) {
					oldEntity = kk.get();
				}
				if(oldEntity!=null && oldEntity.getItemId()>0) {
					
					List<MaterialMasterEntity> testItemName = materialMasterRepository.findByItemName(materialMasterEntity.getItemName(), oldEntity.getItemId());
					if(testItemName!=null && testItemName.size()>0) {
						return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered Item Name already used\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
					}
					List<MaterialMasterEntity> testItemCode = materialMasterRepository.findByItemCode(materialMasterEntity.getItemCode(), oldEntity.getItemId());
					if(testItemCode!=null && testItemCode.size()>0) {
						return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered Item Code already used\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
					}
					materialMasterEntity.setUpdatedBy( materialMasterRequest.getUserId());
					materialMasterEntity.setUpdatedOn(new Date());
					materialMasterEntity.setCreatedBy(oldEntity.getCreatedBy());
					materialMasterEntity.setCreatedOn(oldEntity.getCreatedOn());
					materialMasterEntity.setItemImage(oldEntity.getItemImage());
					materialMasterEntity.setCrossSectionalImage(oldEntity.getCrossSectionalImage());
					materialMasterEntity.setIsDeleted(false);
					message="Materal details updated successfully..! ";
				} else {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Please enter valid data\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				List<MaterialMasterEntity> testItemName = materialMasterRepository.findByItemName(materialMasterEntity.getItemName());
				if(testItemName!=null && testItemName.size()>0) {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered Item Name already used\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				List<MaterialMasterEntity> testItemCode = materialMasterRepository.findByItemCode(materialMasterEntity.getItemCode());
				if(testItemCode!=null && testItemCode.size()>0) {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered Item Code already used\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				materialMasterEntity.setCreatedBy(materialMasterRequest.getUserId());
				materialMasterEntity.setCreatedOn(new Date());
				materialMasterEntity.setIsDeleted(false);
			}

			if (itemImage != null) {
				String fileUrl = awsS3Service.persistTradingFiles(templateFilesPath, materialMasterRequest.getItemCode(), itemImage);
				materialMasterEntity.setItemImage(fileUrl);
			}
			if (crossSectionalImage != null) {
				String fileUrl = awsS3Service.persistTradingFiles(templateFilesPath, materialMasterRequest.getItemCode(), crossSectionalImage);
				materialMasterEntity.setCrossSectionalImage(fileUrl);
			}
			materialMasterRepository.save(materialMasterEntity);
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \""+message+" \"}",	new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("error is ==" + e.getMessage());
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Error Occurred\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public Page<MaterialMasterEntity> getMaterialList(SearchRequest searchListPageRequest) {
		log.info("In getMaterialList page ");
		Pageable pageable = PageRequest.of((searchListPageRequest.getPageNo() - 1), searchListPageRequest.getPageSize(), Sort.by("itemId").descending());

		if (searchListPageRequest.getSearchText() != null && searchListPageRequest.getSearchText().length() > 0) {
			Page<MaterialMasterEntity> pageResult = materialMasterRepository.findAllWithSearchText(searchListPageRequest.getSearchText(), pageable);
			return pageResult;
		} else {
			Page<MaterialMasterEntity> pageResult = materialMasterRepository.findAll(pageable);
			return pageResult;
		}
	}

	@Override
	public MaterialMasterEntity findByItemId(Integer itemId) {
		Optional<MaterialMasterEntity> kk = materialMasterRepository.findByItemIdAndIsDeleted(itemId, false);
		MaterialMasterEntity materialMasterEntity = null;
		if (kk.isPresent()) {
			materialMasterEntity = kk.get();
			if (materialMasterEntity.getItemImage() != null && materialMasterEntity.getItemImage().length() > 0) {
				materialMasterEntity.setItemImagePresignedURL(awsS3Service.generatePresignedUrlForTrading(materialMasterEntity.getItemImage()));
			}
			if (materialMasterEntity.getCrossSectionalImage() != null && materialMasterEntity.getCrossSectionalImage().length() > 0) {
				materialMasterEntity.setCrossSectionalImagePresignedURL(awsS3Service.generatePresignedUrlForTrading(materialMasterEntity.getCrossSectionalImage()));
			}
		}
		return materialMasterEntity;
	}

	@Override
	public ResponseEntity<Object> materialDelete(DeleteRequest deleteRequest) {
		log.info("In materialDelete page ");
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		
		try {
			materialMasterRepository.deleteData(deleteRequest.getIds(), deleteRequest.getUserId());
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Selected material has been deleted successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Error Occurred\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	// Category Master APIs
	
	@Override
	public ResponseEntity<Object> categorySave(CategoryRequest categoryRequest) {
		log.info("In categorySave page ");
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		String message="Category details saved successfully..! ";
		try {
			CategoryEntity categoryEntity = new CategoryEntity();
			BeanUtils.copyProperties(categoryRequest, categoryEntity);
			categoryEntity.setIsDeleted(false);
			if (categoryEntity.getCategoryId() != null && categoryEntity.getCategoryId() > 0) {
				CategoryEntity oldEntity = null;

				Optional<CategoryEntity> kk = categoryRepository.findById(categoryEntity.getCategoryId());
				if (kk.isPresent()) {
					oldEntity = kk.get();
				}
				if(oldEntity!=null && oldEntity.getCategoryId()>0) {
					
					List<CategoryEntity> testItemName = categoryRepository.findByCategoryName(categoryEntity.getCategoryName(), oldEntity.getCategoryId());
					if(testItemName!=null && testItemName.size()>0) {
						return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered Category Name already used\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
					}
					categoryEntity.setUpdatedBy( categoryRequest.getUserId());
					categoryEntity.setUpdatedOn(new Date());
					categoryEntity.setCreatedBy(oldEntity.getCreatedBy());
					categoryEntity.setCreatedOn(oldEntity.getCreatedOn());
					message="Category details updated successfully..! ";
				} else {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Please enter valid data\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				List<CategoryEntity> testItemName = categoryRepository.findByCategoryName(categoryEntity.getCategoryName());
				if(testItemName!=null && testItemName.size()>0) {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered Category Name already used\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				categoryEntity.setCreatedBy(categoryRequest.getUserId());
				categoryEntity.setCreatedOn(new Date());
			}
			categoryRepository.save(categoryEntity);
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \""+message+" \"}",	new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("error is ==" + e.getMessage());
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Error Occurred\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public Page<CategoryEntity> getCategoryList(SearchRequest searchListPageRequest) {
		log.info("In getCategoryList page ");
		Pageable pageable = PageRequest.of((searchListPageRequest.getPageNo() - 1), searchListPageRequest.getPageSize(), Sort.by("categoryId").descending());

		if (searchListPageRequest.getSearchText() != null && searchListPageRequest.getSearchText().length() > 0) {
			Page<CategoryEntity> pageResult = categoryRepository.findAllWithSearchText(searchListPageRequest.getSearchText(), pageable);
			return pageResult;
		} else {
			Page<CategoryEntity> pageResult = categoryRepository.findAll(pageable);
			return pageResult;
		}
	}

	@Override
	public CategoryEntity findByCategoryId(Integer id) {
		log.info("In findByCategoryId page ");
		Optional<CategoryEntity> kk = categoryRepository.findByCategoryIdAndIsDeleted(id, false);
		CategoryEntity categoryEntity = null;
		if (kk.isPresent()) {
			categoryEntity = kk.get();
		}
		return categoryEntity;
	}

	@Override
	public ResponseEntity<Object> categoryDelete(DeleteRequest deleteRequest) {
		log.info("In categoryDelete page ");
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		
		try {
			categoryRepository.deleteData(deleteRequest.getIds(), deleteRequest.getUserId());
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Selected category has been deleted successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Error Occurred\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	// Sub-Category Master APIs

	@Override
	public ResponseEntity<Object> subcategorySave(SubCategoryRequest categoryRequest) {
		log.info("In subcategorySave page ");
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		String message="Sub-Category details saved successfully..! ";
		try {
			SubCategoryEntity subcategoryEntity = new SubCategoryEntity();
			BeanUtils.copyProperties(categoryRequest, subcategoryEntity);
			subcategoryEntity.setIsDeleted(false);
			if (subcategoryEntity.getSubcategoryId() != null && subcategoryEntity.getSubcategoryId() > 0) {
				SubCategoryEntity oldEntity = null;

				Optional<SubCategoryEntity> kk = subCategoryRepository.findById(subcategoryEntity.getSubcategoryId());
				if (kk.isPresent()) {
					oldEntity = kk.get();
				}
				if(oldEntity!=null && oldEntity.getSubcategoryId()>0) {
					
					List<SubCategoryEntity> testItemName = subCategoryRepository.findBySubCategoryNameforUpdate(subcategoryEntity.getSubcategoryName(), subcategoryEntity.getCategoryId(), oldEntity.getSubcategoryId());
					if(testItemName!=null && testItemName.size()>0) {
						return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered Sub-Category Name already used\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
					}
					subcategoryEntity.setUpdatedBy( categoryRequest.getUserId());
					subcategoryEntity.setUpdatedOn(new Date());
					subcategoryEntity.setCreatedBy(oldEntity.getCreatedBy());
					subcategoryEntity.setCreatedOn(oldEntity.getCreatedOn());
					message="Sub-Category details updated successfully..! ";
				} else {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Please enter valid data\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				List<SubCategoryEntity> testItemName = subCategoryRepository.findBySubCategoryNameforInsert(subcategoryEntity.getSubcategoryName(), subcategoryEntity.getCategoryId());
				if(testItemName!=null && testItemName.size()>0) {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered Sub-Category Name already used\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				subcategoryEntity.setCreatedBy(categoryRequest.getUserId());
				subcategoryEntity.setCreatedOn(new Date());
			}
			subCategoryRepository.save(subcategoryEntity);
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \""+message+" \"}",	new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("error is ==" + e.getMessage());
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Error Occurred\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public Page<SubCategoryEntity> getSubCategoryList(SearchRequest searchListPageRequest) {
		log.info("In getSubCategoryList page ");
		Pageable pageable = PageRequest.of((searchListPageRequest.getPageNo() - 1), searchListPageRequest.getPageSize(), Sort.by("subcategoryId").descending());

		if (searchListPageRequest.getSearchText() != null && searchListPageRequest.getSearchText().length() > 0) {
			Page<SubCategoryEntity> pageResult = subCategoryRepository.findAllWithSearchText(searchListPageRequest.getSearchText(), pageable);
			return pageResult;
		} else {
			Page<SubCategoryEntity> pageResult = subCategoryRepository.findAll(pageable);
			return pageResult;
		}
	}

	@Override
	public SubCategoryEntity findBySubCategoryId(Integer id) {
		log.info("In findBySubCategoryId page ");
		Optional<SubCategoryEntity> kk = subCategoryRepository.findBySubcategoryIdAndIsDeleted(id, false);
		SubCategoryEntity categoryEntity = null;
		if (kk.isPresent()) {
			categoryEntity = kk.get();
		}
		return categoryEntity;
	}

	@Override
	public ResponseEntity<Object> subcategoryDelete(DeleteRequest deleteRequest) {
		log.info("In subcategoryDelete page ");
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		
		try {
			subCategoryRepository.deleteData(deleteRequest.getIds(), deleteRequest.getUserId());
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Selected category has been deleted successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Error Occurred\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
}
