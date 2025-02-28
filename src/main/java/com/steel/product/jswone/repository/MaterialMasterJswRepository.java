package com.steel.product.jswone.repository;

import com.steel.product.jswone.entity.MaterialMasterJswEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialMasterJswRepository extends JpaRepository<MaterialMasterJswEntity, Integer> {
}
