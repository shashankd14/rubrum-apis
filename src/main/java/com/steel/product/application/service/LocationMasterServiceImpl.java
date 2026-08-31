package com.steel.product.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.steel.product.application.dao.LocationMasterRepository;
import com.steel.product.application.dto.party.LocationMasterDto;
import com.steel.product.application.entity.AdminUserEntity;
import com.steel.product.application.entity.LocationMasterEntity;
import com.steel.product.application.entity.UserLocationMappingEntity;
import com.steel.product.application.util.CommonUtil;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class LocationMasterServiceImpl implements LocationMasterService {

	private final LocationMasterRepository locationMasterRepository;
	
	private final CommonUtil commonUtil;

	public LocationMasterServiceImpl(LocationMasterRepository locationMasterRepository, CommonUtil commonUtil) {
		this.locationMasterRepository = locationMasterRepository;
		this.commonUtil = commonUtil;
	}

	@Override
	public List<LocationMasterDto> getAllLocations() {
		AdminUserEntity adminUserEntity = commonUtil.getUserDetails();
		List<UserLocationMappingEntity> userPartyMaps = adminUserEntity.getLocationMap();

		List<LocationMasterEntity> locationsIdsList = null;
		List<Integer> locationIds = null;
		if (!CollectionUtils.isEmpty(userPartyMaps)) {
			locationIds = userPartyMaps.stream().map(UserLocationMappingEntity::getLocationId).collect(Collectors.toList());
			log.info("Fetching locations for partyIds: {}", locationIds);
		}
		locationsIdsList = locationMasterRepository.findAllLocations(locationIds);
		
		return locationsIdsList.stream().map(this::toDto).collect(Collectors.toList());
	}

	public List<LocationMasterDto> getAllLocadtions() {
		log.info("Fetching all active locations");
		try {
			List<LocationMasterEntity> locations = locationMasterRepository.findAllActive();
			return locations.stream().map(this::toDto).collect(Collectors.toList());
		} catch (Exception e) {
			log.error("Error fetching locations: {}", e.getMessage());
			throw new RuntimeException("Failed to fetch locations", e);
		}
	}
	
	@Override
	public LocationMasterEntity getByLocationId(Integer locationId) {
	    log.info("Fetching location by id={}", locationId);
	    try {
	        LocationMasterEntity entity = locationMasterRepository.findByLocationId(locationId)
	                .orElseThrow(() -> new RuntimeException("Location not found for id: " + locationId));
	        return entity;
	    } catch (Exception e) {
	        log.error("Error fetching location by id={}, error={}", locationId, e.getMessage());
	        throw new RuntimeException("Failed to fetch location: " + e.getMessage(), e);
	    }
	}

	private LocationMasterDto toDto(LocationMasterEntity entity) {
	    LocationMasterDto dto = new LocationMasterDto();
	    dto.setLocationId(entity.getLocationId());
	    dto.setLocationName(entity.getLocationName());
	    dto.setLocationDesc(entity.getDesc());
	    return dto;
	}
}