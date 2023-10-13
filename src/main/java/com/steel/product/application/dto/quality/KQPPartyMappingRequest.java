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

	private Integer userId;

	private List<Integer> endUserTagIdList;

	private List<BigDecimal> thicknessList;

	private List<Integer> matGradeIdList;
	
	private List<BigDecimal> widthList;

	private List<BigDecimal> lengthList;

	private List<Integer> partyIdList;

}
