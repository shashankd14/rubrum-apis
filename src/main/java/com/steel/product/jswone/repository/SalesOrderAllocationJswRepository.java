package com.steel.product.jswone.repository;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.SalesOrderAllocationEntity;

@Repository
public interface SalesOrderAllocationJswRepository extends JpaRepository<SalesOrderAllocationEntity, Integer> {

	List<SalesOrderAllocationEntity> findByInwardEntryId(int inwardEntryId);

	@Query(value = "SELECT SUM(allocated_soqty) FROM jsw_sales_order_allocation WHERE so_child_id = :soChildId", nativeQuery = true)
	BigDecimal getTotalAllocatedQtyBySoChildId(@Param("soChildId") Integer soChildId);

	@Modifying
	@Query("update SalesOrderAllocationEntity inw set inw.instructionId = :instructionId where inw.soAllocationId = :so_allocation_id")
	void updateAllocation(@Param("so_allocation_id") Integer so_allocation_id, @Param("instructionId") int instructionId);

	@Modifying
	@Transactional
	@Query("update SalesOrderAllocationEntity inw set inw.pdfGenerationPart = :pdfGenerationPart where inw.soAllocationId in :soAllocationList")
	void updatePDFGenerationPart(@Param("pdfGenerationPart") String pdfGenerationPart, @Param("soAllocationList") List<Integer> soAllocationList);
	
	@Query(value = "SELECT so.salesorder_id, chld.item_id, "
	        + "       ROUND(SUM(allo.allocated_soqty) / 1000, 3) AS allocated_qty, "
	        + "       wh.ware_house_id   AS werehouse_id, "
	        + "       wh.ware_house_name AS whname, "
	        + "       ROUND(chld.soqty / 1000, 3) AS so_qty, "
	        + "       chld.wearhouse_id, "
	        + "       wh1.ware_house_name AS itemWhName "
	        + "FROM jsw_sales_order so "
	        + "JOIN jsw_sales_order_child chld "
	        + "     ON chld.so_id = so.so_id "
	        + "JOIN jsw_sales_order_allocation allo "
	        + "     ON allo.so_child_id = chld.so_child_id AND allo.so_id = so.so_id "
	        + "JOIN product_tblinwardentry inw "
	        + "     ON inw.inwardentryid = allo.inward_entry_id "
	        + "LEFT JOIN jsw_warehouse_master wh "
	        + "     ON wh.ware_house_id = (SELECT MIN(w.ware_house_id) "
	        + "                            FROM jsw_warehouse_master w "
	        + "                            WHERE w.party_id = inw.npartyid) "
	        + "LEFT JOIN jsw_warehouse_master wh1 "
	        + "     ON wh1.ware_house_id = chld.wearhouse_id "
	        + "WHERE allo.so_child_id = (SELECT a.so_child_id "
	        + "                          FROM jsw_sales_order_allocation a "
	        + "                          WHERE a.so_allocation_id = :soAllocationId) "
	        + "GROUP BY so.salesorder_id, chld.item_id, chld.so_child_id, chld.soqty, "
	        + "         chld.wearhouse_id, wh1.ware_house_name, "
	        + "         wh.ware_house_id, wh.ware_house_name",
	        nativeQuery = true)
	List<Object[]> wareHouseReassignmentDetails(@Param("soAllocationId") Integer soAllocationId);
	
	@Modifying
	@Transactional
	@Query("update SalesOrderAllocationEntity inw set inw.zohoSyncStts = :zohoSyncStts, zohoSyncRemarks = :message where inw.soAllocationId =:soAllocationId ")
	void updateZohoSyncRemarks(int soAllocationId, String message, String zohoSyncStts );
}
