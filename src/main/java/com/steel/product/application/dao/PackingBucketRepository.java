package com.steel.product.application.dao;

import com.steel.product.application.entity.PackingBucketEntity;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PackingBucketRepository extends JpaRepository<PackingBucketEntity, Integer> {

	List<PackingBucketEntity> findByPackingBucketId(String packingBucketId);

	@Modifying
	@Transactional
	@Query("delete from PackingBucketChildEntity where bucketId = :bucketId")
	void deleteByBucketId(@Param("bucketId") Integer partyId);

}
