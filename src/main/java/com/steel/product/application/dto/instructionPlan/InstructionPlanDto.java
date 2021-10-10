package com.steel.product.application.dto.instructionPlan;

import lombok.*;


@Getter
@Setter
public class InstructionPlanDto {

    private Float targetWeight;
    private Float length;
    private Integer noOfParts;
    private Boolean isEqual;
    private String instructionPlanId;
    private Integer createdBy;
    private Integer updatedBy;

}
