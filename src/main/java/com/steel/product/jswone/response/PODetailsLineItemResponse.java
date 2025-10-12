package com.steel.product.jswone.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PODetailsLineItemResponse {

	private String purchaseorder_id;

	private String item_id;

	private String line_item_id;

	private String sku;

	private String location_name;

	private String location_id;

	private String date;

	private String reference_number;

	private String hsn_or_sac;

	private String warehouse_id;

	private String warehouse_name;

	private String name;

	private String tax_id;

	private String rate;

	private String quantity;

	List<PODetailsLineItemChildResponse> line_items = new ArrayList<>();

}
