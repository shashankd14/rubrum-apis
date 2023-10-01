package com.steel.product.application.dao;

import com.steel.product.application.entity.PriceMasterEntity;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceMasterRepository extends JpaRepository<PriceMasterEntity, Integer> {

	@Query("select pc from PriceMasterEntity pc where pc.party.nPartyId = :partyId and pc.process.processId = :processId and "
			+ " pc.matGrade.gradeId = :matGradeId and :rangeValue between pc.thicknessFrom and pc.thicknessTo")
	List<PriceMasterEntity> validateRange(@Param("partyId") Integer partyId, @Param("processId") Integer processId,
			@Param("matGradeId") Integer matGradeId, @Param("rangeValue") BigDecimal rangeValue);

	@Query("select id, pc.party.nPartyId, pc.process.processId, pc.matGrade.gradeId, thicknessFrom, thicknessTo, price, createdBy, updatedBy, createdOn, updatedOn, "
            + " (SELECT partyName from Party party where party.nPartyId=pc.party.nPartyId) as partyName, "
            + " (SELECT processName from Process process where process.processId=pc.process.processId) as processName, "
            + " (SELECT gradeName from MaterialGrade mg where mg.gradeId= pc.matGrade.gradeId) as gradeName, "
            + " (SELECT mg.parentMaterial.description from MaterialGrade mg where mg.gradeId=pc.matGrade.gradeId) as description, "
            + " (SELECT mg.parentMaterial.matId from MaterialGrade mg where mg.gradeId=pc.matGrade.gradeId) as matId "
			+ " from PriceMasterEntity pc where pc.id = :id")
	List<Object[]> findById1(@Param("id") Integer id);

	@Query("select id, pc.party.nPartyId, pc.process.processId, pc.matGrade.gradeId, thicknessFrom, thicknessTo, price, createdBy, updatedBy, createdOn, updatedOn, "
            + " (SELECT partyName from Party party where party.nPartyId=pc.party.nPartyId) as partyName, "
            + " (SELECT processName from Process process where process.processId=pc.process.processId) as processName, "
            + " (SELECT gradeName from MaterialGrade mg where mg.gradeId=pc.matGrade.gradeId) as gradeName, "
            + " (SELECT mg.parentMaterial.description from MaterialGrade mg where mg.gradeId=pc.matGrade.gradeId) as description, "
            + " (SELECT mg.parentMaterial.matId from MaterialGrade mg where mg.gradeId=pc.matGrade.gradeId) as matId "
			+ " from PriceMasterEntity pc where pc.party.nPartyId =:partyId and pc.matGrade.gradeId=:matGradeId and pc.process.processId=:processId ")
	List<Object[]> findByPartyIdAndProcessIdAndMatGradeIdss(@Param("partyId") Integer partyId,
			@Param("processId") Integer processId,
			@Param("matGradeId") Integer matGradeId);
	
	@Query("select id, pc.party.nPartyId, pc.process.processId, pc.matGrade.gradeId, thicknessFrom, thicknessTo, price, createdBy, updatedBy, createdOn, updatedOn, "
            + " (SELECT partyName from Party party where party.nPartyId=pc.party.nPartyId) as partyName, "
            + " (SELECT processName from Process process where process.processId=pc.process.processId) as processName, "
            + " (SELECT gradeName from MaterialGrade mg where mg.gradeId=pc.matGrade.gradeId) as gradeName, "
            + " (SELECT mg.parentMaterial.description from MaterialGrade mg where mg.gradeId=pc.matGrade.gradeId) as description, "
            + " (SELECT mg.parentMaterial.matId from MaterialGrade mg where mg.gradeId=pc.matGrade.gradeId) as matId "
			+ " from PriceMasterEntity pc")
	List<Object[]> findAll1();
	
	@Query("select id, pc.party.nPartyId, pc.process.processId, pc.matGrade.gradeId, thicknessFrom, thicknessTo, price, createdBy, updatedBy, createdOn, updatedOn, "
            + " (SELECT partyName from Party party where party.nPartyId=pc.party.nPartyId) as partyName, "
            + " (SELECT processName from Process process where process.processId=pc.process.processId) as processName, "
            + " (SELECT gradeName from MaterialGrade mg where mg.gradeId=pc.matGrade.gradeId) as gradeName, "
            + " (SELECT mg.parentMaterial.description from MaterialGrade mg where mg.gradeId=pc.matGrade.gradeId) as description, "
            + " (SELECT mg.parentMaterial.matId from MaterialGrade mg where mg.gradeId=pc.matGrade.gradeId) as matId "
			+ " from PriceMasterEntity pc where pc.party.nPartyId =:partyId ")
	List<Object[]> findAllDetails(@Param("partyId") Integer partyId);
	
	@Query("select pc from PriceMasterEntity pc where "
			+ " pc.party.partyName like %:searchText% or "
			+ " pc.process.processName like %:searchText% or "
			+ " pc.matGrade.gradeName like %:searchText% or "
			+ " pc.matGrade.parentMaterial.description like %:searchText% ")
	Page<PriceMasterEntity> findAll(@Param("searchText") String searchText, Pageable pageable);

}
