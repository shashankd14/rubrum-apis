package com.steel.product.application.dao;

import com.steel.product.application.entity.StockDetailsReportViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockDetailsReportViewRepository extends JpaRepository<StockDetailsReportViewEntity, Integer> {

	List<StockDetailsReportViewEntity> findByPartyId(int partyId);

}
