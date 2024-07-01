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
	@Query("update Instruction set deliveryId =:deliveryId, remarks =:remarks, status=4 where instructionId =:instructionId")
	public void updateInstructionWithDeliveryRemarks(@Param("instructionId") int instructionId,
			@Param("deliveryId") int deliveryId, @Param("remarks") String remarks);

    @Query(" from Instruction order by instructionId desc")
    public List<Instruction> getAll();

    @Query(" from Instruction where processId !=7")
    public List<Instruction> getAllWIP();

    @Query(" from Instruction where status = 2")
    public List<Instruction> getAllWIPList();

    @Query("select i from Instruction i where i.groupId = :groupId")
    List<Instruction> findByGroupId(@Param("groupId") Integer groupId);

    @Query("select i from Instruction i where i.parentGroupId = :parentGroupId")
    List<Instruction> findByParentGroupId(@Param("parentGroupId") Integer parentGroupId);

    @Query("select i from Instruction i where i.parentInstruction.instructionId = :parentInstructionId")
    List<Instruction> findByParentInstructionId(@Param("parentInstructionId") Integer parentInstructionId);

    List<Instruction> findInstructionsByInstructionIdInAndStatusNot(List<Integer> instructionIds, Status status);

    @Query("select ins from Instruction ins left join fetch ins.inwardId where ins.instructionId = :instructionId")
    Optional<Instruction> findInstructionById(@Param("instructionId") Integer instructionId);

    @Query("select ins from Instruction ins left join fetch ins.inwardId left join fetch ins.parentInstruction where ins.instructionId in :instructionIds and ins.status.statusId = :statusId")
    List<Instruction> findAllByInstructionIdInAndStatus(@Param("instructionIds") List<Integer> instructionIds, @Param("statusId") Integer statusId);

    @Query("select ins from Instruction ins join fetch ins.deliveryDetails dd where ins.instructionId in :instructionIds")
    List<Instruction> findInstructionsWithDeliveryDetails(@Param("instructionIds") List<Integer> instructionIds);

    @Query("select ins from Instruction ins join fetch ins.partDetails dd where ins.instructionId in :instructionIds")
    List<Instruction> findInstructionsWithPartDetails(@Param("instructionIds") List<Integer> instructionIds);

    @Query("select ins from Instruction ins join fetch ins.inwardId inw join fetch inw.party join fetch inw.material join fetch inw.materialGrade" +
            " where inw.inwardEntryId = :inwardId and (ins.groupId is not null or ins.parentGroupId is not null)")
    List<Instruction> findSlitAndCutInstructionByInwardId(@Param("inwardId") Integer inwardId);

    @Query("select ins from Instruction ins join fetch ins.inwardId inw where inw.inwardEntryId = :inwardId and ins.groupId >0 ")
    List<Instruction> findSlitAndCutInstructionByInwardId1(@Param("inwardId") Integer inwardId);

    @Query("select ins from Instruction ins where ins.status != 4 and ins.parentGroupId = :groupId")
    List<Instruction> findSlitAndCutInstructionByGroupId (@Param("groupId") Integer groupId);

    @Query("select COALESCE(SUM(ins.plannedWeight),0) from Instruction ins where ins.groupId = :groupId")
    Float sumOfPlannedWeightOfInstructionsHavingGroupId(@Param("groupId") Integer groupId);

    @Query("select COALESCE(SUM(ins.plannedWeight),0) from Instruction ins where ins.parentInstruction.instructionId = :parentInstructionId")
    Float sumOfPlannedWeightOfInstructionHavingParentInstructionId(@Param("parentInstructionId") Integer parentInstructionId);

    @Query("select COALESCE(SUM(ins.plannedLength),0) from Instruction ins where ins.parentInstruction.instructionId = :parentInstructionId")
    Float sumOfPlannedLengthOfInstructionHavingParentInstructionId(@Param("parentInstructionId") Integer parentInstructionId);

    @Query("select new com.steel.product.application.mapper.TotalLengthAndWeight(COALESCE(SUM(ins.plannedLength),0),COALESCE(SUM(ins.plannedWeight),0)) from Instruction ins where ins.parentInstruction.instructionId = :parentInstructionId")
    TotalLengthAndWeight sumOfPlannedLengthAndWeightOfInstructionsHavingParentInstructionId(@Param("parentInstructionId") Integer parentInstructionId);

    @Query("select new com.steel.product.application.mapper.TotalLengthAndWeight(COALESCE(SUM(ins.plannedLength),0),COALESCE(SUM(ins.plannedWeight),0)) from Instruction ins where ins.groupId in :groupIds")
    TotalLengthAndWeight sumOfPlannedLengthAndWeightOfInstructionsHavingGroupId(@Param("groupIds") List<Integer> groupIds);

    List<Instruction> getAllByInstructionIdIn(List<Integer> instructionIds);

    @Query("select pd,ins,COUNT(ins.plannedWeight) from PartDetails pd join fetch pd.instructions ins where pd.partDetailsId = :partDetailsId group by ins.plannedWeight,ins.partDetails,ins.process,ins.endUserTagsEntity order by ins.instructionId asc")
    List<Object[]> findPartDetailsJoinFetchInstructions(@Param("partDetailsId") String partDetailsId);

    @Query("select pd,ins,COUNT(ins.plannedWeight) from PartDetails pd join fetch pd.instructions ins where parentGroupId in :groupIds group by ins.plannedWeight,ins.partDetails,ins.process,ins.endUserTagsEntity order by ins.instructionId asc")
    List<Object[]> findPartDetailsJoinFetchInstructionsAndGroupIds(@Param("groupIds")List<Integer> groupIds);

    @Query("select pd,ins,COUNT(ins.plannedWeight) from PartDetails pd join fetch pd.instructions ins where pd.partDetailsId = :partDetailsId or parentGroupId in :groupIds group by ins.plannedWeight,ins.partDetails,ins.process,ins.endUserTagsEntity  order by ins.instructionId asc")
    List<Object[]> findPartDetailsJoinFetchInstructionsByPartDetailsIdOrGroupIds(@Param("partDetailsId") String partDetailsId,@Param("groupIds")List<Integer> groupIds);

