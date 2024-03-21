package com.steel.product.trading.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest extends BaseRequest {

	private Integer categoryId;

	private String categoryName;

	private String categoryHsnCode;
}
