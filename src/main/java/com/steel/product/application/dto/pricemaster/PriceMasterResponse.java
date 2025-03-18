package com.steel.product.application.dto.pricemaster;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class PriceMasterResponse {

	private Integer id;

	private Integer partyId;

	private Integer processId;

	private Integer gradeId;
	
	private Integer productId;
	
	private Integer locationId;
	
	private Integer locationName;

	private String partyName;

	private String processName;

	private String gradeName;
	
	private String productName;	

	private BigDecimal thicknessFrom;

	private BigDecimal thicknessTo;

	private BigDecimal price;

}
