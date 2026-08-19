package com.steel.product.application.dto.pdf;

import java.util.List;

public class DeliveryPdfDto {

    private List<Integer> instructionIds;
    
    private Integer packingRateId;
    
    private Integer laminationId;
    
    private String deliveryType;
    
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

	public Integer getLaminationId() {
		return laminationId;
	}

	public void setLaminationId(Integer laminationId) {
		this.laminationId = laminationId;
	}

	public String getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}
    
    
}
