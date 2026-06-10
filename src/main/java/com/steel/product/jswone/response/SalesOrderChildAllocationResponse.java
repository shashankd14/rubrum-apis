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

	private String customerBatchId;

	private String productDetails;

	private String locationName;

	private String packing;

	private String size;

	private Integer noofPieces;

	private String status;

	private String allocationType;

    private String zohoSyncRemarks;

    private String zohoSyncStts;

}
