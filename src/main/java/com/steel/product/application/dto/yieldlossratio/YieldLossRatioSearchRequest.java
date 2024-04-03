package com.steel.product.application.dto.yieldlossratio;

import com.steel.product.trading.request.BaseRequest;
import lombok.Data;

@Data
public class YieldLossRatioSearchRequest extends BaseRequest{

	private Integer ylrId;

	private Integer partyId;

	private Integer processId;

	private Integer pageNo;

	private Integer pageSize;

}
