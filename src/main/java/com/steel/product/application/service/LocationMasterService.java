package com.steel.product.application.service;

import java.util.List;

import com.steel.product.application.dto.party.LocationMasterDto;
import com.steel.product.application.entity.LocationMasterEntity;

public interface LocationMasterService {
	
	List<LocationMasterDto> getAllLocations();

	LocationMasterEntity getByLocationId(Integer locationId);

}