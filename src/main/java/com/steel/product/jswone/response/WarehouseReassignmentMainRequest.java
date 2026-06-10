package com.steel.product.jswone.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class WarehouseReassignmentMainRequest {

	private String customer_id;
	
	private String salesorder_id;

	private List<WarehouseReassignmentLineItemsDto> line_items = new ArrayList<>();
}
