
package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.GradeMasterJswEntity;

@Repository
public interface GradeMasterJswRepository extends JpaRepository<GradeMasterJswEntity, Integer> {

	List<GradeMasterJswEntity> findByGradeName(String gradeName);

	List<GradeMasterJswEntity> findByProductId(Integer productId);

	@Query(value = "select product.grade_id, grade_name "
			+ " from jsw_material_master material, jsw_grade_master product  "
			+ " where product.grade_id=material.grade_id and mm_id=:mmId limit 1", nativeQuery = true)
	List<Object[]> getGradeName(String mmId);

	@Query(value = "select product.subgrade_id, subgrade_name "
			+ " from jsw_material_master material, jsw_subgrade_master product  "
			+ " where product.subgrade_id=material.subgrade_id and mm_id=:mmId limit 1", nativeQuery = true)
	List<Object[]> getSubGradeName(String mmId);

	@Query(value = "select product_name, grade_name,product.product_id,grade.grade_id "
			+ " from jsw_material_master material, jsw_product_master product, jsw_grade_master grade   "
			+ " where product.product_id=material.producttype_id and grade.grade_id=material.grade_id and mm_id=:mmId limit 1", nativeQuery = true)
	List<Object[]> getGradeProductName(String mmId);

	@Query(value = "select distinct product.gradeId, product.gradeName from GradeMasterJswEntity product where 1=1 ")
	List<Object[]> distinctValues();
	
}
