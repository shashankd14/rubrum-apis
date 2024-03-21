package com.steel.product.trading.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubCategoryRequest extends BaseRequest {

	private Integer subcategoryId;

	private String subcategoryName;

	private String subcategoryHsnCode;

	private Integer categoryId;

}