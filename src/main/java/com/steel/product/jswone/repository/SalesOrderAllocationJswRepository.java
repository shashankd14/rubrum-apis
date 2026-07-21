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
	
	@Query(value = "select customerid, salesorder_id, item_id, allo.allocated_soqty, "
			+ " (SELECT ware_house_id FROM jsw_warehouse_master wh where wh.party_id=inw.npartyid limit 1) werehouse_id,  "
			+ " (SELECT ware_house_name FROM jsw_warehouse_master wh where wh.party_id=inw.npartyid limit 1) whname,  "
			+ " (SELECT branch_id FROM jsw_warehouse_master wh where wh.party_id=inw.npartyid limit 1) branch_id,  "
			+ " (SELECT branch_name FROM jsw_warehouse_master wh, jsw_branch_master bm where bm.branch_id = wh.branch_id and  wh.party_id=inw.npartyid limit 1) branch_name "
			+ " from jsw_sales_order so, jsw_sales_order_child chld, "
			+ " jsw_sales_order_allocation allo, product_tblinwardentry inw " 
			+ " where so.so_id=allo.so_id and so.so_id=chld.so_id and allo.so_child_id=chld.so_child_id " 
			+ " and allo.inward_entry_id=inw.inwardentryid and allo.so_allocation_id= :soAllocationId", 
		nativeQuery = true)
	public List<Object[]> wareHouseReassignmentDetails(Integer soAllocationId);

	@Modifying
	@Transactional
	@Query("update SalesOrderAllocationEntity inw set inw.zohoSyncStts = :zohoSyncStts, zohoSyncRemarks = :message where inw.soAllocationId =:soAllocationId ")
	void updateZohoSyncRemarks(int soAllocationId, String message, String zohoSyncStts );
}
