package com.steel.product.application.dto.instruction;

import com.steel.product.application.dto.partDetails.partDetailsRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InstructionSaveRequestDto {

	private partDetailsRequest partDetailsRequest;
	
	private List<InstructionRequestDto> instructionRequestDTOs;
	
	private ParentInstructionDTO parentInstructionIds;

}
