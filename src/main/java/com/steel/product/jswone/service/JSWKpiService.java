package com.steel.product.jswone.service;

import com.steel.product.jswone.request.JSWReportRequest;
import com.steel.product.jswone.request.ReportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * KPI cards for the 6 WMS reports (Audit Trail has no KPIs).
 *
 * Only two dates drive everything: :fromDate (00:00:00) and :toDate (23:59:59), both inclusive,
 * same as the download API. Defaults: 1st of current month .. today (UTC).
 *   period_*    = fromDate .. toDate
 *   last_day_*  = the toDate day only
 * Stock is a current snapshot (no date filter, same as the Stock report); Job Orders WIP / ready
 * are current, with ageing measured to toDate.
 *
 * Location: request.locationId > 0 filters on the coil's party (product_tblinwardentry.npartyid);
 * Open Orders uses the SKU warehouse's party (jsw_warehouse_master.party_id).
 */
@Service
public class JSWKpiService {

	private static final Logger log = LoggerFactory.getLogger(JSWKpiService.class);
	private static final ZoneId ZONE = ZoneOffset.UTC;

	private static final String LOC_IE = " AND (:locationId = 0 OR ie.npartyid = :locationId) ";

	// ------------------------------------------------------------------ 1. Purchase
	private static final String PURCHASE_KPI =
			"SELECT " +
			"  CAST(ROUND(COALESCE(SUM(ie.fquantity), 0) / 1000, 3) AS DECIMAL(18,3)) AS period_purchase_qty_mt, " +
			"  CAST(ROUND(COALESCE(SUM(CASE WHEN UPPER(cst.statusname) LIKE '%DEFECT%' THEN ie.fquantity END), 0) / 1000, 3) AS DECIMAL(18,3)) AS period_defective_coils_qty_mt " +
			"FROM product_tblinwardentry ie " +
			"LEFT JOIN product_status cst ON cst.statusid = ie.vstatus " +
			"WHERE COALESCE(ie.isdeleted, 0) = 0 " +
			"  AND ie.dreceiveddate BETWEEN :fromDate AND :toDate " +
			LOC_IE;

	// ------------------------------------------------------------------ 2. Sales
	private static final String SALES_KPI =
			"SELECT " +
			"  CAST(ROUND(COALESCE(SUM(x.w), 0) / 1000, 3) AS DECIMAL(18,3)) AS period_sales_qty_mt, " +
			"  CAST(ROUND(COALESCE(SUM(CASE WHEN DATE(x.dc_ts) = DATE(:toDate) THEN x.w END), 0) / 1000, 3) AS DECIMAL(18,3)) AS last_day_sales_qty_mt, " +
			"  CAST(ROUND(COALESCE(SUM(CASE WHEN x.processed = 1 THEN x.w END), 0) / 1000, 3) AS DECIMAL(18,3)) AS period_processed_sales_qty_mt, " +
			"  CAST(ROUND(COALESCE(SUM(CASE WHEN DATE(x.dc_ts) = DATE(:toDate) AND x.processed = 1 THEN x.w END), 0) / 1000, 3) AS DECIMAL(18,3)) AS last_day_processed_sales_qty_mt " +
			"FROM ( " +
			"  SELECT d.createdon                                       AS dc_ts, " +
			"         COALESCE(pi.actualweight, pi.plannedweight, 0)    AS w, " +
			"         CASE WHEN pi.processid IS NULL THEN 0 ELSE 1 END  AS processed " +          // VERIFY process rule
			"    FROM product_instruction pi " +
			"    JOIN product_tbl_delivery_details d ON d.deliveryid = pi.deliveryid AND COALESCE(d.isdeleted, 0) = 0 " +
			"    JOIN product_tblinwardentry ie      ON ie.inwardentryid = pi.inwardid " +
			"   WHERE COALESCE(pi.isdeleted, 0) = 0 " +
			"     AND d.createdon BETWEEN :fromDate AND :toDate " +
			LOC_IE +
			") x";

