
package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.steel.product.jswone.entity.SubgradeMasterJswEntity;

@Repository
public interface SubGradeJswRepository extends JpaRepository<SubgradeMasterJswEntity, Integer> {

	List<SubgradeMasterJswEntity> findBySubgradeName(String subgradeName);
}
