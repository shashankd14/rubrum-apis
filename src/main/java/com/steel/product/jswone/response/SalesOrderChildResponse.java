package com.steel.product.jswone.response;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesOrderChildResponse {

	private Integer soChildId;

	private String mmId;

	private BigDecimal soqty;

	private BigDecimal allocatedSoqty;

	private String allocatedStts;

	private Integer instructionId;

	private Integer inwardEntryId;

	private String itemStatus;

}
