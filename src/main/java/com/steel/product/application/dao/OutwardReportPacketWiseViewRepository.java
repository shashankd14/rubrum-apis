package com.steel.product.application.dao;

import com.steel.product.application.entity.OutwardPacketwiseReportViewEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OutwardReportPacketWiseViewRepository extends JpaRepository<OutwardPacketwiseReportViewEntity, Integer> {

	List<OutwardPacketwiseReportViewEntity> findByPartyIdAndMnthAndYer(int partyId, Integer mnth, Integer year);

	List<OutwardPacketwiseReportViewEntity> findByPartyId(Integer partyId);

}
