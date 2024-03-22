package com.steel.product.trading.repository;

import com.steel.product.trading.entity.ManufacturerEntity;
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
public interface ManufacturerRepository extends JpaRepository<ManufacturerEntity, Integer> {

	@Query("select inw from ManufacturerEntity inw where inw.isDeleted is false and inw.manufacturerName = :manufacturerName and inw.manufacturerId not in :manufacturerId")
	List<ManufacturerEntity> findByManufacturerName(@Param("manufacturerName") String manufacturerName, @Param("manufacturerId") Integer manufacturerId);

	@Query("select inw from ManufacturerEntity inw where inw.isDeleted is false and inw.manufacturerName = :manufacturerName")
	List<ManufacturerEntity> findByManufacturerName(@Param("manufacturerName") String categoryName);

	@Query("select inw from ManufacturerEntity inw where inw.isDeleted is false and inw.manufacturerName like %:searchText%")
	Page<ManufacturerEntity> findAllWithSearchText(@Param("searchText") String searchText, Pageable pageable);

	@Query("select inw from ManufacturerEntity inw where inw.isDeleted is false")
	Page<ManufacturerEntity> findAll(Pageable pageable);

	@Modifying
	@Transactional
	@Query("update ManufacturerEntity inw set inw.isDeleted = true, inw.updatedBy=:userId, inw.updatedOn=CURRENT_TIMESTAMP where inw.manufacturerId in :itemIds")
	void deleteData(@Param("itemIds") List<Integer> itemIds, @Param("userId") Integer userId);
	
	Optional<ManufacturerEntity> findByManufacturerIdAndIsDeleted(Integer manufacturerId, Boolean isDeleted);
	
}
