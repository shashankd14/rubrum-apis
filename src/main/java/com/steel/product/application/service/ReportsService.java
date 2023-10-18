package com.steel.product.application.service;

import java.util.List;
import java.util.Map;

import org.springframework.mail.javamail.MimeMessageHelper;

import com.steel.product.application.dto.report.StockReportRequest;
import com.steel.product.application.entity.StockSummaryReportViewEntity;

public interface ReportsService {

	String generateAndMailStockReport(StockReportRequest stockReportRequest);

	boolean createStockReport(int partyId, String strDate, MimeMessageHelper helper);

	boolean createFGReport(int partyId, String strDate, MimeMessageHelper helper);

	boolean createWIPReport(int partyId, String strDate, MimeMessageHelper helper);

	boolean createStockSummaryReport(int partyId, String strDate, MimeMessageHelper helper);

	List<StockSummaryReportViewEntity> reconcileReport(String coilNumber);

	boolean createRMReport(int partyId, String strDate, MimeMessageHelper helper);

	boolean createInwardMonthlyReport(Integer partyId, MimeMessageHelper helper, Integer month,
			Map<Integer, String> months);

	boolean createStockMonthlyReport(Integer partyId, MimeMessageHelper helper, Integer month,
			Map<Integer, String> months);
}
