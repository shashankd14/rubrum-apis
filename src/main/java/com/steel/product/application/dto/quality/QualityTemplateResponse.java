package com.steel.product.application.dto.quality;

import java.util.Date;

import lombok.Data;

@Data
public class QualityTemplateResponse {

	private Integer templateId;

	private String stageName;

	private Integer processId;

	private String fieldDetails;

	private String remarks;

	private Integer createdBy;

	private Integer updatedBy;

	private Date createdOn;

	private Date updatedOn;

}
