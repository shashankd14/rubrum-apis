package com.steel.product.jswone.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PoGrnLineItem {

	private String item_id;

	private String purchase_order_line_item_id;

	private String sku;

	private String rate;

	private BigDecimal quantity;

	private String hsn_or_sac;

	private String tax_id;

	List<PoGrnLineItemBatches> batches = new ArrayList<>();

}
