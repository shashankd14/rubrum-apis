package com.steel.product.trading.request;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InwardTradingRequest extends BaseRequest {

	private String qcCertificateNo;

	private String qcStatus;

	private BigDecimal qcYieldStrength;

	private BigDecimal qcTensileStrength;

	private BigDecimal qcElongation;

	private String qcRemark;

	private String qcCertificatesJson;


	private Integer inwardId;

	private String inwardNo;

	private String status;

	private String purposeType;

	private Integer purposeId;

	private String consignmentId;

	private Integer vendorId;

	private String transporterName;

	private String transporterPhoneNo;

	private List<InwardTradingItemRequest> itemsList;

	private String vendorBatchNo;

	private String poNumber;

	private Integer locationId;

	private String vehicleNo;

	private String documentNo;

	private String documentType;

	private Date documentDate;

	private String ewayBillNo;

	private Date ewayBillDate;

	private BigDecimal valueOfGoods;

	private String currencyCode;

	private String extraChargesOption;

	private BigDecimal freightCharges;

	private BigDecimal insuranceAmount;

	private BigDecimal loadingCharges;

	private BigDecimal weightmenCharges;

	private BigDecimal cgst;

	private BigDecimal sgst;

	private BigDecimal igst;

	private BigDecimal totalInwardVolume;

	private BigDecimal totalWeight;

	private BigDecimal totalVolume;

	private String reconciliationRemark;

	private Boolean deductFreight;

	private Boolean raiseDebitNote;

	private BigDecimal invoiceValue;

	private Date receivedDate;

	private String documentsJson;

}
