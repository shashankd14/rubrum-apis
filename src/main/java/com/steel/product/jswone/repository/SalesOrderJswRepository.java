package com.steel.product.jswone.repository;


import java.util.List;

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
	Page<Object[]> listAllSOIDs(@Param("searchText") String searchText, @Param("soId") Integer soId, Pageable pageable);
	
	@Query(value = "SELECT so.so_id, so.so_number, so.socreatedate, so.deliverymethod, "
			+ " so.destinationcode, so.refno, so.joplsorefno, so.bizsegment, so.ecommerce, so.supplysource, so.typeofsupply, "
			+ " so.incomingpayment, so.paymentmode, so.terms, so.customerid, so.total_soqty, so.total_allocated_soqty, "
			+ " so.allocated_stts as soallstts, so.so_status, so_child.so_child_id,  "
			+ " so_child.mm_id, so_child.instruction_id, so_child.inward_entry_d, so_child.soqty, so_child.allocated_soqty, "
			+ " so_child.allocated_stts, so_child.item_status " + 
			" FROM jsw_sales_order so, jsw_sales_order_child so_child" + 
			" where so.is_deleted = 0 and so_child.is_deleted = 0 and so_child.so_id = so.so_id" + 
			" and so.so_id in :soIDsList" + 
			"", nativeQuery = true)
	List<Object[]> listIdWisedetails (@Param("soIDsList") List<Integer> soIDsList);

	
}
