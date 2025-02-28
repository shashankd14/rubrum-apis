
package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.CategoryMasterJswEntity;

@Repository
public interface CategoryMasterJswRepository extends JpaRepository<CategoryMasterJswEntity, Integer> {

	List<CategoryMasterJswEntity> findByCategoryName(String categoryName);
}
