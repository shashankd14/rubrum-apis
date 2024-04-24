package com.steel.product.trading.dto;

import lombok.Data;

@Data
public class EQPChildResponse {

	private Integer enquiryChildId;

	private Integer itemId;
	
	private String itemName;

	private String itemSpecs;

	private String make;

	private String altMake;
	
	private Integer locationId;
	
	private String locationName;

	private Integer qty1;

	private String unit1;

	private Integer qty2;

	private String unit2;

	private String estimateDeliveryDate;

	private String remarks;
	
	private String status;

}