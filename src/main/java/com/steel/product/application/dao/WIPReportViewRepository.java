package com.steel.product.application.dao;

import com.steel.product.application.entity.WIPReportViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WIPReportViewRepository extends JpaRepository<WIPReportViewEntity, Integer> {

	List<WIPReportViewEntity> findByPartyId(int partyId);

}
