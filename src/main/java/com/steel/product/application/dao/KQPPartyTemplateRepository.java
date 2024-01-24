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

	//@Query("select ins from KQPPartyTemplateEntity ins where ins.party.nPartyId=:partyId ")
	//List<KQPPartyTemplateEntity> findByPartyId(@Param("partyId") Integer partyId);	
	
	@Query("select ins from KQPPartyTemplateEntity ins where ins.kqpEntity.kqpId=:kqpId ")
	List<KQPPartyTemplateEntity> findByKqpId(@Param("kqpId") Integer kqpId);	

	@Query("select distinct kqpmap from KQPPartyTemplateEntity kqpmap "
			+ " INNER JOIN KQPEntity kqp on kqp.kqpId =kqpmap.kqpEntity.kqpId "
			+ " where 1=1 ")
	List<KQPPartyTemplateEntity> findByAll2();	
	
	@Query(value = "SELECT distinct inwardentryid, coilnumber `Coil No`,customerbatchid `Batch No`," + 
			" DATE_FORMAT(createdon,'%d/%m/%Y') `Plan Date`," + 
			" (select aa.`gradename` from `product_material_grades` aa where aa.`gradeid` = `inward`.`materialgradeid`) AS `material_grade`," + 
			" fthickness, grossweight, inward.npartyid, " + 
			" (SELECT rpt.qir_id FROM quality_inspection_report rpt where rpt.coil_no=inward.coilnumber limit 1) qirid, " +
			" (SELECT aaa.partyname from product_tblpartydetails aaa where aaa.npartyid=inward.npartyid) , fwidth, " + 
			" (select matdes.`material_code` from `product_tblmatdescription` matdes where matdes.`nmatid` = `inward`.`nmatid`) AS `material_code`  " +
			" FROM product_tblinwardentry inward WHERE 1=1 ", nativeQuery = true)
	List<Object[]> qirInwardListPage();
	
	@Query(value = "SELECT distinct part.part_details_id `Plan ID`, coilnumber `Coil No`,customerbatchid `Batch No`," + 
			" DATE_FORMAT(instructiondate,'%d/%m/%Y') `Plan Date`," + 
			" (select aa.`gradename` from `product_material_grades` aa where aa.`gradeid` = `inward`.`materialgradeid`) AS `material_grade`," + 
			" fthickness, part.target_weight, inward.npartyid, " + 
			" (SELECT rpt.qir_id FROM quality_inspection_report rpt where rpt.plan_id=part.part_details_id and rpt.stage_name = 'PRE_PROCESSING' limit 1) qirid, " +
			" (SELECT aaa.partyname from product_tblpartydetails aaa where aaa.npartyid=inward.npartyid) partyname, fwidth, "  +
			" (select matdes.`material_code` from `product_tblmatdescription` matdes where matdes.`nmatid` = `inward`.`nmatid`) AS `material_code`, " + 
			" inward.inwardentryid " + 
			" FROM product_part_details part" + 
			" INNER JOIN product_instruction ins ON part.id = ins.part_details_id" + 
			" INNER JOIN product_tblinwardentry inward ON ins.inwardid = inward.inwardentryid" + 
			" WHERE 1=1 ", nativeQuery = true)
	List<Object[]> qirPreProcessingListPage();

	@Query(value = "SELECT distinct part.part_details_id `Plan ID`, coilnumber `Coil No`,customerbatchid `Batch No`," + 
			" DATE_FORMAT(instructiondate,'%d/%m/%Y') `Plan Date`," + 
			" (select aa.`gradename` from `product_material_grades` aa where aa.`gradeid` = `inward`.`materialgradeid`) AS `material_grade`," + 
			" fthickness, part.target_weight, inward.npartyid, " + 
			" (SELECT rpt.qir_id FROM quality_inspection_report rpt where rpt.plan_id=part.part_details_id and stage_name = 'PROCESSING'limit 1) qirid, " +
			" (SELECT aaa.partyname from product_tblpartydetails aaa where aaa.npartyid=inward.npartyid) partyname, fwidth, "  +
			" (select matdes.`material_code` from `product_tblmatdescription` matdes where matdes.`nmatid` = `inward`.`nmatid`) AS `material_code`, " + 
			" inward.inwardentryid " + 
			" FROM product_part_details part" + 
			" INNER JOIN product_instruction ins ON part.id = ins.part_details_id" + 
			" INNER JOIN product_tblinwardentry inward ON ins.inwardid = inward.inwardentryid" + 
			" WHERE 1=1 ", nativeQuery = true)
	List<Object[]> qirProcessingListPage();
	
	@Query(value = "SELECT ins FROM Instruction ins where ins.inwardId.coilNumber = :coilNo and ins.partDetails.partDetailsId = :partDetailsId ")
	List<Instruction> fetchpacketdtls(@Param("coilNo") String coilNo, @Param("partDetailsId") String partDetailsId);
	
	@Query(value = "SELECT ins FROM Instruction ins where ins.inwardId.inwardEntryId = :inwardEntryId and ins.partDetails.partDetailsId = :partDetailsId ")
	List<Instruction> labelFetchPacketDetails(@Param("inwardEntryId") Integer inwardEntryId, @Param("partDetailsId") String partDetailsId);

	@Query(value = "SELECT DISTINCT coilnumber, DATE_FORMAT(deli.createdon, '%d/%m/%Y'), "
			+ " deli.deliveryid, customerbatchid, totalweight, vehicleno, inward.customerinvoiceno, "
			+ " DATE_FORMAT(inward.dinvoicedate, '%d/%m/%Y'), "
			+ " (SELECT group_concat( distinct `tag_name` SEPARATOR  ', ') FROM product_enduser_tags deli, product_instruction ins WHERE  deli.tag_id = ins.enduser_tag_id and ins.inwardid = inward.inwardentryid group by ins.inwardid ), "
			+ " inward.npartyid, (SELECT rpt.qir_id FROM quality_inspection_report rpt where rpt.delivery_chalan_no=deli.deliveryid and rpt.stage_name = 'PRE_DISPATCH' limit 1) qirid, "
			+ " (SELECT aaa.partyname from product_tblpartydetails aaa where aaa.npartyid=inward.npartyid) partyname, fwidth, "  
			+ " (select matdes.`material_code` from `product_tblmatdescription` matdes where matdes.`nmatid` = `inward`.`nmatid`) AS `material_code`, "  
			+ " (select aa.`gradename` from `product_material_grades` aa where aa.`gradeid` = `inward`.`materialgradeid`) AS `material_grade`  " 
			+ " FROM product_tbl_delivery_details deli, product_instruction ins, product_tblinwardentry inward "
			+ " WHERE deli.deliveryid = ins.deliveryid and ins.inwardid = inward.inwardentryid", nativeQuery = true)
	List<Object[]> qirPreDispatchList();

	@Query(value = "SELECT DISTINCT coilnumber, DATE_FORMAT(deli.createdon, '%d/%m/%Y'), "
			+ " deli.deliveryid, customerbatchid, totalweight, vehicleno, inward.customerinvoiceno, "
			+ " DATE_FORMAT(inward.dinvoicedate, '%d/%m/%Y'), "
			+ " (SELECT group_concat( distinct `tag_name` SEPARATOR  ', ') FROM product_enduser_tags deli, product_instruction ins WHERE deli.tag_id = ins.enduser_tag_id and ins.inwardid = inward.inwardentryid group by ins.inwardid ), "
			+ " inward.npartyid, (SELECT rpt.qir_id FROM quality_inspection_report rpt where rpt.delivery_chalan_no=deli.deliveryid and rpt.stage_name = 'POST_DISPATCH' limit 1) qirid, "
			+ " (SELECT aaa.partyname from product_tblpartydetails aaa where aaa.npartyid=inward.npartyid) partyname, fwidth, "  
			+ " (select matdes.`material_code` from `product_tblmatdescription` matdes where matdes.`nmatid` = `inward`.`nmatid`) AS `material_code`, "  
			+ " (select aa.`gradename` from `product_material_grades` aa where aa.`gradeid` = `inward`.`materialgradeid`) AS `material_grade`  " 
			+ " FROM product_tbl_delivery_details deli, product_instruction ins, product_tblinwardentry inward "
			+ " WHERE deli.deliveryid = ins.deliveryid and ins.inwardid = inward.inwardentryid", nativeQuery = true)
	List<Object[]> qirPostDispatchList();

	@Query(value = "SELECT ins FROM Instruction ins where ins.inwardId.coilNumber = :coilNo and ins.deliveryDetails.deliveryId = :partDetailsId ")
	List<Instruction> getDispatchDetails(@Param("coilNo") String coilNo, @Param("partDetailsId") Integer partDetailsId);
	
}
