package com.steel.product.application.service;

import com.steel.product.application.dao.InstructionPlanRepository;
import com.steel.product.application.dto.instructionPlan.InstructionPlanDto;
import com.steel.product.application.entity.InstructionPlan;
import org.springframework.beans.factory.annotation.Autowired;

public class InstructionPlanServiceImpl implements InstructionPlanService {

    private InstructionPlanRepository instructionPlanRepository;
    private ProcessService processService;

    @Autowired
    public InstructionPlanServiceImpl(InstructionPlanRepository instructionPlanRepository, ProcessService processService) {
        this.instructionPlanRepository = instructionPlanRepository;
        this.processService = processService;
    }

    @Override
    public InstructionPlan addInstructionPlan(InstructionPlanDto instructionPlanDto) {

        return null;
    }

    @Override
    public InstructionPlan getInstructionPlan(Integer instructionPlanId) {
        return null;
    }
}
