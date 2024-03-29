package com.steel.product.application.dto.quality;

import java.util.Date;

import lombok.Data;

@Data
public class QualityTemplateResponse {

	private Integer templateId;
	
	private String templateName;

	private String stageName;

	private String templateDetails;

	private Integer processId;
	
	private String rustObserved;
	
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
