package com.steel.product.application.dto.partDetails;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
public class PartDetailsRequest {

	private Float targetWeight;
	private Float length;
	private Integer createdBy;
	private Integer updatedBy;
	private Boolean isDeleted;
	private BigDecimal plannedYieldLossRatio;
    private BigDecimal actualYieldLossRatio;
}
