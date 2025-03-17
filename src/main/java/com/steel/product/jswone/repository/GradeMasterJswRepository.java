
package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.GradeMasterJswEntity;

@Repository
public interface GradeMasterJswRepository extends JpaRepository<GradeMasterJswEntity, Integer> {

	List<GradeMasterJswEntity> findByGradeName(String gradeName);

	List<GradeMasterJswEntity> findByProductId(Integer productId);
}
