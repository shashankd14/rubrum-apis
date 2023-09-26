package com.steel.product.application.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.steel.product.application.entity.RoleEntity;

@Transactional(readOnly = false)
public interface RoleRepository extends PagingAndSortingRepository<RoleEntity, Integer>, JpaSpecificationExecutor<RoleEntity> {

	int countByRoleNameIgnoreCase(String roleName);
	
	@Query("SELECT MAX(roleId) FROM RoleEntity")
	Integer getLastRoleId();
	
	RoleEntity findByRoleId (int roleId );
	
	void deleteByRoleId (Integer roleId );
	
	@Query("SELECT re.roleName FROM RoleEntity re where re.roleId In(:roleId) ")
	List<String> findRoleNameByRoleIdIn (@Param("roleId") List<Integer> roleId );
	
	@Query(value = "SELECT nextval('SEQ_ADMIN_ROLE_ID')", nativeQuery =true)
	Integer getNextRoleId();

}
