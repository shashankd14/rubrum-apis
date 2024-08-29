package com.steel.product.application.dao;

import com.steel.product.application.entity.MonthwisePlanTrackerViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MonthwisePlanTrackerViewRepository extends JpaRepository<MonthwisePlanTrackerViewEntity, Integer> {

	List<MonthwisePlanTrackerViewEntity> findByPartyidAndMnthAndYer(Integer partyId, Integer month, Integer year);

}
