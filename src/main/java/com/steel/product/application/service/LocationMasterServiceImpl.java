package com.steel.product.application.service;

import com.steel.product.application.dao.LocationMasterRepository;
import com.steel.product.application.dto.party.LocationMasterDto;
import com.steel.product.application.entity.LocationMasterEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationMasterServiceImpl implements LocationMasterService {

	private static final Logger logger = LoggerFactory.getLogger(LocationMasterServiceImpl.class);

	private final LocationMasterRepository locationMasterRepository;

	public LocationMasterServiceImpl(LocationMasterRepository locationMasterRepository) {
		this.locationMasterRepository = locationMasterRepository;
	}

	@Override
	public List<LocationMasterDto> getAllLocations() {
		logger.info("Fetching all active locations");
		try {
			List<LocationMasterEntity> locations = locationMasterRepository.findAllActive();
			return locations.stream().map(this::toDto).collect(Collectors.toList());
		} catch (Exception e) {
			logger.error("Error fetching locations: {}", e.getMessage());
			throw new RuntimeException("Failed to fetch locations", e);
		}
	}
	
	@Override
	public LocationMasterEntity getByLocationId(Integer locationId) {
	    logger.info("Fetching location by id={}", locationId);
	    try {
	        LocationMasterEntity entity = locationMasterRepository.findByLocationId(locationId)
	                .orElseThrow(() -> new RuntimeException("Location not found for id: " + locationId));
	        return entity;
	    } catch (Exception e) {
	        logger.error("Error fetching location by id={}, error={}", locationId, e.getMessage());
	        throw new RuntimeException("Failed to fetch location: " + e.getMessage(), e);
	    }
	}

	private LocationMasterDto toDto(LocationMasterEntity entity) {
	    LocationMasterDto dto = new LocationMasterDto();
	    dto.setLocationId(entity.getLocationId());
	    dto.setLocationName(entity.getLocationName());
	    dto.setLocationDesc(entity.getLocationDesc());
	    return dto;
	}
}