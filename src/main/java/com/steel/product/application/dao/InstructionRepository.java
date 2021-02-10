package com.steel.product.application.dao;

import com.steel.product.application.entity.Instruction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface InstructionRepository extends JpaRepository<Instruction, Integer> {
    @Modifying
    @Transactional
    @Query("update Instruction set deliveryId =:deliveryId, remarks =:remarks, status=4, " +
            "rateId =:rateId where instructionId =:instructionId")
    public void updateInstructionWithDeliveryInfo(@Param("instructionId") int instructionId,
                                                  @Param("deliveryId") int deliveryId,
                                                  @Param("remarks") String remarks,
                                                  @Param("rateId") int rateId);

}
