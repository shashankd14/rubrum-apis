package com.steel.product.jswone.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class WarehouseReassignmentLineItemsDto {

	private String item_id;
	private BigDecimal quantity;
	private String warehouse_id;
	private String warehouse_name;
}
