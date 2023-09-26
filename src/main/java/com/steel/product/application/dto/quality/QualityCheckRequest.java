package com.steel.product.application.dto.quality;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QualityCheckRequest {

	private Integer partyId;

	private Integer templateId;

	private Integer inwardId;

	private Integer instructionId;

}
