package com.steel.product.application.controller;

import com.steel.product.application.dto.report.StockReportRequest;
import com.steel.product.application.service.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/report")
@CrossOrigin
public class ReportsController {

    private final ReportsService reportsService;

    @Autowired
    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @PostMapping("")
    public String generateAndMailStockReport(@RequestBody StockReportRequest stockReportRequest){
        try {
            reportsService.generateAndMailStockReport(stockReportRequest);
            return "Sent email ok !!";
        }catch (Exception e){
            e.printStackTrace();
        }
        return "Error in sending mail";
    }

}
