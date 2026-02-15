package com.steel.product.jswone.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class FromSku {
	private String skuId;
	private BigDecimal quantity_adjusted; // negative value
	private String uom;
	private List<InventoryAdjustmentBatch> batches = new ArrayList<>();
}
