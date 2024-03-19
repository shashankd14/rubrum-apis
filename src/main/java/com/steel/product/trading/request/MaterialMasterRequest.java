package com.steel.product.trading.request;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialMasterRequest  {

	private Integer id;

	private List<Integer> partyId;

	private Integer processId;

	private List<Integer> matGradeId;
	
	private BigDecimal thicknessFrom;

	private BigDecimal thicknessTo;

	private BigDecimal price;

	private Integer toPartyId;

	private Integer toProcessId;

	private Integer toMatGradeId;

	private Integer userId;

	private String ipAddress;

	private String requestId;
	
	
	
}
