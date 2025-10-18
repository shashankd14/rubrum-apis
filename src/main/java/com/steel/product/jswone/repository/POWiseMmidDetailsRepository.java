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

	@Query(value = "SELECT coilnumber,customerbatchid, pode.po_reference,pode.po_id, pode.mm_id, pode.mmid_details_object "
			+ " from product_tblinwardentry inward, jsw_po_receive_dtls po,	jsw_powise_mmid_details pode "
			+ " WHERE  inward.po_id = po.po_id and pode.po_id = po.po_id and "
			+ " pode.mm_id = inward.mm_id and inward.po_id = pode.po_id and "
			+ " inward.po_id = :poId ", nativeQuery = true)
	List<Object[]> getInwardDetailsByPoId(String poId);

	List<POWiseMmidDetailsEntity> findByPoId(@Param("poId") String poId);

}
