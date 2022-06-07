package com.steel.product.application.dto.pdf;

import java.util.List;

import com.steel.product.application.dto.BaseReq;

public class DeliveryPdfDto extends BaseReq {

    private List<Integer> instructionIds;

    public List<Integer> getInstructionIds() {
        return instructionIds;
    }

    public void setInstructionIds(List<Integer> instructionIds) {
        this.instructionIds = instructionIds;
    }
}
