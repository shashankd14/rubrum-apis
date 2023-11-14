package com.steel.product.application.dto.pricemaster;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Data;

@Data
public class PriceCalculateDTO {
	
	private int instructionId;
	
	private String coilNo;
	
	private String customerBatchNo;
	
	private String matGradeName;
	
	private BigDecimal thickness;
	
	private Float actualWeight;

	private BigDecimal basePrice = new BigDecimal(BigInteger.ZERO,  2);

	private BigDecimal packingPrice = new BigDecimal(BigInteger.ZERO,  2);

	private BigDecimal laminationCharges = new BigDecimal(BigInteger.ZERO,  2);

	private BigDecimal additionalPrice  = new BigDecimal(BigInteger.ZERO,  2);

	private BigDecimal rate  = new BigDecimal(BigInteger.ZERO,  2);
	
	private BigDecimal totalPrice = new BigDecimal(BigInteger.ZERO,  2);

}
