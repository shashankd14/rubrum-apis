package com.steel.product.trading.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One named other charge of an inward, e.g. "Unloading labour" 1500.00 (table trading_inward_other_charge). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtherChargeLine {
	private String label;
	private BigDecimal amount;
}
