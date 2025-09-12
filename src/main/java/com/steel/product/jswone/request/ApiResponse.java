package com.steel.product.jswone.request;

import lombok.Data;

@Data
public class ApiResponse {
	private String status;
	private String message;
	private Object details;

	public ApiResponse(String status, String message, Object details) {
		this.status = status;
		this.message = message;
		this.details = details;
	}
}
