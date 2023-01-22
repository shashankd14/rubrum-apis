package com.steel.product.application.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.steel.product.application.entity.RolePermissionMap;

@Transactional(readOnly = false)
public interface RolePermissionMapRepository
		extends PagingAndSortingRepository<RolePermissionMap, Integer>, JpaSpecificationExecutor<RolePermissionMap> {

	@Query("SELECT MAX(id) FROM RolePermissionMap")
	Integer getLastId();

	void deleteByRoleId (Integer roleId);

	List<RolePermissionMap> findByRoleId (int roleId);

}
