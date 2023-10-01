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
	
	private String partyName;

	private String processName;

	private String matGradeName;
	
	private String materialDescription;
	
	private Integer matId;

	private BigDecimal thicknessFrom;

	private BigDecimal thicknessTo;

	private BigDecimal price;

}
