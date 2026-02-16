package com.steel.product.jswone.request;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CPSplitRequest {

	private int instructionId;

	private int inwardEntryId;

	private BigDecimal splitQty;

	private String remarks;

	private String soNumber;

}
