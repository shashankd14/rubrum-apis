package com.steel.product.application.dao;

import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.KQPPartyTemplateEntity;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KQPPartyTemplateRepository extends JpaRepository<KQPPartyTemplateEntity, Integer> {

	@Query("select ins from KQPPartyTemplateEntity ins where ins.party.nPartyId=:partyId ")
	List<KQPPartyTemplateEntity> findByPartyId(@Param("partyId") Integer partyId);	
	
	@Query("select ins from KQPPartyTemplateEntity ins where ins.kqpEntity.kqpId=:kqpId ")
	List<KQPPartyTemplateEntity> findByKqpId(@Param("kqpId") Integer kqpId);	

	@Query(value = "SELECT distinct part.part_details_id `Plan ID`, coilnumber `Coil No`,customerbatchid `Batch No`,\r\n" + 
			" DATE_FORMAT(instructiondate,'%d/%m/%Y') `Plan Date`,\r\n" + 
			" (select aa.`gradename` from `product_material_grades` aa where aa.`gradeid` = `inward`.`materialgradeid`) AS `material_grade`,\r\n" + 
			" fthickness FROM product_part_details part\r\n" + 
			" INNER JOIN\r\n" + 
			" product_instruction ins ON part.id = ins.part_details_id\r\n" + 
			" INNER JOIN " + 
			" product_tblinwardentry inward ON ins.inwardid = inward.inwardentryid\r\n" + 
			" WHERE 1=1 ", nativeQuery = true)
	List<Object[]> qirListPage();

	@Query(value = "SELECT ins FROM Instruction ins where ins.inwardId.coilNumber = :coilNo and ins.partDetails.partDetailsId = :partDetailsId ")
	List<Instruction> fetchpacketdtls(@Param("coilNo") String coilNo, @Param("partDetailsId") String partDetailsId);
	
}
