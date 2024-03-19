package com.steel.product.trading.repository;

import com.steel.product.trading.entity.MaterialMasterEntity;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialMasterRepository extends JpaRepository<MaterialMasterEntity, Integer> {

	@Query("select inw from MaterialMasterEntity inw where (inw.itemHsnCode like %:searchText% or inw.itemCode like %:searchText% or inw.itemGrade like %:searchText% or inw.itemName like %:searchText% )")
	Page<MaterialMasterEntity> findAllWithSearchText(@Param("searchText") String searchText, Pageable pageable);

	Optional<MaterialMasterEntity> findById(Integer itemId);

}
