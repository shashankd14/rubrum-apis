package com.steel.product.application.dao;

import com.steel.product.application.entity.Material;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialDescriptionRepository extends JpaRepository<Material, Integer> {

    Material findByDescription(String description);
    
    @Query("select mat from Material mat where mat.createdBy in :userIds order by mat.matId desc")
    public List<Material> findAllMaterials(@Param("userIds") List<Integer> userIds);

}
