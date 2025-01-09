package com.steel.product.application.dao;

import com.steel.product.application.entity.PackingItemEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PackingItemRepository extends JpaRepository<PackingItemEntity, Integer> {

	List<PackingItemEntity> findByPackingItemId(String packingItemId);

	PackingItemEntity findByItemId(int itemId);

	@Query("select mat from PackingItemEntity mat where mat.createdBy in :userIds order by mat.itemId desc")
	public List<PackingItemEntity> findAllItems(@Param("userIds") List<Integer> userIds);
	

}
