package com.steel.product.application.service;

import com.steel.product.application.dto.instructionPlan.InstructionPlanDto;
import com.steel.product.application.entity.InstructionPlan;
import org.springframework.stereotype.Service;

@Service
public interface InstructionPlanService {

    public InstructionPlan addInstructionPlan(InstructionPlanDto instructionPlanDto);

    public InstructionPlan getInstructionPlan(Integer instructionPlanId);
}
