package com.steel.product.jswone.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.SalesOrderJswEntity;

@Repository
public interface SalesOrderJswRepository extends JpaRepository<SalesOrderJswEntity, Integer> {
	
	

	@Query(value = "SELECT distinct so.so_id, so.so_number "
			+ " FROM jsw_sales_order so, jsw_sales_order_child so_child "
			+ " where so.is_deleted = 0 and so_child.is_deleted = 0 and so_child.so_id = so.so_id "
			+ " and case when :soId is not null and LENGTH(:soId) >0 then so.so_id = :soId else so.so_id end " 
		  	+ " and case when :status is not null and LENGTH(:status) > 0 then so.so_status = :status else so.so_status  = so.so_status end "  
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (so_child.mm_id like %:searchText% or so.so_number like %:searchText%) else 1=1 end " 
			+ " order by so.so_id desc",
		countQuery = "SELECT count(distinct so.so_id ) " + 
			"  FROM jsw_sales_order so, jsw_sales_order_child so_child" + 
		  	"  where so.is_deleted = 0 and so_child.is_deleted = 0 and so_child.so_id = so.so_id" + 
		  	"  and case when :soId is not null and LENGTH(:soId) >0 then so.so_id = :soId else so.so_id end " +
		  	"  and case when :status is not null and LENGTH(:status) > 0 then so.so_status in :status else 1=1 end " + 
		  	"  and case when :searchText is not null and LENGTH(:searchText) >0 then (so_child.mm_id like %:searchText% or so.so_number like %:searchText%) else 1=1 end " +
		  	"  order by so.so_id desc", 
		nativeQuery = true)
	Page<Object[]> listAllSOIDs(@Param("searchText") String searchText, @Param("soId") Integer soId,
			@Param("status") List<String> status, Pageable pageable);
	
	@Query(value = "SELECT so.so_id, so.so_number, so.socreatedate, so.deliverymethod, "
			+ " so.destinationcode, so.refno, so.joplsorefno, so.bizsegment, so.ecommerce, so.supplysource, so.typeofsupply, "
			+ " so.incomingpayment, so.paymentmode, so.terms, so.customerid, so.total_soqty, so.total_allocated_soqty, "
			+ " so.allocated_stts as soallstts, so.so_status , so.zbooks_so, so.expected_delivery_date, so.likely_material_date, so.standard_material_date, so_child.so_child_id,  "
			+ " so_child.mm_id, '' instruction_id, '' inward_entry_d, so_child.soqty, so_child.allocated_soqty, "
			+ " so_child.allocated_stts, so_child.item_so_status, so_child.wearhouse_id , so_child.tax_percentage, mm.mm_description, so_child.hsn_or_sac, wm.ware_house_name, br.branch_name, so.cam_code , so.remarks"
			+ " FROM jsw_sales_order so "
			+ " left outer JOIN jsw_sales_order_child so_child ON so_child.so_id = so.so_id "
			+ " left outer JOIN jsw_branch_master br ON br.branch_id = so.branch_id"
			+ " left outer JOIN jsw_material_master mm ON mm.mm_id = so_child.mm_id" 
			+"  left outer join jsw_warehouse_master wm on wm.ware_house_id = so_child.wearhouse_id "
			+ "where so.is_deleted = 0 and so_child.is_deleted = 0 " +
			" and so.so_id in :soIDsList order by so.so_id desc", nativeQuery = true)
	List<Object[]> listIdWisedetails (@Param("soIDsList") List<Integer> soIDsList);
	
