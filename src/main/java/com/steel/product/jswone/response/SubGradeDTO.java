package com.steel.product.jswone.response;

import lombok.Data;

@Data
public class SubGradeDTO {

	private int subgradeId;

	private String subgradeName;

	public SubGradeDTO(int subgradeId, String subgradeName) {
		super();
		this.subgradeId = subgradeId;
		this.subgradeName = subgradeName;
	}

}