package com.steel.product.jswone.request;

import lombok.Data;

@Data
public class MMIDReceiveUOM {

	private String name;
	private String uomId;
	private String uomType;
	private String uomClass;
	private String categoryId;
	private String categoryKey;
	private String uiLabelPrice;
	private String categoryUomId;
	private String uiLabelQuantity;
	private String quantityPrecision;
}