	@Query(value = "SELECT distinct so.so_id, so.so_number "
			+ " FROM jsw_sales_order so"
			+ " left outer join jsw_sales_order_child so_child  on so_child.is_deleted = 0 and so_child.so_id = so.so_id "
			+ " where so.is_deleted = 0 "
			+ " and case when :soId is not null and LENGTH(:soId) >0 then so.so_id = :soId else so.so_id end " 
		  	+ " and case when :status is not null and LENGTH(:status) > 0 then so.so_status = :status else so.so_status  = so.so_status end "  
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (so_child.mm_id like %:searchText% or so_child.wearhouse_id like %:searchText% or so.branch_id like %:searchText% or so.so_number like %:searchText%) else 1=1 end " 
			+ " order by so.so_id desc",
		countQuery = "SELECT count(distinct so.so_id ) " + 
			" FROM jsw_sales_order so"+ 
			" left outer join jsw_sales_order_child so_child  on so_child.is_deleted = 0 and so_child.so_id = so.so_id "+ 
			" where so.is_deleted = 0 "+ 
		  	" and case when :soId is not null and LENGTH(:soId) >0 then so.so_id = :soId else so.so_id end " +
		  	" and case when :status is not null and LENGTH(:status) > 0 then so.so_status in :status else 1=1 end " + 
		  	" and case when :searchText is not null and LENGTH(:searchText) >0 then (so_child.mm_id like %:searchText% or so_child.wearhouse_id like %:searchText% or so.branch_id like %:searchText% or so.so_number like %:searchText%) else 1=1 end " +
		  	" order by so.so_id desc", 
		nativeQuery = true)
	Page<Object[]> listAllSOIDsCP(@Param("searchText") String searchText, @Param("soId") Integer soId, @Param("status") List<String> status, Pageable pageable);

	@Query(value = "select so.so_id,so.so_number,so.expected_delivery_date,so.customerid,so.total_soqty,so.cp_status,"
			+ " so_child.so_child_id,instruction_id,so_child.mm_id,inward_entry_id,so_child.soqty,so_child.allocated_soqty,"
			+ " so_child.allocated_stts,so_child.item_so_status,mm.mm_description, alloca.so_allocation_id, alloca.allocated_soqty allallocated_soqty, "
			+ " (select coilnumber from product_tblinwardentry inw where inw.inwardentryid = alloca.inward_entry_id) coilno, "
			+ " (select ifnull(actualweight, plannedweight ) from product_instruction ins where ins.instructionid = alloca.instruction_id) packetweight, "
			+ " 'Sticks roll' packing, "
			+ " (SELECT partyname FROM product_tblpartydetails where npartyid= wm.party_id) partyname,"
			+ " (select statusname from product_tblinwardentry inw, product_status stts where inw.inwardentryid = alloca.inward_entry_id and inw.vstatus = stts.statusid ) stts, "
			+ " so.branch_id, ware_house_name, "
			+ " (select branch_name from jsw_branch_master brnch where brnch.branch_id = so.branch_id) brnchname "
			+ " FROM jsw_sales_order so "
			+ " left OUTER JOIN jsw_sales_order_child so_child ON so_child.so_id = so.so_id and so_child.is_deleted = 0  "
			+ " left outer JOIN jsw_sales_order_allocation alloca ON so_child.so_child_id = alloca.so_child_id"
			+ " LEFT OUTER JOIN jsw_material_master mm ON mm.mm_id = so_child.mm_id"
			+ " left OUTER join jsw_warehouse_master wm on wm.ware_house_id = so_child.wearhouse_id "
			+ " where so.is_deleted = 0 " + " and so.so_id in :soIDsList order by so.so_id desc", nativeQuery = true)
	List<Object[]> listIdWisedetailsCP(@Param("soIDsList") List<Integer> soIDsList);
	
