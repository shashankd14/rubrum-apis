
package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.BranchMasterEntity;

@Repository
public interface BranchMasterRepository extends JpaRepository<BranchMasterEntity, Integer> {

	@Query(value = "select jbm.id, jbm.branch_name, wareh.ware_house_id, wareh.ware_house_name"
			+ " from jsw_branch_master jbm, jsw_warehouse_master wareh "
			+ " where jbm.branch_id =wareh.branch_id order by jbm.branch_name asc ", nativeQuery = true)
	List<Object[]> findAllBranchDetails();

}
