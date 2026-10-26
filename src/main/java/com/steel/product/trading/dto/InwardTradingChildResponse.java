package com.steel.product.trading.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class InwardTradingChildResponse {

	// child items
	private Integer itemchildId;

	private Integer itemId;
	
	private String inwardItemId;

	private String itemName;

	private String unit;

	private BigDecimal unitVolume;

	private BigDecimal netWeight;

	private BigDecimal rate;

	private BigDecimal volume;

	private Integer actualNoofPieces;

	private BigDecimal theoreticalWeight;

	private BigDecimal weightVariance;

	private Integer theoreticalNoofPieces;

	private String heatNo;

	private String bundleNumbers;

	private String remark;

	private String imagesJson;

	private String technicalSpecs;

	private String additionalParams;

	private String materialGrade;

	private String brandName;

	private String itemCode;

	private String categoryName;

	private String subCategoryName;

	private String manufacturerName;

	private String unitWeights;

}
