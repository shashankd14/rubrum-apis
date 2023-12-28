package com.steel.product.application.dto.quality;

import lombok.Data;

@Data
public class QIRPanDetailsJsonArrayChildDTO {

	private String instructionId;
	private String thickness;
	private String plannedLength;
	private String plannedWidth;
	private String actualThickness;
	private String actualWidth;
	private String actualLength;
	private String burrHeight;
	private String diagonalDifference;
	private String remarks;

}