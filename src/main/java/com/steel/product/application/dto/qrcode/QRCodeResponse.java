package com.steel.product.application.dto.qrcode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QRCodeResponse {
	
	private String coilNo;
	
	private Integer instructionId;

	private String customerBatchNo;

	private String partyName;

	private String materialDesc;

	private String materialGrade;

	private String endUserTag;

	private String fthickness;

	private String netWeight;

	private String fwidth;

	private String flength;

	private String grossWeight;
	
}
