package com.steel.product.jswone.response;

import lombok.Data;

@Data
public class PODetailsLineItemChildResponse {

	private String item_id;

	private String purchase_order_line_item;

	private String sku;

	private String rate;

	private String quantity;

	private String hsn_or_sac;

	private String tax_id;

}
