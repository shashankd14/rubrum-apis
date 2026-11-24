package com.steel.product.trading.repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.steel.product.trading.request.OtherChargeLine;

/** Rows of trading_inward_other_charge. An inward's rows are always replaced as a whole on save. */
@Repository
public class InwardOtherChargeRepository {

	public static final int MAX_LINES = 50;
	public static final int MAX_LABEL_LENGTH = 100;

	private final JdbcTemplate jdbc;
	private final NamedParameterJdbcTemplate named;

	public InwardOtherChargeRepository(JdbcTemplate jdbc, NamedParameterJdbcTemplate named) {
		this.jdbc = jdbc;
		this.named = named;
	}

	/**
	 * Checks the rows a client sent and returns them cleaned: label trimmed, amount to 2 decimals.
	 * Rows with no amount are dropped so an empty row on screen is not saved.
	 */
	public static List<OtherChargeLine> normalize(List<OtherChargeLine> lines) {
		if (lines == null) return Collections.emptyList();
		List<OtherChargeLine> result = new ArrayList<>();
		for (OtherChargeLine line : lines) {
			if (line == null || line.getAmount() == null || line.getAmount().signum() == 0) continue;
			if (line.getAmount().signum() < 0) throw new IllegalArgumentException("Other charge amounts cannot be negative.");
			String label = line.getLabel() == null ? "" : line.getLabel().trim();
			if (label.isEmpty()) throw new IllegalArgumentException("Enter a description for each other charge.");
			if (label.length() > MAX_LABEL_LENGTH) {
				throw new IllegalArgumentException("Other charge description must be at most " + MAX_LABEL_LENGTH + " characters.");
			}
			result.add(new OtherChargeLine(label, line.getAmount().setScale(2, RoundingMode.HALF_UP)));
		}
		if (result.size() > MAX_LINES) throw new IllegalArgumentException("A maximum of " + MAX_LINES + " other charges is allowed.");
		return result;
	}

	public static BigDecimal total(List<OtherChargeLine> lines) {
		BigDecimal total = BigDecimal.ZERO;
		for (OtherChargeLine line : lines) total = total.add(line.getAmount());
		return total.setScale(2, RoundingMode.HALF_UP);
	}

	public void replace(int inwardId, List<OtherChargeLine> lines) {
		jdbc.update("DELETE FROM trading_inward_other_charge WHERE inward_id = ?", inwardId);
		if (lines.isEmpty()) return;
		List<Object[]> rows = new ArrayList<>();
		for (int i = 0; i < lines.size(); i++) {
			rows.add(new Object[] { inwardId, lines.get(i).getLabel(), lines.get(i).getAmount(), i + 1 });
		}
		jdbc.batchUpdate("INSERT INTO trading_inward_other_charge (inward_id, label, amount, sort_order) VALUES (?, ?, ?, ?)", rows);
	}

	/** Rows of many inwards in one query, keyed by inward id, in their saved order. */
	public Map<Integer, List<OtherChargeLine>> load(Collection<Integer> inwardIds) {
		Map<Integer, List<OtherChargeLine>> result = new HashMap<>();
		if (inwardIds == null || inwardIds.isEmpty()) return result;
		named.query("SELECT inward_id, label, amount FROM trading_inward_other_charge WHERE inward_id IN (:ids) ORDER BY inward_id, sort_order",
				new MapSqlParameterSource("ids", inwardIds),
				rs -> {
					result.computeIfAbsent(rs.getInt("inward_id"), id -> new ArrayList<>())
							.add(new OtherChargeLine(rs.getString("label"), rs.getBigDecimal("amount")));
				});
		return result;
	}
}