	@Query(value = "select packet_id, inwardid, coilnumber, customerbatchid, mm_id, materialdesc, materialgrade,fthickness, flength, fpresent,  partyname,fWidth, "
			+ " inStockWeight, actualNoOfPieces , process_status, instruction_status, classification_tag, enduser_tag_name "
			+ " from ( "
			+ " SELECT parent.inwardentryid inwardid,  coilnumber, customerbatchid, parent.mm_id, " 
			+ " (SELECT product_name FROM jsw_product_master a, jsw_material_master mat where a.product_id=mat.producttype_id and mat.mm_id=parent.mm_id limit 1) as  materialdesc, " 
			+ " (SELECT grade_name FROM jsw_grade_master grade, jsw_material_master mat where grade.grade_id=mat.grade_id and mat.mm_id=parent.mm_id limit 1) as  materialgrade, "  
			+ " fthickness, NULL  as packet_id, fWidth," 
			+ " coalesce( parent.flength,0) flength,  fpresent, " 
			+ " fquantity,in_stock_weight inStockWeight, 0 actualNoOfPieces, "
			+ " NULL as process_status , " 
			+ " NULL as instruction_status,partyname, " 
			+ " NULL as classification_tag," 
			+ " NULL as enduser_tag_name,0  as siltcutcnt, parent.npartyid"  
			+ " FROM product_tblinwardentry parent, product_tblpartydetails party, jsw_material_master material"
			+ " where fpresent>0 and parent.isdeleted=0 and  party.npartyid = parent.npartyid and parent.mm_id = material.mm_id "
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (parent.coilNumber like %:searchText% or parent.customerBatchId like %:searchText% or parent.customerInvoiceNo like %:searchText%) else 1=1 end " 
			+ " and case when :allocationType = 'INWARDSHEET' then form_id = 21 else form_id != 21 end " 
			+ " and vstatus in (2,3) " 
			+ " and case when :partyIdsFlag=true then parent.npartyid in :partyIds else 1=1 end"
			+ ") a "
			+ " where 1=1 ",
		countQuery = "SELECT count(inwardid) from "
			+ " (select parent.inwardentryid inwardid, coilnumber "
			+ " FROM product_tblinwardentry parent, product_tblpartydetails party, jsw_material_master material"
			+ " where fpresent>0 and parent.isdeleted=0 and party.npartyid = parent.npartyid and parent.mm_id = material.mm_id  "
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (parent.coilNumber like %:searchText% or parent.customerBatchId like %:searchText% or parent.customerInvoiceNo like %:searchText%) else 1=1 end " 
			+ " and case when :allocationType = 'INWARDSHEET' then form_id = 21 else form_id != 21 end " 
			+ " and vstatus in (2,3) " 
			+ " and case when :partyIdsFlag=true then parent.npartyid in :partyIds else 1=1 end"
			+ ") a "
			+ " where 1=1", nativeQuery = true)
	Page<Object[]> findCoilInventory(@Param("searchText") String searchText,@Param("allocationType") String allocationType,
			@Param("partyIds") List<Integer> partyIds, @Param("partyIdsFlag") boolean partyIdsFlag,
			 Pageable pageable);

