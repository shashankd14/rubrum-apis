package com.steel.product.application.dto;

import java.util.List;

public class InstructionFinishDto {

    private List<InstructionDto> instructionDtos;

    private boolean isFinishTask;

    public List<InstructionDto> getInstructionDtos() {
        return instructionDtos;
    }

    public void setInstructionDtos(List<InstructionDto> instructionDtos) {
        this.instructionDtos = instructionDtos;
    }

    public boolean getIsFinishTask() {
        return isFinishTask;
    }

    public void setFinishTask(boolean finishTask) {
        isFinishTask = finishTask;
    }
}
