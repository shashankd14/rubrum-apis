package com.steel.product.application.dto.inward;

import lombok.Data;

@Data
public class SearchListPageRequest {

	private Integer pageNo;

	private Integer pageSize;

	private String partyId;

	private String searchText;

	private String sortColumn;

	private String sortOrder;
}
