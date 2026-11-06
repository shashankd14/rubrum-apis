package com.steel.product.jswone.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

/**
 * Inventory-adjustment payload for the Finished Goods (FG / cut-to-length) flow.
 * Kept separate from {@link DC_InventoryAdjustmentMainRequest} so the DC payload stays unchanged.
 * DC-only fields (salesOrderNumber, dcNumber, fileName, pdf, ewaybillVehicleNumber,
 * motorVehicleNumber, shipmentReferenceNo, ptQuantities) are intentionally absent,
 * motorVehicleNumber, shipmentReferenceNo, ptQuantities, batch_id) are intentionally absent,
 * and nulls are never serialized.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "transactionType", "fgReferenceId", "date", "reason", "adjustmentType", "branchID",
		"warehouseId", "line_items" })
public class FG_InventoryAdjustmentMainRequest {

	public static final String TRANSACTION_TYPE_FG = "FG";

	private String transactionType = TRANSACTION_TYPE_FG;
	private String fgReferenceId;
	private String date;
	private String reason;
	private String adjustmentType;
	private String account;
	private String branchID;
	private String warehouseId;

	private List<LineItem> line_items = new ArrayList<>();

	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "fromSkus", "toSku" })
	public static class LineItem {
		private List<FromSku> fromSkus = new ArrayList<>();
		private ToSku toSku;
	}

	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "skuId", "quantity_adjusted", "uom", "batches" })
	public static class FromSku {
		private String skuId;
		private BigDecimal quantity_adjusted; // negative value
		private String uom;
		private List<Batch> batches = new ArrayList<>();
	}

	@Data
	@JsonPropertyOrder({ "batch_number", "in_quantity" })
	public static class Batch {
		private String batch_number;
		private BigDecimal in_quantity;
	}
}
