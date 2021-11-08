package com.steel.product.application.mapper;

import com.steel.product.application.entity.Instruction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CutInstruction {

//    private Double plannedWeight;
//    private Double plannedWidth;
//    private Double plannedNoOfPieces;
//    private Double

    private Long weightCount;
    private Instruction instruction;

    public CutInstruction(Instruction instruction,Long weightCount) {
        this.weightCount = weightCount;
        this.instruction = instruction;
    }
}
