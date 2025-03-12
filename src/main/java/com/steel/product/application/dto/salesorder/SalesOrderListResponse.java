package com.steel.product.application.dto.salesorder;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesOrderListResponse {

	private String soNumber;

	private String customerCode;

	private Integer soId;

	private Integer partyId;

	private String soStatus;

	private String partyName;

	List<SalesOrderListDTO> childListResp = new ArrayList<>();

}
