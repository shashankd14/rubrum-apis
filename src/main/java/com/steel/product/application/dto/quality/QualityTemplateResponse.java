package com.steel.product.application.dto.quality;

import java.util.Date;

import lombok.Data;

@Data
public class QualityTemplateResponse {

	private Integer templateId;

	private String stageName;

	private String templateDetails;

	private Integer processId;

	private Integer createdBy;

	private Integer updatedBy;

	private Date createdOn;

	private Date updatedOn;

}
