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

	POWiseMmidDetailsEntity findByMmIdAndPoId(String sku, String poid);
	
	@Query(value = "SELECT pode.po_reference, pode.mmid_details_object, "
			+ " customerinvoiceno, DATE_FORMAT( inward.dinvoicedate, '%d-%m-%Y') postdate, "
			+ " round((sum(fquantity ) / 1000),3) fquantity, sum(valueofgoods) "
			+ " from product_tblinwardentry inward, jsw_powise_mmid_details pode "
			+ " WHERE pode.mm_id = inward.mm_id and inward.po_id = pode.po_id and "
			+ " inward.customerinvoiceno = :customerinvoiceno "
			+ " group by pode.po_reference,inward.po_id,inward.mm_id,"
			+ " pode.mmid_details_object,customerinvoiceno", 
		nativeQuery = true)
	List<Object[]> getInwardDetailsByPoId(@Param("customerinvoiceno") String customerinvoiceno);

	@Query(value = "SELECT inwardentryid, testcertificatefileurl, invoicecopy_fileurl, " 
			+ " invoice_copy, testcertificatenumber "
			+ " from product_tblinwardentry inward  "
			+ " WHERE inward.bill_id = :billId ", 
		nativeQuery = true)
	List<Object[]> getDocDetailsbyPoId (@Param("billId") String billId);

	@Query(value = "SELECT pode.po_reference, inward.po_id, inward.mm_id,coilnumber, customerbatchid, "
			+ " DATE_FORMAT( inward.createdon, '%d-%m-%Y') postdate, mm.mm_description, inward.fquantity, valueofgoods ,"
			+ " (SELECT sum(valueofgoods) from product_tblinwardentry inward, jsw_material_master mm, jsw_po_receive_dtls pode "  
			+ " WHERE mm.mm_id = inward.mm_id and pode.po_id = inward.po_id and inward.customerinvoiceno = :customerinvoiceno ) as total  "
			+ " from product_tblinwardentry inward, jsw_material_master mm, jsw_po_receive_dtls pode"
			+ " WHERE mm.mm_id = inward.mm_id and pode.po_id = inward.po_id and "
			+ " inward.customerinvoiceno = :customerinvoiceno ", nativeQuery = true)
	List<Object[]> poWiseInwardList(@Param("customerinvoiceno") String customerinvoiceno);

	@Query(value = "SELECT distinct inward.customerinvoiceno, inward.zoho_sync_stts from product_tblinwardentry inward where 1=1", 
		nativeQuery = true)
	List<Object[]> allpoinvlists();

	@Query(value = "SELECT distinct inward.customerinvoiceno, inward.zoho_sync_stts, inward.zoho_sync_remarks, manual_po_flag, "
			+ " bill_id, zoho_docupload_stts,zoho_docupload_remarks,coilnumber,customerbatchid, stts.statusname, "
			+ " DATE_FORMAT( inward.dinvoicedate, '%d-%m-%Y') postdate, DATE_FORMAT( inward.dreceiveddate, '%d-%m-%Y') dreceiveddate, "
			+ " (Select partyname from product_tblpartydetails part where part.npartyid =inward.npartyid) partyname,po_reference "
			+ " from product_tblinwardentry inward "
			+ " left outer join jsw_po_receive_dtls po on po.po_id = inward.po_id "
			+ " left outer join product_status stts on inward.vstatus = stts.statusid "
			+ " where 1=1 "
			+ " and (case when :partyId >0 then inward.npartyid=:partyId else 1=1 end )  " 
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (inward.coilnumber like %:searchText% or inward.customerinvoiceno like %:searchText% or po_reference like %:searchText% ) else 1=1 end " 
			+ " order by inwardentryid desc",
		countQuery = "SELECT count(distinct inward.customerinvoiceno) "
				+ " from product_tblinwardentry inward "
				+ " left outer join jsw_po_receive_dtls po on po.po_id = inward.po_id "
				+ " left outer join product_status stts on inward.vstatus = stts.statusid "
				+ " where 1=1 "
				+ " and (case when :partyId >0 then inward.npartyid=:partyId else 1=1 end )  " 
				+ " and case when :searchText is not null and LENGTH(:searchText) >0 then (inward.coilnumber like %:searchText% or inward.customerinvoiceno like %:searchText% or po_reference like %:searchText% ) else 1=1 end ", 
		nativeQuery = true)
	Page<Object[]> allpoinvlist(@Param("searchText") String searchText, @Param("partyId") int partyId,
			Pageable pageable);

	@Query(value = "SELECT inward.coilnumber, round(((fquantity ) / 1000),3) fquantity "
			+ " from product_tblinwardentry inward, jsw_powise_mmid_details pode "
			+ " WHERE pode.mm_id = inward.mm_id and inward.po_id = pode.po_id and"
			+ " inward.customerinvoiceno = :customerinvoiceno and inward.mm_id = :mmid ", nativeQuery = true)
	List<Object[]> getInwardDetailsByPoIdBatch(@Param("customerinvoiceno") String customerinvoiceno,
			@Param("mmid") String mmid);
	
	
}
