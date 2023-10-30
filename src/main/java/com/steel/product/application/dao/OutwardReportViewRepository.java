package com.steel.product.application.dao;

import com.steel.product.application.entity.OutwardReportViewEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OutwardReportViewRepository extends JpaRepository<OutwardReportViewEntity, Integer> {

	List<OutwardReportViewEntity> findByPartyId(int partyId);

	List<OutwardReportViewEntity> findByPartyIdAndMnth(int partyId, Integer mnth);

}
