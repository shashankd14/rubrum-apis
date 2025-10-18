package com.steel.product.jswone.response;

import lombok.Data;

@Data
public class PODetailsLineItemResponse {

	private String purchaseorder_id;

	private String item_id;

	private String line_item_id;

	private String sku;

	private String date;

	private String hsn_or_sac;

	private String warehouse_id;

	private String warehouse_name;

	private String name;

	private String reference_number;

	private String rate;

	private String tax_id;

	private String quantity;

}
