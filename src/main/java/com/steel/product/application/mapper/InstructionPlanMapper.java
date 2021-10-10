package com.steel.product.application.mapper;

import com.steel.product.application.dto.instructionPlan.InstructionPlanDto;
import com.steel.product.application.entity.InstructionPlan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InstructionPlanMapper {

    InstructionPlanDto toDto(InstructionPlan instructionPlan);

    InstructionPlan toEntity(InstructionPlanDto instructionPlanDto);
}
