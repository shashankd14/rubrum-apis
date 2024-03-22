package com.steel.product.trading.repository;

import com.steel.product.trading.entity.CategoryEntity;
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
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {

	@Query("select inw from CategoryEntity inw where inw.isDeleted is false and inw.categoryName = :categoryName and inw.categoryId not in :categoryId")
	List<CategoryEntity> findByCategoryName(@Param("categoryName") String categoryName, @Param("categoryId") Integer categoryId);

	@Query("select inw from CategoryEntity inw where inw.isDeleted is false and inw.categoryName = :categoryName")
	List<CategoryEntity> findByCategoryName(@Param("categoryName") String categoryName);

	@Query("select inw from CategoryEntity inw where inw.isDeleted is false and inw.categoryName like %:searchText%")
	Page<CategoryEntity> findAllWithSearchText(@Param("searchText") String searchText, Pageable pageable);

	@Query("select inw from CategoryEntity inw where inw.isDeleted is false")
	Page<CategoryEntity> findAll(Pageable pageable);

	@Modifying
	@Transactional
	@Query("update CategoryEntity inw set inw.isDeleted = true, inw.updatedBy=:userId, inw.updatedOn=CURRENT_TIMESTAMP where inw.categoryId in :itemIds")
	void deleteData(@Param("itemIds") List<Integer> itemIds, @Param("userId") Integer userId);
	
	Optional<CategoryEntity> findByCategoryIdAndIsDeleted(Integer categoryId, Boolean isDeleted);
	
}
