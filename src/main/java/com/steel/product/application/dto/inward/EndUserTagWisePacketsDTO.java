package com.steel.product.application.dto.inward;

import lombok.Data;

@Data
public class EndUserTagWisePacketsDTO {

	private String coilNumber;

	private String customerBatchId;

	private String materialDesc;
	
	private String materialGrade;

	private float thickness;

	private float width;

	private float length;

	private String classificationTag;

	private String endUserTagName;

	private String inwardStatus;

	private String packetStatus;

}
