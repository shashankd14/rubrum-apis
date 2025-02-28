
package com.steel.product.jswone.repository;

import com.steel.product.jswone.entity.CategoryMasterJswEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryMasterRepository extends JpaRepository<CategoryMasterJswEntity, Integer> {

	List<CategoryMasterJswEntity> findByCategoryName(String categoryName);
}
