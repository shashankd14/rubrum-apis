package com.steel.product.jswone.service;

import com.steel.product.application.service.StatusService;
import com.steel.product.application.util.CommonUtil;
import com.steel.product.jswone.entity.SalesOrderJswEntity;
import com.steel.product.jswone.entity.SalesOrderPacketsJswEntity;
import com.steel.product.jswone.repository.SalesOrderChildJswRepository;
import com.steel.product.jswone.repository.SalesOrderJswRepository;
import com.steel.product.jswone.request.SalesOrderChildRequest;
import com.steel.product.jswone.request.SalesOrderMainRequest;

import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
@Log4j2
public class SalesOrderServiceJswImpl implements SalesOrderJswService {

	private SalesOrderJswRepository salesOrderRepository;

	private SalesOrderChildJswRepository childRepository;

	private CommonUtil commonUtil;

	private StatusService statusService;

	private SpringTemplateEngine templateEngine;
	
	@Autowired
	public SalesOrderServiceJswImpl(SalesOrderJswRepository salesOrderRepository, CommonUtil commonUtil,
			StatusService statusService, SalesOrderChildJswRepository childRepository,
			SpringTemplateEngine templateEngine) {
		this.childRepository = childRepository;
		this.salesOrderRepository = salesOrderRepository;
		this.commonUtil = commonUtil;
		this.statusService = statusService;
		this.templateEngine = templateEngine;
	}

	@Override
	public ResponseEntity<Object> save(SalesOrderMainRequest salesOrderMainRequest) {
		log.info("SalesOrderServiceImpl.Save ");
		ResponseEntity<Object> responseEntity = null;
		String message = "Sales Order created successfully !";
		try {
			BigDecimal totalqty = new BigDecimal("0.00");
			SalesOrderJswEntity salesOrderEntity = new SalesOrderJswEntity();
			BeanUtils.copyProperties(salesOrderEntity, salesOrderMainRequest);
			salesOrderEntity.setCreatedBy(commonUtil.getUserId());
			salesOrderEntity.setAllocatedStts("PENDING");
			salesOrderEntity.setIsDeleted(false);
			salesOrderEntity.setStatus(this.statusService.getStatusById(1));
			for (SalesOrderChildRequest dto : salesOrderMainRequest.getItemsList()) {
				SalesOrderPacketsJswEntity childEntity = new SalesOrderPacketsJswEntity();
				BeanUtils.copyProperties(childEntity, dto);
				childEntity.setCreatedBy(commonUtil.getUserId());
				childEntity.setStatus(this.statusService.getStatusById(1));
				childEntity.setAllocatedStts("PENDING");
				childEntity.setIsDeleted(false);
				salesOrderEntity.addItem(childEntity);
				totalqty = totalqty.add(childEntity.getSoqty());
			}
			salesOrderEntity.setTotalSoqty( totalqty);
			salesOrderRepository.save(salesOrderEntity);
		} catch (Exception e) {
			e.printStackTrace();
			responseEntity = new ResponseEntity<>("{\"status\": \"failure\", \"message\": \"" + e.getMessage() + "\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		responseEntity = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"" + message + "\"}", new HttpHeaders(), HttpStatus.OK);
		return responseEntity;
	}
 
}
