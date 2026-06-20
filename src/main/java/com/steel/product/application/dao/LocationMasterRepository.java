package com.steel.product.application.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.steel.product.application.entity.LocationMasterEntity;

public interface LocationMasterRepository extends JpaRepository<LocationMasterEntity, Integer> {

	@Query("SELECT l FROM LocationMasterEntity l WHERE (l.isDeleted = false OR l.isDeleted IS NULL) "
			+ "ORDER BY l.id DESC")
	List<LocationMasterEntity> findAllActive();

	@Query(value = "SELECT * FROM location_master WHERE location_id = :locationId AND is_deleted = 0", nativeQuery = true)
	Optional<LocationMasterEntity> findByLocationId(@Param("locationId") Integer locationId);

}
