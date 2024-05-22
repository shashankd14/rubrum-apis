package com.steel.product.trading.request;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryOrderRequest extends BaseRequest {

	private Integer doId;
	
	private String doNo;
	
	private Integer enquiryId;

	private Date doDate;

	private Integer fromLocationId;

	private Integer billToCustomerId;

	private String vehicleNo;

	private String driverNumber;

	private Date deliveryDate;

	private String dcNo;

	private String invoiceNumber;;

	private String ewayBillNo;

	private String dcIssuedBy;

	private String loadingBy;

	private String storeLoadingIncharge;

	private String receiverSignatures;

}
