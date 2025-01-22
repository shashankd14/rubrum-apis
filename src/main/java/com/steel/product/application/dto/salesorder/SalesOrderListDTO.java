package com.steel.product.application.dto.salesorder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesOrderListDTO {

	private Integer instructionId;

	private Integer inwardEntryId;

	private String coilNo;

	private String customerBatchNo;

	private String partyName;

	private String materialGrade;

	private String packetStatus;

	private String materialDesc;

	private Float fthickness;

	private Float fwidth;

	private Float flenghth;

	private Float fweight;

	private Integer partyId;
	
	

}
