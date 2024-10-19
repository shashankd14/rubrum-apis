package com.steel.product.application.dto.quality;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QualityInspDispatchListResponse {

	private String deliveryDate;

	private Integer deliveryChalanNo;

	private String customerBatchNo;

	private BigDecimal qtyDelivered;

	private String coilNo;

	private String vehicleNo;

	private String endUserTags;

	private String customerInvoiceNo;

	private String partyName;

	private String customerInvoiceDate;

	private int nPartyId;

	private Integer qirId;

	private String materialGrade;

	private String materialDesc;

	private Float fthickness;

	private Float fwidth;

	private String deliveryRemarks;

}
