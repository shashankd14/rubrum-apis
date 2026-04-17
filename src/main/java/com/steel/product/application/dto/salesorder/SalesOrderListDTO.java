package com.steel.product.application.dto.salesorder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesOrderListDTO {

	private Integer instructionId;

	private int soAllocationId;

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

	private String materialDetails;

	private String subGrade;

	private String brand;

	private String mmid;

	private Date instructionDate;

	private String diagonal;

	private String edgeBurr;

	private String width;

	private Float fthickness;

	private Float fwidth;

	private Float flenghth;

	private Float fweight;

	private Float allocatedQty;

	private Integer partyId;

	private int soChildId;

	private int plannedNoofPieces;

	private List<String> mappedSOList = new ArrayList<>();

}
