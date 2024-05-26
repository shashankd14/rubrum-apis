package com.steel.product.trading.request;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class DeliveryChalanRequest extends BaseRequest {

	private Integer dcId;

	private Integer dispatchFromId;
	
	private Integer enquiryId;

	private Integer billTo;

	private Integer shipTo;

	private String vehicleNo;

	private String transportDoc;

	private Date deliveryDate;

	private String ewayBillNo;

	private String placeOfSupply;

	private BigDecimal weighBridge;

	private BigDecimal grossWeight;

	private BigDecimal tareWeight;

	private BigDecimal netWeight;

	private String sac1;

	private String sac2;

	private String sac3;

	private String sac4;

	private BigDecimal loadingTaxable;

	private BigDecimal transportTaxable;

	private BigDecimal otherChargesTaxable;

	private BigDecimal taxableAmount;

	private BigDecimal sgst;

	private BigDecimal cgst;

	private BigDecimal total;

	private BigDecimal otherCharges1NonTaxable;

	private BigDecimal otherCharges2NonTaxable;

	private BigDecimal grandTotal;

	private String dcIssuedBy;

	private String storeLoadingIncharge;

	private String receiverSignatures;

	private Integer toBeBilledTo;

	private String purpose;

	private String amountInWords;

	private String eAndOE;

	private String dcVersion;

}
