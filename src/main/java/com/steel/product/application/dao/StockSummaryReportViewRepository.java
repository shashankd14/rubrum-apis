package com.steel.product.application.dao;

import com.steel.product.application.entity.StockSummaryReportViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockSummaryReportViewRepository extends JpaRepository<StockSummaryReportViewEntity, Integer> {

	List<StockSummaryReportViewEntity> findByPartyId(int partyId);

	List<StockSummaryReportViewEntity> findByCoilNumber (String coilNumber);

	// select coilnumber, wipqty from stock_summary_report_vw where coilnumber in (...)
	@Query("select s.coilNumber, s.wipqty from StockSummaryReportViewEntity s where s.coilNumber in :coilNumbers")
	List<Object[]> findWipQtyByCoilNumbers(@Param("coilNumbers") List<String> coilNumbers);

}
