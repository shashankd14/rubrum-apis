package com.steel.product.application.dto.quality;

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
}
