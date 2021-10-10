package com.steel.product.application.service;

import com.steel.product.application.entity.InstructionPlan;

import java.util.List;
import java.util.Set;

public interface InstructionPlanService {

    public List<InstructionPlan> saveAll(List<InstructionPlan> instructionPlans);

    public List<InstructionPlan> saveAll(Set<InstructionPlan> instructionPlans);

    public InstructionPlan save(InstructionPlan instructionPlan);

    public InstructionPlan getByInstructionPlanId(String instructionPlanId);
}
