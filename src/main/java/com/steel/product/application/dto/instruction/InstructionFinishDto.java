package com.steel.product.application.dto.instruction;

import java.util.List;

public class InstructionFinishDto {

    private List<InstructionRequestDto> instructionDtos;

    private String taskType;

    public List<InstructionRequestDto> getInstructionDtos() {
        return instructionDtos;
    }

    public void setInstructionDtos(List<InstructionRequestDto> InstructionRequestDtos) {
        this.instructionDtos = InstructionRequestDtos;
    }

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}


}
