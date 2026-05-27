package com.steel.product.jswone.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.SalesOrderJswEntity;

@Repository
public interface SalesOrderJswRepository extends JpaRepository<SalesOrderJswEntity, Integer> {

	@Query(value = "SELECT distinct so.so_id, so.so_number "
			+ " FROM jsw_sales_order so, jsw_sales_order_child so_child "
			+ " where so.is_deleted = 0 and so_child.is_deleted = 0 and so_child.so_id = so.so_id "
			+ " and (case when :warehouseFlag=true then so_child.wearhouse_id in :warehouseList else 1=1 end ) "
			+ " and case when :soId is not null and LENGTH(:soId) >0 then so.so_id = :soId else so.so_id end " 
		  	+ " and case when :status is not null and LENGTH(:status) > 0 then so.so_status = :status else so.so_status  = so.so_status end "  
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (so_child.mm_id like %:searchText% or so.so_number like %:searchText% or so.refno like %:searchText%) else 1=1 end " 
			+ " order by so.so_id desc",
		countQuery = "SELECT count(distinct so.so_id ) " + 
			"  FROM jsw_sales_order so, jsw_sales_order_child so_child" + 
		  	"  where so.is_deleted = 0 and so_child.is_deleted = 0 and so_child.so_id = so.so_id" + 
			"  and (case when :warehouseFlag=true then so_child.wearhouse_id in :warehouseList else 1=1 end ) "+ 
		  	"  and case when :soId is not null and LENGTH(:soId) >0 then so.so_id = :soId else so.so_id end " +
		  	"  and case when :status is not null and LENGTH(:status) > 0 then so.so_status in :status else 1=1 end " + 
		  	"  and case when :searchText is not null and LENGTH(:searchText) >0 then (so_child.mm_id like %:searchText% or so.so_number like %:searchText%) else 1=1 end " +
		  	"  order by so.so_id desc", 
		nativeQuery = true)
	Page<Object[]> listAllSOIDs(@Param("searchText") String searchText, @Param("soId") Integer soId,
			@Param("status") List<String> status, @Param("warehouseFlag") boolean warehouseFlag,
			@Param("warehouseList") List<String> warehouseList, Pageable pageable);
	
	@Query(value = "SELECT so.so_id, so.so_number, so.socreatedate, so.deliverymethod, "
			+ " so.destinationcode, so.refno, so.joplsorefno, so.bizsegment, so.ecommerce, so.supplysource, so.typeofsupply, "
			+ " so.incomingpayment, so.paymentmode, so.terms, so.customerid, so.total_soqty, so.total_allocated_soqty, "
			+ " so.allocated_stts as soallstts, so.so_status , so.zbooks_so, so.expected_delivery_date, so.likely_material_date, so.standard_material_date, so_child.so_child_id,  "
			+ " so_child.mm_id, '' instruction_id, '' inward_entry_d, so_child.soqty, so_child.allocated_soqty, "
			+ " so_child.allocated_stts, so_child.item_so_status, so_child.wearhouse_id , so_child.tax_percentage, mm.mm_description, so_child.hsn_or_sac, wm.ware_house_name, " +
			" br.branch_name, so.cam_code , so.remarks , so.customer_name, so.customer_number , so_child.number_of_sheets , so.special_delivery_instructions, so.order_confirmation_time"
			+ " FROM jsw_sales_order so "
			+ " left outer JOIN jsw_sales_order_child so_child ON so_child.so_id = so.so_id "
			+ " left outer JOIN jsw_branch_master br ON br.branch_id = so.branch_id"
			+ " left outer JOIN jsw_material_master mm ON mm.mm_id = so_child.mm_id" 
			+"  left outer join jsw_warehouse_master wm on wm.ware_house_id = so_child.wearhouse_id "
			+ "where so.is_deleted = 0 and so_child.is_deleted = 0 " +
			" and so.so_id in :soIDsList order by so.so_id desc", nativeQuery = true)
	List<Object[]> listIdWisedetails (@Param("soIDsList") List<Integer> soIDsList);
	
