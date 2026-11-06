package com.steel.product.application.dto.salesorder;

import java.util.List;

import lombok.Data;

@Data
public class InventoryAdjustmentFGRequest {

	private Integer inwardId;

	private List<Integer> instructionIds;
}