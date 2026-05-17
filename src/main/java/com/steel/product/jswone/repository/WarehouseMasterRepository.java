
package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.steel.product.jswone.entity.WarehouseMasterJswEntity;

@Repository
public interface WarehouseMasterRepository extends JpaRepository<WarehouseMasterJswEntity, Integer> {

	List<WarehouseMasterJswEntity> findByWareHouseId(String wareHouseId);
	
	@Query(value = "SELECT distinct wareh.party_id" 
	        + " FROM jsw_branch_master branc,"
			+ " jsw_warehouse_master wareh, product_tblpartydetails party"
	        + " WHERE branc.branch_id = wareh.branch_id"
			+ " AND wareh.party_id = party.npartyid and wareh.branch_id=:branchId ", 
	nativeQuery = true)
	List<Object[]> partyIdsByBranchId(@Param("branchId") String branchId );

}