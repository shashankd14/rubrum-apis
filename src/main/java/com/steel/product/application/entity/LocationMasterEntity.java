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

	@Column(name = "address", length = 500)
	private String address;

	@Column(name = "city", length = 500)
	private String city;

	@Column(name = "state", length = 500)
	private String state;

	@Column(name = "desc", length = 500)
	private String desc;

	@Column(name = "pincode")
	private Integer pincode;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@Column(name = "created_on", insertable = false, updatable = false)
	private java.time.LocalDateTime createdOn;

	@Column(name = "updated_on", insertable = false, updatable = false)
	private java.time.LocalDateTime updatedOn;

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
