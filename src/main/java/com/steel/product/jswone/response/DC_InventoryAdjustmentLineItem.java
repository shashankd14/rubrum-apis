package com.steel.product.jswone.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class DC_InventoryAdjustmentLineItem {

	private ToSku toSku;

	private List<FromSku> fromSkus = new ArrayList<>();

	private List<PtQuantity> ptQuantities = new ArrayList<>();
}
