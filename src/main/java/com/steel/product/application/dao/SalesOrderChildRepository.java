package com.steel.product.application.dao;

import com.steel.product.application.entity.SalesOrderPacketsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import javax.transaction.Transactional;

@Repository
public interface SalesOrderChildRepository extends JpaRepository<SalesOrderPacketsEntity, Integer> {

	@Modifying
	@Transactional
	@Query("update SalesOrderPacketsEntity inw set inw.isDeleted = true, inw.updatedBy=:userId, inw.updatedOn=CURRENT_TIMESTAMP where inw.soChildId in :itemIds ")
	void deleteData(@Param("itemIds") List<Integer> itemIds, @Param("userId") Integer userId);
	
	
}
