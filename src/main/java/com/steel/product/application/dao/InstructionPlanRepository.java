package com.steel.product.application.dao;

import com.steel.product.application.entity.InstructionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstructionPlanRepository extends JpaRepository<InstructionPlan, Integer> {

    Optional<InstructionPlan> findByInstructionPlanId(String instructionPlanId);
}
