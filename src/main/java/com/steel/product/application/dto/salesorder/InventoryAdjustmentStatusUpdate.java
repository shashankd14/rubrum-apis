package com.steel.product.application.dto.salesorder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryAdjustmentStatusUpdate {

	/** "FG" for the FG (cut-to-length) callback; "DC" or absent for the existing DC callback. */
	private String transactionType;

	/** FG callback only: identifies the cut; every packet with this fg_reference_id is updated. */
	private String fgReferenceId;

	private int dcNumber;

	private String salesInvoiceNo;

	private String invAdjustmentRemarks;

	private String zohoSyncStatus;

}
