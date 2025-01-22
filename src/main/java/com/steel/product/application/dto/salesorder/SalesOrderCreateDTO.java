package com.steel.product.application.dto.salesorder;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesOrderCreateDTO {

	private Integer instructionId;

	private Integer inwardEntryId;

	private String coilNo;

	private String soNumber;

	private String customerBatchNo;

	private BigDecimal fthickness;

	private BigDecimal fwidth;

	private BigDecimal flength;

	private BigDecimal fweight;

	private String remarks;

	private Integer userId;

	private Integer partyId;

}
