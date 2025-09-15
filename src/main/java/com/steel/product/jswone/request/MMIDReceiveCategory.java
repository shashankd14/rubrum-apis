package com.steel.product.jswone.request;

import java.util.List;

import lombok.Data;

@Data
public class MMIDReceiveCategory {

	private List<MMIDReceiveUOM> uom ;

	private String form;
	private String category_key;
	private String product_type;
	private String sub_category;
	private String leaf_category;
	private String master_category;

}