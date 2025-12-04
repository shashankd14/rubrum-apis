
package com.steel.product.jswone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.BranchMasterEntity;

@Repository
public interface BranchMasterRepository extends JpaRepository<BranchMasterEntity, Integer> {
}
