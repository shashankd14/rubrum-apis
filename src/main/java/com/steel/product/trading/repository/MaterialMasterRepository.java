package com.steel.product.trading.repository;

import com.steel.product.trading.entity.MaterialMasterEntity;

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
public interface MaterialMasterRepository extends JpaRepository<MaterialMasterEntity, Integer> {

	@Query("select material from MaterialMasterEntity material where material.isDeleted is false and (material.itemHsnCode like %:searchText% or "
			+ "material.itemCode like %:searchText% or material.itemGradeId like %:searchText% or "
			+ "material.itemName like %:searchText% )")
	Page<MaterialMasterEntity> findAllWithSearchText(@Param("searchText") String searchText, Pageable pageable);

	@Query("select material from MaterialMasterEntity material where material.isDeleted is false")
	Page<MaterialMasterEntity> findAll(Pageable pageable);

	Optional<MaterialMasterEntity> findByItemIdAndIsDeleted(Integer itemId, Boolean isDeleted);

	@Query("select material from MaterialMasterEntity material where material.isDeleted is false and material.itemName = :itemName and material.itemId not in :itemId")
	List<MaterialMasterEntity> findByItemName(@Param("itemName") String itemName, @Param("itemId") Integer itemId);

	@Query("select material from MaterialMasterEntity material where material.isDeleted is false and material.itemCode = :itemCode and material.itemId not in :itemId")
	List<MaterialMasterEntity> findByItemCode(@Param("itemCode") String itemCode, @Param("itemId") Integer itemId);
	
	@Query("select material from MaterialMasterEntity material where material.isDeleted is false and material.itemName = :itemName")
	List<MaterialMasterEntity> findByItemName(@Param("itemName") String itemName);

	@Query("select material from MaterialMasterEntity material where material.isDeleted is false and material.itemCode = :itemCode")
	List<MaterialMasterEntity> findByItemCode(@Param("itemCode") String itemCode);

	@Modifying
	@Transactional
	@Query("update MaterialMasterEntity material set material.isDeleted = true, material.updatedBy=:userId, material.updatedOn=CURRENT_TIMESTAMP  where material.itemId in :itemIds")
	void deleteData(@Param("itemIds") List<Integer> itemIds, @Param("userId") Integer userId);

}