//    @Query(value = "SELECT ANY_VALUE(pd.target_weight) as targetWeight,ANY_VALUE(pd.length) AS length, ANY_VALUE(ins.inwardId) AS inwardId, ANY_VALUE(ins.processId) as processId" +
//            ",ANY_VALUE(ins.plannedWeight) AS plannedWeight,ANY_VALUE(ins.plannedWidth) AS plannedWidth,ANY_VALUE(ins.plannedNoOfPieces) AS plannedNoOfPieces" +
//            ",COUNT(ins.plannedWeight) AS weightCount, ANY_VALUE(ins.plannedLength) as plannedLength, pd.id AS partId" +
//            " FROM product_instruction as ins INNER JOIN product_part_details AS pd ON pd.id = ins.part_details_id" +
//            "  WHERE pd.part_details_id = :partDetailsId GROUP BY ins.plannedWeight,ins.part_details_id ORDER BY pd.id",nativeQuery = true)
//    public List<PartInstruction> findPartDetailsFetchInstructions(@Param("partDetailsId")String partDetailsId);

    Optional<Instruction> findFirstByGroupIdAndIsDeletedFalse(Integer groupId);

    @Query("select ins from Instruction ins join fetch ins.inwardId inw join ins.partDetails pd where pd.id = :partId and ins.process.processId = :processId")
    List<Instruction> findInstructionsByPartIdAndProcessId(@Param("partId")Long partId,@Param("processId")Integer processId);

    List<Instruction> findAllByGroupIdOrParentGroupId(Integer groupId,Integer parentGroupId);

    @Query(value = "select inwardId,sum(case when actualWeight is null then plannedWeight else actualWeight end) as sum " +
            " from product_instruction where status < 4 and groupId is null and isDeleted is false group by inwardId order by inwardId",nativeQuery = true)
    List<Object[]> findSumOfPlannedWeightAndActualWeightForUnprocessed();

    @Query("select count(ins.instructionId) from Instruction ins where ins.partDetails.id = :partDetailsId")
    int getPartCount(@Param("partDetailsId") Long partDetailsId);

	@Modifying
	@Transactional
	@Query("update PartDetails set pdfS3Url=:url where partDetailsId= :partDetailsId ")
	public void updateS3PlanPDF(@Param("partDetailsId") String partDetailsId, @Param("url") String url);

	@Query("select ins from Instruction ins left join fetch ins.inwardId left join fetch ins.parentInstruction where ins.inwardId.inwardEntryId = :inwardId and ins.status.statusId = :statusId")
	public List<Instruction> findByStatus(@Param("inwardId") Integer inwardId, @Param("statusId") Integer statusId);

    @Query("select ins from Instruction ins left join fetch ins.inwardId left join fetch ins.parentInstruction where ins.instructionId in :instructionIds and ins.status.statusId in :statusId")
    public List<Instruction> findAllByInstructionIdInAndStatus(@Param("instructionIds") List<Integer> instructionIds, @Param("statusId") List<Integer> statusId);

	@Modifying
	@Transactional
	@Query("update Instruction set groupId =:groupId where instructionId in :instructionIds ")
	public void updateInstructionGroupId(@Param("groupId") int groupId, @Param("instructionIds") List<Integer> instructionIds);

	@Query(value = "SELECT distinct coilnumber `Coil No`,customerbatchid `Batch No`," + 
			" (select desc2.vdescription from product_tblmatdescription desc2 where desc2.nmatid=inward.nmatid) as material_desc," + 
			" (select aa.`gradename` from `product_material_grades` aa where aa.`gradeid` = `inward`.`materialgradeid`) AS `material_grade`," + 
			" ROUND(`fthickness`, 2) as `fthickness`, round(fquantity,2) as `net weight`, " +
			" ROUND(`grossweight`, 2) as `grossweight`, ROUND(`fwidth`, 2) as `fwidth` " +
			" FROM product_tblinwardentry inward WHERE inwardentryid=:inwardId ", nativeQuery = true)
	public List<Object[]> getQRCodeDetails1(@Param("inwardId") Integer inwardId);
	
	@Query("select ins from Instruction ins left join fetch ins.inwardId where ins.inwardId.inwardEntryId = :inwardId and ins.status.statusId = :statusId ")
	public List<Instruction> getQRCodeDetails(@Param("inwardId") Integer inwardId, @Param("statusId") Integer statusId);
	
	@Query(value = "SELECT coilnumber, customercoilid, instructionid,\r\n" + 
			"    (select partyname from product_tblpartydetails partt where partt.npartyid = inw.npartyid) custname,\r\n" + 
			"    (select desc2.vdescription from product_tblmatdescription desc2 where desc2.nmatid=inw.nmatid) as material_desc,\r\n" + 
			"    (select tagg.tag_name from product_enduser_tags tagg where tagg.tag_id=instruction.enduser_tag_id) as enduser_tag_name,\r\n" + 
			"     (select aa.`gradename` from `product_material_grades` aa where aa.`gradeid` = inw.`materialgradeid`) AS `material_grade`,     \r\n" + 
			"     ROUND(`fthickness`, 2) as thickness, round(actualweight, 3) as weight, \r\n" + 
			"     ROUND(`actuallength`, 2) as length, ROUND(`actualwidth`, 2) as width\r\n" + 
			" FROM product_instruction instruction, product_tblinwardentry inw\r\n" + 
			" WHERE instruction.inwardid = inw.inwardentryid\r\n" + 
			" AND instruction.inwardid = :inwardId AND instruction.status = 3 ", nativeQuery = true)
	public List<Object[]> getQRCodeDetails_Finish(@Param("inwardId") Integer inwardId);

	@Modifying
	@Transactional
	@Query("update PartDetails set labelpdfWipS3Url=:url where partDetailsId= :partDetailsId ")
	public void updateS3PlanLabelPDF(@Param("partDetailsId") String partDetailsId, @Param("url") String url);

	@Modifying
	@Transactional
	@Query("update PartDetails set labelpdfFgS3Url=:url, labelUpdatedTime=CURRENT_TIMESTAMP where partDetailsId= :partDetailsId ")
	public void updateS3FGLabelPDF(@Param("partDetailsId") String partDetailsId, @Param("url") String url);

	@Modifying
	@Transactional
	@Query(value = "update product_instruction set packet_classification_id= :classificationId where inwardid= :inwardId and instructionid= :instructionId", nativeQuery = true)
	public void updateClassification(@Param("instructionId") Integer instructionId, @Param("inwardId") Integer inwardId,
			@Param("classificationId") Integer classificationId);

}
