package com.steel.product.application.service;

import org.springframework.mail.javamail.MimeMessageHelper;

import com.steel.product.application.dto.report.StockReportRequest;

public interface ReportsService {

	String generateAndMailStockReport(StockReportRequest stockReportRequest);

	boolean createStockReport(int partyId, String strDate, MimeMessageHelper helper);

	boolean createFGReport(int partyId, String strDate, MimeMessageHelper helper);
	
	boolean createWIPReport(int partyId, String strDate, MimeMessageHelper helper);
	
	boolean createStockSummaryReport(int partyId, String strDate, MimeMessageHelper helper);
}
