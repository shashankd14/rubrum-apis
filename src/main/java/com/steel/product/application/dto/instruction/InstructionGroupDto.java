package com.steel.product.application.dto.instruction;

import java.util.List;

public class InstructionGroupDto {

    List<Integer> instructionId;
    int count;

    public List<Integer> getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(List<Integer> instructionId) {
        this.instructionId = instructionId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
