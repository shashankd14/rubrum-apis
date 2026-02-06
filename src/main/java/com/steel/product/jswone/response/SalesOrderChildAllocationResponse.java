package com.steel.product.jswone.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesOrderChildAllocationResponse {

	private Integer inwardId;

	private Integer instructionId;

	private Integer soAllocationId;

	private BigDecimal allocatedqty;

	private String coilNumber;

	private String packing;

	private String size;

	private Integer pcs;

	private BigDecimal qty;

	private String status;

}
