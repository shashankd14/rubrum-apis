package com.steel.product.application.dao;

import com.steel.product.application.entity.SalesOrderEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import javax.transaction.Transactional;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrderEntity, Integer> {

	@Query(value = "select planid, inwardid, batchno, customerbatchid, materialgrade, materialdesc, fthickness, fweight, locationid, partyname,fWidth, flength,fquantity, "
			+ "inStockWeight, actualNoOfPieces , process_status, instruction_status, classification_tag, enduser_tag_name,sono,customer_code,"
			+ " subgrade,brandname,createdon, mmid "
			+ " from ( SELECT inwardid,  coilnumber as batchno, customerbatchid, "
			+ " (SELECT product_name FROM jsw_product_master a, jsw_material_master mat where a.product_id=mat.producttype_id and mat.mm_id=parent.mm_id limit 1) as  materialdesc,"
			+ " (SELECT grade_name FROM jsw_grade_master grade, jsw_material_master mat where grade.grade_id=mat.grade_id and mat.mm_id=parent.mm_id limit 1) as  materialgrade,"
			+ "	fthickness, instructionid as planid,  coalesce(actualwidth, plannedwidth) fWidth, coalesce( actuallength,plannedlength) flength, "
			+ " coalesce(actualweight, plannedweight) fweight, fquantity,in_stock_weight inStockWeight, plannednoofpieces actualNoOfPieces,"
			+ " (select processname from product_process where processid=child.processid) as process_status ,"
			+ " (select statusname from product_status where statusid=child.status) as instruction_status,partyname, "
			+ " (select classification_name from product_packet_classification where classification_id=child.packet_classification_id) as classification_tag, "
			+ " (select tag_name from product_enduser_tags where tag_id=child.enduser_tag_id) as enduser_tag_name,	 "
			+ " (SELECT count(distinct a.instructionid) cnt FROM product_instruction a where a.inwardid=parent.inwardentryid and status!=4 and a.parentgroupid=child.groupid) as siltcutcnt, parent.npartyid locationid, "
			+ " (SELECT so.so_number from sales_order so, sales_order_child sochild where so.so_id=sochild.so_id and sochild.instruction_id = child.instructionid limit 1) as sono,"
			+ " (SELECT so.customer_code_id from sales_order so, sales_order_child sochild where so.so_id=sochild.so_id and sochild.instruction_id = child.instructionid limit 1) as customer_code,"
			+ " (SELECT subgrade.subgrade_name FROM jsw_subgrade_master subgrade, jsw_material_master mat where subgrade.subgrade_id=mat.subgrade_id and mat.mm_id=parent.mm_id limit 1) as  subgrade,"
			+ " (SELECT brand.brand_name FROM jsw_brand_master brand, jsw_material_master mat where brand.brand_id=mat.brand_id and mat.mm_id=parent.mm_id limit 1) as brandname,"
			+ " child.createdon as createdon, child.mmid "
			+ " FROM product_tblinwardentry parent, product_instruction child, product_tblpartydetails party  "
			+ " where child.isdeleted=0 and parent.isdeleted=0 and parent.inwardentryid = child.inwardid and party.npartyid = parent.npartyid "
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (parent.coilnumber like %:searchText% or parent.customerBatchId like %:searchText% or parent.customerInvoiceNo like %:searchText%) else 1=1 end " 
			+ " and (case when :planId > 0 then child.instructionid = :planId else 1=1 end )"
			+ " and (case when :locationId > 0 then parent.npartyid = :locationId else 1=1 end )"
			+ " and (case when :batchNo is not null and LENGTH(:batchNo) >0 then parent.coilnumber = :batchNo else 1=1 end) " 
			+ " and case when :partyIdsFlag=true then parent.npartyid in :partyIds else 1=1 end"
			+ ") a "
			+ " where (case when :mappingFlag=1 then LENGTH(a.sono)>0 when :mappingFlag = 2 then LENGTH(a.sono) is null else 1=1 end) AND CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END",
		countQuery = "SELECT count(planid) "
			+ " from ( SELECT inwardid,  coilnumber as batchno, customerbatchid, "
			+ " (SELECT product_name FROM jsw_product_master a, jsw_material_master mat where a.product_id=mat.producttype_id and mat.mm_id=parent.mm_id limit 1) as  materialdesc,"
			+ " (SELECT grade_name FROM jsw_grade_master grade, jsw_material_master mat where grade.grade_id=mat.grade_id and mat.mm_id=parent.mm_id limit 1) as  materialgrade,"
			+ "	fthickness, instructionid as planid,  coalesce(actualwidth, plannedwidth) fWidth, coalesce( actuallength,plannedlength) flength, "
			+ " coalesce(actualweight, plannedweight) fweight, fquantity,in_stock_weight inStockWeight, plannednoofpieces actualNoOfPieces,"
			+ " (select processname from product_process where processid=child.processid) as process_status ,"
			+ " (select statusname from product_status where statusid=child.status) as instruction_status,partyname, "
			+ " (select classification_name from product_packet_classification where classification_id=child.packet_classification_id) as classification_tag, "
			+ " (select tag_name from product_enduser_tags where tag_id=child.enduser_tag_id) as enduser_tag_name,	 "
			+ " (SELECT count(distinct a.instructionid) cnt FROM product_instruction a where a.inwardid=parent.inwardentryid and status!=4 and a.parentgroupid=child.groupid) as siltcutcnt, parent.npartyid locationid, "
			+ " (SELECT so.so_number from sales_order so, sales_order_child sochild where so.so_id=sochild.so_id and sochild.instruction_id = child.instructionid limit 1) as sono,"
			+ " (SELECT so.customer_code_id from sales_order so, sales_order_child sochild where so.so_id=sochild.so_id and sochild.instruction_id = child.instructionid limit 1) as customer_code,"
			+ " (SELECT subgrade.subgrade_name FROM jsw_subgrade_master subgrade, jsw_material_master mat where subgrade.subgrade_id=mat.subgrade_id and mat.mm_id=parent.mm_id limit 1) as  subgrade,"
			+ " (SELECT brand.brand_name FROM jsw_brand_master brand, jsw_material_master mat where brand.brand_id=mat.brand_id and mat.mm_id=parent.mm_id limit 1) as brandname,"
			+ " child.createdon as createdon "
			+ " FROM product_tblinwardentry parent, product_instruction child, product_tblpartydetails party "
			+ " where child.isdeleted=0 and parent.isdeleted=0 and parent.inwardentryid = child.inwardid and party.npartyid = parent.npartyid "
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (parent.coilnumber like %:searchText% or parent.customerBatchId like %:searchText% or parent.customerInvoiceNo like %:searchText%) else 1=1 end " 
			+ " and (case when :planId > 0 then child.instructionid = :planId else 1=1 end )"
			+ " and (case when :locationId > 0 then parent.npartyid = :locationId else 1=1 end )"
			+ " and (case when :batchNo is not null and LENGTH(:batchNo) >0 then parent.coilnumber = :batchNo else 1=1 end) " 
			+ " and case when :partyIdsFlag=true then parent.npartyid in :partyIds else 1=1 end "
			+ ") a "
			+ " where (case when :mappingFlag=1 then LENGTH(a.sono)>0 when :mappingFlag = 2 then LENGTH(a.sono) is null else 1=1 end) AND CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END", nativeQuery = true)
	Page<Object[]> listAllPackets(@Param("searchText") String searchText,
			@Param("partyIds") List<Integer> partyIds,
			@Param("partyIdsFlag") boolean partyIdsFlag,
			@Param("planId") int planId,
			@Param("mappingFlag") int mappingFlag,
			@Param("batchNo") String batchNo,
			@Param("locationId") int locationId,
			Pageable pageable);

	@Query(value = "SELECT distinct so.so_number,inwardid "
			+ " FROM product_tblinwardentry parent, product_instruction child, sales_order_child so_child, sales_order so, product_tblpartydetails party "
			+ " where so.is_deleted = 0 and so_child.is_deleted = 0 and parent.inwardentryid = child.inwardid and parent.inwardentryid = so_child.inward_entry_d and so_child.so_id = so.so_id and so_child.instruction_id = child.instructionid and party.npartyid = parent.npartyid "
			+ " and case when :soId is not null and LENGTH(:soId) >0 then so.so_id = :soId else so.so_id end " 
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (parent.coilNumber like %:searchText% or parent.customerBatchId like %:searchText% or so.so_number =:searchText) else 1=1 end " 
			+ " and case when :partyIdsFlag=true then parent.npartyid in :partyIds else 1=1 end "
			+ " order by so.so_id desc",
		countQuery = "SELECT count(distinct so.so_number) "
			+ " FROM product_tblinwardentry parent, product_instruction child, sales_order_child so_child, sales_order so,product_tblpartydetails party "
			+ " where so.is_deleted=0 and so_child.is_deleted = 0 and parent.inwardentryid = child.inwardid and parent.inwardentryid = so_child.inward_entry_d and so_child.so_id = so.so_id and so_child.instruction_id = child.instructionid and party.npartyid = parent.npartyid "
			+ " and case when :soId is not null and LENGTH(:soId) >0 then so.so_id = :soId else so.so_id end " 
			+ " and case when :partyIdsFlag=true then parent.npartyid in :partyIds else 1=1 end "
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (parent.coilNumber like %:searchText% or parent.customerBatchId like %:searchText% or so.so_number =:searchText) else 1=1 end " 
			+ "  ", nativeQuery = true)
	Page<Object[]> listAllSOIDs(@Param("searchText") String searchText,
			@Param("partyIds") List<Integer> partyIds, @Param("partyIdsFlag") boolean partyIdsFlag,
			@Param("soId") Integer soId, 
			  Pageable pageable);
	
	@Query(value = "select packet_id, inwardid, coilnumber, customerbatchid, materialgrade, materialdesc, fthickness,  weight, npartyid,partyname,width, length, "
			+ "process_status, instruction_status,(select so_number from sales_order so where so.so_id= a.so_id) sonumber, "
			+ " a.so_id, classification_tag, enduser_tag_name, customer_code, plannednoofpieces, mmid"
			+ " from ( SELECT inwardid,  coilnumber, customerbatchid, "
			+ " (SELECT product_name FROM jsw_product_master product, jsw_material_master mat where product.product_id=mat.producttype_id and mat.mm_id=parent.mm_id limit 1) as  materialdesc,"
			+ " (SELECT grade_name FROM jsw_grade_master grade, jsw_material_master mat where grade.grade_id=mat.grade_id and mat.mm_id=parent.mm_id limit 1) as  materialgrade,"
			+ "	parent.fthickness, instructionid as packet_id,  coalesce(actualwidth, plannedwidth) width, coalesce( actuallength,plannedlength) length, "
			+ " coalesce(actualweight, plannedweight) weight, fquantity,in_stock_weight, plannednoofpieces,"
			+ " (select processname from product_process where processid=child.processid) as process_status ,"
			+ " (select statusname from product_status where statusid=child.status) as instruction_status,partyname, "
			+ " (select classification_name from product_packet_classification where classification_id=child.packet_classification_id) as classification_tag, "
			+ " (select tag_name from product_enduser_tags where tag_id=child.enduser_tag_id) as enduser_tag_name,	 "
			+ " (SELECT count(distinct inss.instructionid) cnt FROM product_instruction inss where inss.inwardid=parent.inwardentryid  and status!=4 and inss.parentgroupid=child.groupid) as siltcutcnt, "
			+ " parent.npartyid, so_child.so_id, child.mmid,  "
			+ " (select tag_name from product_enduser_tags tags where so.customer_code_id=tags.tag_id) as customer_code"
			+ " FROM product_tblinwardentry parent, product_instruction child, sales_order_child so_child, sales_order so, product_tblpartydetails party "
			+ " where so.is_deleted = 0 and so_child.is_deleted = 0 and parent.inwardentryid = child.inwardid and parent.inwardentryid = so_child.inward_entry_d and so_child.so_id = so.so_id and so_child.instruction_id = child.instructionid and party.npartyid = parent.npartyid "
			+ " and  so.so_number in :soIDsList ) a "
			+ " where 1=1 AND CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END order by a.so_id desc", nativeQuery = true)
	List<Object[]> listAllSOs(@Param("soIDsList") List<String> soIDsList);
	
	@Query(value = "select packet_id, inwardid, coilnumber, customerbatchid, materialgrade, materialdesc, fthickness, plannedweight, npartyid,partyname,plannedwidth, plannedlength, "
			+ "process_status, instruction_status,(select so_number from sales_order so where so.so_id= a.so_id) sonumber, "
			+ " a.so_id, classification_tag, enduser_tag_name, customer_code, order_date,category_name, plannednoofpieces,formname"
			+ " from ( SELECT inwardid,  coilnumber, customerbatchid, "
			+ " (SELECT product_name FROM jsw_product_master product, jsw_material_master mat where product.product_id=mat.producttype_id and mat.mm_id=parent.mm_id limit 1) as  materialdesc,"
			+ " (SELECT grade_name FROM jsw_grade_master grade, jsw_material_master mat where grade.grade_id=mat.grade_id and mat.mm_id=parent.mm_id limit 1) as  materialgrade,"
			+ "	parent.fthickness, instructionid as packet_id,  plannedwidth, plannedlength, "
			+ "  plannedweight, fquantity,in_stock_weight, plannednoofpieces,"
			+ " (select processname from product_process where processid=child.processid) as process_status ,"
			+ " (select statusname from product_status where statusid=child.status) as instruction_status,partyname, "
			+ " (select classification_name from product_packet_classification where classification_id=child.packet_classification_id) as classification_tag, "
			+ " (select tag_name from product_enduser_tags where tag_id=child.enduser_tag_id) as enduser_tag_name,	 "
			+ " (SELECT count(distinct inss.instructionid) cnt FROM product_instruction inss where inss.inwardid=parent.inwardentryid  and status!=4 and inss.parentgroupid=child.groupid) as siltcutcnt, "
			+ " parent.npartyid, so_child.so_id,  "
			+ " (select tag_name from product_enduser_tags tags where so.customer_code_id=tags.tag_id) as customer_code,"
			+ " DATE_FORMAT(so.created_on, '%d-%m-%Y') AS order_date, "
			+ " (SELECT category_name FROM jsw_category_master grade, jsw_material_master mat where grade.category_id=mat.category_id and mat.mm_id=parent.mm_id limit 1) as  category_name, "
			+ " (SELECT form_name FROM jsw_form_master form, jsw_material_master mat where form.form_id=mat.form_id and mat.mm_id=parent.mm_id limit 1) as formname "
			+ " FROM product_tblinwardentry parent, product_instruction child, sales_order_child so_child, sales_order so, product_tblpartydetails party "
			+ " where so.is_deleted = 0 and so_child.is_deleted = 0 and parent.inwardentryid = child.inwardid and parent.inwardentryid = so_child.inward_entry_d and so_child.so_id = so.so_id and so_child.instruction_id = child.instructionid and party.npartyid = parent.npartyid "
			+ " and  so.so_id = :soId ) a "
			+ " where 1=1 AND CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END order by a.so_id desc", nativeQuery = true)
	List<Object[]> soDetailsBySoId(@Param("soId") Integer soId);
	
	@Modifying
	@Transactional
	@Query("update SalesOrderEntity inw set inw.isDeleted = true, inw.updatedBy=:userId, inw.updatedOn=CURRENT_TIMESTAMP where inw.soId in :itemIds")
	void deleteData(@Param("itemIds") List<Integer> itemIds, @Param("userId") Integer userId);

	List<SalesOrderEntity> findBySoNumber(String soNumber);

	@Query(value = "select distinct so_number, customer_code_id, (select tag_name from product_enduser_tags tags where tags.tag_id = so.customer_code_id ) as customer_code FROM sales_order so, sales_order_child so_child "
			+ " where so.is_deleted = 0 and so_child.is_deleted = 0 and so_child.so_id=so.so_id "
			+ " and so_child.instruction_id in :instructionIdList ", 
		nativeQuery = true)
	List<Object[]> validateSoNoAndCustCode(@Param("instructionIdList") List<Integer> instructionIdList);

	@Query(value = "SELECT distinct so.so_number, wm.party_id "
			+ " FROM jsw_sales_order so, jsw_sales_order_child so_child, jsw_material_master mm, jsw_warehouse_master wm"
			+ " WHERE so_child.so_id = so.so_id and mm.mm_id = so_child.mm_id AND wm.ware_house_id = so_child.wearhouse_id AND so.is_deleted = 0"
			+ " AND so_child.is_deleted = 0 AND wm.party_id in :locationList ", 
		nativeQuery = true)
	List<Object[]> fetchMappedSOList(@Param("locationList") List<Integer> locationList);

	
	@Query(value = "SELECT distinct so_child.mm_id, so_child.soqty "
			+ " FROM jsw_sales_order so, jsw_sales_order_child so_child, jsw_material_master mm "
			+ " WHERE so_child.so_id = so.so_id and mm.mm_id = so_child.mm_id"
			+ " AND so.is_deleted = 0 AND so_child.is_deleted = 0 AND so.so_number= :soNo ", 
		nativeQuery = true)
	List<Object[]> mmidBySO(@Param("soNo") String soNo);
	
}
