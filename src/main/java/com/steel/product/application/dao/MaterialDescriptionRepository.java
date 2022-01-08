package com.steel.product.application.dao;

import com.steel.product.application.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialDescriptionRepository extends JpaRepository<Material, Integer> {

    Material findByDescription(String description);
}
