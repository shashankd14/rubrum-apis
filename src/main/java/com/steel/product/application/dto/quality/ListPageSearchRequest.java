package com.steel.product.application.dto.quality;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListPageSearchRequest {

	private Integer pageNo;

	private Integer pageSize;

	private String searchText;

	private Integer partyId;
}
