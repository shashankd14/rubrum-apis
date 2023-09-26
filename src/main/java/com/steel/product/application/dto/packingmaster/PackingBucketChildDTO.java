package com.steel.product.application.dto.packingmaster;

import lombok.Data;

@Data
public class PackingBucketChildDTO {

	private Integer bucketChildId;
	
	private Integer itemId;

	private String packingItemId;

	private String description;

	private String unit;

}
