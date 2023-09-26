package com.steel.product.application.service;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.steel.product.application.dao.AdminMenuRepository;
import com.steel.product.application.dto.admin.AdminMenuAccessRequest;
import com.steel.product.application.dto.admin.AdminMenuDto;
import com.steel.product.application.dto.admin.AdminMenuRequest;
import com.steel.product.application.entity.AdminMenuEntity;
import com.steel.product.application.exception.BaseException;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class AdminMenuService {

	@Autowired
	AdminMenuRepository adminMenuRepository;

	@PersistenceContext
	private EntityManager entityManager;

	public ResponseEntity<Object> getMenusAccess(AdminMenuAccessRequest adminMenuAccessRequest) throws BaseException {
		log.info("******AdminMenuService.getMenusAccess**************");
		ResponseEntity<Object> response = null;
		List<AdminMenuDto> result = new ArrayList<>();

		try {
			result = adminMenuRepository.findMenus(adminMenuAccessRequest.getUserId());
			response = new ResponseEntity<Object>(result, new HttpHeaders(), HttpStatus.OK);

		} catch (Exception ex) {
			response = new ResponseEntity<Object>(result, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	public ResponseEntity<Object> getMenus(AdminMenuAccessRequest adminMenuAccessRequest) throws BaseException {
		log.info("******AdminMenuService.getMenus**************");
		ResponseEntity<Object> response = null;
		List<AdminMenuDto> result = new ArrayList<>();
		try {
			result = adminMenuRepository.findAllMenus();
			response = new ResponseEntity<Object>(result, new HttpHeaders(), HttpStatus.OK);

		} catch (Exception ex) {
			response = new ResponseEntity<Object>(result, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	public ResponseEntity<Object> getParentMenus(AdminMenuRequest adminMenuRequest) throws BaseException {
		log.info("******AdminMenuService.getParentMenus**************");
		List<AdminMenuEntity> menus = new ArrayList<>();
		ResponseEntity<Object> response = null;

		try {

			menus = adminMenuRepository.findByParentMenuIdAndActiveFlagOrderByDisplayOrder(0, "Y");
			response = new ResponseEntity<Object>(menus, new HttpHeaders(), HttpStatus.OK);

		} catch (Exception e) {
			response = new ResponseEntity<Object>(menus, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
}
