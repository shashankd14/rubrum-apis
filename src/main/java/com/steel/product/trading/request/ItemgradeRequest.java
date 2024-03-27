package com.steel.product.trading.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemgradeRequest extends BaseRequest {

	private Integer itemgradeId;

	private String itemgradeName;

	private String itemgradeDesc;

}
