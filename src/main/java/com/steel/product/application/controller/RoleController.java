package com.steel.product.application.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.steel.product.application.dto.admin.RoleRequest;
import com.steel.product.application.dto.admin.RoleSearch;
import com.steel.product.application.service.RoleService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;

@RestController
@CrossOrigin
@Log4j2
@Tag(name = "Role Controller", description = "Role Controller")
@RequestMapping("/role")
public class RoleController {

	private final RoleService roleService;

	@Autowired
	RoleController(RoleService roleService) {
		this.roleService = roleService;
	}

	@PostMapping(value = "/create", produces = "application/json")
	public ResponseEntity<Object> createRole(@Valid @RequestBody RoleRequest createRoleDetails) throws Exception {
		log.info("******RoleController.createRole**************");
		return roleService.createRole(createRoleDetails);
	}

	@PutMapping(value = "/update", produces = "application/json")
	public ResponseEntity<Object> updateRole(@Valid @RequestBody RoleRequest createRoleDetails) throws Exception {
		log.info("******RoleController.updateRole**************");
		return roleService.updateRole(createRoleDetails);
	}

	@GetMapping(value = "/getRoleDetails", produces = "application/json")
	public Map<String, Object> getRoleDetails() throws Exception {
		RoleSearch roleSearch=new RoleSearch();
		roleSearch.setPage( 0);
		roleSearch.setSize(10000);
		log.info("******RoleController.getRoleDetails**************");
		Pageable paging = PageRequest.of(roleSearch.getPage(), roleSearch.getSize());
		return roleService.getAllRoles(roleSearch, paging);
	}

	@DeleteMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Object> deleteRole(@PathVariable("id") int id) throws Exception {
		log.info("******RoleController.deleteRole**************");
		return roleService.deleteRole(id);
	}

}
