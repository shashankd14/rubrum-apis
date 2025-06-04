package com.steel.product.jswone.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesOrderListResponse {

	private String soNumber;

	private String customerCode;

	private String orderDate;

	private Integer soId;

	private Integer partyId;

	private String soStatus;

	private String partyName;

	private String cagtegoryName;

	private Float fweightTotal;
	
	private String processCenter;
	
	List<SalesOrderListResponse> childListResp = new ArrayList<>();

}
