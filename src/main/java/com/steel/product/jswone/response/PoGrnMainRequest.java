package com.steel.product.jswone.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PoGrnMainRequest {

	private String po_number;
	private String bill_number;
	private String reference_number;
	private String date;

	List<PoGrnCustomType> custom_fields = new ArrayList<>();

	List<PoGrnLineItem> line_items = new ArrayList<>();

}
