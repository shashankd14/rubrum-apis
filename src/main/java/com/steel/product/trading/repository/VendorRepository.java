package com.steel.product.trading.repository;

import com.steel.product.trading.entity.VendorEntity;

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
public interface VendorRepository extends JpaRepository<VendorEntity, Integer> {

	@Query("select inw from VendorEntity inw where inw.isDeleted is false and inw.vendorName = :vendorName and inw.vendorId not in :vendorId")
	List<VendorEntity> findByVendorName(@Param("vendorName") String vendorName, @Param("vendorId") Integer vendorId);

	@Query("select inw from VendorEntity inw where inw.isDeleted is false and inw.vendorName = :vendorName")
	List<VendorEntity> findByVendorName(@Param("vendorName") String categoryName);

	@Query("select inw from VendorEntity inw where inw.isDeleted is false and inw.vendorName like %:searchText%")
	Page<VendorEntity> findAllWithSearchText(@Param("searchText") String searchText, Pageable pageable);

	@Query("select inw from VendorEntity inw where inw.isDeleted is false")
	Page<VendorEntity> findAll(Pageable pageable);

	@Modifying
	@Transactional
	@Query("update VendorEntity inw set inw.isDeleted = true, inw.updatedBy=:userId, inw.updatedOn=CURRENT_TIMESTAMP where inw.vendorId in :itemIds")
	void deleteData(@Param("itemIds") List<Integer> itemIds, @Param("userId") Integer userId);
	
}
