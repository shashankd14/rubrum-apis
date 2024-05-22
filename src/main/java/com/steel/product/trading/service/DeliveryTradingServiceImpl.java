package com.steel.product.trading.service;

import com.steel.product.trading.entity.DOEntity;
import com.steel.product.trading.entity.EQPEntity;
import com.steel.product.trading.repository.DORepository;
import com.steel.product.trading.repository.EQPChildRepository;
import com.steel.product.trading.repository.EQPRepository;
import com.steel.product.trading.request.DeleteRequest;
import com.steel.product.trading.request.DeliveryOrderRequest;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class DeliveryTradingServiceImpl implements DeliveryTradingService {

	@Autowired
	DORepository doRepository;

	@Autowired
	EQPRepository eqpRepository;
	
	@Autowired
	EQPChildRepository childRepository;

	@Override
	public ResponseEntity<Object> save(DeliveryOrderRequest request) {
		log.info("In DeliveryTradingServiceImpl.save page ");
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		String message = "Delivery Order Details saved successfully..!";
		try {

			EQPEntity eqpEntity = new EQPEntity();
			Optional<EQPEntity> kk1 = eqpRepository.findById(request.getEnquiryId());
			if (kk1.isPresent()) {
				eqpEntity = kk1.get();
			} else {
				return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Please enter valid enquiry data\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			DOEntity dummydoEntity = new DOEntity();
			List<DOEntity> oldDOEntity = doRepository.findAllEnqIds(request.getEnquiryId());
			if (oldDOEntity!= null && oldDOEntity.size()> 0 ) {
				dummydoEntity = oldDOEntity.get(0);
			}

			DOEntity doEntity = new DOEntity();
			BeanUtils.copyProperties(request, doEntity);
			doEntity.setIsDeleted(false);

			if (dummydoEntity.getDoId() != null && dummydoEntity.getDoId() > 0) {
				doEntity.setDoId( dummydoEntity.getDoId());

				if (dummydoEntity != null && dummydoEntity.getDoId() > 0) {
					doEntity.setUpdatedBy(request.getUserId());
					doEntity.setUpdatedOn(new Date());
					doEntity.setCreatedBy(dummydoEntity.getCreatedBy());
					doEntity.setCreatedOn(dummydoEntity.getCreatedOn());
					doEntity.setEnquiryId(eqpEntity);
					message = "Delivery Order details updated successfully..! ";
				} else {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Please enter valid data\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				if (eqpEntity.getCurrentStatus() == null || (!"PROFORMA".equals(eqpEntity.getCurrentStatus()))) {
					return new ResponseEntity<>( "{\"status\": \"fail\", \"message\": \"Please enter valid enquiry data\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				doEntity.setCreatedBy(request.getUserId());
				doEntity.setCreatedOn(new Date());
				doEntity.setEnquiryId(eqpEntity);
			}
			doRepository.save(doEntity);
			List<Integer> ids = new ArrayList<>();
			ids.add(eqpEntity.getEnquiryId());
			eqpRepository.updateDOStatus(ids, request.getUserId());
			childRepository.updateChildDOStatus(eqpEntity.getEnquiryId(), request.getUserId());
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"" + message + " \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("error is ==" + e.getMessage());
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Error Occurred\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	
	@Override
	public ResponseEntity<Object> doDelete(DeleteRequest deleteRequest) {
		log.info("In quoteDelete page ");
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		try {
			doRepository.deleteDOMainData(deleteRequest.getIds(), deleteRequest.getUserId());
			eqpRepository.deleteDOMainData(deleteRequest.getIds(), deleteRequest.getUserId());
			childRepository.deleteDOChildData(deleteRequest.getIds(), deleteRequest.getUserId());
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Selected DO details has been deleted successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Error Occurred\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
}