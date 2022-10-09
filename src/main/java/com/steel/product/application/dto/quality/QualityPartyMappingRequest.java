package com.steel.product.application.dto.quality;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QualityPartyMappingRequest {

	private Integer id;

	private Integer partyId;

	private List<Integer> templateIdList;

}
