package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.POWiseMmidDetailsEntity;

@Repository
public interface POWiseMmidDetailsRepository extends JpaRepository<POWiseMmidDetailsEntity, Integer> {

	POWiseMmidDetailsEntity findByMmId(String sku);

	@Query(value = "SELECT pode.po_reference, inward.po_id, inward.mm_id, pode.mmid_details_object, "
			+ " customerinvoiceno, customerbatchid, DATE_FORMAT( inward.dinvoicedate, '%d-%m-%Y') postdate,fquantity "
			+ " from product_tblinwardentry inward, jsw_powise_mmid_details pode "
			+ " WHERE pode.mm_id = inward.mm_id and inward.po_id = pode.po_id and "
			+ " inward.customerinvoiceno = :customerinvoiceno ", 
		nativeQuery = true)
	List<Object[]> getInwardDetailsByPoId(@Param("customerinvoiceno") String customerinvoiceno);

	@Query(value = "SELECT pode.po_reference, inward.po_id, inward.mm_id,coilnumber, customerbatchid, "
			+ " DATE_FORMAT( inward.createdon, '%d-%m-%Y') postdate, mm.mm_description, inward.fquantity"
			+ " from product_tblinwardentry inward, jsw_material_master mm, jsw_po_receive_dtls pode"
			+ " WHERE  mm.mm_id = inward.mm_id and pode.po_id = inward.po_id and "
			+ " inward.customerinvoiceno = :customerinvoiceno ", nativeQuery = true)
	List<Object[]> poWiseInwardList(@Param("customerinvoiceno") String customerinvoiceno);

	@Query(value = "SELECT distinct inward.customerinvoiceno, inward.zoho_sync_stts from product_tblinwardentry inward where 1=1", 
		nativeQuery = true)
	List<Object[]> allpoinvlists();

	@Query(value = "SELECT distinct inward.customerinvoiceno, inward.zoho_sync_stts, inward.zoho_sync_remarks, manual_po_flag "
			+ " from product_tblinwardentry inward "
			+ " where case when :searchText is not null and LENGTH(:searchText) >0 then (inward.customerinvoiceno like %:searchText%) else 1=1 end " 
			+ " order by inwardentryid desc",
		countQuery = "SELECT count(distinct inward.customerinvoiceno) from product_tblinwardentry inward  " + 
				 " where case when :searchText is not null and LENGTH(:searchText) >0 then (inward.customerinvoiceno like %:searchText%) else 1=1 end ", 
		nativeQuery = true)
	Page<Object[]> allpoinvlist(@Param("searchText") String searchText, Pageable pageable);
	
	
	
}
