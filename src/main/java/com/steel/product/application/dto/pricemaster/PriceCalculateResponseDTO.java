package com.steel.product.application.dto.pricemaster;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PriceCalculateResponseDTO {

	private boolean validationStatus;

	private String remarks;

	private List<PriceCalculateDTO> priceDetailsList =new ArrayList<>();
}