	@Query(value = "SELECT distinct so.so_id, so.so_number "
			+ " FROM jsw_sales_order so, jsw_sales_order_child so_child "
			+ " where so_child.is_deleted = 0 and so_child.so_id = so.so_id"
			+ " and (case when :warehouseFlag=true then so_child.wearhouse_id in :warehouseList else 1=1 end ) "
			+ " and so.is_deleted = 0 "
			+ " and case when :soId is not null and LENGTH(:soId) >0 then so.so_id = :soId else so.so_id end " 
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (so_child.mm_id like %:searchText% or so_child.wearhouse_id like %:searchText% or so.branch_id like %:searchText% or so.refno like %:searchText% or so.so_number like %:searchText%) else 1=1 end " 
			+ " order by so.so_id desc",
		countQuery = "SELECT count(distinct so.so_id ) " + 
			" FROM jsw_sales_order so, jsw_sales_order_child so_child "+ 
			" where so_child.is_deleted = 0 and so_child.so_id = so.so_id"+ 
			" and (case when :warehouseFlag=true then so_child.wearhouse_id in :warehouseList else 1=1 end ) "+ 
			" and so.is_deleted = 0 "+ 
		  	" and case when :soId is not null and LENGTH(:soId) >0 then so.so_id = :soId else so.so_id end " +
		  	" and case when :searchText is not null and LENGTH(:searchText) >0 then (so_child.mm_id like %:searchText% or so_child.wearhouse_id like %:searchText% or so.branch_id like %:searchText% or so.so_number like %:searchText%) else 1=1 end " +
		  	" order by so.so_id desc", 
			nativeQuery = true)
	Page<Object[]> listAllSOIDsCP(
			@Param("searchText") String searchText, 
			@Param("soId") Integer soId,
			@Param("warehouseFlag") boolean warehouseFlag, 
			@Param("warehouseList") List<String> warehouseList,
			Pageable pageable);

	@Query(value = "select so.so_id,so.so_number,so.expected_delivery_date,so.customerid,so.total_soqty, so.cp_status,"
			+ " so_child.so_child_id,instruction_id,so_child.mm_id,inward_entry_id, so_child.soqty, so_child.allocated_soqty, "
			+ " so_child.allocated_stts,so_child.item_so_status,mm.mm_description, alloca.so_allocation_id, alloca.allocated_soqty alloqty, "
			+ " (select coilnumber from product_tblinwardentry inw where inw.inwardentryid = alloca.inward_entry_id) coilno, "
			+ " (select ifnull(actualnoofpieces, plannednoofpieces) from product_instruction ins where ins.instructionid = alloca.instruction_id) noofpieces, "
			+ " 'Sticks roll' packing, "
			+ " (SELECT partyname FROM product_tblpartydetails where npartyid= wm.party_id) partyname,"
			+ " (select statusname from product_tblinwardentry inw, product_status stts where inw.inwardentryid = alloca.inward_entry_id and inw.vstatus = stts.statusid ) stts, "
			+ " so_child.wearhouse_id, ware_house_name, "
			+ " (select concat(inw.fthickness,'*',fwidth,'*',flength) from product_tblinwardentry inw where inw.inwardentryid = alloca.inward_entry_id) size, so.refno, "
			+ " so.branch_id, (select jbm.branch_name from jsw_branch_master jbm where jbm.branch_id =so.branch_id) as branch_name,"
			+ " (select GROUP_CONCAT(distinct aa.pdf_generation_part) from jsw_sales_order_allocation aa where aa.so_id = alloca.so_id)  as parts,"
			+ " (select customerbatchid from product_tblinwardentry inw where inw.inwardentryid = alloca.inward_entry_id) customerbatchid, "
			+ " (SELECT product_name FROM jsw_product_master a where a.product_id=mm.producttype_id limit 1) as materialdesc, " 
			+ " (SELECT grade_name FROM jsw_grade_master grade where grade.grade_id=mm.grade_id limit 1) as materialgrade "  
			+ " FROM jsw_sales_order so "
			+ " left OUTER JOIN jsw_sales_order_child so_child ON so_child.so_id = so.so_id and so_child.is_deleted = 0 "
			+ " left outer JOIN jsw_sales_order_allocation alloca ON so_child.so_child_id = alloca.so_child_id"
			+ " LEFT OUTER JOIN jsw_material_master mm ON mm.mm_id = so_child.mm_id"
			+ " left OUTER join jsw_warehouse_master wm on wm.ware_house_id = so_child.wearhouse_id "
			+ " where (case when :warehouseFlag=true then so_child.wearhouse_id in :warehouseList else 1=1 end ) and so.is_deleted = 0 and so.so_id in :soIDsList order by so.so_id desc", 
			nativeQuery = true)
	List<Object[]> listIdWisedetailsCP(@Param("soIDsList") List<Integer> soIDsList,
			@Param("warehouseFlag") boolean warehouseFlag,
			@Param("warehouseList") List<String> warehouseList);
	
