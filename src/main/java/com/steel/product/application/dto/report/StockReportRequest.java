package com.steel.product.application.dto.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockReportRequest {

    private Integer partyId;
    private String reportName;
}
