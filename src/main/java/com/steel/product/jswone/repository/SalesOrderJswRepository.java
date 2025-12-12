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
			+ " so_child.allocated_stts, so_child.item_status, so_child.wearhouse_id , mm.mm_description, mm.hsn, mm.tax, wm.ware_house_name, br.branch_name" +
			" FROM jsw_sales_order so INNER JOIN jsw_sales_order_child so_child ON so_child.so_id = so.so_id INNER JOIN jsw_sales_order_branch br ON br.branch_id = so.branch_id LEFT JOIN jsw_material_master mm ON mm.mm_id = so_child.mm_id" +
			" left join jsw_warehouse_master wm on wm.ware_house_id = so_child.wearhouse_id where so.is_deleted = 0 and so_child.is_deleted = 0 " +
			" and so.so_id in :soIDsList order by so.so_id desc", nativeQuery = true)
	List<Object[]> listIdWisedetails (@Param("soIDsList") List<Integer> soIDsList);
	
	@Query(value = "select packet_id, inwardid, coilnumber, customerbatchid, mm_id, materialdesc, materialgrade,fthickness, flength,fquantity,  partyname,fweight,fWidth, "
			+ "inStockWeight, actualNoOfPieces , process_status, instruction_status, classification_tag, enduser_tag_name "
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
			+ " FROM product_tblinwardentry parent "
			+ " left outer join product_instruction child on parent.inwardentryid = child.inwardid and (coalesce(actualweight,0)-coalesce(child.allocated_soqty,0)) >0 and child.isdeleted=0  "
			+ " left outer join product_tblpartydetails party on party.npartyid = parent.npartyid  "
			+ " where parent.isdeleted=0"
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (parent.coilNumber like %:searchText% or parent.customerBatchId like %:searchText% or parent.customerInvoiceNo like %:searchText%) else 1=1 end " 
			+ " and case when :partyIdsFlag=true then parent.npartyid in :partyIds else 1=1 end"
			+ ") a "
			+ " where 1=1 AND CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END",
		countQuery = "SELECT count(packet_id) from "
			+ " (select instructionid as packet_id, "
			+ " (SELECT count(distinct inss.instructionid) cnt FROM product_instruction inss where inss.inwardid=parent.inwardentryid  and status!=4 and inss.parentgroupid=child.groupid) as siltcutcnt"
			+ " FROM product_tblinwardentry parent" + 
			"	left outer join product_instruction child on parent.inwardentryid = child.inwardid and (coalesce(actualweight,0)-coalesce(child.allocated_soqty,0)) >0 and child.isdeleted=0 " + 
			"	left outer join product_tblpartydetails party on party.npartyid = parent.npartyid " + 
			"	where parent.isdeleted=0 " + 
			"	and case when :searchText is not null and LENGTH(:searchText) >0 then (parent.coilNumber like %:searchText% or parent.customerBatchId like %:searchText% or parent.customerInvoiceNo like %:searchText%) else 1=1 end " + 
			"	and case when :partyIdsFlag=true then parent.npartyid in :partyIds else 1=1 end"
			+ ") a "
			+ " where 1=1 AND CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END", nativeQuery = true)
	Page<Object[]> findInventory(@Param("searchText") String searchText,
			@Param("partyIds") List<Integer> partyIds, @Param("partyIdsFlag") boolean partyIdsFlag,
			 Pageable pageable);

	Optional<SalesOrderJswEntity> findBySoNumberIgnoreCase(String soNumber);
	
	@Query(value = "SELECT distinct so.so_id, so.so_number "
			+ " FROM jsw_sales_order so, jsw_sales_order_child so_child "
			+ " where so.is_deleted = 0 and so_child.is_deleted = 0 and so_child.so_id = so.so_id "
			+ " and case when :soId is not null and LENGTH(:soId) >0 then so.so_id = :soId else so.so_id end " 
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (so_child.mm_id like %:searchText% or so.so_number like %:searchText%) else 1=1 end " 
			+ " order by so.so_id desc",
		countQuery = "SELECT count(distinct so.so_id ) " + 
			"  FROM jsw_sales_order so, jsw_sales_order_child so_child" + 
		  	"  where so.is_deleted = 0 and so_child.is_deleted = 0 and so_child.so_id = so.so_id" + 
		  	"  and case when :soId is not null and LENGTH(:soId) >0 then so.so_id = :soId else so.so_id end \" \r\n" + 
		  	"  and case when :searchText is not null and LENGTH(:searchText) >0 then (so_child.mm_id like %:searchText% or so.so_number like %:searchText%) else 1=1 end \" \r\n" + 
		  	"  order by so.so_id desc", 
		nativeQuery = true)
	Page<Object[]> listAllSOIDsConsolidatePlan(@Param("searchText") String searchText, @Param("soId") Integer soId, Pageable pageable);
	
	
	
	
}
