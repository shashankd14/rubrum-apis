package com.steel.product.application.dto.quality;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class QualityTemplateMainResponse {

	private Integer id;
	
	private String templateName;

	private List<QualityTemplateResponse> stageDetails = new ArrayList<QualityTemplateResponse>();

}
