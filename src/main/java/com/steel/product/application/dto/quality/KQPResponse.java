package com.steel.product.application.dto.quality;

import java.util.Date;

import lombok.Data;

@Data
public class KQPResponse {

	private Integer kqpId;

	private String kqpName;

	private String kqpDesc;

	private String kqpSummary;

	private String stageName;

	private Integer createdBy;

	private Integer updatedBy;

	private Date createdOn;

	private Date updatedOn;

}