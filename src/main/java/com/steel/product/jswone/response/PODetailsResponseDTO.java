package com.steel.product.jswone.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PODetailsResponseDTO {

	private String sku;

	private BigDecimal quantity;

	private BigDecimal quantity_billed;

	private String name;

}
