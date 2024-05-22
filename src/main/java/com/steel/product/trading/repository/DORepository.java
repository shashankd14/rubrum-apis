package com.steel.product.trading.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.steel.product.trading.entity.DOEntity;

@Repository
public interface DORepository extends JpaRepository<DOEntity, Integer> {

	@Modifying
	@Transactional
	@Query(value = "update trading_delivery_order inward set inward.is_deleted = true, inward.updated_by=:userId, inward.updated_on=CURRENT_TIMESTAMP where inward.enquiryid in :enquiryIds", nativeQuery = true)
	void deleteDOMainData(@Param("enquiryIds") List<Integer> enquiryIds, @Param("userId") Integer userId);

	@Query("select inw from DOEntity inw where inw.enquiryId.enquiryId = :enquiryId")
	public List<DOEntity> findAllEnqIds(@Param("enquiryId") Integer enquiryId);

}
