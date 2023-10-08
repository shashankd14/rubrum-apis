package com.steel.product.application.dto.pricemaster;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriceMasterListPageRequest {

	private Integer pageNo;
	
	private Integer pageSize;

	private String searchText;

	private BigDecimal thicknessRange;
}
