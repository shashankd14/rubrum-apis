package com.steel.product.application.dto.instruction;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class InstructionFinishDto {

	private List<InstructionRequestDto> instructionDtos;

	private String taskType;

	private BigDecimal actualYieldLossRatio;

}
