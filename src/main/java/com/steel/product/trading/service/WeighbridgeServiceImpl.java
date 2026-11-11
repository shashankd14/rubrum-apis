package com.steel.product.trading.service;

import com.steel.product.trading.exception.TradingErrors;
import com.steel.product.trading.entity.WeighbridgeEntity;
import com.steel.product.trading.repository.WeighbridgeRepository;
import com.steel.product.trading.request.DeleteRequest;
import com.steel.product.trading.request.SearchRequest;
import com.steel.product.trading.request.WeighbridgeRequest;

import lombok.extern.log4j.Log4j2;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class WeighbridgeServiceImpl implements WeighbridgeService {

	@Autowired
	WeighbridgeRepository weighbridgeRepository;
	
	@Override
	public ResponseEntity<Object> save(WeighbridgeRequest vendorRequest) {
		log.debug("In Weighbridge save page ");
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		String message="Weighbridge details saved successfully..! ";
		try {
			WeighbridgeEntity weighbridgeEntity = new WeighbridgeEntity();
			BeanUtils.copyProperties(vendorRequest, weighbridgeEntity);
			weighbridgeEntity.setIsDeleted(false);
			if (weighbridgeEntity.getWeighbridgeId() != null && weighbridgeEntity.getWeighbridgeId() > 0) {
				WeighbridgeEntity oldEntity = null;

				Optional<WeighbridgeEntity> kk = weighbridgeRepository.findById(weighbridgeEntity.getWeighbridgeId());
				if (kk.isPresent()) {
					oldEntity = kk.get();
				}
				if(oldEntity!=null && oldEntity.getWeighbridgeId()>0) {
					
					List<WeighbridgeEntity> testItemName = weighbridgeRepository.findByWeighbridgeName( weighbridgeEntity.getWeighbridgeName() , oldEntity.getWeighbridgeId());
					if(testItemName!=null && testItemName.size()>0) {
						return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered Weighbridge already used.\"}", header, HttpStatus.CONFLICT);
					}
					weighbridgeEntity.setUpdatedBy( vendorRequest.getUserId());
					weighbridgeEntity.setUpdatedOn(new Date());
					weighbridgeEntity.setCreatedBy(oldEntity.getCreatedBy());
					weighbridgeEntity.setCreatedOn(oldEntity.getCreatedOn());
					message="Weighbridge details updated successfully..! ";
				} else {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Please enter valid data.\"}", header, HttpStatus.BAD_REQUEST);
				}
			} else {
				List<WeighbridgeEntity> testItemName = weighbridgeRepository.findByWeighbridgeName(weighbridgeEntity.getWeighbridgeName());
				if(testItemName!=null && testItemName.size()>0) {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered Weighbridge already used\"}", header, HttpStatus.CONFLICT);
				}
				weighbridgeEntity.setCreatedBy(vendorRequest.getUserId());
				weighbridgeEntity.setCreatedOn(new Date());
			}
			weighbridgeRepository.save(weighbridgeEntity);
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \""+message+" \"}",	new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			response = TradingErrors.failure("save the weighbridge", e);
		}
		return response;
	}

	@Override
	public Page<WeighbridgeEntity> getWeighbridgeList(SearchRequest searchListPageRequest) {
		log.debug("In getWeighbridgeList page ");
		Pageable pageable = PageRequest.of((searchListPageRequest.getPageNo() - 1), searchListPageRequest.getPageSize(), Sort.by("weighbridgeId").descending());

		if (searchListPageRequest.getSearchText() != null && searchListPageRequest.getSearchText().length() > 0) {
			Page<WeighbridgeEntity> pageResult = weighbridgeRepository.findAllWithSearchText(searchListPageRequest.getSearchText(), pageable);
			return pageResult;
		} else {
			Page<WeighbridgeEntity> pageResult = weighbridgeRepository.findAll(pageable);
			return pageResult;
		}
	}

	@Override
	public WeighbridgeEntity findByWeighbridgeId(Integer id) {
		log.debug("In findByWeighbridgeId page ");
		Optional<WeighbridgeEntity> kk = weighbridgeRepository.findByWeighbridgeIdAndIsDeleted(id, false);
		WeighbridgeEntity categoryEntity = null;
		if (kk.isPresent()) {
			categoryEntity = kk.get();
		}
		return categoryEntity;
	}

	@Override
	public ResponseEntity<Object>  weighbridgeDelete(DeleteRequest deleteRequest) {
		log.debug("In weighbridgeDelete page ");
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		
		try {
			weighbridgeRepository.deleteData(deleteRequest.getIds(), deleteRequest.getUserId());
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Selected Weighbridge has been deleted successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			response = TradingErrors.failure("delete the selected weighbridges", e);
		}
		return response;
	}
	
}
