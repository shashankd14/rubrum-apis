package com.steel.product.application.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class TallyBillingInvoiceListDTO {

	private Integer voucherRef;
	private Integer dcNo;
	private String dcDate;
	private String voucherType;
	private String customerCode;
	private String customerName;
	private String customerMobileNo;
	private String underGroup;
	private String address1;
	private String address2;
	private String address3;
	private String city;
	private String pincode;
	private String state;
	private String gstno;
	private String productNo;
	private String productDesc;;
	private String coilNo;
	private String customerBatchNo;
	private String materialGrade;
	private String materialDesc;
	private Float thickness;
	private Float width;
	private Float length;
	private String hsnCode;
	private String godown;
	private String uom;
	private Float quantity;
	private BigDecimal rate;
	private BigDecimal amount;
	private BigDecimal gstPercentage;
	private BigDecimal igst;
	private BigDecimal cgst;
	private BigDecimal sgst;
	private String ledger1;
	private String ledger2;
	private String ledger3;
	private String ledger4;
	private String ledger5;
	private String ledger6;
	private String ledger7;
	private String roundOff;
	private String total;
	private String remarks;

}