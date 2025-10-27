package com.steel.product.jswone.request;

import lombok.Data;

@Data
public class POSOIntegrationRequest {

	private String poReference;

	private String soNo;

	private String soId;

	private String poInvoiceNo;

	private String warehouseId;

	private String customerBatchNo;

	private String poId;

	private int locationId;

	private String status;

	private String zohoSyncStatus;

	private String ipAddress;
}
