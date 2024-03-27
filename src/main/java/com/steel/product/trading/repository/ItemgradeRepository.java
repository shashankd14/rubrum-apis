package com.steel.product.trading.repository;

import com.steel.product.trading.entity.ItemgradeEntity;
import com.steel.product.trading.entity.ItemgradeEntity;

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
public interface ItemgradeRepository extends JpaRepository<ItemgradeEntity, Integer> {

	@Query("select inw from ItemgradeEntity inw where inw.isDeleted is false and inw.itemgradeName = :itemgradeName and inw.itemgradeId not in :itemgradeId")
	List<ItemgradeEntity> findByItemgradeName(@Param("itemgradeName") String itemgradeName, @Param("itemgradeId") Integer itemgradeId);

	@Query("select inw from ItemgradeEntity inw where inw.isDeleted is false and inw.itemgradeName = :itemgradeName")
	List<ItemgradeEntity> findByItemgradeName(@Param("itemgradeName") String itemgradeName);

	@Query("select inw from ItemgradeEntity inw where inw.isDeleted is false and inw.itemgradeName like %:searchText%")
	Page<ItemgradeEntity> findAllWithSearchText(@Param("searchText") String searchText, Pageable pageable);

	@Query("select inw from ItemgradeEntity inw where inw.isDeleted is false")
	Page<ItemgradeEntity> findAll(Pageable pageable);

	@Modifying
	@Transactional
	@Query("update ItemgradeEntity inw set inw.isDeleted = true, inw.updatedBy=:userId, inw.updatedOn=CURRENT_TIMESTAMP where inw.itemgradeId in :itemIds")
	void deleteData(@Param("itemIds") List<Integer> itemIds, @Param("userId") Integer userId);
	
	Optional<ItemgradeEntity> findByItemgradeIdAndIsDeleted(Integer itemgradeId, Boolean isDeleted);
	
}
