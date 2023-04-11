package com.steel.product.application.dao;

import com.steel.product.application.entity.KQPPartyTemplateEntity;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KQPPartyTemplateRepository extends JpaRepository<KQPPartyTemplateEntity, Integer> {

	@Query("select ins from KQPPartyTemplateEntity ins where ins.party.nPartyId=:partyId ")
	List<KQPPartyTemplateEntity> findByPartyId(@Param("partyId") Integer partyId);	
	
	@Query("select ins from KQPPartyTemplateEntity ins where ins.kqpEntity.kqpId=:kqpId ")
	List<KQPPartyTemplateEntity> findByKqpId(@Param("kqpId") Integer kqpId);	
	
}
