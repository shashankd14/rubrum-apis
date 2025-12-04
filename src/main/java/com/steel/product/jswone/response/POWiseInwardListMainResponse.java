package com.steel.product.jswone.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class POWiseInwardListMainResponse {

	private String totalValueOfGoods;

	private String poReference;

	private String poId;

	List<POWiseInwardListResponse> inwardList = new ArrayList<>();

}
