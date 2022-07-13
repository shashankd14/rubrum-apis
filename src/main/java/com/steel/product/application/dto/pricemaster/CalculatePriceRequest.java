package com.steel.product.application.dto.pricemaster;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculatePriceRequest {

	private List<Integer> instructionIds;
}
