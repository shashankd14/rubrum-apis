package com.steel.product.trading.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.steel.product.trading.entity.EQPEntity;

@Repository
public interface EQPRepository extends JpaRepository<EQPEntity, Integer> {
	
	@Query(value = "SELECT inward.enquiry_id, inward.enq_qty"
			+ " FROM trading_eqp inward, trading_customer_master customer \r\n"
			+ " where inward.is_deleted = 0"
			+ " and inward.status = :status"
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (customer.customer_name like %:searchText% or inward.enq_enquiry_from like %:searchText%) else 1=1 end " 
			+ " and inward.enq_customer_id = ifnull(:customerId, inward.enq_customer_id) \r\n"
			+ " and inward.enquiry_id = ifnull(:enquiryId, inward.enquiry_id) \r\n"
			+ " and inward.enq_customer_id=customer.customer_id order by inward.enquiry_id desc ",
	countQuery = "SELECT count(inward.enquiry_id) "
			+ " FROM trading_eqp inward, trading_customer_master customer \r\n"
			+ " where inward.is_deleted = 0" 
			+ " and inward.status = :status"
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (customer.customer_name like %:searchText% or inward.enq_enquiry_from like %:searchText%) else 1=1 end " 
			+ " and inward.customer_id = ifnull(:customerId, inward.customer_id) \r\n"
			+ " and inward.enquiry_id = ifnull(:enquiryId, inward.enquiry_id) \r\n"
			+ " and inward.enq_customer_id=customer.customer_id",
	nativeQuery = true)
	Page<Object[]> findAllInwardsWithSearchText(@Param("enquiryId") Integer enquiryId, @Param("status") String status, 
			@Param("customerId") Integer customerId, @Param("searchText") String searchText, Pageable pageable);
	
	@Query(value = "SELECT inward.enquiry_id, inward.enq_customer_id, customer.customer_name, inward.enq_enquiry_from, DATE_FORMAT(inward.enq_enquiry_date, '%d-%m-%y') enqenquirydate, "
			+ " inward.enq_qty, inward.enq_value, inward.quote_enquiry_from,  "
			+ " DATE_FORMAT(inward.quote_enquiry_date, '%d-%m-%y') , inward.quote_qty, inward.quote_value, inward.status,  "
			+ " (select cust.customer_name from trading_customer_master cust where cust.customer_id =  inward.quote_customer_id) as quoteCustomer_name, "
			+ " child.enquiry_child_id, child.item_id, child.item_specs, child.make, child.alt_make, child.qty1,child.unit1,"
			+ " child.qty2, child.unit2, child.estimate_delivery_date, child.remarks, child.status chldstts, child.location_id,  "
			+ " (select item_name from trading_material_master mat where mat.item_id =  child.item_id) as itemName,"
			+ " (select location_name from trading_location_master location where location.location_id =  child.location_id) "
			+ " FROM trading_eqp inward, trading_customer_master customer, trading_eqp_items child \r\n"
			+ " where inward.is_deleted = 0 and child.is_deleted = 0 " 
			+ " and inward.status = :status and child.status = :status \r\n"
			+ " and inward.enquiry_id in :enquiryIds \r\n"
			+ " and inward.enq_customer_id=customer.customer_id "
			+ " and inward.enquiry_id=child.enquiryid "
			+ " order by inward.enquiry_id desc ",
	nativeQuery = true)
	List<Object[]> findAllInwardsWiseData(@Param("enquiryIds") List<Integer> enquiryIds, @Param("status") String status);

	@Modifying
	@Transactional
	@Query("update EQPEntity inward set inward.isDeleted = true, inward.updatedBy=:userId, inward.updatedOn=CURRENT_TIMESTAMP where inward.enquiryId in :enquiryIds")
	void deleteData(@Param("enquiryIds") List<Integer> enquiryIds, @Param("userId") Integer userId);
}
