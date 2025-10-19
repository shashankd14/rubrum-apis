package com.steel.product.jswone.request;

import lombok.Data;

@Data
public class POIntegrationRequest {

	private String poReference;

	private String poInvoiceNo;

	private String warehouseId;

	private String poId;

	private int locationId;

	private String status;

	private String ipAddress;
}
