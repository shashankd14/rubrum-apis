package com.steel.product.application.dao;

import com.steel.product.application.entity.PriceMasterEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceMasterRepository extends JpaRepository<PriceMasterEntity, Integer> {

	@Query("select pc from PriceMasterEntity pc where pc.locationId = :locationId and pc.process.processId = :processId and "
			+ " pc.grade.gradeId = :matGradeId and :rangeValue between pc.thicknessFrom and pc.thicknessTo")
	List<PriceMasterEntity> validateRange(@Param("locationId") Integer locationId, @Param("processId") Integer processId,
			@Param("matGradeId") Integer matGradeId, @Param("rangeValue") BigDecimal rangeValue);
	
	Optional<PriceMasterEntity> findById(Integer id);
	
	@Query("select pc from PriceMasterEntity pc where pc.locationId = :locationId and "
			+ " pc.process.processId = :processId and "
			+ " pc.grade.gradeId = :matGradeId and "
			+ " pc.id != :priceId and "
			+ " :rangeValue between pc.thicknessFrom and pc.thicknessTo")
	List<PriceMasterEntity> validateRangeUpdate(@Param("locationId") Integer locationId,
			@Param("processId") Integer processId, @Param("matGradeId") Integer matGradeId,
			@Param("rangeValue") BigDecimal rangeValue, @Param("priceId") Integer priceId);

	@Query("select id, pc.locationId, pc.process.processId, pc.grade.gradeId, thicknessFrom, thicknessTo, price, createdBy, updatedBy, createdOn, updatedOn, "
            + " (SELECT partyName from Party party where party.nPartyId=pc.locationId) as partyName, "
            + " (SELECT processName from Process process where process.processId=pc.process.processId) as processName, "
            + " (SELECT gradeName from MaterialGrade mg where mg.gradeId= pc.grade.gradeId) as gradeName, "
            + " (SELECT mg.parentMaterial.description from MaterialGrade mg where mg.gradeId=pc.grade.gradeId) as description, "
            + " (SELECT mg.parentMaterial.matId from MaterialGrade mg where mg.gradeId=pc.grade.gradeId) as matId "
			+ " from PriceMasterEntity pc where pc.id = :id")
	List<Object[]> findById1(@Param("id") Integer id);

	@Query("select id, pc.locationId, pc.process.processId, pc.grade.gradeId, thicknessFrom, thicknessTo, price, createdBy, updatedBy, createdOn, updatedOn, "
            + " (SELECT partyName from Party party where party.nPartyId=pc.locationId) as partyName, "
            + " (SELECT processName from Process process where process.processId=pc.process.processId) as processName, "
            + " pc.grade.gradeName as gradeName, "
            + " pc.product.productName as productName, "
            + " pc.product.productId as productId "
			+ " from PriceMasterEntity pc where pc.product.productId =:productId and pc.grade.gradeId=:gradeId and pc.process.processId=:processId ")
	List<Object[]> findByPartyIdAndProcessIdAndMatGradeIds(
			@Param("processId") Integer processId,
			@Param("gradeId") Integer gradeId,
			@Param("productId") Integer productId);
	
	@Query("select id, pc.locationId, pc.process.processId, pc.grade.gradeId, thicknessFrom, thicknessTo, price, createdBy, updatedBy, createdOn, updatedOn, "
            + " (SELECT partyName from Party party where party.nPartyId=pc.locationId) as partyName, "
            + " (SELECT processName from Process process where process.processId=pc.process.processId) as processName, "
            + " (SELECT gradeName from MaterialGrade mg where mg.gradeId=pc.grade.gradeId) as gradeName, "
            + " (SELECT mg.parentMaterial.description from MaterialGrade mg where mg.gradeId=pc.grade.gradeId) as description, "
            + " (SELECT mg.parentMaterial.matId from MaterialGrade mg where mg.gradeId=pc.grade.gradeId) as matId "
			+ " from PriceMasterEntity pc")
	List<Object[]> findAll1();
	
	@Query("select id, pc.locationId, pc.process.processId, pc.grade.gradeId, thicknessFrom, thicknessTo, price, createdBy, updatedBy, createdOn, updatedOn, "
            + " (SELECT partyName from Party party where party.nPartyId=pc.locationId) as partyName, "
            + " (SELECT processName from Process process where process.processId=pc.process.processId) as processName, "
            + " (SELECT gradeName from MaterialGrade mg where mg.gradeId=pc.grade.gradeId) as gradeName, "
            + " (SELECT mg.parentMaterial.description from MaterialGrade mg where mg.gradeId=pc.grade.gradeId) as description, "
            + " (SELECT mg.parentMaterial.matId from MaterialGrade mg where mg.gradeId=pc.grade.gradeId) as matId "
			+ " from PriceMasterEntity pc where pc.locationId =:locationId ")
	List<Object[]> findAllDetails(@Param("locationId") Integer locationId);
	
	@Query("select pc from PriceMasterEntity pc where "
			+ " ( pc.thicknessFrom = CASE WHEN :thicknesRange IS NOT NULL THEN :thicknesRange ELSE pc.thicknessFrom END or "
			+ " pc.thicknessTo = CASE WHEN :thicknesRange IS NOT NULL THEN :thicknesRange ELSE pc.thicknessTo END )  "
			+ " and pc.locationId in ( :locationIds) and ( pc.process.processName like %:searchText% or "
			+ " pc.grade.gradeName like %:searchText% ) order by id desc ")
	Page<PriceMasterEntity> findAll(@Param("searchText") String searchText, 
			@Param("thicknesRange") BigDecimal thicknesRange, @Param("locationIds") List<Integer> locationIds, Pageable pageable);

}
