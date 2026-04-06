package com.steel.product.jswone.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CoilAllocationDTO {

	// private Integer soId;

	private String soNumber;

	private String refno;

	private String customerCode;

	// private BigDecimal totalQty;

	private String cpStatus;

	// private String branchId;

	// private String branchName;

	private String expectedDeliveryDate;

	// private Integer soChildId;

	private String mmId;

	private String materialDescription;

	private BigDecimal itemQty;

	private BigDecimal allocatedSoqty;

	private String allocatedStts;

	// private String itemStatus;

	// private String location;

	// private String wareHouseName;;

	private Integer inwardId;

	private Integer instructionId;

	private Integer soAllocationId;

	private BigDecimal allocatedqty;

	private Float thickness;

	private Float width;

	private Float length;

	private String coilNumber;

	private String locationName;

	private String subGrade;

	private String grade;

	// private BigDecimal qty;

	// private String status;

}
