package com.steel.product.application.service;

import com.steel.product.application.dao.InstructionPlanRepository;
import com.steel.product.application.entity.InstructionPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class InstructionPlanServiceImpl implements InstructionPlanService {

    private InstructionPlanRepository instructionPlanRepository;

    @Autowired
    public InstructionPlanServiceImpl(InstructionPlanRepository instructionPlanRepository) {
        this.instructionPlanRepository = instructionPlanRepository;
    }


    @Override
    public List<InstructionPlan> saveAll(List<InstructionPlan> instructionPlans) {
        return instructionPlanRepository.saveAll(instructionPlans);
    }

    @Override
    public List<InstructionPlan> saveAll(Set<InstructionPlan> instructionPlans) {
        return instructionPlanRepository.saveAll(instructionPlans);
    }

    @Override
    public InstructionPlan save(InstructionPlan instructionPlan) {
        return instructionPlanRepository.save(instructionPlan);
    }

    @Override
    public InstructionPlan getByInstructionPlanId(String instructionPlanId) {
        Optional<InstructionPlan> instructionPlanOptional = instructionPlanRepository.findByInstructionPlanId(instructionPlanId);
        if (!instructionPlanOptional.isPresent()) {
            throw new RuntimeException("Instruction plan with plan id " + instructionPlanId + " not found");
        }
        return instructionPlanOptional.get();
    }
}
