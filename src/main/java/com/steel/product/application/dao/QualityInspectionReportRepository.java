package com.steel.product.application.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.steel.product.application.entity.QualityInspectionReportEntity;

@Repository
public interface QualityInspectionReportRepository extends JpaRepository<QualityInspectionReportEntity, Integer> {

	QualityInspectionReportEntity findByCoilNo(String inwardId);

	QualityInspectionReportEntity findByPlanId(String planId);

	QualityInspectionReportEntity findByDeliveryChalanNo(String deliveryChalanNo);

	QualityInspectionReportEntity findTop1ByCoilNoAndPlanId(@Param("coilNo") String coilNo, @Param("planId") String planId);

	QualityInspectionReportEntity findTop1ByCoilNo(@Param("coilNo") String coilNo);
}
