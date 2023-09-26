package com.steel.product.application.dto.quality;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KQPRequest {

	private Integer kqpId;

	private String kqpName;

	private String kqpDesc;

	private String kqpSummary;

	private String stageName;

}
