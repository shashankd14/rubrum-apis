package com.steel.product.trading.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EQPChildRequest extends BaseRequest {

	private Integer enquiryChildId;

	private Integer itemId;

	private String itemSpecs;

	private String make;

	private String altMake;
	
	private Integer locationId;

	private Integer qty1;

	private String unit1;

	private Integer qty2;

	private String unit2;

	private String estimateDeliveryDate;

	private String remarks;

}
