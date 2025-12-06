package com.steel.product.jswone.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class BranchMasterDTO {

	private Integer branchId;

	private String branchName;

	List<BranchMasterListRespose> locationList = new ArrayList<>();

}