	@Query(value = "select packet_id, inwardid, coilnumber, customerbatchid, mm_id, materialdesc, materialgrade, fthickness, flength, fpresent, partyname,0 noofpieces,fWidth,coilage "
			+ " from ( "
			+ " SELECT parent.inwardentryid inwardid,  coilnumber, customerbatchid, parent.mm_id, " 
			+ " (SELECT product_name FROM jsw_product_master a, jsw_material_master mat where a.product_id=mat.producttype_id and mat.mm_id=parent.mm_id limit 1) as  materialdesc, " 
			+ " (SELECT subgrade_name FROM jsw_subgrade_master grade, jsw_material_master mat where grade.subgrade_id=mat.subgrade_id and mat.mm_id=parent.mm_id limit 1) as  materialgrade, "  
			+ " fthickness, NULL as packet_id, fWidth, " 
			+ " coalesce( parent.flength,0) flength, fpresent, " 
			+ " fquantity, partyname,0  as siltcutcnt, DATEDIFF(curdate() , date_format(parent.createdon, '%Y-%m-%d')) coilage "  
			+ " FROM product_tblinwardentry parent, product_tblpartydetails party, "
			+ " jsw_material_master mat "
			+ " where fpresent>0 and parent.isdeleted=0 and  party.npartyid = parent.npartyid "
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (parent.coilNumber like %:searchText% or parent.customerBatchId like %:searchText% or parent.customerInvoiceNo like %:searchText%) else 1=1 end " 
			+ " and vstatus in (1,2,3) " 
			+ " and mat.mm_id = parent.mm_id " 
			+ " and mat.grade_id = :gradeId "
			+ " and mat.subgrade_id= :subgradeId "
			+ " and mat.form_id not in (21)"
			+ " and mat.thickness= :thickness "
			+ " AND parent.fwidth = :width "
			+ " and case when :partyIdsFlag=true then parent.npartyid in :partyIds else 1=1 end"
			+ ") a "
			+ " where 1=1 ",
		countQuery = "SELECT count(inwardid) from "
			+ " (select parent.inwardentryid inwardid, coilnumber "
			+ " FROM product_tblinwardentry parent, product_tblpartydetails party, "
			+ " jsw_material_master mat "
			+ " where fpresent>0 and parent.isdeleted=0 and  party.npartyid = parent.npartyid "
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (parent.coilNumber like %:searchText% or parent.customerBatchId like %:searchText% or parent.customerInvoiceNo like %:searchText%) else 1=1 end " 
			+ " and vstatus in (1,2,3) " 
			+ " and mat.mm_id = parent.mm_id " 
			+ " and mat.grade_id = :gradeId "
			+ " and mat.subgrade_id= :subgradeId "
			+ " and mat.form_id not in (21) "
			+ " and mat.thickness= :thickness "
			+ " AND parent.fwidth = :width "
			+ " and case when :partyIdsFlag=true then parent.npartyid in :partyIds else 1=1 end"
			+ ") a "
			+ " where 1=1", nativeQuery = true)
	Page<Object[]> findCoilInventory(
			@Param("searchText") String searchText,
			@Param("partyIds") List<Integer> partyIds,
			@Param("partyIdsFlag") boolean partyIdsFlag,
			@Param("gradeId") int gradeId,
			@Param("subgradeId") int subgradeId,
			@Param("thickness") BigDecimal thickness,
			@Param("width") BigDecimal width,
			Pageable pageable);

