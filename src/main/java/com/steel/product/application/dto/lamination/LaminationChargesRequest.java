package com.steel.product.application.dto.lamination;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LaminationChargesRequest  {

	private Integer laminationId;

	private Integer partyId;

	private Integer laminationDetailsId;

	private BigDecimal charges;

}
