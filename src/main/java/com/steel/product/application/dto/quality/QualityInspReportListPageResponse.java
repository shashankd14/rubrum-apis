package com.steel.product.application.dto.quality;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QualityInspReportListPageResponse {
	
	private Integer inwardEntryId;

	private String coilNo;

	private String customerBatchNo;

	private String partyName;

	private String planId;

	private String planDate;

	private String materialGrade;

	private String materialDesc;

	private Float fthickness;

	private Float fwidth;

	private Float targetWeight;

	private int nPartyId;

	private Integer qirId;
	
}
