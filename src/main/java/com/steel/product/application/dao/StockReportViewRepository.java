package com.steel.product.application.dao;

import com.steel.product.application.entity.StockReportViewEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockReportViewRepository extends JpaRepository<StockReportViewEntity, Integer> {

	List<StockReportViewEntity> findByPartyId(int partyId);

}
