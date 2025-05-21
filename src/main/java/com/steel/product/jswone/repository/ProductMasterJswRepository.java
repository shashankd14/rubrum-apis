
package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.steel.product.jswone.entity.ProductMasterJswEntity;

@Repository
public interface ProductMasterJswRepository extends JpaRepository<ProductMasterJswEntity, Integer> {

	List<ProductMasterJswEntity> findByProductName(String productName);

	List<ProductMasterJswEntity> findByBrandId(Integer gradeId);

	@Query(value = "select product.product_id, product_name, material.mm_description "
			+ " from jsw_material_master material, jsw_product_master product  "
			+ " where product.product_id=material.producttype_id and mm_id=:mmId limit 1", nativeQuery = true)
	List<Object[]> getProductName(String mmId);
}
