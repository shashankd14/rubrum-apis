package com.steel.product.application.dto.pricemaster;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class PriceMasterResponse {

	private Integer id;

	private Integer partyId;

	private Integer processId;

	private Integer matGradeId;

	private BigDecimal thicknessFrom;

	private BigDecimal thicknessTo;

	private BigDecimal price;

	private Integer createdBy;

	private Integer updatedBy;

	private Date createdOn;

	private Date updatedOn;

}
