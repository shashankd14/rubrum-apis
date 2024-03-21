package com.steel.product.trading.repository;

import com.steel.product.trading.entity.CustomerEntity;
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
public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer> {

	@Query("select inw from CustomerEntity inw where inw.isDeleted is false and inw.customerName = :customerName and inw.customerId not in :customerId")
	List<CustomerEntity> findByCustomerName(@Param("customerName") String customerName, @Param("customerId") Integer customerId);

	@Query("select inw from CustomerEntity inw where inw.isDeleted is false and inw.customerName = :customerName")
	List<CustomerEntity> findByCustomerName(@Param("customerName") String customerName);

	@Query("select inw from CustomerEntity inw where inw.isDeleted is false and inw.customerName like %:searchText%")
	Page<CustomerEntity> findAllWithSearchText(@Param("searchText") String searchText, Pageable pageable);

	@Query("select inw from CustomerEntity inw where inw.isDeleted is false")
	Page<CustomerEntity> findAll(Pageable pageable);

	@Modifying
	@Transactional
	@Query("update CustomerEntity inw set inw.isDeleted = true, inw.updatedBy=:userId, inw.updatedOn=CURRENT_TIMESTAMP where inw.customerId in :itemIds")
	void deleteData(@Param("itemIds") List<Integer> itemIds, @Param("userId") Integer userId);
	
	Optional<CustomerEntity> findByCustomerIdAndIsDeleted(Integer customerId, Boolean isDeleted);

}
