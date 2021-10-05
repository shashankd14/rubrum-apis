package com.steel.product.application.service;

import com.steel.product.application.entity.InstructionPlan;
import org.springframework.stereotype.Service;

public interface InstructionPlanService {

    public InstructionPlan addInstructionPlan(InstructionPlan instructionPlan);

    public InstructionPlan getInstructionPlan(Long instructionPlanId);
}
