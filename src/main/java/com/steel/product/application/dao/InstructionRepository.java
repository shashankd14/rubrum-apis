package com.steel.product.application.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.steel.product.application.entity.Instruction;

public interface InstructionRepository extends JpaRepository<Instruction, Integer> {

}