	@Query(value = "select packet_id, inwardid, coilnumber, customerbatchid, mm_id, materialdesc, materialgrade,fthickness, flength, fweight, partyname,"
			+ "  noofpieces, fWidth,coilage "
			+ " from ( SELECT parent.inwardentryid inwardid,  coilnumber, customerbatchid, parent.mm_id,"
			+ " (SELECT product_name FROM jsw_product_master a, jsw_material_master mat where a.product_id=mat.producttype_id and mat.mm_id=parent.mm_id limit 1) as  materialdesc,"
			+ " (SELECT subgrade_name FROM jsw_subgrade_master grade, jsw_material_master mat where grade.subgrade_id=mat.subgrade_id and mat.mm_id=parent.mm_id limit 1) as  materialgrade, "  
			+ "	fthickness, instructionid as packet_id,  coalesce(actualwidth, plannedwidth) fWidth, coalesce( parent.flength,0) flength, "
			+ " coalesce(child.actualweight, child.plannedweight) fweight, fquantity, partyname,  "
			+ " coalesce(child.actualnoofpieces, child.plannednoofpieces) noofpieces, "
			+ " (SELECT count(distinct a.instructionid) cnt FROM product_instruction a where a.inwardid=parent.inwardentryid and status!=4 and a.parentgroupid=child.groupid) as siltcutcnt, "
			+ " parent.npartyid, DATEDIFF(curdate(), date_format(parent.createdon, '%Y-%m-%d')) coilage"
			+ " FROM product_tblinwardentry parent, " 
			+ "	product_instruction child, "  
			+ "	product_tblpartydetails party, " 
			+ "	jsw_material_master mat " 
			+ "	where child.status in (1,2,3) and parent.vstatus in (1,2,3) and parent.inwardentryid = child.inwardid "
			+ " and (child.allocated_soqty=0 or child.allocated_soqty is null ) and child.isdeleted=0  "
			+ " and parent.isdeleted=0 and party.npartyid = parent.npartyid" 
			+ " and mat.mm_id = parent.mm_id " 
			+ "	and case when :searchText is not null and LENGTH(:searchText) >0 then (parent.coilNumber like %:searchText% or parent.customerBatchId like %:searchText% or parent.customerInvoiceNo like %:searchText%) else 1=1 end " 
			+ "	and case when :partyIdsFlag=true then parent.npartyid in :partyIds else 1=1 end  "
			+ " and case when :packetStatus in (2, 3) then child.status = :packetStatus else 1=1 end "
			+ " and mat.grade_id = :gradeId "
			+ " and mat.subgrade_id= :subgradeId "
			+ " and mat.thickness= :thickness "
			+ " AND coalesce(child.actualwidth, child.plannedwidth) = :width "
			+ " and coalesce(child.actuallength, child.plannedlength) = :length) a "
			+ " where fweight>0 AND CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END",
		countQuery = "SELECT count(packet_id) from "
			+ " (select instructionid as packet_id, coalesce(actualweight, plannedweight) fweight, "
			+ " (SELECT count(distinct inss.instructionid) cnt FROM product_instruction inss where inss.inwardid=parent.inwardentryid  and status!=4 and inss.parentgroupid=child.groupid) as siltcutcnt"
			+ " FROM product_tblinwardentry parent, " 
			+ "	product_instruction child, "  
			+ "	product_tblpartydetails party, " 
			+ "	jsw_material_master mat " 
			+ "	where child.status in (1,2,3) and parent.vstatus in (1,2,3) and parent.inwardentryid = child.inwardid "
			+ " and (child.allocated_soqty=0 or child.allocated_soqty is null ) and child.isdeleted=0  "
			+ " and parent.isdeleted=0 and party.npartyid = parent.npartyid" 
			+ " and mat.mm_id = parent.mm_id " 
			+ "	and case when :searchText is not null and LENGTH(:searchText) >0 then (parent.coilNumber like %:searchText% or parent.customerBatchId like %:searchText% or parent.customerInvoiceNo like %:searchText%) else 1=1 end " 
			+ "	and case when :partyIdsFlag=true then parent.npartyid in :partyIds else 1=1 end  "
			+ " and case when :packetStatus in (2, 3) then child.status = :packetStatus else 1=1 end "
			+ " and mat.grade_id = :gradeId "
			+ " and mat.subgrade_id= :subgradeId "
			+ " and mat.thickness= :thickness "
			+ " AND coalesce(child.actualwidth, child.plannedwidth) = :width "
			+ " and coalesce(child.actuallength, child.plannedlength) = :length) a "
			+ " where fweight>0 AND CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END", nativeQuery = true)
	Page<Object[]> findPacketInventory(
			@Param("searchText") String searchText,
			@Param("partyIds") List<Integer> partyIds,
			@Param("partyIdsFlag") boolean partyIdsFlag,
			@Param("packetStatus") int packetStatus, 
			@Param("gradeId") int gradeId,
			@Param("subgradeId") int subgradeId,
			@Param("thickness") BigDecimal thickness,
			@Param("width") BigDecimal width,
			@Param("length") BigDecimal length,
			Pageable pageable);

