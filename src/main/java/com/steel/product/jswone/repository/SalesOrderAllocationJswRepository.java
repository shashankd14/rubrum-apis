package com.steel.product.jswone.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.SalesOrderAllocationEntity;

@Repository
public interface SalesOrderAllocationJswRepository extends JpaRepository<SalesOrderAllocationEntity, Integer> {

	List<SalesOrderAllocationEntity> findByInwardEntryId(int inwardEntryId);

	@Query(value = "SELECT round((SUM(allocated_soqty) / 1000),3) FROM jsw_sales_order_allocation WHERE so_child_id = :soChildId", nativeQuery = true)
	BigDecimal getTotalAllocatedQtyBySoChildId(@Param("soChildId") Integer soChildId);

	@Modifying
	@Query("update SalesOrderAllocationEntity inw set inw.instructionId = :instructionId where inw.so_allocation_id = :so_allocation_id")
	void updateAllocation(@Param("so_allocation_id") Integer so_allocation_id,
			@Param("instructionId") int instructionId);

}
