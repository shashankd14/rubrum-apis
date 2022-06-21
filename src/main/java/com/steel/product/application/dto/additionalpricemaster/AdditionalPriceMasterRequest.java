package com.steel.product.application.dto.additionalpricemaster;

import java.math.BigDecimal;

import com.steel.product.application.dto.BaseReq;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalPriceMasterRequest extends BaseReq {

	private Integer id;

	private Integer partyId;

	private Integer processId;

	private Integer additionalPriceId;
	
	private BigDecimal rangeFrom;

	private BigDecimal rangeTo;

	private BigDecimal price; 
}
