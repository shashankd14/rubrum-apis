package com.steel.product.application.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.steel.product.application.entity.QualityReportEntity;

@Repository
public interface QualityReportRepository extends JpaRepository<QualityReportEntity, Integer> {

	QualityReportEntity findByInspectionId(Integer inspectionId);

}
