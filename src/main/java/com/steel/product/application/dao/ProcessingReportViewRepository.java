package com.steel.product.application.dao;

import com.steel.product.application.entity.ProcessingReportViewEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProcessingReportViewRepository extends JpaRepository<ProcessingReportViewEntity, Integer> {

	List<ProcessingReportViewEntity> findByPartyIdAndMnthAndYer(int partyId, Integer mnth, Integer year);

}