	// ------------------------------------------------------------------ 3. Stock (current snapshot; days on hand from period sales)
	private static final String STOCK_KPI =
			"WITH pkt AS ( " +
			"  SELECT pi.inwardid, " +
			"         SUM(CASE WHEN pi.deliveryid IS NOT NULL THEN COALESCE(pi.actualweight, pi.plannedweight, 0) ELSE 0 END) AS dispatched_wt, " +
			"         SUM(CASE WHEN pi.deliveryid IS NULL AND pst.statusname = 'READY TO DELIVER' " +
			"                   AND NOT (UPPER(COALESCE(pc.classification_name, '')) LIKE '%SCRAP%' OR UPPER(COALESCE(pc.classification_name, '')) LIKE '%DEFECT%') " +
			"                  THEN COALESCE(pi.actualweight, pi.plannedweight, 0) ELSE 0 END) AS sellable_fg_wt " +
			"    FROM product_instruction pi " +
			"    LEFT JOIN product_status pst ON pst.statusid = pi.status " +
			"    LEFT JOIN product_packet_classification pc ON pc.classification_id = pi.packet_classification_id " +
			"   WHERE COALESCE(pi.isdeleted, 0) = 0 " +
			"     AND NOT EXISTS (SELECT 1 FROM product_instruction c " +
			"                      WHERE c.parentinstructionid = pi.instructionid AND COALESCE(c.isdeleted, 0) = 0) " +
			"   GROUP BY pi.inwardid " +
			"), " +
			"stk AS ( " +
			"  SELECT SUM(GREATEST(COALESCE(ie.fquantity, 0) - COALESCE(p.dispatched_wt, 0), 0)) AS current_stock, " +
			"         SUM(COALESCE(p.sellable_fg_wt, 0))                                         AS sellable_fg, " +
			"         SUM(CASE WHEN UPPER(COALESCE(cst.statusname, '')) LIKE '%DEFECT%' THEN 0 ELSE COALESCE(ie.fpresent, 0) END) AS coil_stock " +
			"    FROM product_tblinwardentry ie " +
			"    LEFT JOIN pkt p ON p.inwardid = ie.inwardentryid " +
			"    LEFT JOIN product_status cst ON cst.statusid = ie.vstatus " +
			"   WHERE COALESCE(ie.isdeleted, 0) = 0 " +
			LOC_IE +
			"), " +
			"sale AS ( " +                                                                   // average daily sale over fromDate..toDate
			"  SELECT SUM(COALESCE(pi.actualweight, pi.plannedweight, 0)) " +
			"         / (DATEDIFF(DATE(:toDate), DATE(:fromDate)) + 1) AS avg_daily_sale " +
			"    FROM product_instruction pi " +
			"    JOIN product_tbl_delivery_details d ON d.deliveryid = pi.deliveryid AND COALESCE(d.isdeleted, 0) = 0 " +
			"    JOIN product_tblinwardentry ie      ON ie.inwardentryid = pi.inwardid " +
			"   WHERE COALESCE(pi.isdeleted, 0) = 0 " +
			"     AND d.createdon BETWEEN :fromDate AND :toDate " +
			LOC_IE +
			") " +
			"SELECT " +
			"  CAST(ROUND(COALESCE(stk.current_stock, 0) / 1000, 3) AS DECIMAL(18,3)) AS total_current_stock_mt, " +
			"  CAST(ROUND(COALESCE(stk.sellable_fg,   0) / 1000, 3) AS DECIMAL(18,3)) AS sellable_fg_stock_mt, " +
			"  CAST(ROUND(COALESCE(stk.coil_stock,    0) / 1000, 3) AS DECIMAL(18,3)) AS coil_stock_mt, " +
			"  CAST(ROUND(stk.current_stock / NULLIF(sale.avg_daily_sale, 0), 1) AS DECIMAL(18,1)) AS days_on_hand " +
			"FROM stk CROSS JOIN sale";

