package com.steel.product.application.dto.additionalpricemaster;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalPriceMasterRequest {

	private Integer id;

	private List<Integer> partyId;

	private Integer processId;

	private List<Integer> additionalPriceId;
	
	private BigDecimal rangeFrom;

	private BigDecimal rangeTo;

	private BigDecimal price; 
}
