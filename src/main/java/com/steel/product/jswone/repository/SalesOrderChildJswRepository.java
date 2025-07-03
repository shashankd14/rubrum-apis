package com.steel.product.jswone.repository;

import java.math.BigDecimal;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.SalesOrderPacketsJswEntity;

@Repository
public interface SalesOrderChildJswRepository extends JpaRepository<SalesOrderPacketsJswEntity, Integer> {

	@Modifying
	@Transactional
	@Query("update SalesOrderPacketsJswEntity inw set inw.allocatedStts = :allocatedStts, "
			+ " inw.allocatedSoqty = :allocatedSoqty,"
			+ " inw.specialInstructions = :specialInstructions, "
			+ " inw.allocationBy = :userId, "
			+ " inw.allocationDate = CURRENT_TIMESTAMP "
			+ " where inw.soChildId = :soChildId")
	void consolidatePlanner(@Param("soChildId") Integer soChildId,
			@Param("allocatedSoqty") BigDecimal allocatedSoqty,
			@Param("allocatedStts") String allocatedStts,
			@Param("specialInstructions") String specialInstructions, 
			@Param("userId") int userId);

	SalesOrderPacketsJswEntity findBySoChildId(Integer soChildId); 

}
