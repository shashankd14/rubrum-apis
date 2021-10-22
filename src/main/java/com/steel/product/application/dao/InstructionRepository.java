package com.steel.product.application.dao;

import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

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
    public List<Instruction> findByGroupId(@Param("groupId") Integer groupId);

    @Query("select i from Instruction i where i.parentGroupId = :parentGroupId")
    public List<Instruction> findByParentGroupId(@Param("parentGroupId") Integer parentGroupId);

    @Query("select i from Instruction i where i.parentInstruction.instructionId = :parentInstructionId")
    public List<Instruction> findByParentInstructionId(@Param("parentInstructionId") Integer parentInstructionId);

    public List<Instruction> findInstructionsByInstructionIdInAndStatusNot(List<Integer> instructionIds, Status status);

    @Query("select ins from Instruction ins left join fetch ins.inwardId where ins.instructionId = :instructionId")
    public Optional<Instruction> findInstructionById(@Param("instructionId") Integer instructionId);

    @Query("select ins from Instruction ins left join fetch ins.inwardId left join fetch ins.parentInstruction where ins.instructionId in :instructionIds and ins.status.statusId = :statusId")
    public List<Instruction> findAllByInstructionIdInAndStatus(@Param("instructionIds") List<Integer> instructionIds, @Param("statusId") Integer statusId);

    @Query("select ins from Instruction ins join fetch ins.deliveryDetails dd where ins.instructionId in :instructionIds")
    public List<Instruction> findInstructionsWithDeliveryDetails(@Param("instructionIds") List<Integer> instructionIds);

    @Query("select ins from Instruction ins join fetch ins.inwardId inw join fetch inw.party join fetch inw.material join fetch inw.materialGrade" +
            " where inw.inwardEntryId = :inwardId and (ins.groupId is not null or ins.parentGroupId is not null)")
    public List<Instruction> findSlitAndCutInstructionByInwardId(@Param("inwardId") Integer inwardId);

    @Query("select COALESCE(SUM(ins.plannedWeight),0) from Instruction ins where ins.groupId = :groupId")
    public Float sumOfPlannedWeightOfInstructionsHavingGroupId(@Param("groupId") Integer groupId);

    @Query("select COALESCE(SUM(ins.plannedWeight),0) from Instruction ins where ins.parentInstruction.instructionId = :parentInstructionId")
    public Float sumOfPlannedWeightOfInstructionHavingParentInstructionId(@Param("parentInstructionId") Integer parentInstructionId);

    public List<Instruction> getAllByInstructionIdIn(List<Integer> instructionIds);

    @Query("select ins, count(ins.plannedWeight) from Instruction ins join fetch ins.partDetails pd where pd.partDetailsId = :partDetailsId group by ins.plannedWeight order by pd.id")
    public List<Object[]> findInstructionsByPartDetailsIdJoinFetch(@Param("partDetailsId") String partDetailsId);

    @Query("select pd,ins,COUNT(ins.plannedWeight) from PartDetails pd join fetch pd.instructions ins where pd.partDetailsId = :partDetailsId group by ins.plannedWeight")
    public List<Object[]> findPartDetailsJoinFetchInstructions(@Param("partDetailsId") String partDetailsId);


}
