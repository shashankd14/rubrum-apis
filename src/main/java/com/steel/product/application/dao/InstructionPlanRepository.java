package com.steel.product.application.dao;

import com.steel.product.application.entity.InstructionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructionPlanRepository extends JpaRepository<InstructionPlan, Long> {
}
