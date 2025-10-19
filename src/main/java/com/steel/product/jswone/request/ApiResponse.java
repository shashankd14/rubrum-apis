package com.steel.product.jswone.request;

import lombok.Data;

@Data
public class ApiResponse {
	private String code;
	private String message;
	private Object details;

	public ApiResponse(String code, String message, Object details) {
		this.code = code;
		this.message = message;
		this.details = details;
	}
}
