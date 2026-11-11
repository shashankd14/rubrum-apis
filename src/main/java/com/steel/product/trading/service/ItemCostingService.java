package com.steel.product.trading.service;

import com.steel.product.trading.repository.EQPRepository;
import com.steel.product.trading.repository.StockRegisterRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Average landed cost per KG per item, from COMPLETED inwards (same basis as the stock register): item value plus its value-proportional share of
 * the inward's Actual Cost (extra charges, GST and other charges), divided by received net weight.
 */
@Service
@Log4j2
public class ItemCostingService {

	@Autowired
	private EQPRepository eqpRepository;

	@Autowired
	private StockRegisterRepository stockRegisterRepository;

	/** Available stock in KG per item; empty map on failure so list APIs keep working. */
	public Map<Integer, BigDecimal> availableKg(Collection<Integer> itemIds) {
		LinkedHashSet<Integer> ids = new LinkedHashSet<>(itemIds);
		ids.removeIf(Objects::isNull);
		try {
			return stockRegisterRepository.availableKgByItem(ids);
		} catch (Exception e) {
			log.error("Unable to load available stock", e);
			return new HashMap<>();
		}
	}

	public Map<Integer, BigDecimal> averageCostPerKg(Collection<Integer> itemIds) {
		Map<Integer, BigDecimal> costs = new HashMap<>();
		LinkedHashSet<Integer> ids = new LinkedHashSet<>(itemIds);
		ids.removeIf(Objects::isNull);
		if (ids.isEmpty()) return costs;
		try {
			Map<Integer, BigDecimal> onHand = availableKg(ids);
			Map<Integer, BigDecimal[]> totals = new HashMap<>();
			for (Object[] row : eqpRepository.findInwardCostLayersNewestFirst(new ArrayList<>(ids))) {
				if (row[0] == null || row[1] == null || row[2] == null) continue;
				int itemId = ((Number) row[0]).intValue();
				BigDecimal value = decimal(row[1]);
				BigDecimal kg = decimal(row[2]);
				if (kg.signum() <= 0) continue;
				BigDecimal[] total = totals.computeIfAbsent(itemId, k -> new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO });
				BigDecimal target = onHand.getOrDefault(itemId, BigDecimal.ZERO);
				BigDecimal remaining = target.signum() > 0 ? target.subtract(total[1]) : (total[1].signum() == 0 ? kg : BigDecimal.ZERO);
				if (remaining.signum() <= 0) continue;
				BigDecimal used = kg.min(remaining);
				total[0] = total[0].add(value.multiply(used).divide(kg, 10, RoundingMode.HALF_UP));
				total[1] = total[1].add(used);
			}
			totals.forEach((itemId, total) -> {
				if (total[1].signum() > 0) costs.put(itemId, total[0].divide(total[1], 2, RoundingMode.HALF_UP));
			});
		} catch (Exception e) {
			// Costing is informational; never fail the calling list API because of it.
			log.error("Unable to load average item cost", e);
		}
		return costs;
	}

	private static BigDecimal decimal(Object value) {
		return value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(value.toString());
	}
}
