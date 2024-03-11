package com.steel.product.application.entity;

import com.steel.product.application.dto.instruction.InstructionResponseDto;
import java.util.*;

public class MyInstructionIdComp implements Comparator<InstructionResponseDto> {

	@Override
	public int compare(InstructionResponseDto e1, InstructionResponseDto e2) {
		if (e1.getInstructionId() > e2.getInstructionId()) {
			return 1;
		} else {
			return -1;
		}
	}
}
