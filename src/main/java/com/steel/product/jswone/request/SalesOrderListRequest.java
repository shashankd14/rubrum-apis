package com.steel.product.jswone.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesOrderListRequest {

	private Integer instructionId;

	private Integer inwardEntryId;

	private String coilNo;

	private String coilSKU;

	private String processing;

	private String finalProcessingSKU;

	private String packingMode;

	private String specilaInstructions;

	private String soNumber;

	private Integer customerCodeId;

	private String customerBatchNo;

	private String partyName;

	private String materialGrade;

	private String packetStatus;

	private String materialDesc;

	private String diagonal;

	private String edgeBurr;

	private Float fthickness;

	private Float fwidth;

	private Float flenghth;

	private Float fweight;
	
	private Integer partyId;

	private Integer plannedNoofPieces;
	
	

}
