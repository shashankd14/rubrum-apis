package com.steel.product.jswone.request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class SalesOrderMainRequest extends BaseRequest {

	private Integer soId;

	private String soNumber;

	private Date socreatedate;

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

	List<SalesOrderChildRequest> itemsList = new ArrayList<>();

}
