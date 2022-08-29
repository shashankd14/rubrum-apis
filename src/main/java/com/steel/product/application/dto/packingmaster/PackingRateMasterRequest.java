package com.steel.product.application.dto.packingmaster;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PackingRateMasterRequest {

	private Integer packingRateId;

	private Integer partyId;

	private Integer packingBucketId;

	private BigDecimal packingRate;

	private String packingRateDesc;

}
