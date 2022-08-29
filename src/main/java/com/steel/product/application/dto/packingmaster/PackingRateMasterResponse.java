package com.steel.product.application.dto.packingmaster;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class PackingRateMasterResponse {

	private Integer packingRateId;
	
	private Integer partyId;
	
	private String partyName;

	private Integer packingBucketId;

	private String packingBucketName;

	private BigDecimal packingRate;

	private String packingRateDesc;

	private Integer createdBy;

	private Integer updatedBy;

	private Date createdOn;

	private Date updatedOn;

}
