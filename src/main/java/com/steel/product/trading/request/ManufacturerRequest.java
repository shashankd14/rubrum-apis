package com.steel.product.trading.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManufacturerRequest extends BaseRequest {

	private Integer manufacturerId;

	private String manufacturerName;

	private String manufacturerDesc;

}
