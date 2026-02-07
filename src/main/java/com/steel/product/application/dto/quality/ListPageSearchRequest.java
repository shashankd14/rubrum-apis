package com.steel.product.application.dto.quality;

import java.util.ArrayList;
import java.util.List;

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

	private String soNo;

	private String sortColumn;

	private String sortOrder;

	private List<String> status = new ArrayList<>();

	private int planId;

	private int mappingFlag;

	private int location;

	private int branchId;

	private String batchNo;

	private String inventoryType;

	private String allocationType;

}
