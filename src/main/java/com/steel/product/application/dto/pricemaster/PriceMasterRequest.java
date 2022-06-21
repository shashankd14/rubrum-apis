package com.steel.product.application.dto.pricemaster;

import java.math.BigDecimal;

import com.steel.product.application.dto.BaseReq;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriceMasterRequest extends BaseReq {

	private Integer id;

	private Integer partyId;

	private Integer processId;

	private Integer matGradeId;
	
	private BigDecimal thicknessFrom;

	private BigDecimal thicknessTo;

	private BigDecimal price;

	private Integer toPartyId;

	private Integer toProcessId;

	private Integer toMatGradeId;
}
