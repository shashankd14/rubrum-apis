package com.steel.product.application.dto.delivery;

import lombok.Data;

@Data
public class DeliveryItemDetails {

	private int instructionId;

	private int inwardId;

	private String remarks;

	private Float weight;

	private Float additionalWeight;

	private String soNumber;

	private String mmid;

}
