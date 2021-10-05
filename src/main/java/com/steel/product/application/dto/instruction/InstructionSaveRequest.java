package com.steel.product.application.dto.instruction;

import com.steel.product.application.dto.instructionPlan.InstructionPlanDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InstructionSaveRequest {

    private InstructionPlanDto instructionPlanDto;
    private List<InstructionRequestDto> instructionRequestDTOs;

}
