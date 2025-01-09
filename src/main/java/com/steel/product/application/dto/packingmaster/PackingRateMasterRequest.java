package com.steel.product.application.dto.packingmaster;

import java.math.BigDecimal;

import com.steel.product.trading.request.BaseRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PackingRateMasterRequest extends BaseRequest  {

	private Integer packingRateId;

	private Integer partyId;

	private Integer packingBucketId;

	private BigDecimal packingRate;

	private String packingRateDesc;

}
