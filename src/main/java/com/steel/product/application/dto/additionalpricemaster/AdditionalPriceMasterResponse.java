package com.steel.product.application.dto.additionalpricemaster;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class AdditionalPriceMasterResponse {

	private Integer id;

	private Integer partyId;

	private Integer processId;

	private Integer additionalPriceId;
	
	private String partyName;

	private String processName;

	private String additionalPriceDesc;

	private BigDecimal rangeFrom;

	private BigDecimal rangeTo;

	private BigDecimal price;

	private Integer createdBy;

	private Integer updatedBy;

	private Date createdOn;

	private Date updatedOn;

}