	@Query(value = "select packet_id, inwardid, coilnumber, customerbatchid, mm_id, materialdesc, materialgrade,fthickness, flength, "
			+ " fweight, partyname, 0 as actualNoOfPieces,fWidth,coilage "
			+ " from ( 	"
			+ " SELECT parent.inwardentryid inwardid, coilnumber, customerbatchid, parent.mm_id,  "
			+ " (SELECT product_name FROM jsw_product_master a, jsw_material_master mat where a.product_id=mat.producttype_id and mat.mm_id=parent.mm_id limit 1) as  materialdesc,  "
			+ " (SELECT subgrade_name FROM jsw_subgrade_master grade, jsw_material_master mat where grade.subgrade_id=mat.subgrade_id and mat.mm_id=parent.mm_id limit 1) as  materialgrade, "  
			+ " fthickness, 0 as packet_id,  fWidth,  "
			+ " coalesce( parent.flength,0) flength,  fpresent fweight, fquantity, partyname,"
			+ " parent.npartyid, DATEDIFF(curdate(), date_format(parent.createdon, '%Y-%m-%d')) coilage"
			+ " FROM product_tblinwardentry parent, product_tblpartydetails party  "
			+ " where parent.vstatus in (1,2,3) and parent.isdeleted=0 and party.npartyid = parent.npartyid  "
			+ " and parent.mm_id = :mmid) a where 1=1",
			countQuery = "SELECT count(inwardid) from ("
			+ " SELECT parent.inwardentryid inwardid,  coilnumber, customerbatchid, parent.mm_id,  "
			+ " (SELECT product_name FROM jsw_product_master a, jsw_material_master mat where a.product_id=mat.producttype_id and mat.mm_id=parent.mm_id limit 1) as  materialdesc,  "
			+ " (SELECT grade_name FROM jsw_grade_master grade, jsw_material_master mat where grade.grade_id=mat.grade_id and mat.mm_id=parent.mm_id limit 1) as  materialgrade,	"
			+ " fthickness, '' as packet_id,  fWidth, "
			+ " coalesce( parent.flength,0) flength,  fpresent fweight, fquantity,in_stock_weight inStockWeight, "
			+ " 0 actualNoOfPieces,'' as process_status, '' instruction_status,partyname, '' as classification_tag, "
			+ " '' as enduser_tag_name, parent.npartyid"
			+ " FROM product_tblinwardentry parent, product_tblpartydetails party  "
			+ " where parent.vstatus in (1,2,3) and parent.isdeleted=0 and party.npartyid = parent.npartyid  "
			+ " and parent.mm_id = :mmid) a  where 1=1", 
		nativeQuery = true)
	Page<Object[]> findSheetInventory(@Param("mmid") String mmid, Pageable pageable);

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