	// ------------------------------------------------------------------ 4. Open Orders (SOs created fromDate..toDate)
	private static final String OPEN_ORDERS_KPI =
			"WITH jw AS ( " +
			"  SELECT a.so_child_id, " +
			"         SUM(CASE WHEN pi.deliveryid IS NULL AND pst.statusname = 'IN PROGRESS'      THEN COALESCE(pi.plannedweight, 0) ELSE 0 END) AS wip_qty, " +
			"         SUM(CASE WHEN pi.deliveryid IS NULL AND pst.statusname = 'READY TO DELIVER' THEN COALESCE(pi.actualweight, pi.plannedweight, 0) ELSE 0 END) AS ready_qty " +
			"    FROM (SELECT DISTINCT so_child_id, instruction_id " +
			"            FROM jsw_sales_order_allocation WHERE instruction_id IS NOT NULL) a " +
			"    JOIN product_instruction pi  ON pi.instructionid = a.instruction_id " +
			"    LEFT JOIN product_status pst ON pst.statusid = pi.status " +
			"   WHERE COALESCE(pi.isdeleted, 0) = 0 " +
			"   GROUP BY a.so_child_id " +
			") " +
			"SELECT " +
			"  CAST(ROUND(COALESCE(SUM(GREATEST(COALESCE(soc.soqty, 0) - COALESCE(soc.quantity_invoiced, 0), 0)), 0) / 1000, 3) AS DECIMAL(18,3)) AS open_orders_qty_mt, " +
			"  CAST(ROUND(COALESCE(SUM(COALESCE(soc.allocated_soqty, 0)), 0) / 1000, 3) AS DECIMAL(18,3))                                          AS allocated_qty_mt, " +
			"  CAST(ROUND(COALESCE(SUM(COALESCE(j.wip_qty, 0)), 0) / 1000, 3) AS DECIMAL(18,3))                                                    AS wip_qty_mt, " +
			"  CAST(ROUND(COALESCE(SUM(COALESCE(j.ready_qty, 0)), 0) / 1000, 3) AS DECIMAL(18,3))                                                  AS ready_to_deliver_qty_mt, " +
			"  CAST(ROUND(COALESCE(SUM(GREATEST(COALESCE(soc.soqty, 0) - COALESCE(soc.allocated_soqty, 0), 0)), 0) / 1000, 3) AS DECIMAL(18,3))   AS pending_allocation_qty_mt " +
			"FROM jsw_sales_order_child soc " +
			"JOIN jsw_sales_order so ON so.so_id = soc.so_id " +
			"LEFT JOIN jw j          ON j.so_child_id = soc.so_child_id " +
			"WHERE COALESCE(so.is_deleted, 0) = 0 " +
			"  AND COALESCE(soc.is_deleted, 0) = 0 " +
			"  AND COALESCE(soc.quantity_invoiced, 0) < COALESCE(soc.soqty, 0) " +
			"  AND COALESCE(so.zoho_status, '') NOT IN ('closed', 'void', 'cancelled') " +          // VERIFY status values
			"  AND so.socreatedate BETWEEN :fromDate AND :toDate " +
			"  AND (:locationId = 0 OR EXISTS (SELECT 1 FROM jsw_warehouse_master wh " +            // VERIFY: SKU warehouse = location
			"                                   WHERE wh.ware_house_id = soc.wearhouse_id AND wh.party_id = :locationId))";

	// ------------------------------------------------------------------ 5. Job Orders (current WIP / ready, ageing to toDate)
	private static final String JOB_ORDERS_KPI =
			"SELECT " +
			"  CAST(ROUND(COALESCE(SUM(CASE WHEN pst.statusname = 'IN PROGRESS' THEN COALESCE(pi.plannedweight, 0) ELSE 0 END), 0) / 1000, 3) AS DECIMAL(18,3)) AS wip_qty_mt, " +
			"  CAST(ROUND(SUM(CASE WHEN pst.statusname = 'IN PROGRESS' THEN COALESCE(pi.plannedweight, 0) * DATEDIFF(DATE(:toDate), pi.createdon) END) " +
			"           / NULLIF(SUM(CASE WHEN pst.statusname = 'IN PROGRESS' THEN COALESCE(pi.plannedweight, 0) END), 0), 1) AS DECIMAL(18,1)) AS wavg_ageing_job_work_release_days, " +
			"  CAST(ROUND(COALESCE(SUM(CASE WHEN pst.statusname = 'READY TO DELIVER' THEN COALESCE(pi.actualweight, pi.plannedweight, 0) ELSE 0 END), 0) / 1000, 3) AS DECIMAL(18,3)) AS ready_to_deliver_qty_mt, " +
			"  CAST(ROUND(SUM(CASE WHEN pst.statusname = 'READY TO DELIVER' THEN COALESCE(pi.actualweight, pi.plannedweight, 0) * DATEDIFF(DATE(:toDate), COALESCE(pi.finished_date, pi.updatedon)) END) " +
			"           / NULLIF(SUM(CASE WHEN pst.statusname = 'READY TO DELIVER' THEN COALESCE(pi.actualweight, pi.plannedweight, 0) END), 0), 1) AS DECIMAL(18,1)) AS wavg_ageing_ready_to_deliver_days " +
			"FROM product_instruction pi " +
			"JOIN product_tblinwardentry ie ON ie.inwardentryid = pi.inwardid " +
			"LEFT JOIN product_status pst   ON pst.statusid = pi.status " +
			"WHERE COALESCE(pi.isdeleted, 0) = 0 " +
			"  AND pi.deliveryid IS NULL " +
			"  AND NOT EXISTS (SELECT 1 FROM product_instruction c " +
			"                   WHERE c.parentinstructionid = pi.instructionid AND COALESCE(c.isdeleted, 0) = 0) " +
			LOC_IE;

