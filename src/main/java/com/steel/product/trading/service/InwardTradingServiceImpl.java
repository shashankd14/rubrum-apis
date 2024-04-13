package com.steel.product.trading.service;

import com.steel.product.trading.entity.InwardTradingChildEntity;
import com.steel.product.trading.entity.InwardTradingEntity;
import com.steel.product.trading.repository.InwardTradingRepository;
import com.steel.product.trading.request.InwardTradingItemRequest;
import com.steel.product.trading.request.InwardTradingRequest;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class InwardTradingServiceImpl implements InwardTradingService {

	@Autowired
	InwardTradingRepository inwardTradingRepository;

	@Override
	public ResponseEntity<Object> save(InwardTradingRequest request) {
		log.info("In InwardTradingService page ");
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		String message = "Inward details saved successfully..! ";
		try {
			InwardTradingEntity inwardTradingEntity = new InwardTradingEntity();
			BeanUtils.copyProperties(request, inwardTradingEntity);
			
			for(InwardTradingItemRequest childReq : request.getItemsList()) {
				InwardTradingChildEntity childEntity = new InwardTradingChildEntity();
				BeanUtils.copyProperties(childReq, childEntity);
				inwardTradingEntity.addItem( childEntity);
			}
			System.out.println("hi getItemsList().size - "+inwardTradingEntity.getItemsList().size());
			inwardTradingRepository.save(inwardTradingEntity);
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \""+message+" \"}",	new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("error is ==" + e.getMessage());
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Error Occurred\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

}
