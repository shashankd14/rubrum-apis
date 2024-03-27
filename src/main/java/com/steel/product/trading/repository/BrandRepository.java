package com.steel.product.trading.repository;

import com.steel.product.trading.entity.BrandEntity;
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
public interface BrandRepository extends JpaRepository<BrandEntity, Integer> {

	@Query("select inw from BrandEntity inw where inw.isDeleted is false and inw.brandName = :brandName and inw.brandId not in :brandId")
	List<BrandEntity> findByBrandName(@Param("brandName") String brandName, @Param("brandId") Integer brandId);

	@Query("select inw from BrandEntity inw where inw.isDeleted is false and inw.brandName = :brandName")
	List<BrandEntity> findByBrandName(@Param("brandName") String brandName);

	@Query("select inw from BrandEntity inw where inw.isDeleted is false and inw.brandName like %:searchText%")
	Page<BrandEntity> findAllWithSearchText(@Param("searchText") String searchText, Pageable pageable);

	@Query("select inw from BrandEntity inw where inw.isDeleted is false")
	Page<BrandEntity> findAll(Pageable pageable);

	@Modifying
	@Transactional
	@Query("update BrandEntity inw set inw.isDeleted = true, inw.updatedBy=:userId, inw.updatedOn=CURRENT_TIMESTAMP where inw.brandId in :itemIds")
	void deleteData(@Param("itemIds") List<Integer> itemIds, @Param("userId") Integer userId);
	
	Optional<BrandEntity> findByBrandIdAndIsDeleted(Integer brandId, Boolean isDeleted);
	
}
