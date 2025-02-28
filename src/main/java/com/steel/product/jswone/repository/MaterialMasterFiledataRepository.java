package com.steel.product.jswone.repository;

import com.steel.product.jswone.entity.MaterialMasterFiledataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialMasterFiledataRepository extends JpaRepository<MaterialMasterFiledataEntity, Integer> {
}
