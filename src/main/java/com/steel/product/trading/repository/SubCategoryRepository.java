package com.steel.product.trading.repository;

import com.steel.product.trading.entity.SubCategoryEntity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategoryEntity, Integer> {

	@Query("select inw from SubCategoryEntity inw where inw.isDeleted is false and inw.subcategoryName = :subcategoryName and inw.categoryId = :categoryId and inw.subcategoryId not in :subcategoryId")
	List<SubCategoryEntity> findBySubCategoryNameforUpdate(@Param("subcategoryName") String subcategoryName, @Param("categoryId") Integer categoryId, @Param("subcategoryId") Integer subcategoryId);
	
	@Query("select inw from SubCategoryEntity inw where inw.isDeleted is false and inw.subcategoryName = :subcategoryName and inw.categoryId = :categoryId")
	List<SubCategoryEntity> findBySubCategoryNameforInsert(@Param("subcategoryName") String subcategoryName, @Param("categoryId") Integer categoryId);

	@Query("select inw from SubCategoryEntity inw where inw.isDeleted is false and inw.subcategoryName like %:searchText%")
	Page<SubCategoryEntity> findAllWithSearchText(@Param("searchText") String searchText, Pageable pageable);

	@Query("select inw from SubCategoryEntity inw where inw.isDeleted is false")
	Page<SubCategoryEntity> findAll(Pageable pageable);
	
	@Modifying
	@Transactional
	@Query("update SubCategoryEntity inw set inw.isDeleted = true, inw.updatedBy=:userId, inw.updatedOn=CURRENT_TIMESTAMP where inw.subcategoryId in :itemIds")
	void deleteData(@Param("itemIds") List<Integer> itemIds, @Param("userId") Integer userId);
	
}
