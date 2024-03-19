package com.steel.product.trading.repository;

import com.steel.product.trading.entity.MaterialMasterEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialMasterRepository extends JpaRepository<MaterialMasterEntity, Integer> {

}
