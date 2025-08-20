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

	private Integer soId;

	private String sortColumn;

	private String sortOrder;

	private int planId;

	private int mappingFlag;

	private int location;

	private String batchNo;

}
