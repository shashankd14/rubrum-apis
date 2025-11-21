package com.steel.product.jswone.response;

import lombok.Data;

@Data
public class PoGrnMainResponse {

	private String code;

	private String message;

	private PoGrnDtailsResponse bill;
}
