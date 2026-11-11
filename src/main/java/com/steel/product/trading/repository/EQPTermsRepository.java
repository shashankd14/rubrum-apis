package com.steel.product.trading.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.steel.product.trading.entity.EQPTermsEntity;

@Repository
public interface EQPTermsRepository extends JpaRepository<EQPTermsEntity, Integer> {


	@Modifying
	@Query("update EQPTermsEntity terms set terms.status='QUOTE' where terms.enquiryId.enquiryId in :enquiryIds and terms.status='PROFORMA'")
	void revertProformaTerms(@Param("enquiryIds") List<Integer> enquiryIds);
}
