package com.steel.product.application.dto.quality;

import lombok.Data;

@Data
public class QIRPanToleranceChildDTO {

	private String toleranceThicknessFrom;
	private String toleranceThicknessTo;
	private String toleranceWidthFrom;
	private String toleranceWidthTo;
	private String toleranceSlitSizeFrom;
	private String toleranceSlitSizeTo;
	private String toleranceLengthFrom;
	private String toleranceLengthTo;
	private String toleranceBurrHeightFrom;
	private String toleranceBurrHeightTo;
	private String toleranceDiagonalDifferenceFrom;
	private String toleranceDiagonalDifferenceTo;

}