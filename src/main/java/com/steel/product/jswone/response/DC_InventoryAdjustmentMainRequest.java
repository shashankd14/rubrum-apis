package com.steel.product.jswone.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class DC_InventoryAdjustmentMainRequest {

	private String salesOrderNumber;
	private String date;
	private String reason;
	private String adjustmentType;
	private String account;
	private String branchID;
	private String warehouseId;
	private String shipmentReferenceNo;
	private String ewaybillVehicleNumber;
	private String motorVehicleNumber;
	//private String fileName;
	//private String pdf;
	//private String dcNumber;

	private List<DC_InventoryAdjustmentLineItem> line_items = new ArrayList<>();
}
