package com.steel.product.jswone.repository;

import com.steel.product.jswone.entity.InwardFileDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InwardFiledataRepository extends JpaRepository<InwardFileDataEntity, Integer> {
}
