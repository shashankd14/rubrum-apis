package com.steel.product.application.dao;

import com.steel.product.application.entity.LaminationStaticDataEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaminationStaticDataRepository extends JpaRepository<LaminationStaticDataEntity, Integer> {

}
