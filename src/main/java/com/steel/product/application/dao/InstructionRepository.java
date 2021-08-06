package com.steel.product.application.dao;

import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InwardEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface InstructionRepository extends JpaRepository<Instruction, Integer> {
    @Modifying
    @Transactional
    @Query("update Instruction set deliveryId =:deliveryId, remarks =:remarks, status=4 " +
            " where instructionId =:instructionId")
    public void updateInstructionWithDeliveryRemarks(@Param("instructionId") int instructionId,
                                                  @Param("deliveryId") int deliveryId,
                                                  @Param("remarks") String remarks);

    @Query(" from Instruction where processId !=7")
    public List<Instruction> getAllWIP();

    @Query(" from Instruction where status = 2")
    public List<Instruction> getAllWIPList();

    @Query("select i from Instruction i where i.groupId = :groupId")
    public List<Instruction> findByGroupId(@Param("groupId")Integer groupId);

    @Query("select i from Instruction i where i.parentGroupId = :parentGroupId")
    public List<Instruction> findByParentGroupId(@Param("parentGroupId")Integer parentGroupId);

    @Query("select i from Instruction i where i.parentInstruction.instructionId = :parentInstructionId")
    public List<Instruction> findByParentInstructionId(@Param("parentInstructionId")Integer parentInstructionId);

    public List<Instruction> findByInstructionIdIn(List<Integer> ids);



}
