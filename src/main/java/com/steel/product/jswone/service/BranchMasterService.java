package com.steel.product.jswone.service;

import java.util.List;

import com.steel.product.jswone.request.SearchRequest;
import com.steel.product.jswone.response.BranchMasterDTO;

public interface BranchMasterService {

	List<BranchMasterDTO> getList(SearchRequest searchPageRequest);

}
