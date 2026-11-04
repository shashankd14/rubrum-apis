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
 * freight, insurance, loading and weighment charges (GST excluded), divided by received net weight.
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
			for (Object[] row : eqpRepository.findAverageLandedCostPerKg(new ArrayList<>(ids))) {
				if (row[0] == null || row[1] == null) continue;
				BigDecimal cost = row[1] instanceof BigDecimal ? (BigDecimal) row[1] : new BigDecimal(row[1].toString());
				costs.put(((Number) row[0]).intValue(), cost.setScale(2, RoundingMode.HALF_UP));
			}
		} catch (Exception e) {
			// Costing is informational; never fail the calling list API because of it.
			log.error("Unable to load average item cost", e);
		}
		return costs;
	}
}