	@Query(value = "select packet_id, inwardid, coilnumber, customerbatchid, mm_id, materialdesc, materialgrade,fthickness, flength,fweight,  partyname,fWidth, "
			+ " inStockWeight, actualNoOfPieces , process_status, instruction_status, classification_tag, enduser_tag_name "
			+ " from ( SELECT parent.inwardentryid inwardid,  coilnumber, customerbatchid, parent.mm_id,"
			+ " (SELECT product_name FROM jsw_product_master a, jsw_material_master mat where a.product_id=mat.producttype_id and mat.mm_id=parent.mm_id limit 1) as  materialdesc,"
			+ " (SELECT grade_name FROM jsw_grade_master grade, jsw_material_master mat where grade.grade_id=mat.grade_id and mat.mm_id=parent.mm_id limit 1) as  materialgrade,"
			+ "	fthickness, instructionid as packet_id,  coalesce(actualwidth, plannedwidth) fWidth, coalesce( parent.flength,0) flength, "
			+ " coalesce(actualweight, plannedweight) fweight, fquantity,in_stock_weight inStockWeight, plannednoofpieces actualNoOfPieces,"
			+ " (select processname from product_process where processid=child.processid) as process_status ,"
			+ " (select statusname from product_status where statusid=child.status) as instruction_status,partyname, "
			+ " (select classification_name from product_packet_classification where classification_id=child.packet_classification_id) as classification_tag, "
			+ " (select tag_name from product_enduser_tags where tag_id=child.enduser_tag_id) as enduser_tag_name,	 "
			+ " (SELECT count(distinct a.instructionid) cnt FROM product_instruction a where a.inwardid=parent.inwardentryid and status!=4 and a.parentgroupid=child.groupid) as siltcutcnt, parent.npartyid "
			+ " FROM product_tblinwardentry parent, " 
			+ "	product_instruction child , "  
			+ "	product_tblpartydetails party " 
			+ "	where parent.inwardentryid = child.inwardid "
			+ " and (child.allocated_soqty=0 or child.allocated_soqty is null ) and child.isdeleted=0  "
			+ " and parent.isdeleted=0 and party.npartyid = parent.npartyid" 
			+ "	and case when :searchText is not null and LENGTH(:searchText) >0 then (parent.coilNumber like %:searchText% or parent.customerBatchId like %:searchText% or parent.customerInvoiceNo like %:searchText%) else 1=1 end " 
			+ "	and case when :partyIdsFlag=true then parent.npartyid in :partyIds else 1=1 end  "
			+ " and case when :packetStatus in (2, 3) then child.status = :packetStatus else 1=1 end "
			+ ") a "
			+ " where fweight>0 AND CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END",
		countQuery = "SELECT count(packet_id) from "
			+ " (select instructionid as packet_id, coalesce(actualweight, plannedweight) fweight, "
			+ " (SELECT count(distinct inss.instructionid) cnt FROM product_instruction inss where inss.inwardid=parent.inwardentryid  and status!=4 and inss.parentgroupid=child.groupid) as siltcutcnt"
			+ " FROM product_tblinwardentry parent, " 
			+ "	product_instruction child , "  
			+ "	product_tblpartydetails party " 
			+ "	where parent.inwardentryid = child.inwardid "
			+ " and (child.allocated_soqty=0 or child.allocated_soqty is null ) and child.isdeleted=0  "
			+ " and parent.isdeleted=0 and party.npartyid = parent.npartyid" 
			+ "	and case when :searchText is not null and LENGTH(:searchText) >0 then (parent.coilNumber like %:searchText% or parent.customerBatchId like %:searchText% or parent.customerInvoiceNo like %:searchText%) else 1=1 end " 
			+ "	and case when :partyIdsFlag=true then parent.npartyid in :partyIds else 1=1 end  "
			+ " and case when :packetStatus in (2, 3) then child.status = :packetStatus else 1=1 end "
			+ ") a "
			+ " where fweight>0 AND CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END", nativeQuery = true)
	Page<Object[]> findInventory(@Param("searchText") String searchText,
			@Param("partyIds") List<Integer> partyIds, @Param("partyIdsFlag") boolean partyIdsFlag,
			@Param("packetStatus") int packetStatus, Pageable pageable);

	Optional<SalesOrderJswEntity> findBySoNumberIgnoreCase(String soNumber);

	Optional<SalesOrderJswEntity> findBySoNumberAndIsDeletedFalse(String soNumber);
	
	@Query(value = "SELECT distinct so.so_id, so.so_number "
			+ " FROM jsw_sales_order so, jsw_sales_order_child so_child "
			+ " where so.is_deleted = 0 and so_child.is_deleted = 0 and so_child.so_id = so.so_id "
			+ " and case when :soId is not null and LENGTH(:soId) >0 then so.so_id = :soId else so.so_id end " 
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (so_child.mm_id like %:searchText% or so.so_number like %:searchText%) else 1=1 end " 
			+ " order by so.so_id desc",
		countQuery = "SELECT count(distinct so.so_id ) " + 
			"  FROM jsw_sales_order so, jsw_sales_order_child so_child" + 
		  	"  where so.is_deleted = 0 and so_child.is_deleted = 0 and so_child.so_id = so.so_id" + 
		  	"  and case when :soId is not null and LENGTH(:soId) >0 then so.so_id = :soId else so.so_id end " + 
		  	"  and case when :searchText is not null and LENGTH(:searchText) >0 then (so_child.mm_id like %:searchText% or so.so_number like %:searchText%) else 1=1 end" + 
		  	"  order by so.so_id desc", 
		nativeQuery = true)
	Page<Object[]> listAllSOIDsConsolidatePlan(@Param("searchText") String searchText, @Param("soId") Integer soId, Pageable pageable);

	SalesOrderJswEntity findBySoId(Integer soId);

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
}
