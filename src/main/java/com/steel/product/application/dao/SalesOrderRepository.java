package com.steel.product.application.dao;

import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.SalesOrderEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrderEntity, Integer> {

	@Query(value = "select packet_id, inwardid, coilnumber, customerbatchid, materialgrade, materialdesc, fthickness,  weight, npartyid,partyname,width, length,fquantity, "
			+ "in_stock_weight, plannednoofpieces, process_status, instruction_status, classification_tag, enduser_tag_name"
			+ " from ( SELECT inwardid,  coilnumber, customerbatchid, "
			+ " (select vdescription from product_tblmatdescription where nmatid=parent.nmatid) as  materialdesc,"
			+ " (select gradename from product_material_grades where gradeid=parent.materialgradeid) as  materialgrade,	"
			+ "	fthickness,  instructionid as packet_id,  coalesce(actualwidth, plannedwidth) width, coalesce( actuallength,plannedlength) length, "
			+ " coalesce(actualweight, plannedweight) weight, fquantity,in_stock_weight, plannednoofpieces,"
			+ " (select processname from product_process where processid=child.processid) as process_status ,"
			+ " (select statusname from product_status where statusid=child.status) as instruction_status,partyname, "
			+ " (select classification_name from product_packet_classification where classification_id=child.packet_classification_id) as classification_tag, "
			+ " (select tag_name from product_enduser_tags where tag_id=child.enduser_tag_id) as enduser_tag_name,	 "
			+ " (SELECT count(distinct inss.instructionid) cnt FROM product_instruction inss where inss.inwardid=parent.inwardentryid  and status!=4 and inss.parentgroupid=child.groupid) as siltcutcnt, parent.npartyid"
			+ " FROM product_tblinwardentry parent, product_instruction child, product_tblpartydetails party  "
			+ " where child.isdeleted=0 and parent.inwardentryid = child.inwardid and party.npartyid = parent.npartyid "
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (parent.coilNumber like %:searchText% or parent.customerBatchId like %:searchText% or parent.customerInvoiceNo like %:searchText%) else 1=1 end " 
			+ " and parent.createdby in (:userIds) "
			+ " and case when :partyIdsFlag=true then parent.npartyid in :partyIds else 1=1 end "
			+ ") a "
			+ " where 1=1 AND CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END order by packet_id desc",
		countQuery = "SELECT count(packet_id) from "
			+ " (select instructionid as packet_id, "
			+ " (SELECT count(distinct inss.instructionid) cnt FROM product_instruction inss where inss.inwardid=parent.inwardentryid  and status!=4 and inss.parentgroupid=child.groupid) as siltcutcnt"
			+ " FROM product_tblinwardentry parent, product_instruction child, product_tblpartydetails party "
			+ " where child.isdeleted=0 and parent.inwardentryid = child.inwardid and party.npartyid = parent.npartyid "
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (parent.coilNumber like %:searchText% or parent.customerBatchId like %:searchText% or parent.customerInvoiceNo like %:searchText%) else 1=1 end " 
			+ " and parent.createdby in (:userIds) "
			+ " and case when :partyIdsFlag=true then parent.npartyid in :partyIds else 1=1 end "
			+ ") a "
			+ " where 1=1 AND CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END", nativeQuery = true)
	Page<Object[]> listAllPackets(@Param("searchText") String searchText,
			@Param("partyIds") List<Integer> partyIds, @Param("partyIdsFlag") boolean partyIdsFlag,
			@Param("userIds") List<Integer> userIds, Pageable pageable);

}
