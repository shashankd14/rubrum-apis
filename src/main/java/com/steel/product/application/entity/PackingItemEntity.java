package com.steel.product.application.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.steel.product.application.dto.packingmaster.PackingItemResponse;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

import java.util.Date;

@Entity
@Table(name = "packing_item")
@Data
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PackingItemEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "item_id")
	private Integer itemId;
	
	@Column(name = "packing_id")
	private String packingItemId;

	@Column(name = "description")
	private String description;
	
	@Column(name = "unit")
	private String unit;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@Column(name = "created_on", updatable = false)
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "updated_on")
	@UpdateTimestamp
	private Date updatedOn;

	public static PackingItemResponse valueOf(PackingItemEntity entity){
		PackingItemResponse dtoResponse = new PackingItemResponse();
		dtoResponse.setItemId(entity.getItemId());
		dtoResponse.setPackingItemId(entity.getPackingItemId());
		dtoResponse.setDescription( entity.getDescription());
		dtoResponse.setUnit( entity.getUnit());
		dtoResponse.setCreatedBy(entity.getCreatedBy());
		dtoResponse.setUpdatedBy(entity.getUpdatedBy());
		dtoResponse.setCreatedOn(entity.getCreatedOn());
		dtoResponse.setUpdatedOn(entity.getUpdatedOn());
        return dtoResponse;
    }

}