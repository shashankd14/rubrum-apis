package com.steel.product.jswone.response;

import lombok.Data;

@Data
public class PODetailsMainResponse {

	private String code;

	private String message;

	private PODetailsResponse purchaseorder;
}
