package com.steel.product.jswone.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ToSku {

	private String skuId;
	private BigDecimal quantity_adjusted;
	private String uom;

}
