package com.steel.product.application.dto.quality;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QualityPartyMappingRequest {

	private Integer id;

	private Integer templateId;

	private Integer endUserTagId;

	private Integer matGradeId;

	private Integer userId;

	private BigDecimal thickness;

	private List<Integer> partyIdList;

}
