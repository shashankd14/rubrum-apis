package com.steel.product.application.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.steel.product.application.entity.AuditReqRespTrackerEntity;

@Repository
public interface AuditReqRespTrackerRepository extends JpaRepository<AuditReqRespTrackerEntity, Long> {
}
