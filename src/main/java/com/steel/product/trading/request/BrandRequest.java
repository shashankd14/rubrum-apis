package com.steel.product.trading.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandRequest extends BaseRequest {

	private Integer brandId;

	private String brandName;

	private String brandDesc;

}
