package com.steel.product.application.dto.qrcode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QRCodeResponse {

	private String coilNo;

	private Integer instructionId;

	private String instructionDate;

	private String coilBatchNo;

	private String customerBatchNo;

	private String customerInvoiceNo;

	private String partyName;

	private String materialDesc;

	private String materialGrade;

	private String endUserTag;

	private String fthickness;

	private String fweight;

	private String fwidth;

	private String flength;

	private String grossWeight;

	private String actuallength;

	private String actualweight;

	private String actualwidth;

	private String receivedDate;

	private String finishedDate;

	private String motherCoilNo;

	private Integer plannedNoOfPieces;

	private Integer processId;

	private Boolean isSlitAndCut;
	
}