	// ------------------------------------------------------------------ 6. FIFO Breach (allocations fromDate..toDate; same rules as so_fifo_breach_vw)
	private static final String FIFO_BREACH_KPI =
			"WITH al AS ( " +
			"  SELECT soa.so_allocation_id, soa.allocation_date, ie.inwardentryid, ie.npartyid, " +
			"         ie.mm_id, ie.fthickness, ie.fwidth, ie.dreceiveddate " +
			"    FROM jsw_sales_order_allocation soa " +
			"    JOIN jsw_sales_order_child soc    ON soc.so_child_id = soa.so_child_id AND COALESCE(soc.is_deleted, 0) = 0 " +
			"    JOIN jsw_sales_order so           ON so.so_id = soc.so_id AND COALESCE(so.is_deleted, 0) = 0 " +
			"    LEFT JOIN product_instruction api ON api.instructionid = soa.instruction_id " +
			"    JOIN product_tblinwardentry ie    ON ie.inwardentryid = COALESCE(soa.inward_entry_id, api.inwardid) " +
			"   WHERE soa.allocation_date BETWEEN :fromDate AND :toDate " +
			LOC_IE +
			"), " +
			"breach AS ( " +
			"  SELECT al.so_allocation_id, al.allocation_date, al.npartyid " +
			"    FROM al " +
			"   WHERE EXISTS ( " +
			"         SELECT 1 " +
			"           FROM product_tblinwardentry ie2 " +
			"           LEFT JOIN product_status cst2 ON cst2.statusid = ie2.vstatus " +
			"          WHERE ie2.npartyid       = al.npartyid " +
			"            AND ie2.inwardentryid <> al.inwardentryid " +
			"            AND ie2.dreceiveddate  < al.dreceiveddate " +
			"            AND ie2.dreceiveddate <= al.allocation_date " +
			"            AND COALESCE(ie2.isdeleted, 0) = 0 " +
			"            AND (   ie2.mm_id = al.mm_id " +
			"                 OR (al.mm_id IS NULL AND ie2.fthickness = al.fthickness AND ie2.fwidth = al.fwidth)) " +
			"            AND UPPER(COALESCE(cst2.statusname, '')) NOT LIKE '%DEFECT%' " +
			"            AND UPPER(COALESCE(cst2.statusname, '')) NOT LIKE '%SCRAP%' " +
			"            AND COALESCE(ie2.fquantity, 0) > COALESCE(( " +
			"                  SELECT SUM(COALESCE(pi.actualweight, pi.plannedweight, 0)) " +
			"                    FROM product_instruction pi " +
			"                    JOIN product_tbl_delivery_details d ON d.deliveryid = pi.deliveryid " +
			"                   WHERE pi.inwardid = ie2.inwardentryid " +
			"                     AND COALESCE(pi.isdeleted, 0) = 0 " +
			"                     AND COALESCE(d.isdeleted, 0) = 0 " +
			"                     AND d.createdon < al.allocation_date), 0)) " +
			") " +
			"SELECT " +
			"  (SELECT COUNT(*) FROM breach)                                                   AS period_fifo_breach_cases, " +
			"  (SELECT COUNT(*) FROM breach WHERE DATE(allocation_date) = DATE(:toDate))       AS last_day_fifo_breach_cases, " +
			"  (SELECT wh.ware_house_name " +
			"     FROM breach b " +
			"     JOIN (SELECT party_id, MIN(ware_house_name) AS ware_house_name " +
			"             FROM jsw_warehouse_master GROUP BY party_id) wh ON wh.party_id = b.npartyid " +
			"    GROUP BY wh.ware_house_name " +
			"    ORDER BY COUNT(*) DESC " +
			"    LIMIT 1)                                                                      AS top_defaulting_warehouse";

