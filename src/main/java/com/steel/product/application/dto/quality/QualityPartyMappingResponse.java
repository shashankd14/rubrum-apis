package com.steel.product.application.dto.quality;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QualityPartyMappingResponse {

	private Integer id;

	private Integer partyId;

	private String partyName;

	private String templateName;

	private Integer templateId;

	private Integer endUserTagId;

	private Integer matGradeId;

	private Integer userId;

	private BigDecimal thickness;
}
