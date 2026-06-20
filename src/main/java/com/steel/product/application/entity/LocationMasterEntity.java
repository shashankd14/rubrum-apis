package com.steel.product.application.entity;

import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.steel.product.application.dto.party.LocationMasterDto;

import lombok.Data;

@Entity
@Data
@Table(name = "location_master")
public class LocationMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "location_id")
	private Integer locationId;

	@Column(name = "location_name", nullable = false, length = 255)
	private String locationName;

	@Column(name = "location_desc", length = 500)
	private String locationDesc;

	@Column(name = "is_deleted", columnDefinition = "BIT DEFAULT 0")
	private Boolean isDeleted = Boolean.FALSE;

	@JsonManagedReference(value = "party-inward")
	@OneToMany(mappedBy = "party")
	private List<InwardEntry> inwardEntry;
	
	public static LocationMasterDto valueOf(LocationMasterEntity entity) {
	    LocationMasterDto dto = new LocationMasterDto();
	    dto.setLocationId(entity.getLocationId());
	    dto.setLocationName(entity.getLocationName());
	    return dto;
	}

}
