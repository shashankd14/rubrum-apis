package com.steel.product.application.dto.additionalpricemaster;

import java.math.BigDecimal;
import java.util.List;

import com.steel.product.trading.request.BaseRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalPriceMasterRequest extends BaseRequest{

	private Integer id;

	private List<Integer> partyId;

	private Integer processId;

	private Integer additionalPriceId;
	
	private BigDecimal rangeFrom;

	private BigDecimal rangeTo;

	private BigDecimal price; 
}
