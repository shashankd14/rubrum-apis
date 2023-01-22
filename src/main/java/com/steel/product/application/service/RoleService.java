package com.steel.product.application.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.steel.product.application.dao.RolePermissionMapRepository;
import com.steel.product.application.dao.RoleRepository;
import com.steel.product.application.dao.RoleSpecification;
import com.steel.product.application.dto.admin.RoleRequest;
import com.steel.product.application.dto.admin.RoleSearch;
import com.steel.product.application.dto.admin.RoleEntityDto;
import com.steel.product.application.dto.admin.RolePermissionList;
import com.steel.product.application.entity.RoleEntity;
import com.steel.product.application.entity.RolePermissionMap;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class RoleService {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private RolePermissionMapRepository rolePermissionMapRepository;

	public ResponseEntity<Object> createRole(@Valid RoleRequest createRoleDetails) {

		ResponseEntity<Object> response = null;
		
		int roleNameCount=0;
		try {
			roleNameCount = roleRepository.countByRoleNameIgnoreCase(createRoleDetails.getRoleName());
		} catch (Exception ex) {
			log.error("Unable to fetch role details", ex);
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + ex.getMessage() + " \"}",
					new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if (roleNameCount > 0) {
			return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Role name already exists.\"}",
					new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);

		} 
		
		RoleEntity roles = new RoleEntity();
		roles.setRoleName(createRoleDetails.getRoleName());
		roles.setRoleDesc(createRoleDetails.getRoleDesc());
		roles.setModifyBy(createRoleDetails.getUserId());
		roles.setActiveStatus("Y");
		roles.setModifyDate(new Date());
		roles.setPermissions(new ArrayList<>());

		for (RolePermissionList permission : createRoleDetails.getRolePermissionList()) {

			RolePermissionMap rolepermission = new RolePermissionMap();
			rolepermission.setMenuId(permission.getMenuId());
			rolepermission.setRole(roles);

			rolepermission.setPermission(permission.getPermission());
			roles.getPermissions().add(rolepermission);
		}

		try {
			roleRepository.save(roles);

			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Role created successfully..! \"}",
					new HttpHeaders(), HttpStatus.OK);

		} catch (Exception ex) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + ex.getMessage() + " \"}",
					new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
		return response;
	}
	
	public ResponseEntity<Object> updateRole(@Valid RoleRequest createRoleDetails) {

		ResponseEntity<Object> response = null;

		RoleEntity roles = new RoleEntity();
		roles.setRoleId( createRoleDetails.getRoleId());
		roles.setRoleName(createRoleDetails.getRoleName());
		roles.setRoleDesc(createRoleDetails.getRoleDesc());
		roles.setModifyBy(createRoleDetails.getUserId());
		roles.setActiveStatus( createRoleDetails.getActiveStatus());
		roles.setModifyDate(new Date());
		roles.setPermissions(new ArrayList<>());

		rolePermissionMapRepository.deleteByRoleId(createRoleDetails.getRoleId());
		
		for (RolePermissionList permission : createRoleDetails.getRolePermissionList()) {

			RolePermissionMap rolepermission = new RolePermissionMap();
			rolepermission.setMenuId(permission.getMenuId());
			rolepermission.setRole(roles);

			rolepermission.setPermission(permission.getPermission());
			roles.getPermissions().add(rolepermission);
		}

		try {
			roleRepository.save(roles);

			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Role updated successfully..! \"}", new HttpHeaders(), HttpStatus.OK);

		} catch (Exception ex) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + ex.getMessage() + " \"}", new HttpHeaders(), HttpStatus.OK);

		}
		return response;
	}

	public Map<String, Object> getAllRoles(RoleSearch roleSearch, Pageable paging) throws Exception {
		log.info("******RoleService.getAllRoles**************");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Map<String, Object> response = new HashMap<>();
		Page<RoleEntity> pageResult;
		Page<RoleEntityDto> dtos;
		try {
			RoleSpecification roleSpecification = new RoleSpecification(roleSearch);
			pageResult = roleRepository.findAll(roleSpecification, paging);
			dtos = pageResult.map(obj -> mapper.convertValue(obj, RoleEntityDto.class));

			response.put("roleDetails", dtos.getContent());
			response.put("currentPage", pageResult.getNumber());
			response.put("totalItems", pageResult.getTotalElements());
			response.put("totalPages", pageResult.getTotalPages());

		} catch (Exception e) {
			response = new HashMap<>();
		}

		return response;
	}

	public ResponseEntity<Object> deleteRole(Integer id) {
		ResponseEntity<Object> response = null;
		try {
			roleRepository.deleteById( id);
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Role details deleted successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

}
