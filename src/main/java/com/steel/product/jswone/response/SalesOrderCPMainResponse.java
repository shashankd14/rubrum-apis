package com.steel.product.jswone.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesOrderCPMainResponse {

	private Integer soId;

	private String soNumber;

	private String customerCode;

	private BigDecimal totalQty;

	private String refno;
	
	private String cpStatus;

	private String branchId;

	private String branchName;
	
	private String expectedDeliveryDate;

	private List<SalesOrderCPChildResponse> itemslist = new ArrayList<>();

}