	@Query(value = "select distinct so.so_id,so.so_number,so.expected_delivery_date,so.customerid, so.total_soqty,so.cp_status,"
			+ " so_child.so_child_id,instruction_id,so_child.mm_id,inward_entry_id, so_child.soqty, alloca.allocated_soqty,"
			+ " so_child.allocated_stts,so_child.item_so_status,mm.mm_description, alloca.so_allocation_id,alloca.allocated_soqty allocated_s, "
			+ " (select coilnumber from product_tblinwardentry inw where inw.inwardentryid = alloca.inward_entry_id) coilno, "
			+ " (select ifnull(actualweight, plannedweight ) from product_instruction ins where ins.instructionid = alloca.instruction_id) packetweight, "
			+ " (SELECT subgrade_name FROM jsw_subgrade_master a where a.subgrade_id=mm.subgrade_id) as subgrade_name, " 
			+ " (SELECT grade_name FROM jsw_grade_master grade where grade.grade_id=mm.grade_id) as  grade_name,"
			+ " thickness, width, length, (SELECT partyname FROM product_tblpartydetails where npartyid= wm.party_id) partyname, "
			+ " so.refno, so.branch_id, ware_house_name,"
			+ " (select branch_name from jsw_branch_master brnch where brnch.branch_id = so.branch_id) brnchname "
			+ " FROM jsw_sales_order so "
			+ " left OUTER JOIN jsw_sales_order_child so_child ON so_child.so_id = so.so_id and so_child.is_deleted = 0  "
			+ " left outer JOIN jsw_sales_order_allocation alloca ON so_child.so_child_id = alloca.so_child_id"
			+ " LEFT OUTER JOIN jsw_material_master mm ON mm.mm_id = so_child.mm_id"
			+ " left OUTER join jsw_warehouse_master wm on wm.ware_house_id = so_child.wearhouse_id "
			+ " where so.is_deleted = 0 and alloca.inward_entry_id = :inwardEntryId order by so.so_id desc", nativeQuery = true)
	List<Object[]> coilAllocationDetails(@Param("inwardEntryId") int inwardEntryId);

	@Query(value = "select so.so_id,so.refno so_number,material_name,so.customerid,so.total_soqty, so.cp_status,"
			+ " so_child.so_child_id, DATE_FORMAT(socreatedate, '%d-%m-%Y'), so_child.mm_id, inward.inwardentryid, so_child.soqty, so_child.allocated_soqty, "
			+ " so_child.allocated_stts,so_child.item_so_status,mm.mm_description, alloca.so_allocation_id, alloca.allocated_soqty alloqty, "
			+ " coilnumber, "
			+ " customerbatchid, "
			+ " (select ifnull(actualnoofpieces, plannednoofpieces) from product_instruction ins where ins.instructionid = alloca.instruction_id) noofpieces, "
			+ " 'Loose Bundle' packing, "
			+ " (SELECT partyname FROM product_tblpartydetails where npartyid= wm.party_id) partyname,"
			+ " (select statusname from product_status stts where stts.statusid =inward.vstatus) stts, "
			+ " inward.fwidth, ware_house_name, "
			+ " inward.fthickness , "
			+ " (SELECT grade_name FROM jsw_grade_master grade, jsw_material_master mat where grade.grade_id=mat.grade_id and mat.mm_id=inward.mm_id limit 1) as  materialgrade,"
			+ " (SELECT product_name FROM jsw_product_master a, jsw_material_master mat where a.product_id=mat.producttype_id and mat.mm_id=inward.mm_id limit 1) as  materialdesc,  "
			+ " (select jbm.branch_name from jsw_branch_master jbm where jbm.branch_id =so.branch_id) as branch_name,"
			+ " (select GROUP_CONCAT(distinct aa.pdf_generation_part) from jsw_sales_order_allocation aa where aa.so_id = :soId)  as parts"
			+ " FROM jsw_sales_order so "
			+ " left OUTER JOIN jsw_sales_order_child so_child ON so_child.so_id = so.so_id and so_child.is_deleted = 0 "
			+ " left outer JOIN jsw_sales_order_allocation alloca ON so_child.so_child_id = alloca.so_child_id"
			+ " left outer JOIN product_tblinwardentry inward ON inward.inwardentryid = alloca.inward_entry_id"
			+ " LEFT OUTER JOIN jsw_material_master mm ON mm.mm_id = so_child.mm_id"
			+ " left OUTER join jsw_warehouse_master wm on wm.ware_house_id = so_child.wearhouse_id "
			+ " where so_child.so_id = so.so_id and so_child.is_deleted = 0 "
			+ " and so_child.so_child_id = alloca.so_child_id"
			+ " and inward.inwardentryid = alloca.inward_entry_id and so.so_id = alloca.so_id "
		  	+"  and case when :pdfGenerationPart is not null and LENGTH(:pdfGenerationPart) >0 then alloca.pdf_generation_part= :pdfGenerationPart else (alloca.pdf_generation_part IS NULL OR alloca.pdf_generation_part = '') end" 
			+ " and mm.mm_id = so_child.mm_id and so_child.so_id = so.so_id "
			+ " and alloca.so_id = :soId and so.is_deleted = 0 "
			+ " order by so_child.so_child_id asc ", nativeQuery = true)
	List<Object[]> soDetailsBySoId(@Param("soId") Integer soId, @Param("pdfGenerationPart") String pdfGenerationPart);

