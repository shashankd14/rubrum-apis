package com.steel.product.trading.service;

import com.steel.product.application.service.AWSS3Service;
import com.steel.product.trading.entity.MaterialMasterEntity;
import com.steel.product.trading.repository.MaterialMasterRepository;
import com.steel.product.trading.request.MaterialMasterRequest;
import com.steel.product.trading.request.MaterialMasterSearch;

import lombok.extern.log4j.Log4j2;

import java.util.Date;
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
	MaterialMasterRepository materialMasterRepository;

	@Autowired
	MaterialMasterService materialMasterService;

	@Autowired
	AWSS3Service awsS3Service;

	@Value("${templateFilesPath}")
	private String templateFilesPath;
	
	@Override
	public ResponseEntity<Object> save(MaterialMasterRequest materialMasterRequest, MultipartFile itemImage,
			MultipartFile crossSectionalImage) {

		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		try {
			MaterialMasterEntity materialMasterEntity = new MaterialMasterEntity();
			BeanUtils.copyProperties(materialMasterRequest, materialMasterEntity);
			materialMasterEntity.setCreatedBy(materialMasterRequest.getUserId());
			materialMasterEntity.setCreatedOn(new Date());

			if (itemImage != null) {
				String fileUrl = awsS3Service.persistTradingFiles(templateFilesPath, materialMasterRequest.getItemCode(), itemImage);
				materialMasterEntity.setItemImage(fileUrl);
			}
			if (crossSectionalImage != null) {
				String fileUrl = awsS3Service.persistTradingFiles(templateFilesPath, materialMasterRequest.getItemCode(), crossSectionalImage);
				materialMasterEntity.setCrossSectionalImage(fileUrl);
			}
			materialMasterRepository.save(materialMasterEntity);
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Materal details saved successfully..! \"}",	new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("error is ==" + e.getMessage());
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Error Occurred\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public Page<MaterialMasterEntity> getMaterialList(MaterialMasterSearch searchListPageRequest) {
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
		Optional<MaterialMasterEntity> kk =  materialMasterRepository.findById(itemId);
		MaterialMasterEntity materialMasterEntity = null;
		if(kk.isPresent()) {
			materialMasterEntity = kk.get();
		}
		return materialMasterEntity;
	}
 

}
