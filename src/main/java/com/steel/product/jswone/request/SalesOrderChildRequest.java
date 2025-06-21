package com.steel.product.jswone.request;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class SalesOrderChildRequest {

	private Integer soChildId;

	private Integer soId;

	private String mmId;

	private BigDecimal soqty;

	private BigDecimal allocatedSoqty;

	private Integer instructionId;

	private Integer inwardEntryId;

	private String specialInstructions;

}
