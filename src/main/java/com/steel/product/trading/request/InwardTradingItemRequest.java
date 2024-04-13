package com.steel.product.trading.request;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InwardTradingItemRequest {

	private Integer itemchildId;
	
	private Integer itemId;
	
	private Integer inwardId;

	private String unit;

	private Integer unitVolume;

	private BigDecimal netWeight;

	private BigDecimal rate;

	private Integer volume;

	private Integer actualNoofPieces;

	private BigDecimal theoreticalWeight;

	private BigDecimal weightVariance;

	private Integer theoreticalNoofPieces;
	
	

}
