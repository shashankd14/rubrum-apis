package com.steel.product.trading.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatesDTO{

	private String stateName;

	public StatesDTO(String stateName) {
		super();
		this.stateName = stateName;
	}

}