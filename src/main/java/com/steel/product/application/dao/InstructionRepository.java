package com.steel.product.application.dao;

import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.Status;
import com.steel.product.application.mapper.TotalLengthAndWeight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
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

    @Query("select COALESCE(SUM(ins.plannedLength),0) from Instruction ins where ins.parentInstruction.instructionId = :parentInstructionId")
    public Float sumOfPlannedLengthOfInstructionHavingParentInstructionId(@Param("parentInstructionId") Integer parentInstructionId);

    @Query("select new com.steel.product.application.mapper.TotalLengthAndWeight(COALESCE(SUM(ins.plannedLength),0),COALESCE(SUM(ins.plannedWeight),0)) from Instruction ins where ins.parentInstruction.instructionId = :parentInstructionId")
    public TotalLengthAndWeight sumOfPlannedLengthAndWeightOfInstructionsHavingParentInstructionId(@Param("parentInstructionId") Integer parentInstructionId);

    @Query("select new com.steel.product.application.mapper.TotalLengthAndWeight(COALESCE(SUM(ins.plannedLength),0),COALESCE(SUM(ins.plannedWeight),0)) from Instruction ins where ins.groupId = :groupId")
    public TotalLengthAndWeight sumOfPlannedLengthAndWeightOfInstructionsHavingGroupId(@Param("groupId") Integer groupId);

    public List<Instruction> getAllByInstructionIdIn(List<Integer> instructionIds);

    @Query("select pd,ins,COUNT(ins.plannedWeight) from PartDetails pd join fetch pd.instructions ins where pd.partDetailsId = :partDetailsId group by ins.plannedWeight,ins.partDetails order by ins.createdOn")
    List<Object[]> findPartDetailsJoinFetchInstructions(@Param("partDetailsId") String partDetailsId);

//    @Query(value = "SELECT ANY_VALUE(pd.target_weight) as targetWeight,ANY_VALUE(pd.length) AS length, ANY_VALUE(ins.inwardId) AS inwardId, ANY_VALUE(ins.processId) as processId" +
//            ",ANY_VALUE(ins.plannedWeight) AS plannedWeight,ANY_VALUE(ins.plannedWidth) AS plannedWidth,ANY_VALUE(ins.plannedNoOfPieces) AS plannedNoOfPieces" +
//            ",COUNT(ins.plannedWeight) AS weightCount, ANY_VALUE(ins.plannedLength) as plannedLength, pd.id AS partId" +
//            " FROM product_instruction as ins INNER JOIN product_part_details AS pd ON pd.id = ins.part_details_id" +
//            "  WHERE pd.part_details_id = :partDetailsId GROUP BY ins.plannedWeight,ins.part_details_id ORDER BY pd.id",nativeQuery = true)
//    public List<PartInstruction> findPartDetailsFetchInstructions(@Param("partDetailsId")String partDetailsId);

    Instruction findFirstByGroupId(Integer groupId);

}
