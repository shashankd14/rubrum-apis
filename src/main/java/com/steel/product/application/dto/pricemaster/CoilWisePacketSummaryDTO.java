package com.steel.product.application.dto.pricemaster;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CoilWisePacketSummaryDTO {

	private Integer inwardId;

	private String coilNo;

	private String customerBatchNo;

	private Integer totalNoOfPackets;

	private BigDecimal totalWeight;

}
