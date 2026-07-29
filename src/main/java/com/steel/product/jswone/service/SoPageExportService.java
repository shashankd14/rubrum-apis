package com.steel.product.jswone.service;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.steel.product.application.dto.quality.ListPageSearchRequest;
import com.steel.product.jswone.request.SoPageExportDto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SO Page Export.
 *
 * One sheet row per jsw_sales_order_child line, joined up to jsw_sales_order and
 * aggregated against jsw_sales_order_allocation. Layout is taken verbatim from
 * SO_Page_Export_Template.xlsx (25 columns, A..Y).
 *
 * Uses XSSFWorkbook and deliberately NOT SXSSFWorkbook: the streaming
 * implementation spills to native temp files and has crashed this application on
 * the Windows server with a malloc failure. Volume is bounded by MAX_EXPORT_ROWS
 * instead.
 */
@Service
public class SoPageExportService {

	private static final Logger log = LoggerFactory.getLogger(SoPageExportService.class);

	/**
	 * Hard ceiling. The workbook, the byte[] and the base64 String are all on
	 * heap simultaneously and base64 inflates the payload by ~33%, so an
	 * unbounded export is the shortest route back to an OutOfMemoryError.
	 */
	private static final int MAX_EXPORT_ROWS = 50_000;

	/**
	 * Column L. There is no invoice table among the SO entities, so this
	 * defaults to 0 rather than silently reporting allocated quantity as
	 * invoiced. Replace with the real correlated subquery, e.g.
	 *
	 *   (SELECT SUM(inv.invoiced_qty) FROM jsw_invoice_child inv
	 *     WHERE inv.so_child_id = soc.so_child_id AND COALESCE(inv.is_deleted,0) = 0)
	 *
	 * Keep it wrapped in the CAST/COALESCE below so the driver always hands back
	 * a BigDecimal and never a Double.
	 */
	private static final String INVOICED_QTY_EXPR = "0";

	private static final String SHEET_NAME = "Sheet1";
	private static final int HEADER_ROW = 0;
	private static final int FIRST_DATA_ROW = 1;
	private static final short HEADER_ROW_HEIGHT = 900; // twips, ~45pt: fits wrapped headers
	private static final int QTY_SCALE = 2;
	private static final int MAX_EXCEL_TEXT = 32767;

