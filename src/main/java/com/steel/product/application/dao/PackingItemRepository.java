package com.steel.product.application.dao;

import com.steel.product.application.entity.PackingItemEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackingItemRepository extends JpaRepository<PackingItemEntity, Integer> {

	List<PackingItemEntity> findByPackingItemId(String packingItemId);

}
