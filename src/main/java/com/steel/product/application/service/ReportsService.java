package com.steel.product.application.service;

import java.util.List;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.steel.product.application.dto.report.StockReportRequest;
import com.steel.product.application.entity.StockSummaryReportViewEntity;

public interface ReportsService {

	String generateAndMailStockReport(StockReportRequest stockReportRequest);

	boolean createFGReport(int partyId, String strDate, MimeMessageHelper helper);

	boolean createWIPReport(int partyId, String strDate, MimeMessageHelper helper);

	boolean createStockSummaryReport(int partyId, String strDate, MimeMessageHelper helper);

	List<StockSummaryReportViewEntity> reconcileReport(String coilNumber);

	boolean createOutwardReport(Integer partyId, String strDate, MimeMessageHelper helper);

	boolean createInwardReport(int partyId, String strDate, MimeMessageHelper helper);

	//boolean createStockReport(int partyId, String strDate, MimeMessageHelper helper);
	
	//boolean createStockDetailsReport(int partyId, String strDate, MimeMessageHelper helper);

	//boolean createRMReport(int partyId, String strDate, MimeMessageHelper helper);

	//boolean createInwardMonthlyReport(Integer partyId, MimeMessageHelper helper, Integer month, Map<Integer, String> months, Integer year);

	//boolean createStockMonthlyReport(Integer partyId, MimeMessageHelper helper, Integer month,Map<Integer, String> months);
	
	//boolean createProcessingMonthlyReport(Integer partyId, MimeMessageHelper helper, Integer month, Map<Integer, String> months, Integer year);

	//boolean createFinishingMonthlyReport(Integer getnPartyId, MimeMessageHelper helper, Integer month, Map<Integer, String> months, Integer year);

	//boolean createEndUserTagWiseFGReport(Integer getnPartyId, String strDate, MimeMessageHelper helper);

	//boolean createMonthwisePlanTrackerReport(int partyId, String strDate, MimeMessageHelper helper);

	//boolean createOutwardMonthlyReport(Integer getnPartyId, MimeMessageHelper helper, Integer month, Map<Integer, String> months, Integer year);

	//boolean createWIPReportEndusertagwise(Integer getnPartyId, String strDate, MimeMessageHelper helper);

}