	/** Report -> {response key, SQL}, in card order. */
	private static final Map<ReportType, String[]> KPIS;
	static {
		Map<ReportType, String[]> m = new LinkedHashMap<>();
		m.put(ReportType.PURCHASE,    new String[] {"purchase",   PURCHASE_KPI});
		m.put(ReportType.SALES,       new String[] {"sales",      SALES_KPI});
		m.put(ReportType.STOCK,       new String[] {"stock",      STOCK_KPI});
		m.put(ReportType.OPEN_ORDERS, new String[] {"openOrders", OPEN_ORDERS_KPI});
		m.put(ReportType.JOB_ORDERS,  new String[] {"jobOrders",  JOB_ORDERS_KPI});
		m.put(ReportType.FIFO_BREACH, new String[] {"fifoBreach", FIFO_BREACH_KPI});
		KPIS = Collections.unmodifiableMap(m);
	}

	private final NamedParameterJdbcTemplate jdbc;

	public JSWKpiService(DataSource dataSource) {
		// Plain (non-streaming) template: every KPI query returns a single row.
		JdbcTemplate t = new JdbcTemplate(dataSource);
		t.setQueryTimeout(120);
		this.jdbc = new NamedParameterJdbcTemplate(t);
	}

	/**
	 * reportType blank -> all 6 KPI groups; otherwise only that report's group.
	 * A failing group returns {"error": "..."} so the other cards still render.
	 */
	public Map<String, Object> getKpis(JSWReportRequest request) {
		LocalDate from = request.getFromDate();
		LocalDate to   = request.getToDate();
		LocalDate today = LocalDate.now(ZONE);
		if (from == null && to == null) { from = today.withDayOfMonth(1); to = today; }
		else if (from == null)          { from = to.withDayOfMonth(1); }
		else if (to == null)            { to = today; }
		if (from.isAfter(to)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fromDate must be on or before toDate");
		}
		int locationId = Math.max(request.getLocationId(), 0);

		MapSqlParameterSource params = new MapSqlParameterSource()
				// 00:00:00 .. 23:59:59 so DATETIME columns include the whole toDate (works for DATE columns too)
				.addValue("fromDate",   java.sql.Timestamp.valueOf(from.atStartOfDay()))
				.addValue("toDate",     java.sql.Timestamp.valueOf(to.atTime(23, 59, 59)))
				.addValue("locationId", locationId);

		Map<ReportType, String[]> selected = select(request.getReportType());

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("fromDate", from.toString());
		result.put("toDate", to.toString());
		result.put("locationId", locationId);
		for (Map.Entry<ReportType, String[]> e : selected.entrySet()) {
			String key = e.getValue()[0];
			try {
				result.put(key, jdbc.queryForMap(e.getValue()[1], params));
			} catch (RuntimeException ex) {
				log.error("KPI query failed for {} (from={}, to={}, locationId={})", key, from, to, locationId, ex);
				result.put(key, Collections.singletonMap("error", "Failed to load " + key + " KPIs"));
			}
		}
		return result;
	}

	private static Map<ReportType, String[]> select(String reportType) {
		if (reportType == null || reportType.trim().isEmpty()) {
			return KPIS;
		}
		ReportType type = ReportType.from(reportType);
		String[] def = KPIS.get(type);
		if (def == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"No KPIs for reportType " + type + ". Allowed: " + KPIS.keySet());
		}
		return Collections.singletonMap(type, def);
	}
}