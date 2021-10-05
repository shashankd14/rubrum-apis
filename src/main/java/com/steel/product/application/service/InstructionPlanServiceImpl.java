package com.steel.product.application.service;

import com.steel.product.application.dao.InstructionPlanRepository;
import com.steel.product.application.entity.InstructionPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InstructionPlanServiceImpl implements InstructionPlanService {

    private InstructionPlanRepository instructionPlanRepository;

    @Autowired
    public InstructionPlanServiceImpl(InstructionPlanRepository instructionPlanRepository) {
        this.instructionPlanRepository = instructionPlanRepository;
    }

    @Override
    public InstructionPlan addInstructionPlan(InstructionPlan instructionPlan) {
        String instructionPlanId = "DOC_" + System.nanoTime();
        instructionPlan.setInstructionPlanId(instructionPlanId);
        return instructionPlanRepository.save(instructionPlan);
    }

    @Override
    public InstructionPlan getInstructionPlan(Long instructionPlanId) {
        Optional<InstructionPlan> instructionPlanOptional = instructionPlanRepository.findById(instructionPlanId);
        if (!instructionPlanOptional.isPresent()) {
            throw new RuntimeException("No instruction plan found with id " + instructionPlanId);
        }
        return instructionPlanOptional.get();
    }
}
