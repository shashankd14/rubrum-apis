package com.steel.product.application.dto.instruction;

import java.util.List;

public class InstructionFinishDto {

    private List<InstructionRequestDto> instructionDtos;

    private boolean isFinishTask;

    public List<InstructionRequestDto> getInstructionDtos() {
        return instructionDtos;
    }

    public void setInstructionDtos(List<InstructionRequestDto> InstructionRequestDtos) {
        this.instructionDtos = InstructionRequestDtos;
    }

    public boolean getIsFinishTask() {
        return isFinishTask;
    }

    public void setFinishTask(boolean finishTask) {
        isFinishTask = finishTask;
    }

}
