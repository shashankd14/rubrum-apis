package com.steel.product.application.dao;

import com.steel.product.application.entity.CuttingInstruction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuttingInstructionRepository extends JpaRepository<CuttingInstruction, Integer> {}
