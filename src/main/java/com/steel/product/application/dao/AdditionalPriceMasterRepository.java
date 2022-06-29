package com.steel.product.application.dao;

import com.steel.product.application.entity.AdditionalPriceMasterEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalPriceMasterRepository extends JpaRepository<AdditionalPriceMasterEntity, Integer> {

	@Query("select pc from AdditionalPriceMasterEntity pc where pc.partyId = :partyId  and pc.processId = :processId and "
			+ " pc.additionalPriceId = :additionalPriceId and :rangeValue between pc.rangeFrom and pc.rangeTo")
	List<AdditionalPriceMasterEntity> validateRange(@Param("partyId") Integer partyId, @Param("processId") Integer processId,
			@Param("additionalPriceId") Integer additionalPriceId, @Param("rangeValue") BigDecimal rangeValue);

	Optional<AdditionalPriceMasterEntity> findById(Integer id);

	List<AdditionalPriceMasterEntity> findByPartyId(Integer partyId);

	List<AdditionalPriceMasterEntity> findByProcessId(Integer processId);

	List<AdditionalPriceMasterEntity> findByPartyIdAndProcessId(Integer partyId, Integer processId);

	List<AdditionalPriceMasterEntity> findByPartyIdAndProcessIdAndAdditionalPriceId(Integer partyId, Integer processId, Integer additionalPriceId);

	//List<AdditionalPriceMasterEntity> findAll();
	
	@Query("select id, partyId, processId, additionalPriceId, rangeFrom, rangeTo, price, createdBy, updatedBy, createdOn, updatedOn, "
            + " (SELECT partyName from Party party where party.nPartyId=pc.partyId) as partyName, "
            + " (SELECT processName from Process process where process.processId=pc.processId) as processName, "
            + " (SELECT priceDesc from AdditionalPriceStaticEntity ap where ap.id = pc.additionalPriceId) as additionalPriceDesc"
			+ " from AdditionalPriceMasterEntity pc where pc.id = :id")
	List<Object[]> findById1(@Param("id") Integer id);
	
	
	@Query("select id, partyId, processId, additionalPriceId, rangeFrom, rangeTo, price, createdBy, updatedBy, createdOn, updatedOn, "
            + " (SELECT partyName from Party party where party.nPartyId=pc.partyId) as partyName, "
            + " (SELECT processName from Process process where process.processId=pc.processId) as processName, "
            + " (SELECT priceDesc from AdditionalPriceStaticEntity ap where ap.id = pc.additionalPriceId) as additionalPriceDesc"
			+ " from AdditionalPriceMasterEntity pc")
	List<Object[]> findAll1();
	
}
