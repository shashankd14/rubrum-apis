package com.steel.product.application.dto.instructionPlan;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstructionPlanDto {
    private Float targetWeight;
    private Integer noOfParts;
    private Boolean isEqual;
    private Integer processId;
    private Integer createdBy;
    private Integer updatedBy;
}