	public static final String XLSX_CONTENT_TYPE =
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public SoPageExportService(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// ------------------------------------------------------------------
	// Public API
	// ------------------------------------------------------------------

	/** Workbook wrapped in a JSON-friendly base64 envelope. */
	public Map<String, Object> exportAsBase64(ListPageSearchRequest request) {
		List<SoPageExportDto> rows = fetchRows(request);
		byte[] bytes = buildWorkbook(rows);
		String fileName = buildFileName();

		Map<String, Object> response = new LinkedHashMap<>();
		response.put("fileName", fileName);
		response.put("contentType", XLSX_CONTENT_TYPE);
		response.put("recordCount", rows.size());
		response.put("fileSizeBytes", bytes.length);
		response.put("base64Content", Base64.getEncoder().encodeToString(bytes));
		return response;
	}

	/** Raw .xlsx bytes, for a plain browser download. */
	public byte[] exportAsBytes(ListPageSearchRequest request) {
		return buildWorkbook(fetchRows(request));
	}

	public static String buildFileName() {
		return "SO_Page_Export_"
				+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
				+ ".xlsx";
	}

	// ------------------------------------------------------------------
	// Data access
	// ------------------------------------------------------------------

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SoPageExportDto> fetchRows(ListPageSearchRequest searchRequest) {

	    if (searchRequest == null) {
	        searchRequest = new ListPageSearchRequest();
	    }

	    // Copy, do not mutate the caller's list. The original called
	    // searchRequest.getStatus().add(...) which (a) NPEs when getStatus() is null
	    // and (b) double-adds if the request object is reused or the call retried.
	    List<String> soStatuses = new ArrayList<>();
	    if (searchRequest.getStatus() != null) {
	        soStatuses.addAll(searchRequest.getStatus());
	    }
	    String filterStatus = searchRequest.getFilterStatus();
	    if (filterStatus != null && filterStatus.length() > 0 && !"ALL".equals(filterStatus)) {
	        soStatuses.add(filterStatus);
	    }

	    MapSqlParameterSource params = new MapSqlParameterSource();
	    StringBuilder sql = new StringBuilder(baseSelect());

	    // ---- date range ----
	    if (searchRequest.getFromDate() != null && searchRequest.getToDate() != null) {
	        sql.append(" AND so.socreatedate between  :fromDate and :toDate");
	        params.addValue("fromDate", searchRequest.getFromDate());
	        params.addValue("toDate", searchRequest.getToDate());
	    }

	    // ---- zoho status: expand UI labels to stored values ----
	    List<String> zohoStatusList = expandZohoStatus(searchRequest.getZohoStatus());
	    if (!zohoStatusList.isEmpty()) {
	        sql.append(" AND so.zoho_status IN (:zohoStatusList) ");
	        params.addValue("zohoStatusList", zohoStatusList);
	    }

	    // ---- warehouse ----
	    // FIXED ALIAS: baseSelect() aliases the child table as `soc`, not `so_child`.
	    // `so_child.wearhouse_id` would fail with "Unknown column".
	    if (searchRequest.getWarehouseList() != null && !searchRequest.getWarehouseList().isEmpty()) {
	        sql.append(" AND soc.wearhouse_id IN (:warehouseList) ");
	        params.addValue("warehouseList", searchRequest.getWarehouseList());
	    }

	    // ---- soId ----
	    if (searchRequest.getSoId() != null && searchRequest.getSoId() > 0) {
	        sql.append(" AND so.so_id = :soId ");
	        params.addValue("soId", searchRequest.getSoId());
	    }

	    // ---- free-text search ----
	    //
	    // THIS WAS THE CRASH. `like %:searchText%` is Spring Data JPA syntax. Under
	    // NamedParameterJdbcTemplate the % signs are NOT special: they get emitted
	    // literally and :searchText becomes ?, producing `like %?%` -- invalid MySQL.
	    //
	    // Correct form: bare `LIKE :searchText`, with the wildcards inside the VALUE.
	    if (searchRequest.getSearchText() != null && searchRequest.getSearchText().trim().length() > 0) {
	        sql.append(" AND ( soc.material_name LIKE :searchText ")
	           .append("    OR soc.mm_id        LIKE :searchText ")
	           .append("    OR so.so_number     LIKE :searchText ")
	           .append("    OR so.refno         LIKE :searchText ) ");
	        params.addValue("searchText", "%" + escapeLike(searchRequest.getSearchText().trim()) + "%");
	    }

	    // ---- so_status ----
	    if (!soStatuses.isEmpty()) {
	        sql.append(" AND so.so_status IN (:soStatuses) ");
	        // FIXED: was addValue("satuses", ...) while the SQL bound :soStatuses.
	        // Any request with a status filter would have thrown on a missing param.
	        params.addValue("soStatuses", soStatuses);
	    }

	    sql.append(" ORDER BY so.socreatedate DESC, so.so_id DESC, soc.so_child_id ");

	    int limit = MAX_EXPORT_ROWS;
	    sql.append(" LIMIT :rowLimit ");
	    params.addValue("rowLimit", limit);

	    long startedAt = System.currentTimeMillis();
	    log.info("SO page export sql == {}", sql.toString());

	    List<SoPageExportDto> rows =
	            jdbcTemplate.query(sql.toString(), params, SoPageExportService::mapRow);

	    log.info("SO page export fetched rows={} in {}ms",
	            rows.size(), System.currentTimeMillis() - startedAt);

	    if (rows.size() >= limit) {
	        log.warn("SO page export hit the row cap ({}); output is truncated. "
	                + "Tighten the filters or raise MAX_EXPORT_ROWS deliberately.", limit);
	    }
	    return rows;
	}

	/**
	 * UI-facing Zoho status labels -> raw values stored on jsw_sales_order.zoho_status.
	 * Extracted so the list screen and the export cannot drift on what the labels mean.
	 */
	private List<String> expandZohoStatus(List<String> requested) {

	    List<String> expanded = new ArrayList<>();
	    if (requested == null || requested.isEmpty()) {
	        return expanded;
	    }
	    if (requested.contains("Open") || requested.contains("open")) {
	        expanded.add("confirmed");
	        expanded.add("open");
	    }
	    if (requested.contains("partially_invoiced")) {
	        expanded.add("partially_invoiced");
	    }
	    if (requested.contains("Closed") || requested.contains("closed")) {
	        expanded.add("fulfilled");
	        expanded.add("invoiced");
	        expanded.add("closed");
	    }
	    if (requested.contains("Void") || requested.contains("void")) {
	        expanded.add("void");
	    }
	    return expanded;
	}

	/**
	 * Neutralises LIKE wildcards in user input, so searching "50%" matches the literal
	 * characters instead of "anything starting with 50". Relies on backslash as the
	 * escape character, which is MySQL's default.
	 */
	private static String escapeLike(String input) {
	    return input.replace("\\", "\\\\")
	                .replace("%", "\\%")
	                .replace("_", "\\_");
	}

	/**
	 * One row per SO child line. Allocation is pulled via correlated subqueries
	 * rather than a JOIN + GROUP BY, so a child with several allocation rows does
	 * not fan out and multiply soqty.
	 *
	 * Column aliases are read by name in mapRow, which removes the whole class of
	 * positional-index and ClassCastException bugs.
	 */
	private String baseSelect() {
		return "SELECT "
			+ "   so.branch_id                              AS region, "
			+ "   COALESCE(so.salesorder_id, so.so_number)  AS order_id, "
			+ "   so.bizsegment                             AS sales_channel, "
			+ "   so.deliverymethod                         AS delivery_type, "
			+ "   so.socreatedate                           AS order_date, "
			+ "   DATEDIFF(so.expected_delivery_date, so.socreatedate) AS delivery_timeline, "
			+ "   so.expected_delivery_date                 AS estimated_delivery_date, "
			+ "   soc.material_name                         AS sku_description, "
			// number_of_sheets is a varchar column; NULLIF stops '' becoming 0
			+ "   CAST(NULLIF(TRIM(soc.number_of_sheets), '') AS DECIMAL(18,2)) AS order_qty_sheets, "
			+ "   CAST(COALESCE(soc.soqty, 0) AS DECIMAL(18,3))                 AS order_qty_mt, "
			+ "   CAST(COALESCE(" + INVOICED_QTY_EXPR + ", 0) AS DECIMAL(18,3)) AS invoiced_qty_mt, "
			+ "   so.zoho_status                            AS zoho_status, "
			+ "   so.destinationcode                        AS processing_centre, "
			+ "   (SELECT GROUP_CONCAT(DISTINCT soa.inward_entry_id "
			+ "             ORDER BY soa.inward_entry_id SEPARATOR ', ') "
			+ "      FROM jsw_sales_order_allocation soa "
			+ "     WHERE soa.so_child_id = soc.so_child_id "
			+ "       AND soa.inward_entry_id IS NOT NULL)  AS allocated_coil, "
			+ "   so.typeofsupply                           AS packing_mode, "
			+ "   so.special_delivery_instructions          AS sdi, "
			+ "   so.likely_material_date                   AS likely_mrd, "
			+ "   so.standard_material_date                 AS standard_mrd, "
			+ "   soc.allocated_stts                        AS wms_status, "
			+ "   DATEDIFF(so.likely_material_date, so.standard_material_date) AS delay_days, "
			+ "   soc.special_instructions                  AS delay_reason, "
			+ "   so.remarks                                AS delay_comments "
			+ " FROM jsw_sales_order_child soc "
			+ " JOIN jsw_sales_order so ON so.so_id = soc.so_id "
			+ " WHERE COALESCE(so.is_deleted, 0) = 0 "
			+ "   AND COALESCE(soc.is_deleted, 0) = 0 ";
	}

	private static SoPageExportDto mapRow(ResultSet rs, int rowNum) throws SQLException {

		SoPageExportDto dto = new SoPageExportDto();
		dto.setRegion(rs.getString("region"));
		dto.setOrderId(rs.getString("order_id"));
		dto.setSalesChannel(rs.getString("sales_channel"));
		dto.setDeliveryType(rs.getString("delivery_type"));
		dto.setOrderDate(rs.getTimestamp("order_date"));
		dto.setDeliveryTimeline(getIntegerOrNull(rs, "delivery_timeline"));
		dto.setEstimatedDeliveryDate(rs.getTimestamp("estimated_delivery_date"));
		dto.setSkuDescription(rs.getString("sku_description"));
		dto.setOrderQtySheets(toBigDecimal(rs.getObject("order_qty_sheets")));
		dto.setOrderQtyMt(toBigDecimal(rs.getObject("order_qty_mt")));
		dto.setInvoicedQtyMt(toBigDecimal(rs.getObject("invoiced_qty_mt")));
		dto.setZohoStatusOfSo(rs.getString("zoho_status"));
		dto.setProcessingCentre(rs.getString("processing_centre"));
		dto.setAllocatedCoil(rs.getString("allocated_coil"));
		dto.setPackingMode(rs.getString("packing_mode"));
		dto.setSdi(rs.getString("sdi"));
		dto.setLikelyMaterialReadinessDate(rs.getTimestamp("likely_mrd"));
		dto.setStandardMaterialReadinessDate(rs.getTimestamp("standard_mrd"));
		dto.setWmsStatusOfSku(rs.getString("wms_status"));
		dto.setDelayReason(rs.getString("delay_reason"));
		dto.setCommentsForDelays(rs.getString("delay_comments"));
		dto.setDelayDays(getIntegerOrNull(rs, "delay_days"));

		// Balance = ordered - invoiced. nz() on the invoiced side only: nothing
		// invoiced means the whole order is outstanding. A null soqty is left
		// null rather than producing a misleading negative balance.
		if (dto.getOrderQtyMt() != null) {
			dto.setBalanceQtyMt(dto.getOrderQtyMt()
					.subtract(nz(dto.getInvoicedQtyMt()))
					.setScale(QTY_SCALE, RoundingMode.HALF_UP));
		}

		dto.setDelayStatus(resolveDelayStatus(dto.getDelayDays()));
		dto.setDelayBucket(resolveDelayBucket(dto.getDelayDays()));
		return dto;
	}

	/** Likely readiness later than standard readiness = delayed. */
	private static String resolveDelayStatus(Integer delayDays) {
		if (delayDays == null) {
			return "TBD";
		}
		return delayDays > 0 ? "Delayed" : "On Time";
	}

	private static String resolveDelayBucket(Integer delayDays) {
		if (delayDays == null) {
			return "TBD";
		}
		if (delayDays <= 0) {
			return "On Time";
		}
		if (delayDays <= 3) {
			return "1-3 Days";
		}
		if (delayDays <= 7) {
			return "4-7 Days";
		}
		if (delayDays <= 15) {
			return "8-15 Days";
		}
		return "> 15 Days";
	}

	// ------------------------------------------------------------------
	// Workbook build
	// ------------------------------------------------------------------

	private byte[] buildWorkbook(List<SoPageExportDto> rows) {
		long startedAt = System.currentTimeMillis();

		// try-with-resources on both: the workbook holds POI's OPC package part
		// handles and leaking them retains the whole package on heap.
		try (XSSFWorkbook workbook = new XSSFWorkbook();
			 ByteArrayOutputStream out =
					 new ByteArrayOutputStream(Math.max(16 * 1024, rows.size() * 700))) {

			XSSFSheet sheet = workbook.createSheet(SHEET_NAME);
			Styles styles = new Styles(workbook);

			Row header = sheet.createRow(HEADER_ROW);
			header.setHeight(HEADER_ROW_HEIGHT);
			for (Col col : Col.values()) {
				setString(header, col.ordinal(), col.header, styles.header);
				// Explicit widths rather than autoSizeColumn(): auto-sizing does a
				// font-metrics round trip per cell and is the slowest thing you can
				// do to a wide sheet.
				sheet.setColumnWidth(col.ordinal(), (int) Math.round(col.width * 256));
			}

			int rowIndex = FIRST_DATA_ROW;
			for (SoPageExportDto dto : rows) {
				writeDataRow(sheet, styles, dto, rowIndex, rowIndex - FIRST_DATA_ROW + 1);
				rowIndex++;
			}

			sheet.createFreezePane(0, FIRST_DATA_ROW);
			if (rowIndex > FIRST_DATA_ROW) {
				sheet.setAutoFilter(new CellRangeAddress(
						HEADER_ROW, rowIndex - 1, 0, Col.values().length - 1));
			}

			workbook.write(out);
			byte[] bytes = out.toByteArray();
			log.info("SO page export built: rows={}, bytes={}, elapsedMs={}",
					rows.size(), bytes.length, System.currentTimeMillis() - startedAt);
			return bytes;

		} catch (IOException e) {
			log.error("Failed to build SO page export workbook", e);
			throw new IllegalStateException("Unable to generate SO page export", e);
		}
	}

	private void writeDataRow(XSSFSheet sheet, Styles styles, SoPageExportDto dto,
							  int rowIndex, int serialNumber) {

		Row row = sheet.createRow(rowIndex);

		setNumber(row, Col.SR_NO.ordinal(), serialNumber, styles.centered);
		setString(row, Col.REGION.ordinal(), dto.getRegion(), styles.text);
		setString(row, Col.ORDER_ID.ordinal(), dto.getOrderId(), styles.text);
		setString(row, Col.SALES_CHANNEL.ordinal(), dto.getSalesChannel(), styles.text);
		setString(row, Col.DELIVERY_TYPE.ordinal(), dto.getDeliveryType(), styles.text);
		setDate(row, Col.ORDER_DATE.ordinal(), dto.getOrderDate(), styles.date);
		setNumber(row, Col.DELIVERY_TIMELINE.ordinal(), dto.getDeliveryTimeline(), styles.centered);
		setDate(row, Col.ETA.ordinal(), dto.getEstimatedDeliveryDate(), styles.date);
		setString(row, Col.SKU_DESCRIPTION.ordinal(), dto.getSkuDescription(), styles.wrapText);
		setDecimal(row, Col.ORDER_QTY_SHEETS.ordinal(), dto.getOrderQtySheets(), styles.integer);
		setDecimal(row, Col.ORDER_QTY_MT.ordinal(), dto.getOrderQtyMt(), styles.qty);
		setDecimal(row, Col.INVOICED_QTY_MT.ordinal(), dto.getInvoicedQtyMt(), styles.qty);
		setDecimal(row, Col.BALANCE_QTY_MT.ordinal(), dto.getBalanceQtyMt(), styles.qty);
		setString(row, Col.ZOHO_STATUS.ordinal(), dto.getZohoStatusOfSo(), styles.text);
		setString(row, Col.PROCESSING_CENTRE.ordinal(), dto.getProcessingCentre(), styles.text);
		setString(row, Col.ALLOCATED_COIL.ordinal(), dto.getAllocatedCoil(), styles.text);
		setString(row, Col.PACKING_MODE.ordinal(), dto.getPackingMode(), styles.text);
		setString(row, Col.SDI.ordinal(), dto.getSdi(), styles.wrapText);
		setDate(row, Col.LIKELY_MRD.ordinal(), dto.getLikelyMaterialReadinessDate(), styles.date);
		setDate(row, Col.STANDARD_MRD.ordinal(), dto.getStandardMaterialReadinessDate(),
				styles.date);
		setString(row, Col.WMS_STATUS.ordinal(), dto.getWmsStatusOfSku(), styles.text);
		setString(row, Col.DELAY_STATUS.ordinal(), dto.getDelayStatus(), styles.text);
		setString(row, Col.DELAY_BUCKET.ordinal(), dto.getDelayBucket(), styles.text);
		setString(row, Col.DELAY_REASON.ordinal(), dto.getDelayReason(), styles.wrapText);
		setString(row, Col.COMMENTS.ordinal(), dto.getCommentsForDelays(), styles.wrapText);
	}

	// ------------------------------------------------------------------
	// Cell writers - a null value leaves the cell BLANK, never "null" or 0
	// ------------------------------------------------------------------

	private static void setString(Row row, int col, String value, CellStyle style) {
		Cell cell = row.createCell(col, CellType.STRING);
		cell.setCellStyle(style);
		if (value == null || value.isEmpty()) {
			cell.setBlank();
		} else if (value.length() > MAX_EXCEL_TEXT) {
			cell.setCellValue(value.substring(0, MAX_EXCEL_TEXT));
		} else {
			cell.setCellValue(value);
		}
	}

	private static void setDate(Row row, int col, Date value, CellStyle style) {
		Cell cell = row.createCell(col);
		cell.setCellStyle(style);
		if (value == null) {
			cell.setBlank();
		} else {
			// Written as a real Excel date serial, so the column sorts and
			// filters as a date rather than as text.
			cell.setCellValue(value);
		}
	}

	private static void setDecimal(Row row, int col, BigDecimal value, CellStyle style) {
		Cell cell = row.createCell(col);
		cell.setCellStyle(style);
		if (value == null) {
			cell.setBlank();
		} else {
			cell.setCellValue(value.setScale(QTY_SCALE, RoundingMode.HALF_UP).doubleValue());
		}
	}

	private static void setNumber(Row row, int col, Number value, CellStyle style) {
		Cell cell = row.createCell(col);
		cell.setCellStyle(style);
		if (value == null) {
			cell.setBlank();
		} else {
			cell.setCellValue(value.doubleValue());
		}
	}

	// ------------------------------------------------------------------
	// Helpers
	// ------------------------------------------------------------------

	/**
	 * Coerces whatever the driver returned into a BigDecimal. CAST/COALESCE
	 * expressions can arrive as Double or Long even over a DECIMAL column, so a
	 * straight cast throws ClassCastException. Going through toString() avoids
	 * the binary artefacts that new BigDecimal(double) introduces.
	 */
	private static BigDecimal toBigDecimal(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof BigDecimal) {
			return (BigDecimal) value;
		}
		if (value instanceof BigInteger) {
			return new BigDecimal((BigInteger) value);
		}
		if (value instanceof Number) {
			return new BigDecimal(value.toString());
		}
		String text = value.toString().trim();
		return text.isEmpty() ? null : new BigDecimal(text);
	}

