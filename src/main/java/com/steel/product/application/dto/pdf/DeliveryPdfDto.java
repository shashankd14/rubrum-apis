package com.steel.product.application.dto.pdf;

import java.util.List;

public class DeliveryPdfDto {

    private List<Integer> instructionIds;
    
    private Integer packingRateId;

    public List<Integer> getInstructionIds() {
        return instructionIds;
    }

    public void setInstructionIds(List<Integer> instructionIds) {
        this.instructionIds = instructionIds;
    }

	public Integer getPackingRateId() {
		return packingRateId;
	}

	public void setPackingRateId(Integer packingRateId) {
		this.packingRateId = packingRateId;
	}
    
    
}
