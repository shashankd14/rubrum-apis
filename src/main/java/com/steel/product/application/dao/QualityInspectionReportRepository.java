package com.steel.product.application.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.steel.product.application.entity.QualityInspectionReportEntity;

@Repository
public interface QualityInspectionReportRepository extends JpaRepository<QualityInspectionReportEntity, Integer> {

	QualityInspectionReportEntity findTop1ByCoilNoAndPlanId(String coilNo, String planId);

	QualityInspectionReportEntity findTop1ByCoilNoAndDeliveryChalanNo(String coilNo, String deliveryChalanNo);

	QualityInspectionReportEntity findTop1ByCoilNo(String coilNo);
	
	QualityInspectionReportEntity findByQirId(Integer qirId); 
}
