package com.steel.product.application.dao;

import com.steel.product.application.entity.PriceMasterEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceMasterRepository extends JpaRepository<PriceMasterEntity, Integer> {

	@Query("select pc from PriceMasterEntity pc where pc.partyId = :partyId  and pc.processId = :processId and "
			+ " pc.matGradeId = :matGradeId and :rangeValue between pc.thicknessFrom and pc.thicknessTo")
	List<PriceMasterEntity> validateRange(@Param("partyId") Integer partyId, @Param("processId") Integer processId,
			@Param("matGradeId") Integer matGradeId, @Param("rangeValue") BigDecimal rangeValue);

	Optional<PriceMasterEntity> findById(Integer id);

	List<PriceMasterEntity> findByPartyId(Integer partyId);

	List<PriceMasterEntity> findByProcessId(Integer processId);

	List<PriceMasterEntity> findByPartyIdAndProcessId(Integer partyId, Integer processId);

	List<PriceMasterEntity> findByPartyIdAndProcessIdAndMatGradeId(Integer partyId, Integer processId, Integer matGradeId);

	List<PriceMasterEntity> findAll();
}
