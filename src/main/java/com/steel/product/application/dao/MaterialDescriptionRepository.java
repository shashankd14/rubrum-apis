package com.steel.product.application.dao;

import com.steel.product.application.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialDescriptionRepository extends JpaRepository<Material, Integer> {}