	@Modifying
	@Transactional
	@Query(value = "update jsw_sales_order set cp_status= :cpStatus where so_id =:soId", nativeQuery = true)
	public int updateCPStataus(@Param("cpStatus") String cpStatus,  @Param("soId") int soId);

	@Modifying
	@Transactional
	@Query(value = "update jsw_sales_order set so_status = :soStatus where so_id =:soId", nativeQuery = true)
	public int updateSOStataus( @Param("soStatus") String soStatus, @Param("soId") int soId);

	@Query(value = "SELECT alloca.so_id, CAST(alloca.so_child_id AS SIGNED) AS so_child_id, so_allocation_id, so.cp_status, "
			+ " inward_stts.statusname AS inward_stts, packet_stts.statusname AS packet_stts, "
			+ " alloca.inward_entry_id, alloca.instruction_id "
			+ " FROM jsw_sales_order so"
			+ " JOIN jsw_sales_order_child so_child ON so_child.so_id = so.so_id  AND so_child.is_deleted = 0 "
			+ " JOIN jsw_sales_order_allocation alloca ON so_child.so_child_id = alloca.so_child_id AND so.so_id = alloca.so_id"
			+ " JOIN product_tblinwardentry inward ON inward.inwardentryid = alloca.inward_entry_id"
			+ " LEFT JOIN product_instruction ins ON ins.instructionid = alloca.instruction_id"
			+ " LEFT JOIN product_status inward_stts ON inward_stts.statusid = inward.vstatus"
			+ " LEFT JOIN product_status packet_stts ON packet_stts.statusid = ins.status"
			+ " WHERE so_child.allocated_stts='COMPLETED' and so.cp_status not in ('CP_PLAN_COMPLETED')  and refno='dhruvitest007'"
			+ " order by alloca.so_id desc ", nativeQuery = true)
	List<Object[]> getAllSODetailsWithAllocationStatusforCPStatus();
	
