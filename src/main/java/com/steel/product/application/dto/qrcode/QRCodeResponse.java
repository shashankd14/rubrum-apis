package com.steel.product.application.dto.qrcode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QRCodeResponse {
	
	private String coilNo;

	private String customerBatchNo;

	private String materialDesc;

	private String materialGrade;

	private Double fthickness;

	private Double netWeight;

	private Double fwidth;

	private Double grossWeight;
	
}
