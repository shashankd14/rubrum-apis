package com.steel.product.trading.service;

import com.steel.product.trading.entity.LocationEntity;
import com.steel.product.trading.repository.LocationRepository;
import com.steel.product.trading.request.DeleteRequest;
import com.steel.product.trading.request.LocationRequest;
import com.steel.product.trading.request.SearchRequest;
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
public class LocationServiceImpl implements LocationService {

	@Autowired
	LocationRepository locationRepository;
	
	@Override
	public ResponseEntity<Object> save(LocationRequest vendorRequest) {
		log.info("In location save page ");
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		String message="Location details saved successfully..! ";
		try {
			LocationEntity locationEntity = new LocationEntity();
			BeanUtils.copyProperties(vendorRequest, locationEntity);
			locationEntity.setIsDeleted(false);
			if (locationEntity.getLocationId() != null && locationEntity.getLocationId() > 0) {
				LocationEntity oldEntity = null;

				Optional<LocationEntity> kk = locationRepository.findById(locationEntity.getLocationId());
				if (kk.isPresent()) {
					oldEntity = kk.get();
				}
				if(oldEntity!=null && oldEntity.getLocationId()>0) {
					
					List<LocationEntity> testItemName = locationRepository.findByLocationName( locationEntity.getLocationName(), oldEntity.getLocationId());
					if(testItemName!=null && testItemName.size()>0) {
						return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered Location Name already used.\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
					}
					locationEntity.setUpdatedBy( vendorRequest.getUserId());
					locationEntity.setUpdatedOn(new Date());
					locationEntity.setCreatedBy(oldEntity.getCreatedBy());
					locationEntity.setCreatedOn(oldEntity.getCreatedOn());
					message="Location details updated successfully..! ";
				} else {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Please enter valid data.\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				List<LocationEntity> testItemName = locationRepository.findByLocationName(locationEntity.getLocationName());
				if(testItemName!=null && testItemName.size()>0) {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered Location Name already used\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				locationEntity.setCreatedBy(vendorRequest.getUserId());
				locationEntity.setCreatedOn(new Date());
			}
			locationRepository.save(locationEntity);
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \""+message+" \"}",	new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("error is ==" + e.getMessage());
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Error Occurred\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public Page<LocationEntity> getLocationList(SearchRequest searchListPageRequest) {
		log.info("In getVendorList page ");
		Pageable pageable = PageRequest.of((searchListPageRequest.getPageNo() - 1), searchListPageRequest.getPageSize(), Sort.by("locationId").descending());

		if (searchListPageRequest.getSearchText() != null && searchListPageRequest.getSearchText().length() > 0) {
			Page<LocationEntity> pageResult = locationRepository.findAllWithSearchText(searchListPageRequest.getSearchText(), pageable);
			return pageResult;
		} else {
			Page<LocationEntity> pageResult = locationRepository.findAll(pageable);
			return pageResult;
		}
	}

	@Override
	public LocationEntity findByLocationId(Integer id) {
		log.info("In findByVendorId page ");
		Optional<LocationEntity> kk = locationRepository.findByLocationIdAndIsDeleted(id, false);
		LocationEntity categoryEntity = null;
		if (kk.isPresent()) {
			categoryEntity = kk.get();
		}
		return categoryEntity;
	}

	@Override
	public ResponseEntity<Object> locationDelete(DeleteRequest deleteRequest) {
		log.info("In vendorDelete page ");
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		
		try {
			locationRepository.deleteData(deleteRequest.getIds(), deleteRequest.getUserId());
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Selected Location has been deleted successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Error Occurred\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
}
