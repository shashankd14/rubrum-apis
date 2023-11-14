package com.steel.product.application.dto.lamination;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class LaminationChargesResponse {

	private Integer laminationId;

	private Integer partyId;

	private Integer laminationDetailsId;

	private String partyName;

	private String laminationDetailsDesc;
	
	private BigDecimal charges;

	private Integer createdBy;

	private Integer updatedBy;

	private Date createdOn;

	private Date updatedOn;

	
}
