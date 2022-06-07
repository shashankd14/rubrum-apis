package com.steel.product.application.dto.pdf;

import com.steel.product.application.dto.BaseReq;

public class PdfDto extends BaseReq {

    private Integer inwardId;
    private Integer processId;

    public Integer getInwardId() {
        return inwardId;
    }

    public void setInwardId(Integer inwardId) {
        this.inwardId = inwardId;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }
}
