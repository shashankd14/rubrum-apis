package com.steel.product.application.dto.instruction;

import java.util.List;

public class InstructionFinishDto {

    private List<InstructionRequestDto> instructionDtos;

    private boolean isFinishTask;

    private boolean isCoilFinished;

    private String coilNumber;

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

    public boolean getIsCoilFinished() {
        return isCoilFinished;
    }

    public void setCoilFinished(boolean coilFinished) {
        isCoilFinished = coilFinished;
    }

    public boolean isFinishTask() {
        return isFinishTask;
    }

    public String getCoilNumber() {
        return coilNumber;
    }

    public void setCoilNumber(String coilNumber) {
        this.coilNumber = coilNumber;
    }
}
