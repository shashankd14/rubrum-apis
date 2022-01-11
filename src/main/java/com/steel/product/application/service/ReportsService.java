package com.steel.product.application.service;

import com.steel.product.application.dto.report.StockReportRequest;

import javax.mail.MessagingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

public interface ReportsService {

    String generateAndMailStockReport(StockReportRequest stockReportRequest);

//    void uploadCSV() throws IOException, ParseException;
}
