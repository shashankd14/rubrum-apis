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

	@Query(value = "SELECT distinct part.part_details_id `Plan ID`, coilnumber `Coil No`,customerbatchid `Batch No`," + 
			" DATE_FORMAT(instructiondate,'%d/%m/%Y') `Plan Date`," + 
			" (select aa.`gradename` from `product_material_grades` aa where aa.`gradeid` = `inward`.`materialgradeid`) AS `material_grade`," + 
			" fthickness FROM product_part_details part" + 
			" INNER JOIN" + 
			" product_instruction ins ON part.id = ins.part_details_id" + 
			" INNER JOIN " + 
			" product_tblinwardentry inward ON ins.inwardid = inward.inwardentryid" + 
			" WHERE 1=1 ", nativeQuery = true)
	List<Object[]> qirListPage();

	@Query(value = "SELECT ins FROM Instruction ins where ins.inwardId.coilNumber = :coilNo and ins.partDetails.partDetailsId = :partDetailsId ")
	List<Instruction> fetchpacketdtls(@Param("coilNo") String coilNo, @Param("partDetailsId") String partDetailsId);
	
	@Query(value = "SELECT DISTINCT coilnumber, DATE_FORMAT(deli.createdon, '%d/%m/%Y'), "
			+ " deli.deliveryid, customerbatchid, totalweight, vehicleno, inward.customerinvoiceno, "
			+ " DATE_FORMAT(inward.dinvoicedate, '%d/%m/%Y'), "
			+ " (SELECT group_concat( distinct `tag_name` SEPARATOR  ', ') FROM product_enduser_tags deli, product_instruction ins WHERE  deli.tag_id = ins.enduser_tag_id and ins.inwardid = inward.inwardentryid group by ins.inwardid ) "
			+ " FROM product_tbl_delivery_details deli, product_instruction ins, product_tblinwardentry inward  "
			+ " WHERE deli.deliveryid = ins.deliveryid and ins.inwardid = inward.inwardentryid", nativeQuery = true)
	List<Object[]> qirDispatchList();
	
}
