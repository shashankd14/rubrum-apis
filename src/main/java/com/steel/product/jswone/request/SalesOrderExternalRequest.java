package com.steel.product.jswone.request;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SalesOrderExternalRequest {

	@JsonProperty("SalesOrder_Details")
	private SalesOrderDetails SalesOrder_Details;

	@JsonProperty("line_items")
	private List<SalesOrderLineItem> line_items;

//    private BillingAddress billing_address;

//    private ShippingAddress shipping_address;

	private Integer payment_terms;

	private String payment_terms_label;

	@JsonProperty("custom_fields")
	private SalesOrderCustomFields custom_fields;
}