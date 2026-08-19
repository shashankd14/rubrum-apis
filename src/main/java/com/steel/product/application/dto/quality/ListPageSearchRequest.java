package com.steel.product.application.dto.quality;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

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

	private String soChildMmid;

	private String soNo;

	private String sortColumn;

	private String sortOrder;

	private List<Integer> subgradeList = new ArrayList<>();

	private List<String> status = new ArrayList<>();

	private List<String> zohoStatus = new ArrayList<>();

	private List<String> warehouseList = new ArrayList<>();

	private int planId;

	private int mappingFlag;

	private int location;

	private String branchId;

	private String batchNo;

	private String inventoryType;

	private String allocationType;

	private String pdfGenerationPart;

	private String filterStatus;

	private int fromCoilAge;

	private int toCoilAge;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date fromDate;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date toDate;

}
