package com.steel.product.application.dto.instructionPlan;

public class InstructionPlanDto {
    private Float targetWeight;
    private Integer noOfParts;
    private Boolean isEqual;
    private Integer processId;

    public Float getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(Float targetWeight) {
        this.targetWeight = targetWeight;
    }

    public Integer getNoOfParts() {
        return noOfParts;
    }

    public void setNoOfParts(Integer noOfParts) {
        this.noOfParts = noOfParts;
    }

    public Boolean getEqual() {
        return isEqual;
    }

    public void setEqual(Boolean equal) {
        isEqual = equal;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }
}
