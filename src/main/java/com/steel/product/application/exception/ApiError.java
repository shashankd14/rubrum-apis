package com.steel.product.application.exception;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ApiError {

	private String message;
	private String code;
	private List<String> errors;
	
	public ApiError(String code)
	{
		this.code = code;
	}
	
	public ApiError(String code, String message)
	{
		this.code = code;
		this.message = message;
	}
	
}
