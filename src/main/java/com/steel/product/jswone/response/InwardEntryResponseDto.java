package com.steel.product.jswone.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class InwardEntryResponseDto {

	private Integer inwardEntryId;

	private Integer instructionId;

	private String locationName;

	private String coilNumber;

	private String material;

	private String materialGrade;

	private String mmId;

	private String customerBatchId;

	private Float fThickness;

	private Float fLength;

	private BigDecimal availQty;
}