	@Query(value = "SELECT alloca.so_id, CAST(alloca.so_child_id AS SIGNED) AS so_child_id, so_allocation_id, so.cp_status, "
			+ " inward_stts.statusname AS inward_stts, packet_stts.statusname AS packet_stts, "
			+ " alloca.inward_entry_id, alloca.instruction_id, "
			+ " (select form_id from jsw_material_master wm where wm.mm_id=inward.mm_id) as form_id "
			+ " FROM jsw_sales_order so"
			+ " JOIN jsw_sales_order_child so_child ON so_child.so_id = so.so_id  AND so_child.is_deleted = 0 "
			+ " JOIN jsw_sales_order_allocation alloca ON so_child.so_child_id = alloca.so_child_id AND so.so_id = alloca.so_id"
			+ " JOIN product_tblinwardentry inward ON inward.inwardentryid = alloca.inward_entry_id"
			+ " LEFT JOIN product_instruction ins ON ins.instructionid = alloca.instruction_id"
			+ " LEFT JOIN product_status inward_stts ON inward_stts.statusid = inward.vstatus"
			+ " LEFT JOIN product_status packet_stts ON packet_stts.statusid = ins.status"
			+ " WHERE so.so_status not in ('FULFILLED') "
			+ " order by alloca.so_id desc ", 
		nativeQuery = true)
	List<Object[]> getAllSODetailsWithAllocationStatusforSOStatus();

	@Query(value = "SELECT so_child.so_id, CAST(so_child.so_child_id AS SIGNED) AS so_child_id,"
			+ " so.cp_status, so_child.item_so_status, so.so_status"
			+ " FROM jsw_sales_order so"
			+ " JOIN jsw_sales_order_child so_child ON so_child.so_id = so.so_id  AND so_child.is_deleted = 0 "
			+ " WHERE so.cp_status not in ('CP_PLAN_COMPLETED') order by so_child.so_id desc", nativeQuery = true)
	List<Object[]> getAllSODetailsWithPacketStatus();

	@Query(value = "SELECT \r\n" + 
			"    CAST((\r\n" + 
			"        SELECT COUNT(so_id)\r\n" + 
			"        FROM jsw_sales_order\r\n" + 
			"        WHERE MONTH(created_on) = MONTH(CURDATE())\r\n" + 
			"          AND YEAR(created_on) = YEAR(CURDATE())\r\n" + 
			"    ) AS SIGNED) AS totalOrders,\r\n" + 
			"\r\n" + 
			"    (\r\n" + 
			"        SELECT SUM(total_soqty)\r\n" + 
			"        FROM jsw_sales_order\r\n" + 
			"        WHERE MONTH(created_on) = MONTH(CURDATE())\r\n" + 
			"          AND YEAR(created_on) = YEAR(CURDATE())\r\n" + 
			"    ) AS totalOrders_totalWeight,\r\n" + 
			"\r\n" + 
			"    CAST((\r\n" + 
			"        SELECT COUNT(so_id)\r\n" + 
			"        FROM jsw_sales_order\r\n" + 
			"        WHERE so_status = 'SO_APPROVED'\r\n" + 
			"          AND MONTH(approved_date) = MONTH(CURDATE())\r\n" + 
			"          AND YEAR(approved_date) = YEAR(CURDATE())\r\n" + 
			"    ) AS SIGNED) AS totalOrders1,\r\n" + 
			"\r\n" + 
			"    (\r\n" + 
			"        SELECT SUM(total_soqty)\r\n" + 
			"        FROM jsw_sales_order\r\n" + 
			"        WHERE so_status = 'SO_APPROVED'\r\n" + 
			"          AND MONTH(approved_date) = MONTH(CURDATE())\r\n" + 
			"          AND YEAR(approved_date) = YEAR(CURDATE())\r\n" + 
			"    ) AS totalOrders_totalWeight1 ", nativeQuery = true)
	List<Object[]> dashboard( );
	
	@Query(value = " select distinct so.so_number,chld.mm_id from" + 
			" jsw_sales_order so, jsw_sales_order_child chld, jsw_sales_order_allocation allo, jsw_material_master mm" + 
			" where so.so_id=allo.so_id and so.so_id=chld.so_id and allo.so_child_id=chld.so_child_id" + 
			" and mm.mm_id =chld.mm_id and mm.form_id=21 "+ 
			" and allo.inward_entry_id=:inwardId", nativeQuery = true)
	List<Object[]> fetchMappedSheetSONo(@Param("inwardId") int inwardId);
	
}
