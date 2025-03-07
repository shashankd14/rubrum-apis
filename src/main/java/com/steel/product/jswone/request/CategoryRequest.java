package com.steel.product.jswone.request;

import com.steel.product.trading.request.BaseRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest extends BaseRequest {

	private Integer categoryId;

	private String categoryName;

	private String categoryHsnCode;
}
