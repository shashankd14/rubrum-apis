package com.steel.product.application.dao;

import com.steel.product.application.entity.QualityPartyTemplateEntity;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QualityPartyTemplateRepository extends JpaRepository<QualityPartyTemplateEntity, Integer> {

	@Query("select ins from QualityPartyTemplateEntity ins where ins.templateEntity.templateId =:templateId and ins.party.nPartyId=:partyId ")
	List<QualityPartyTemplateEntity> findByPartyIdAndTemplateId(@Param("partyId") Integer partyId,
			@Param("templateId") Integer templateId);

	@Query("select ins from QualityPartyTemplateEntity ins where ins.party.nPartyId=:partyId ")
	List<QualityPartyTemplateEntity> findByPartyId(@Param("partyId") Integer partyId);	
	
}
