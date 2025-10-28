
package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.LeafCategoryJswEntity;

@Repository
public interface LeafCategoryJswRepository extends JpaRepository<LeafCategoryJswEntity, Integer> {

	List<LeafCategoryJswEntity> findByLeafcategoryName(String leafcategoryName);

	List<LeafCategoryJswEntity> findBySubcategoryId(Integer subCategoryId);

	@Query(value = "select distinct product.leafcategoryId, product.leafcategoryName from LeafCategoryJswEntity product where 1=1 ")
	List<Object[]> distinctValues();

}
