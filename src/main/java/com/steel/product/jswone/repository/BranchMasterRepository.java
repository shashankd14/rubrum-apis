
package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.BranchMasterEntity;

@Repository
public interface BranchMasterRepository extends JpaRepository<BranchMasterEntity, Integer> {

	@Query(value = "select jbm.branch_id, jbm.branch_name, pt.npartyid, pt.partyname"
			+ " from jsw_branch_master jbm, product_tblpartydetails pt"
			+ " where jbm.branch_id =pt.branch_id order by branch_name asc ", nativeQuery = true)
	List<Object[]> findAllBranchDetails();

}
