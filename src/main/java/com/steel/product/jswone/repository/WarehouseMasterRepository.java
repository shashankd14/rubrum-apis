
package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.steel.product.jswone.entity.WarehouseMasterJswEntity;

@Repository
public interface WarehouseMasterRepository extends JpaRepository<WarehouseMasterJswEntity, Integer> {

	List<WarehouseMasterJswEntity> findByWareHouseId(String wareHouseId);
}
