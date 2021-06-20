package com.steel.product.application.dao;

import com.steel.product.application.entity.InstructionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface InstructionGroupRepository extends JpaRepository<InstructionGroup, Integer> {

    @Modifying
    @Transactional
    @Query(" update Instruction set groupId=null where instructionId = :instructionId")
    void deleteGroupId(Integer instructionId);
}
