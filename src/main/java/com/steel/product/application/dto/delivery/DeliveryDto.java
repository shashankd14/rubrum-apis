package com.steel.product.application.dto.delivery;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class DeliveryDto {

	private List<DeliveryItemDetails> deliveryItemDetails;

	private String vehicleNo;

	private Integer deliveryId;

	private String taskType;

	private String customerInvoiceNo;

	private String deliveryType;

	private Date customerInvoiceDate;

	private Integer packingRateId;

	private Integer laminationId;

	private int locationId;

	private String details;

	private String city;

	private int pincode;

	private String state;

	private String toLocationAddress;

}
