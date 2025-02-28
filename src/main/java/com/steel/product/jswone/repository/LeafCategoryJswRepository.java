
package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.LeafCategoryJswEntity;

@Repository
public interface LeafCategoryJswRepository extends JpaRepository<LeafCategoryJswEntity, Integer> {

	List<LeafCategoryJswEntity> findByLeafcategoryName(String leafcategoryName);
}
