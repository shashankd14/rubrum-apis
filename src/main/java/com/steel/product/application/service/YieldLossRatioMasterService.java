package com.steel.product.application.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import com.steel.product.application.dto.yieldlossratio.YieldLossRatioRequest;
import com.steel.product.application.dto.yieldlossratio.YieldLossRatioSearchRequest;
import com.steel.product.trading.request.DeleteRequest;

public interface YieldLossRatioMasterService {

	ResponseEntity<Object> save(List<YieldLossRatioRequest> yieldLossRatioRequest);

	Page<Object[]> getAll(YieldLossRatioSearchRequest request);

	ResponseEntity<Object> delete(DeleteRequest deleteRequest);

}
