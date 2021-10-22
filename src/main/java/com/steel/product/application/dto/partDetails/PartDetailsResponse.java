package com.steel.product.application.dto.partDetails;

import com.steel.product.application.dto.instruction.InstructionResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class PartDetailsResponse {

    private Long id;
    private Float targetWeight;
    private Float length;
    private String partDetailsId;
    private Integer createdBy;
    private Integer updatedBy;
    private Set<InstructionResponseDto> instructions;
}
