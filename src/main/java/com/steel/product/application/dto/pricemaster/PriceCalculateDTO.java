package com.steel.product.application.dto.pricemaster;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PriceCalculateDTO {

	private int instructionId;

	private String coilNo;

	private String customerBatchNo;

	private String matGradeName;

	private int locationId;

	private String sono;

	private String mmid;

	private BigDecimal thickness;

	private Float actualWeight;

	private Float additionalWeight;

	private BigDecimal basePrice = new BigDecimal(BigInteger.ZERO, 2);

	private BigDecimal packingPrice = new BigDecimal(BigInteger.ZERO, 2);

	private BigDecimal laminationCharges = new BigDecimal(BigInteger.ZERO, 2);

	private BigDecimal additionalPrice = new BigDecimal(BigInteger.ZERO, 2);

	private BigDecimal rate = new BigDecimal(BigInteger.ZERO, 2);

	private BigDecimal totalPrice = new BigDecimal(BigInteger.ZERO, 2);

	private List<String> mappedSOList = new ArrayList<>();

}
