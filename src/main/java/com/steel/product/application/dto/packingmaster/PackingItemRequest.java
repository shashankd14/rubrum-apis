package com.steel.product.application.dto.packingmaster;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PackingItemRequest  {

	private Integer id;

	private String packingItemId;

	private String description;

	private String unit;

}
