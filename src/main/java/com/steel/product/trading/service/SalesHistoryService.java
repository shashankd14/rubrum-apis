package com.steel.product.trading.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SalesHistoryService {

	private static final String LAST_SALE = "SELECT child.rate, child.chargeable_unit, child.qty1, child.unit1, terms.payment_method, "
			+ " DATE_FORMAT(COALESCE(e.proforma_created_on, e.updated_on, e.created_on), '%d-%m-%y') sold_on, e.enquiry_id, e.current_status, "
			+ " COALESCE(e.quote_customer_id, e.enq_customer_id) customer_id, COALESCE(NULLIF(c.customer_name, ''), c.customer_nickname) customer_name "
			+ " FROM trading_eqp_items child "
			+ " JOIN trading_eqp e ON e.enquiry_id = child.enquiryid "
			+ " LEFT JOIN trading_eqp_quote_terms terms ON terms.enquiryid = e.enquiry_id "
			+ " LEFT JOIN trading_customer_master c ON c.customer_id = COALESCE(e.quote_customer_id, e.enq_customer_id) "
			+ " WHERE child.item_id = :itemId AND child.is_deleted = 0 AND e.is_deleted = 0 "
			+ " AND child.status IN ('PROFORMA', 'DO', 'DC') AND child.status = e.current_status AND child.rate > 0 "
			+ " AND e.enquiry_id <> :excludeEnquiryId ";

	private static final String ORDER = " ORDER BY COALESCE(e.proforma_created_on, e.updated_on, e.created_on) DESC, e.enquiry_id DESC LIMIT 1";

	private final NamedParameterJdbcTemplate jdbc;

	public SalesHistoryService(NamedParameterJdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Map<String, Object>> lastSellingPrices(List<Integer> itemIds, Integer customerId, Integer excludeEnquiryId) {
		List<Map<String, Object>> result = new ArrayList<>();
		if (itemIds == null) return result;
		LinkedHashSet<Integer> ids = new LinkedHashSet<>(itemIds);
		ids.removeIf(Objects::isNull);
		for (Integer itemId : ids) {
			MapSqlParameterSource params = new MapSqlParameterSource()
					.addValue("itemId", itemId)
					.addValue("excludeEnquiryId", excludeEnquiryId == null ? 0 : excludeEnquiryId)
					.addValue("customerId", customerId);
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("itemId", itemId);
			row.put("customer", customerId == null ? null
					: first(LAST_SALE + " AND COALESCE(e.quote_customer_id, e.enq_customer_id) = :customerId " + ORDER, params));
			row.put("latest", first(LAST_SALE + ORDER, params));
			result.add(row);
		}
		return result;
	}

	private Map<String, Object> first(String sql, MapSqlParameterSource params) {
		List<Map<String, Object>> rows = jdbc.query(sql, params, (rs, i) -> {
			Map<String, Object> sale = new LinkedHashMap<>();
			sale.put("rate", rs.getBigDecimal("rate"));
			sale.put("chargeableUnit", rs.getString("chargeable_unit"));
			sale.put("qty", rs.getBigDecimal("qty1"));
			sale.put("unit", rs.getString("unit1"));
			sale.put("paymentTerms", rs.getString("payment_method"));
			sale.put("soldOn", rs.getString("sold_on"));
			sale.put("enquiryId", rs.getInt("enquiry_id"));
			sale.put("stage", rs.getString("current_status"));
			sale.put("customerId", rs.getObject("customer_id") == null ? null : rs.getInt("customer_id"));
			sale.put("customerName", rs.getString("customer_name"));
			return sale;
		});
		return rows.isEmpty() ? null : rows.get(0);
	}
}