	private static BigDecimal nz(BigDecimal value) {
		return value != null ? value : BigDecimal.ZERO;
	}

	/** rs.getInt returns 0 for SQL NULL, which is a real value here. */
	private static Integer getIntegerOrNull(ResultSet rs, String column) throws SQLException {
		int value = rs.getInt(column);
		return rs.wasNull() ? null : value;
	}

	private int resolveLimit(Integer requested) {
		if (requested == null || requested <= 0) {
			return MAX_EXPORT_ROWS;
		}
		return Math.min(requested, MAX_EXPORT_ROWS);
	}

	private boolean isNotEmpty(List<?> values) {
		return values != null && !values.isEmpty();
	}

	private boolean isNotBlank(String value) {
		return value != null && !value.trim().isEmpty();
	}

	// ------------------------------------------------------------------
	// Layout: header text, order and width, from the template
	// ------------------------------------------------------------------

	/**
	 * Single source of truth for the sheet layout. The header loop iterates
	 * values(), so adding or reordering a column here shifts header and data
	 * together and they cannot drift apart.
	 */
	private enum Col {
		SR_NO("Sr. No.", 6.27),
		REGION("Region", 8.91),
		ORDER_ID("Order ID", 25.00),
		SALES_CHANNEL("Sales Channel", 25.00),
		DELIVERY_TYPE("Delivery Type", 11.91),
		ORDER_DATE("Order Date", 10.09),
		DELIVERY_TIMELINE("Delivery Timeline", 8.09),
		ETA("Estimated Delivery Date", 10.09),
		SKU_DESCRIPTION("SKU Description", 57.18),
		ORDER_QTY_SHEETS("Order Qty (Sheets)", 8.63),
		ORDER_QTY_MT("Order Qty (MT)", 12.63),
		INVOICED_QTY_MT("Invoiced Qty (MT)", 11.18),
		BALANCE_QTY_MT("Balance Qty (MT)", 10.73),
		ZOHO_STATUS("Zoho Status of SO", 14.54),
		PROCESSING_CENTRE("Processing Centre", 9.91),
		ALLOCATED_COIL("Allocated Coil", 12.45),
		PACKING_MODE("Packing Mode", 12.36),
		SDI("SDI", 45.27),
		LIKELY_MRD("Likely Material Readiness Date", 13.91),
		STANDARD_MRD("Standard Material Readiness Date", 18.00),
		WMS_STATUS("WMS Status of SKU", 15.45),
		DELAY_STATUS("Delay Status", 11.27),
		DELAY_BUCKET("Delay Bucket", 11.73),
		DELAY_REASON("Delay Reason", 12.09),
		COMMENTS("Comments for Delays", 12.63);

