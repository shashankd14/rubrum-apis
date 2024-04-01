package com.steel.product.application.dto.yieldlossratio;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class YieldLossRatioResponse{

	private Integer ylrId;

	private Integer partyId;

	private Integer processId;
	
	private String processName;
	
	private String partyName;

	private BigDecimal lossRatioPercentageFrom;

	private BigDecimal lossRatioPercentageTo;

	private String comments;

}
