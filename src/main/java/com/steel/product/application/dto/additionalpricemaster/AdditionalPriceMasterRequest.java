package com.steel.product.application.dto.additionalpricemaster;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalPriceMasterRequest {

	private Integer id;

	private Integer partyId;

	private Integer processId;

	private Integer additionalPriceId;
	
	private BigDecimal rangeFrom;

	private BigDecimal rangeTo;

	private BigDecimal price; 
}
