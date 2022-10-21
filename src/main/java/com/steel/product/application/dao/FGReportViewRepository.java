package com.steel.product.application.dao;

import com.steel.product.application.entity.FGReportViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FGReportViewRepository extends JpaRepository<FGReportViewEntity, Integer> {

	List<FGReportViewEntity> findByPartyId(int partyId);

}
