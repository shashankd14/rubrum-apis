package com.steel.product.application.dto.yieldlossratio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.steel.product.trading.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YieldLossRatioRequest extends BaseRequest {

	private Integer ylrId;

	private List<Integer> partyIdList;
	
	private List<Integer> processIdList;

	private BigDecimal lossRatioPercentageFrom;

	private BigDecimal lossRatioPercentageTo;

	private String comments;

	private List<YieldLossRatioRequest> ratioList = new ArrayList<>();

}
