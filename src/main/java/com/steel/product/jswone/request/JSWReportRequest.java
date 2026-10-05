package com.steel.product.jswone.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import java.time.LocalDate;

/**
 * Request body for the Packet TAT export. Both dates are inclusive and filter
 * on product_instruction.createdon (job work release). If both are omitted, the
 * range defaults to the 1st of the current month through today.
 */
@Data
public class JSWReportRequest {

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate fromDate;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate toDate;

	private String reportType;

	private int locationId;

}
