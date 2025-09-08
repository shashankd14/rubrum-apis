package com.steel.product.jswone.request;

import lombok.Data;

@Data
public class POIntegrationRequest {

	private String poReference;

	private String warehouseId;

	private String status;
	
	private String ipAddress;
}
