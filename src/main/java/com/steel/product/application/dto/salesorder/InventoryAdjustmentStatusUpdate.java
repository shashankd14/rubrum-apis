package com.steel.product.application.dto.salesorder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryAdjustmentStatusUpdate {

	private int dcNumber;

	private String salesInvoiceNo;

	private String invAdjustmentRemarks;

	private String zohoSyncStatus;

}
