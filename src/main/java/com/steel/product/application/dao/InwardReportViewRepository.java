package com.steel.product.application.dao;

import com.steel.product.application.entity.InwardReportViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InwardReportViewRepository extends JpaRepository<InwardReportViewEntity, Integer> {

	List<InwardReportViewEntity> findByPartyId(int partyId);

	List<InwardReportViewEntity> findByPartyIdAndMnth(int partyId, Integer mnth);

}
