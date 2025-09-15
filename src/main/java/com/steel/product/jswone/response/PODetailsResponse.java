package com.steel.product.jswone.response;

import lombok.Data;

@Data
public class PODetailsResponse {

	private String purchaseorder_id;

	private String vendor_id;

	private String vendor_name;

	private String mmid;

	private String company_name;

	private String order_status;

	private String billed_status;

	private String received_status;

	private String status;
}
