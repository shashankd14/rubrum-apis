package com.steel.product.application.dto.quality;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KQPPartyMappingRequest {

	private Integer id;

	private Integer kqpId;

	private Integer endUserTagId;

	private Integer matGradeId;

	private Integer userId;

	private BigDecimal thickness;

	private BigDecimal width;

	private BigDecimal length;

	private List<Integer> partyIdList;

}
