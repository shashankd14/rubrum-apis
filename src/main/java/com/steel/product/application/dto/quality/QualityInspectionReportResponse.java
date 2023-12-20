package com.steel.product.application.dto.quality;

import lombok.Data;
import java.util.Date;

@Data
public class QualityInspectionReportResponse {

	private Integer qirId;

	private Integer templateId;

	private String coilNo;

	private String planId;

	private String deliveryChalanNo;

	private String stageName;

	private Integer processId;

	private String templateDetails;

	private String rustObserved;

	private String coilBend;

	private String safetyIssues;

	private String waterExposure;

	private String wireRopeDamages;

	private String packingIntact;

	private String improperStorage;

	private String strapping;

	private String weighmentSlip;

	private String weighment;

	private String ackReceipt;

	private String unloadingImproper;
	
	private String rustObservedPreSingedURL;
	
	private String coilBendPreSingedURL;
	
	private String safetyIssuesPreSingedURL;
	
	private String waterExposurePreSingedURL;
	
	private String wireRopeDamagesPreSingedURL;
	
	private String packingIntactPreSingedURL;
	
	private String improperStoragePreSingedURL;
	
	private String strappingPreSingedURL;
	
	private String weighmentSlipPreSingedURL;
	
	private String weighmentPreSingedURL;
	
	private String ackReceiptPreSingedURL;
	
	private String unloadingImproperPreSingedURL;

	private Integer createdBy;

	private Integer updatedBy;

	private Date createdOn;

	private Date updatedOn;

}