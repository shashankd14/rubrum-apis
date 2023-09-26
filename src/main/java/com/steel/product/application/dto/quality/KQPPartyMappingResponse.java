package com.steel.product.application.dto.quality;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KQPPartyMappingResponse {

	private Integer id;

	private Integer partyId;

	private String partyName;

	private String kqpName;

	private Integer kqpId;

	private Integer endUserTagId;

	private Integer matGradeId;

	private Integer userId;

	private BigDecimal thickness;

	private BigDecimal width;

	private BigDecimal length;
}
