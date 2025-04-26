package com.steel.product.application.dto.delivery;

public class DeliveryItemDetails {

    private int instructionId;

    private String remarks;

    private Float weight;
    
    private Float additionalWeight;

    public int getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(int instructionId) {
        this.instructionId = instructionId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

	public Float getAdditionalWeight() {
		return additionalWeight;
	}

	public void setAdditionalWeight(Float additionalWeight) {
		this.additionalWeight = additionalWeight;
	}
    
    
    
}
