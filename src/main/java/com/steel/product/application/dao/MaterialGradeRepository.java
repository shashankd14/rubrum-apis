package com.steel.product.application.dao;

import com.steel.product.application.entity.MaterialGrade;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MaterialGradeRepository extends JpaRepository<MaterialGrade, Integer> {
	
	@Query(nativeQuery = true, value = "select * from product_material_grades where nMatId= :materialId  ")
	  List<MaterialGrade> getGradesByMaterialId(@Param("materialId") Integer materialId);
}
