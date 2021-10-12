package com.steel.product.application.dao;

import com.steel.product.application.entity.MaterialGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MaterialGradeRepository extends JpaRepository<MaterialGrade, Integer> {

    @Query(nativeQuery = true, value = "select * from product_material_grades where nMatId= :materialId  ")
    List<MaterialGrade> getGradesByMaterialId(@Param("materialId") Integer materialId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from product_material_grades where nMatId= :materialId  ")
    void deleteGradesByMaterialId(@Param("materialId") Integer materialId);
}
