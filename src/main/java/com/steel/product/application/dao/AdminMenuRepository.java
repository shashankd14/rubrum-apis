package com.steel.product.application.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.steel.product.application.dto.admin.AdminMenuDto;
import com.steel.product.application.entity.AdminMenuEntity;

public interface AdminMenuRepository extends PagingAndSortingRepository<AdminMenuEntity, Integer>, JpaSpecificationExecutor<AdminMenuEntity>{

	public List<AdminMenuEntity> findByParentMenuIdAndActiveFlagOrderByDisplayOrder(int i, String activeFlag);

	@Query(name = "findMenu", nativeQuery = true)
	List<AdminMenuDto> findMenus(@Param("userId") Integer userId);
	
	@Query(name = "findAllMenu", nativeQuery = true)
	List<AdminMenuDto> findAllMenus();
	
	AdminMenuEntity findByIdAndActiveFlag(Integer id, String flag);
		 
}
