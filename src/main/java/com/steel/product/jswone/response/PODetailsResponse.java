package com.steel.product.jswone.response;

import java.util.List;

import lombok.Data;

@Data
public class PODetailsResponse {

	private String purchaseorder_id;

	private String vendor_id;

	private String vendor_name;

	private String company_name;

	private String reference_number;

	private String order_status;

	private String billed_status;

	private String received_status;

	private String purchaseorder_number;

	private String status;

	private List<PODetailsLineItemResponse> line_items;
}
