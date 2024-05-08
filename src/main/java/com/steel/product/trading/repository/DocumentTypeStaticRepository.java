package com.steel.product.trading.repository;

import com.steel.product.trading.entity.DocumentTypeStaticEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentTypeStaticRepository extends JpaRepository<DocumentTypeStaticEntity, Integer> {

}
