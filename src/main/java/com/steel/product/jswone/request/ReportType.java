package com.steel.product.jswone.request;

import java.util.Arrays;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * One entry per downloadable report. View / column / ORDER BY values are
 * compile-time constants only (never taken from the request), so the dynamic
 * SQL is injection-safe.
 *
 * dateColumn : driving date for fromDate/toDate; null = no date filter
 * (as-on-today report) locationColumn : column matched against
 * request.locationid; null = location filter not wired yet
 */
public enum ReportType {

	PURCHASE("Purchase_Batch_Level",
			"Purchase (Batch Level)",
			"purchase_batch_level_vw", 
			"grn_creation_timestamp",
			"coil_inward_date DESC, coil_number",
			"party_id",
			"coil_number, batch_number, coil_status, material_description, grade, subgrade, brand, thickness, width, warehouse, city, ageing_days, coil_inward_date, coil_bill_date, inward_weight_mt, mmid, uts, ys, el, po_number, tc_number, bill_number, vehicle_number, seller_name, remarks, grn_creation_timestamp, zoho_sync_status, zoho_sync_timestamp, bill_timestamp, grn_created_by"),

	SALES("Sales_Batch_Level", 
			"Sales (Batch Level)", 
			"sales_batch_level_vw",
			"dc_creation_timestamp",
			"dc_creation_timestamp DESC, coil_number",
			"party_id",
			"coil_number, batch_number, material_description, grade, subgrade, brand, thickness, width, length, warehouse, city, dc_number, sales_invoice_number, sales_invoice_date, pieces, delivery_weight_mt, additional_weight_mt, classification, mmid, vehicle_number, coil_purchase_date, remarks, so_id, so_date, dc_creation_timestamp, zoho_sync_status, zoho_sync_timestamp, invoice_timestamp, coil_to_coil_or_processed, dc_created_by"),

	STOCK("Stock_Batch_Level", 
			"Stock (Batch Level)", 
			"stock_batch_level_vw",
			null,
			"ageing_days DESC, coil_number",
			"party_id",
			"coil_number, batch_number, coil_status, material_description, grade, subgrade, brand, thickness, width, warehouse, city, ageing_days, coil_inward_date, coil_bill_date, inward_weight_mt, mmid, uts, ys, el, po_number, tc_number, bill_number, vehicle_number, seller_name, remarks, grn_creation_timestamp, zoho_sync_status, zoho_sync_timestamp, bill_timestamp, grn_created_by, party_id"),

	OPEN_ORDERS("Open_Orders_SKU_Level", 
			"Open Orders (SKU Level)", 
			"open_orders_sku_level", 
			"socreatedate",
			"so_timestamp DESC, so_id, sku_description", 
			null,
			"region, so_id, sales_channel, delivery_type, so_timestamp, delivery_timeline, estimated_delivery_date, sku_description, order_qty_sheets, order_qty_mt, invoiced_qty_mt, bti_qty_mt, socreatedate, zoho_status, processing_centre, allocated_coil, allocated_qty_mt, packing_mode, sdi, likely_mrd, standard_mrd, wms_status_sku, wms_status_so, delay_bucket_sku, delay_reason_sku, delay_comments_sku, delay_status_so, delay_bucket_so, delay_reason_so, delay_comments_so, order_type, allocation_timestamp, job_work_release_timestamp, planned_processing_timestamp, finish_cutting_timestamp, nd_breach_reason_sku, processing_delay_reason_sku, allocated_by"),

	JOB_ORDERS("Job_Order_Processing",
			"Job Order Processing",
			"job_orders_sku_level_vw", 
			"finish_cutting_timestamp",
			"job_work_release_timestamp, coil_number",
			"party_id",
			"batch_number, coil_number, coil_status, material_description, grade, subgrade, brand, thickness, width, length, warehouse, city, packet_status, coil_ageing_days, so_id, unprocessed_weight_mt, planned_weight_mt, classification_tag, job_work_release_timestamp, planned_processing_timestamp, finish_cutting_timestamp, planned_dispatch_timestamp, job_work_released_by, finish_cutting_by, ageing_of_job_work_release_days"),

	FIFO_BREACH("FIFO_Breach_SKU_Level", 
			"FIFO Breach (SKU Level)", 
			"so_fifo_breach_vw", 
			"so_timestamp",
			"allocation_timestamp DESC, so_id",
			null,
			"region, warehouse, so_id, so_timestamp, material_description, grade, subgrade, brand, thickness, width, length, mmid, sku_ordered_qty_mt, allocated_coil, allocated_qty_mt, allocated_coil_ageing_days, older_coils_available_nos, older_coils_available_qty_kg, fg_same_length_available, allocation_timestamp, allocated_by"),

	AUDIT_TRAIL("Audit_Trail_Packet_Level", 
			"Audit Trail (Packet Level)", 
			"audit_trail_packet_level_vw",
			"packet_createdon",
			"packet_createdon DESC, coil_number, packet_number",
			"party_id",
			"coil_number, batch_number, material_description, grade, subgrade, brand, thickness, width, length, warehouse, city, grn_creation_timestamp, bill_timestamp, allocation_timestamp, job_work_release_timestamp, planned_processing_timestamp, finish_cutting_timestamp, planned_dispatch_timestamp, dc_creation_timestamp, invoice_timestamp, packet_number, so_id, grn_created_by, packet_createdon, allocated_by, job_work_released_by, finish_cutting_by, dc_created_by");

	private final String fileBase;
	private final String sheetName; // max 31 chars (Excel)
	private final String viewName;
	private final String dateColumn;
	private final String orderBy;
	private final String locationColumn;
	private final String fieldNames;

	ReportType(String fileBase, String sheetName, String viewName, String dateColumn, String orderBy,
			String locationColumn, String fieldNames) {
		this.fileBase = fileBase;
		this.sheetName = sheetName;
		this.viewName = viewName;
		this.dateColumn = dateColumn;
		this.orderBy = orderBy;
		this.locationColumn = locationColumn;
		this.fieldNames = fieldNames;
	}

	public String getFileBase() {
		return fileBase;
	}

	public String getSheetName() {
		return sheetName;
	}

	public String getViewName() {
		return viewName;
	}

	public String getDateColumn() {
		return dateColumn;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public String getLocationColumn() {
		return locationColumn;
	}

	/**
	 * Accepts the enum name or the file base, case-insensitive, '-' or ' ' as '_':
	 * "PURCHASE", "purchase", "open-orders", "Sales_Batch_Level", "audit trail" ...
	 * Blank defaults to AUDIT_TRAIL so existing callers of
	 * /reports/packetlevel_audit keep working.
	 */
	public static ReportType from(String value) {
		if (value == null || value.trim().isEmpty()) {
			return AUDIT_TRAIL;
		}
		String key = value.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
		for (ReportType t : values()) {
			if (t.name().equals(key) || t.fileBase.toUpperCase(Locale.ROOT).equals(key)) {
				return t;
			}
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
				"Unknown reportType '" + value + "'. Allowed: " + Arrays.toString(values()));
	}

	public String getFieldNames() {
		return fieldNames;
	}
	
	
	
	
}
