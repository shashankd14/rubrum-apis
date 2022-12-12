package com.steel.product.application.dao;

import com.steel.product.application.entity.StockSummaryReportViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockSummaryReportViewRepository extends JpaRepository<StockSummaryReportViewEntity, Integer> {

	List<StockSummaryReportViewEntity> findByPartyId(int partyId);

}
