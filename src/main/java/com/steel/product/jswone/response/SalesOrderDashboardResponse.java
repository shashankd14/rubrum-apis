package com.steel.product.jswone.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesOrderDashboardResponse {

	private Integer totalSOCount;

	private BigDecimal totalWeight;

}
