package com.steel.product.application.dao;

import com.steel.product.application.entity.PackingRateEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PackingRateRepository extends JpaRepository<PackingRateEntity, Integer> {

	@Query("select ins from PackingRateEntity ins where ins.bucketEntity.bucketId =:bucketId and ins.party.nPartyId=:partyId ")
	List<PackingRateEntity> findByPartyIdAndBucketId(@Param("bucketId") Integer statusId, @Param("partyId") Integer partyId);

	@Query("select ins from PackingRateEntity ins where ins.party.nPartyId =:partyId ")
	List<PackingRateEntity> findByPartyId(@Param("partyId") Integer partyId);

}
