package com.steel.product.application.dao;

import com.steel.product.application.entity.MonthwisePlanTrackerEnduserViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MonthwisePlanTrackerEndUserwiseViewRepository extends JpaRepository<MonthwisePlanTrackerEnduserViewEntity, Integer> {

	List<MonthwisePlanTrackerEnduserViewEntity> findByPartyidAndMnthAndYer(Integer partyId, Integer month, Integer year);

}
