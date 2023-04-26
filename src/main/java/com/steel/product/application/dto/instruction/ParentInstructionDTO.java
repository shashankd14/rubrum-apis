package com.steel.product.application.dto.instruction;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParentInstructionDTO {

	private List<Integer> instructionIds;

	private Integer groupId;
}
