package com.steel.product.trading.controller;

import com.steel.product.trading.service.SalesHistoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Tag(name = "Sales History", description = "Last selling price per material")
public class SalesHistoryController {

	private final SalesHistoryService salesHistory;

	public SalesHistoryController(SalesHistoryService salesHistory) {
		this.salesHistory = salesHistory;
	}

	public static class LastPriceRequest {
		private List<Integer> itemIds;
		private Integer customerId;
		private Integer excludeEnquiryId;

		public List<Integer> getItemIds() { return itemIds; }
		public void setItemIds(List<Integer> itemIds) { this.itemIds = itemIds; }
		public Integer getCustomerId() { return customerId; }
		public void setCustomerId(Integer customerId) { this.customerId = customerId; }
		public Integer getExcludeEnquiryId() { return excludeEnquiryId; }
		public void setExcludeEnquiryId(Integer excludeEnquiryId) { this.excludeEnquiryId = excludeEnquiryId; }
	}

	@PostMapping(value = "/sales/last-price", produces = "application/json")
	public List<Map<String, Object>> lastPrice(@RequestBody LastPriceRequest request) {
		return salesHistory.lastSellingPrices(request.getItemIds(), request.getCustomerId(), request.getExcludeEnquiryId());
	}
}
