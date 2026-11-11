package com.steel.product.trading.service;

import com.steel.product.trading.exception.TradingErrors;
import com.steel.product.trading.entity.DCEntity;
import com.steel.product.trading.entity.DOEntity;
import com.steel.product.trading.entity.EQPEntity;
import com.steel.product.trading.repository.DCRepository;
import com.steel.product.trading.repository.DORepository;
import com.steel.product.trading.repository.EQPChildRepository;
import com.steel.product.trading.repository.EQPRepository;
import com.steel.product.trading.request.DeleteRequest;
import com.steel.product.trading.request.DeliveryChalanRequest;
import com.steel.product.trading.request.DeliveryOrderRequest;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
@Log4j2
public class DeliveryTradingServiceImpl implements DeliveryTradingService {

	@Autowired
	DORepository doRepository;
	
	@Autowired
	DCRepository dcRepository;

	@Autowired
	EQPRepository eqpRepository;
	
	@Autowired
	EQPChildRepository childRepository;

	@Override
	@Transactional
	public ResponseEntity<Object> save(DeliveryOrderRequest request) {
		log.debug("In DeliveryTradingServiceImpl.save page ");
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		String message = "Delivery Order Details saved successfully..!";
		try {

			EQPEntity eqpEntity = new EQPEntity();
			Optional<EQPEntity> kk1 = eqpRepository.findForDispatchUpdate(request.getEnquiryId());
			if (kk1.isPresent()) {
				eqpEntity = kk1.get();
			} else {
				return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Please enter valid enquiry data\"}", header, HttpStatus.BAD_REQUEST);
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
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Please enter valid data\"}", header, HttpStatus.BAD_REQUEST);
				}
			} else {
				if (eqpEntity.getCurrentStatus() == null || (!"PROFORMA".equals(eqpEntity.getCurrentStatus()) || !"PENDING_DO".equals(eqpEntity.getProformaStatus()))) {
					return new ResponseEntity<>( "{\"status\": \"fail\", \"message\": \"Please enter valid enquiry data\"}", header, HttpStatus.BAD_REQUEST);
				}
				doEntity.setCreatedBy(request.getUserId());
				doEntity.setCreatedOn(new Date());
				doEntity.setEnquiryId(eqpEntity);
			}
			// Editing logistics requires a fresh loading approval.
			if ("DO".equals(eqpEntity.getCurrentStatus())) {
				eqpEntity.setdOStatus("DO");
				eqpRepository.save(eqpEntity);
			}
			doRepository.save(doEntity);
			List<Integer> ids = new ArrayList<>();
			ids.add(eqpEntity.getEnquiryId());
			eqpRepository.updateDOStatus(ids, request.getUserId());
			childRepository.updateChildDOStatus(eqpEntity.getEnquiryId(), request.getUserId());
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"" + message + " \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			response = TradingErrors.failure("save the delivery order", e);
		}
		return response;
	}

	private ResponseEntity<Object> invalidDispatchIds(DeleteRequest request, String stage, String label) {
		if (request.getIds() == null || request.getIds().isEmpty()) {
			return TradingErrors.body(HttpStatus.BAD_REQUEST, "Select a " + label + " to delete.", null);
		}
		List<EQPEntity> records = eqpRepository.findAllById(request.getIds());
		if (records.size() != new HashSet<>(request.getIds()).size()
				|| records.stream().anyMatch(r -> Boolean.TRUE.equals(r.getIsDeleted()) || !stage.equals(r.getCurrentStatus()))) {
			String hint = "DO".equals(stage) ? " Delete its delivery challan first if one exists." : "";
			return TradingErrors.body(HttpStatus.BAD_REQUEST, "Only an existing " + label + " can be deleted." + hint + " Refresh the list and try again.", null);
		}
		return null;
	}

	@Override
	@Transactional
	public ResponseEntity<Object> doDelete(DeleteRequest deleteRequest) {
		log.debug("In doDelete page ");
		try {
			ResponseEntity<Object> invalid = invalidDispatchIds(deleteRequest, "DO", "delivery order");
			if (invalid != null) return invalid;
			doRepository.deleteDOMainData(deleteRequest.getIds(), deleteRequest.getUserId());
			eqpRepository.deleteDOMainData(deleteRequest.getIds(), deleteRequest.getUserId());
			childRepository.deleteDOChildData(deleteRequest.getIds(), deleteRequest.getUserId());
			return new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Delivery order deleted. The proforma is back in Pending Proformas.\"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return TradingErrors.failure("delete the delivery order", e);
		}
	}

	@Override
	@Transactional
	public ResponseEntity<Object> dcDelete(DeleteRequest deleteRequest) {
		log.debug("In dcDelete page ");
		try {
			ResponseEntity<Object> invalid = invalidDispatchIds(deleteRequest, "DC", "delivery challan");
			if (invalid != null) return invalid;
			dcRepository.deleteDCMainData(deleteRequest.getIds(), deleteRequest.getUserId());
			eqpRepository.deleteDCMainData(deleteRequest.getIds(), deleteRequest.getUserId());
			childRepository.deleteDCChildData(deleteRequest.getIds(), deleteRequest.getUserId());
			return new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Delivery challan deleted. The order is back in Delivery Orders.\"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return TradingErrors.failure("delete the delivery challan", e);
		}
	}

	@Override
	@Transactional
	public ResponseEntity<Object> dcSave(DeliveryChalanRequest request) {
		log.debug("In DeliveryTradingServiceImpl.dcSave page ");
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		String message = "Delivery Chalan Details saved successfully..!";
		try {

			EQPEntity eqpEntity = new EQPEntity();
			Optional<EQPEntity> kk1 = eqpRepository.findForDispatchUpdate(request.getEnquiryId());
			if (kk1.isPresent()) {
				eqpEntity = kk1.get();
			} else {
				return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Please enter valid enquiry data\"}", header, HttpStatus.BAD_REQUEST);
			}
			
			DCEntity dummydoEntity = new DCEntity();
			List<DCEntity> oldDOEntity = dcRepository.findAllEnqIds(request.getEnquiryId());
			if (oldDOEntity!= null && oldDOEntity.size()> 0 ) {
				dummydoEntity = oldDOEntity.get(0);
			}

			DCEntity doEntity = new DCEntity();
			BeanUtils.copyProperties(request, doEntity);
			doEntity.setIsDeleted(false);

			if (dummydoEntity.getDcId() != null && dummydoEntity.getDcId() > 0) {
				doEntity.setDcId( dummydoEntity.getDcId());

				if (dummydoEntity != null && dummydoEntity.getDcId() > 0) {
					doEntity.setUpdatedBy(request.getUserId());
					doEntity.setUpdatedOn(new Date());
					doEntity.setCreatedBy(dummydoEntity.getCreatedBy());
					doEntity.setCreatedOn(dummydoEntity.getCreatedOn());
					doEntity.setEnquiryId(eqpEntity);
					message = "Delivery Chalan details updated successfully..! ";
				} else {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Please enter valid data\"}", header, HttpStatus.BAD_REQUEST);
				}
			} else {
				if (!"DO".equals(eqpEntity.getCurrentStatus()) || !"APPROVED".equals(eqpEntity.getdOStatus())) {
					return new ResponseEntity<>( "{\"status\": \"fail\", \"message\": \"Approve delivery order loading before creating a challan.\"}", header, HttpStatus.CONFLICT);
				}
				doEntity.setCreatedBy(request.getUserId());
				doEntity.setCreatedOn(new Date());
				doEntity.setEnquiryId(eqpEntity);
			}
			dcRepository.save(doEntity);
			List<Integer> ids = new ArrayList<>();
			ids.add(eqpEntity.getEnquiryId());
			eqpRepository.updateDCStatus(ids, request.getUserId());
			childRepository.updateChildDCStatus(eqpEntity.getEnquiryId(), request.getUserId());
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"" + message + " \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			response = TradingErrors.failure("save the delivery challan", e);
		}
		return response;
	}

}
