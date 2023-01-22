package com.steel.product.application.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.steel.product.application.dto.admin.AdminMenuAccessRequest;
import com.steel.product.application.dto.admin.AdminMenuRequest;
import com.steel.product.application.exception.BaseException;
import com.steel.product.application.service.AdminMenuService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;

@RestController
@CrossOrigin
@Log4j2
@Tag(name = "Admin Access Details", description = "Admin Access Details")
@RequestMapping({ "/access" })
public class AdminMenusController {

	@Autowired
	AdminMenuService adminMenuService;

	@GetMapping(value = "getParentMenus", produces = "application/json")
	public ResponseEntity<Object> getUserParentMenus(@Valid @RequestBody AdminMenuRequest adminMenuRequest,
			HttpServletRequest request) throws BaseException {
		log.info("******AdminMenusController.getUserParentMenus*****");
		return adminMenuService.getParentMenus(adminMenuRequest);

	}

	@GetMapping(value = "getMenus", produces = "application/json")
	public ResponseEntity<Object> getUserMenus(@Valid @RequestBody AdminMenuAccessRequest adminMenuAccessRequest,
			HttpServletRequest request) throws BaseException {

		log.info("******AdminMenusController.getUserMenus*****");
		return adminMenuService.getMenusAccess(adminMenuAccessRequest);
	}

	@GetMapping(value = "getAllMenus", produces = "application/json")
	public ResponseEntity<Object> geMenus(@Valid @RequestBody AdminMenuAccessRequest adminMenuAccessRequest,
			HttpServletRequest request) throws BaseException {

		log.info("******AdminMenusController.geMenus*****");
		return adminMenuService.getMenus(adminMenuAccessRequest);

	}

}
