package com.steel.product.jswone.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.steel.product.jswone.entity.StatusType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesOrderMainResponse {

	private Integer soId;

	private String soNumber;

	private String socreatedate;

	private String deliverymethod;

	private String destinationcode;

	private String refno;

	private String joplsorefno;

	private String bizsegment;

	private String ecommerce;

	private String supplysource;

	private String typeofsupply;

	private String incomingpayment;

	private String paymentmode;

	private String terms;

	private String customerid;

	private String customerCode;

	private String customer_name;

	private String customer_number;

	private BigDecimal totalSoqty;

	private BigDecimal totalAllocatedSoqty;

	private String allocatedStts;

	private String soStatus;

	private String zohoStatus;
	
	private Integer partyId;
	
	private String partyName;

	private String zbooks_so;

	private String expected_delivery_date;

	private String likely_material_date;

	private String standard_material_date;

	private String branch;

	private String remarks;

	private String cam_code;

	private String order_confirmation_time;

	private String special_delivery_instructions;

	private List<SalesOrderChildResponse> itemslist = new ArrayList<>();

}
