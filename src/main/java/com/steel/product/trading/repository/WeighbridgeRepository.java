package com.steel.product.trading.repository;

import com.steel.product.trading.entity.WeighbridgeEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface WeighbridgeRepository extends JpaRepository<WeighbridgeEntity, Integer> {

	@Query("select inw from WeighbridgeEntity inw where inw.isDeleted is false and inw.weighbridgeName = :weighbridgeName and inw.weighbridgeId not in :weighbridgeId")
	List<WeighbridgeEntity> findByWeighbridgeName(@Param("weighbridgeName") String weighbridgeName, @Param("weighbridgeId") Integer weighbridgeId);

	@Query("select inw from WeighbridgeEntity inw where inw.isDeleted is false and inw.weighbridgeName = :weighbridgeName")
	List<WeighbridgeEntity> findByWeighbridgeName(@Param("weighbridgeName") String weighbridgeName);

	@Query("select inw from WeighbridgeEntity inw where inw.isDeleted is false and inw.weighbridgeName like %:searchText%")
	Page<WeighbridgeEntity> findAllWithSearchText(@Param("searchText") String searchText, Pageable pageable);

	@Query("select inw from WeighbridgeEntity inw where inw.isDeleted is false")
	Page<WeighbridgeEntity> findAll(Pageable pageable);

	@Modifying
	@Transactional
	@Query("update WeighbridgeEntity inw set inw.isDeleted = true, inw.updatedBy=:userId, inw.updatedOn=CURRENT_TIMESTAMP where inw.weighbridgeId in :itemIds")
	void deleteData(@Param("itemIds") List<Integer> itemIds, @Param("userId") Integer userId);
	
	Optional<WeighbridgeEntity> findByWeighbridgeIdAndIsDeleted(Integer weighbridgeId, Boolean isDeleted);

	
}
