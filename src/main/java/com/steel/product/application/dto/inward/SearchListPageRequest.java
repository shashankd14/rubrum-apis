package com.steel.product.application.dto.inward;

import lombok.Data;

@Data
public class SearchListPageRequest {

	private Integer pageNo;

	private Integer pageSize;

	private int status;

	private String partyId;

	private String loginType;

	private String searchText;

	private String sortColumn;

	private String sortOrder;

	private int materialFilterValue;

	private int gradeFilterValue;

	private int subgradeFilterValue;

	private int brandFilterValue;

	private float thicknessMinValue;

	private float thicknessMaxValue;

	private float lengthMaxValue;

	private float lengthMinValue;

	private float widthMaxValue;

	private float widthMinValue;

	private int ageingMinValue;

	private int ageingMaxValue;

	private String batchNoFilter;

	private String scInwardIdFilter;

}
