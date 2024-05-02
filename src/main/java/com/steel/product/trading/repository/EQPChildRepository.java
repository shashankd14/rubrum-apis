package com.steel.product.trading.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.steel.product.trading.entity.EQPChildEntity;

@Repository
public interface EQPChildRepository extends JpaRepository<EQPChildEntity, Integer> {

	@Modifying
	@Transactional
	@Query("update EQPChildEntity inward set inward.isDeleted = true, inward.updatedBy=:userId, inward.updatedOn=CURRENT_TIMESTAMP where inward.enquiryChildId in :enquiryChildIds")
	void deleteData(@Param("enquiryChildIds") List<Integer> enquiryChildId, @Param("userId") Integer userId);

	@Modifying
	@Transactional
	@Query("update EQPChildEntity inward set  inward.isDeleted = true, inward.updatedBy=:userId, inward.updatedOn=CURRENT_TIMESTAMP where inward.enquiryId.enquiryId in :enquiryIds and inward.status='ENQUIRY'")
	void deleteEnquiryChildData(@Param("enquiryIds") List<Integer> enquiryIds, @Param("userId") Integer userId);

	@Modifying
	@Transactional
	@Query("update EQPChildEntity inward set  inward.quoteCreatedBy=null, inward.quoteUpdatedBy=null, inward.quoteCreatedOn=null, inward.quoteUpdatedOn=null, inward.updatedBy=:userId, inward.updatedOn=CURRENT_TIMESTAMP where inward.enquiryId.enquiryId in :enquiryIds and inward.status='QUOTE'")
	void deleteQuoteChildData(@Param("enquiryIds") List<Integer> enquiryIds, @Param("userId") Integer userId);

}
