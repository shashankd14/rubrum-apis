package com.steel.product.trading.repository;

import com.steel.product.trading.entity.LocationEntity;

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
public interface LocationRepository extends JpaRepository<LocationEntity, Integer> {

	@Query("select inw from LocationEntity inw where inw.isDeleted is false and inw.locationName = :locationName and inw.locationId not in :locationId")
	List<LocationEntity> findByLocationName(@Param("locationName") String locationName, @Param("locationId") Integer locationId);

	@Query("select inw from LocationEntity inw where inw.isDeleted is false and inw.locationName = :locationName")
	List<LocationEntity> findByLocationName(@Param("locationName") String locationName);

	@Query("select inw from LocationEntity inw where inw.isDeleted is false and inw.locationName like %:searchText%")
	Page<LocationEntity> findAllWithSearchText(@Param("searchText") String searchText, Pageable pageable);

	@Query("select inw from LocationEntity inw where inw.isDeleted is false")
	Page<LocationEntity> findAll(Pageable pageable);

	@Modifying
	@Transactional
	@Query("update LocationEntity inw set inw.isDeleted = true, inw.updatedBy=:userId, inw.updatedOn=CURRENT_TIMESTAMP where inw.locationId in :itemIds")
	void deleteData(@Param("itemIds") List<Integer> itemIds, @Param("userId") Integer userId);
	
	Optional<LocationEntity> findByLocationIdAndIsDeleted(Integer locationId, Boolean isDeleted);

	
}
