package com.steel.product.application.dto.lamination;

import java.math.BigDecimal;
import com.steel.product.trading.request.BaseRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LaminationChargesRequest extends BaseRequest{

	private Integer laminationId;

	private Integer partyId;

	private Integer laminationDetailsId;

	private BigDecimal charges;

}
