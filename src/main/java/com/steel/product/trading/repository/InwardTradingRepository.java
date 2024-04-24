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

import com.steel.product.trading.entity.InwardTradingEntity;

@Repository
public interface InwardTradingRepository extends JpaRepository<InwardTradingEntity, Integer> {
	
	@Query(value = "SELECT inward.inward_id, inward.consignment_id"
			+ " FROM trading_inward inward, trading_vendor_master vendor \r\n"
			+ " where inward.is_deleted = 0"
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (vendor.vendor_name like %:searchText% or inward.consignment_id like %:searchText% or inward.document_type like %:searchText% or inward.consignment_id like %:searchText%) else 1=1 end " 
			+ " and inward.vendor_id = ifnull(:vendorId, inward.vendor_id) \r\n"
			+ " and inward.inward_id = ifnull(:inwardId, inward.inward_id) \r\n"
			+ " and inward.vendor_id=vendor.vendor_id order by inward.inward_id desc ",
	countQuery = "SELECT count(inward.inward_id) "
			+ " FROM trading_inward inward, trading_vendor_master vendor \r\n"
			+ " where inward.is_deleted = 0" 
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (vendor.vendor_name like %:searchText% or inward.consignment_id like %:searchText% or inward.document_type like %:searchText% or inward.consignment_id like %:searchText%) else 1=1 end " 
			+ " and inward.vendor_id = ifnull(:vendorId, inward.vendor_id) \r\n"
			+ " and inward.inward_id = ifnull(:inwardId, inward.inward_id) \r\n"
			+ " and inward.vendor_id=vendor.vendor_id",
	nativeQuery = true)
	Page<Object[]> findAllInwardsWithSearchText(@Param("inwardId") Integer inwardId, @Param("vendorId") Integer vendorId, @Param("searchText") String searchText, Pageable pageable);
	
	@Query(value = "SELECT inward.inward_id, vendor_name, inward.purpose_type, inward.vendor_id, inward.transporter_name, inward.transporter_phone_no, "
			+ " inward.vendor_batch_no, inward.consignment_id, inward.location_id, inward.vehicle_no, inward.document_no, inward.document_type,"
			+ " inward.document_date, inward.eway_bill_no, inward.eway_bill_date, inward.value_of_goods, inward.extra_charges_option,"
			+ " inward.freight_charges, inward.insurance_amount, inward.loading_charges, inward.weightmen_charges, inward.cgst, inward.sgst, "
			+ " inward.igst, inward.total_inward_volume, inward.total_weight, inward.total_volume, "
			+ " child.itemchild_id, child.item_id, child.unit, child.unit_volume, child.net_weight, child.rate,child.volume,"
			+ " child.actual_noof_pieces, child.theoretical_weight, child.weight_variance, child.theoretical_noof_pieces, "
			+ " (select item_name from trading_material_master mat where mat.item_id =  child.item_id) as itemName, "
			+ " child.inward_item_id"
			+ " FROM trading_inward inward, trading_vendor_master vendor, trading_inward_items child \r\n"
			+ " where inward.is_deleted = 0 and child.is_deleted = 0 " 
			+ " and inward.inward_id in :inwardIds \r\n"
			+ " and inward.vendor_id=vendor.vendor_id "
			+ " and inward.inward_id=child.inwardid "
			+ " order by inward.inward_id desc ",
	nativeQuery = true)
	List<Object[]> findAllInwardsWiseData(@Param("inwardIds") List<Integer> inwardIds);

	@Modifying
	@Transactional
	@Query("update InwardTradingEntity inward set inward.isDeleted = true, inward.updatedBy=:userId, inward.updatedOn=CURRENT_TIMESTAMP where inward.inwardId in :inwardIds")
	void deleteData(@Param("inwardIds") List<Integer> inwardIds, @Param("userId") Integer userId);
}
