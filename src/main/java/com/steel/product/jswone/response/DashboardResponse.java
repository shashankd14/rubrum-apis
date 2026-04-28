package com.steel.product.jswone.response;

import java.util.Map;

import lombok.Data;

@Data
public class DashboardResponse {

	private boolean success;

	private String message;

	private String module;

	private Map<String, SalesOrderDashboardResponse> dashboard;

}
