package com.steel.product.application.dto.packingmaster;

import com.steel.product.trading.request.BaseRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PackingItemRequest extends BaseRequest {

	private Integer id;

	private String packingItemId;

	private String description;

	private String unit;

}
