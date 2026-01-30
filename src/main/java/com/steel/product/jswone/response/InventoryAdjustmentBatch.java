package com.steel.product.jswone.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class InventoryAdjustmentBatch {
	private String batch_number;
	private String batch_id;
	private BigDecimal in_quantity;
}
