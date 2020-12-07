package com.steel.product.application.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.steel.product.application.entity.Instruction;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface InstructionRepository extends JpaRepository<Instruction, Integer> {
    @Modifying
    @Transactional
    @Query("update Instruction set deliveryId =:deliveryId, remarks =:remarks where instructionId =:instructionId")
    public void updateInstructionWithDeliveryId(@Param("instructionId") int instructionId,
                                                @Param("deliveryId") int deliveryId,@Param("remarks") String remarks);

}