		private final String header;
		private final double width;

		Col(String header, double width) {
			this.header = header;
			this.width = width;
		}
	}

	/**
	 * A workbook has a hard ceiling of 64,000 cell styles, and creating one
	 * inside the row loop blows through it while ballooning heap. Every style is
	 * therefore built once and reused.
	 */
	private static final class Styles {

		/** Matches the number format in the supplied template. */
		private static final String DATE_FORMAT = "mm-dd-yy";
		private static final String QTY_FORMAT = "#,##0.00";
		private static final String INT_FORMAT = "#,##0";
		private static final String FONT_NAME = "Calibri";
		private static final short FONT_SIZE = 11;

		private final CellStyle header;
		private final CellStyle text;
		private final CellStyle wrapText;
		private final CellStyle centered;
		private final CellStyle date;
		private final CellStyle qty;
		private final CellStyle integer;

		private Styles(Workbook workbook) {
			DataFormat format = workbook.createDataFormat();

			Font headerFont = workbook.createFont();
			headerFont.setFontName(FONT_NAME);
			headerFont.setFontHeightInPoints(FONT_SIZE);
			headerFont.setBold(true);

			Font bodyFont = workbook.createFont();
			bodyFont.setFontName(FONT_NAME);
			bodyFont.setFontHeightInPoints(FONT_SIZE);

			this.header = workbook.createCellStyle();
			this.header.setFont(headerFont);
			this.header.setWrapText(true);
			this.header.setAlignment(HorizontalAlignment.CENTER);
			this.header.setVerticalAlignment(VerticalAlignment.CENTER);
			this.header.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			this.header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			this.header.setBorderTop(BorderStyle.THIN);
			this.header.setBorderBottom(BorderStyle.THIN);
			this.header.setBorderLeft(BorderStyle.THIN);
			this.header.setBorderRight(BorderStyle.THIN);

			this.text = body(workbook, bodyFont, HorizontalAlignment.LEFT, false, null, format);
			this.wrapText = body(workbook, bodyFont, HorizontalAlignment.LEFT, true, null, format);
			this.centered = body(workbook, bodyFont, HorizontalAlignment.CENTER, false, null, format);
			this.date = body(workbook, bodyFont, HorizontalAlignment.LEFT, false, DATE_FORMAT, format);
			this.qty = body(workbook, bodyFont, HorizontalAlignment.RIGHT, false, QTY_FORMAT, format);
			this.integer = body(workbook, bodyFont, HorizontalAlignment.RIGHT, false, INT_FORMAT, format);
		}

		private CellStyle body(Workbook workbook, Font font, HorizontalAlignment alignment,
							   boolean wrap, String numberFormat, DataFormat format) {
			CellStyle style = workbook.createCellStyle();
			style.setFont(font);
			style.setAlignment(alignment);
			style.setVerticalAlignment(VerticalAlignment.TOP);
			style.setWrapText(wrap);
			if (numberFormat != null) {
				style.setDataFormat(format.getFormat(numberFormat));
			}
			return style;
		}
	}
}
