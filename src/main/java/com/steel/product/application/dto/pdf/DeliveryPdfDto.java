package com.steel.product.application.dto.pdf;

import java.util.List;

public class DeliveryPdfDto {

    private List<Integer> instructionIds;

    public List<Integer> getInstructionIds() {
        return instructionIds;
    }

    public void setInstructionIds(List<Integer> instructionIds) {
        this.instructionIds = instructionIds;
    }
}
