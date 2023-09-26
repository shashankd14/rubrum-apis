package com.steel.product.application.dto.packingmaster;

import java.util.Date;

import lombok.Data;

@Data
public class PackingItemResponse {

	private Integer itemId;

	private String packingItemId;

	private String description;

	private String unit;

	private Integer createdBy;

	private Integer updatedBy;

	private Date createdOn;

	private Date updatedOn;

}
