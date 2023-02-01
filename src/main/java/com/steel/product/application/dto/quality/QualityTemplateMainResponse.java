package com.steel.product.application.dto.quality;

import lombok.Data;

@Data
public class QualityTemplateMainResponse {
	
	private Integer templateId;

	private String templateName;

	private String stageName;

	private String processId;

	private String templateDetails;
}
