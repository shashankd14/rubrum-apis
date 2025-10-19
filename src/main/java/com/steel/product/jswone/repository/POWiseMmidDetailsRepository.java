package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.POWiseMmidDetailsEntity;

@Repository
public interface POWiseMmidDetailsRepository extends JpaRepository<POWiseMmidDetailsEntity, Integer> {

	POWiseMmidDetailsEntity findByMmId(String sku);

	@Query(value = "SELECT pode.po_reference, inward.po_id, inward.mm_id, pode.mmid_details_object, "
			+ " coilnumber, customerbatchid from product_tblinwardentry inward, jsw_powise_mmid_details pode "
			+ " WHERE pode.mm_id = inward.mm_id and inward.po_id = pode.po_id and "
			+ " inward.customerinvoiceno = :customerinvoiceno ", 
		nativeQuery = true)
	List<Object[]> getInwardDetailsByPoId(@Param("customerinvoiceno") String customerinvoiceno);

}
