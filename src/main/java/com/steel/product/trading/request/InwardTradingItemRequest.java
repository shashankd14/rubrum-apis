package com.steel.product.trading.request;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InwardTradingItemRequest {

	private Integer itemchildId;

	private Integer itemId;
	
	private String inwardItemId;

	private Integer inwardId;

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

}
