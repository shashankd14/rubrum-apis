
package com.steel.product.jswone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.steel.product.jswone.entity.JswoneAuditTrailEntity;

@Repository
public interface JswoneAuditTrailRepository extends JpaRepository<JswoneAuditTrailEntity, Integer> {

}
