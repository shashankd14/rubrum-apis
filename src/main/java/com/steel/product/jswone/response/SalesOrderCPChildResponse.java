package com.steel.product.jswone.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesOrderCPChildResponse {

	private Integer soChildId;

	private String mmId;

	private String materialDescription;

	private BigDecimal itemQty;

	private BigDecimal allocatedSoqty;

	private String allocatedStts;

	private String itemStatus;

	private String wareHouseName;

	private String wareHouseId;

	private List<SalesOrderChildAllocationResponse> allocationDetails = new ArrayList<>();

}
