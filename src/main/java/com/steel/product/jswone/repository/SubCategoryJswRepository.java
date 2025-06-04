
package com.steel.product.jswone.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.SubCategoryJswEntity;

@Repository
public interface SubCategoryJswRepository extends JpaRepository<SubCategoryJswEntity, Integer> {

	List<SubCategoryJswEntity> findBySubcategoryName(String subcategoryName);

	List<SubCategoryJswEntity> findByCategoryId(Integer categoryId);
}
