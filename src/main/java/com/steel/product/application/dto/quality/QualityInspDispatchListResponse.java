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

	private String customerInvoiceDate;

}
