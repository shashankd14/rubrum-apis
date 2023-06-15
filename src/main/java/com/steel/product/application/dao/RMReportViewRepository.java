package com.steel.product.application.dao;

import com.steel.product.application.entity.RMReportViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RMReportViewRepository extends JpaRepository<RMReportViewEntity, Integer> {

	List<RMReportViewEntity> findByPartyId(int partyId);

}
