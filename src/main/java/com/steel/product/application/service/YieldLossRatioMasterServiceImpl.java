package com.steel.product.application.service;

import com.steel.product.application.dao.YieldLossRatioMasterRepository;
import com.steel.product.application.dto.yieldlossratio.YieldLossRatioRequest;
import com.steel.product.application.dto.yieldlossratio.YieldLossRatioSearchRequest;
import com.steel.product.application.entity.YieldLossRatioMasterEntity;
import com.steel.product.trading.request.DeleteRequest;

import lombok.extern.log4j.Log4j2;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class YieldLossRatioMasterServiceImpl implements YieldLossRatioMasterService {

	@Autowired
	YieldLossRatioMasterRepository yieldLossRatioMasterRepository;

	@Autowired
	PartyDetailsService partyDetailsService;
	
	@Autowired
	ProcessService processService;

	@Override
	public ResponseEntity<Object> save(List<YieldLossRatioRequest> mainRequestList) {
		log.info("inside YieldLossRatioMasterServiceImpl -insert");
		ResponseEntity<Object> response = null;
		List<YieldLossRatioMasterEntity> list = new ArrayList<>();

		for (YieldLossRatioRequest mainRequest : mainRequestList) {
			for (Integer partyIdRequest : mainRequest.getPartyIdList()) {
				for (Integer processIdRequest : mainRequest.getProcessIdList()) {
					for (YieldLossRatioRequest childRequest : mainRequest.getRatioList()) {
						YieldLossRatioMasterEntity ylrEntity = new YieldLossRatioMasterEntity();
						ylrEntity.setPartyId(partyIdRequest);
						ylrEntity.setProcessId(processIdRequest);
						if (mainRequest.getYlrId() != null && mainRequest.getYlrId() > 0) {
							ylrEntity.setYlrId(mainRequest.getYlrId());
						}
						ylrEntity.setLossRatioPercentageFrom(childRequest.getLossRatioPercentageFrom());
						ylrEntity.setLossRatioPercentageTo(childRequest.getLossRatioPercentageTo());
						ylrEntity.setComments(childRequest.getComments());
						ylrEntity.setIsDeleted(false);
						ylrEntity.setCreatedBy(mainRequest.getUserId());
						ylrEntity.setUpdatedBy(null);
						ylrEntity.setCreatedOn(new Date());
						ylrEntity.setUpdatedOn(null);
						list.add(ylrEntity);
					}
				}
			}
		}

		for (YieldLossRatioMasterEntity entity : list) {
			
			List<YieldLossRatioMasterEntity> fromList = new ArrayList<>();
			List<YieldLossRatioMasterEntity> toList = new ArrayList<>();
						
			if(entity.getYlrId()!=null && entity.getYlrId()>0) {
				YieldLossRatioMasterEntity oldEntity = null;
				Optional<YieldLossRatioMasterEntity> kk = yieldLossRatioMasterRepository.findById(entity.getYlrId());
				if (kk.isPresent()) {
					oldEntity = kk.get();
				}
				
				if(oldEntity!=null && oldEntity.getYlrId()>0) {
					fromList = yieldLossRatioMasterRepository.validateRangeInUpdate(entity.getPartyId(), entity.getProcessId(), entity.getLossRatioPercentageFrom(), entity.getYlrId());
					toList   = yieldLossRatioMasterRepository.validateRangeInUpdate(entity.getPartyId(), entity.getProcessId(), entity.getLossRatioPercentageTo(), entity.getYlrId());
				} 
				
				entity.setUpdatedBy( entity.getCreatedBy());
				entity.setUpdatedOn(new Date());
				entity.setCreatedBy(oldEntity.getCreatedBy());
				entity.setCreatedOn(oldEntity.getCreatedOn());
			} else {
				fromList = yieldLossRatioMasterRepository.validateRange(entity.getPartyId(), entity.getProcessId(), entity.getLossRatioPercentageFrom());
				toList   = yieldLossRatioMasterRepository.validateRange(entity.getPartyId(), entity.getProcessId(), entity.getLossRatioPercentageTo());
			}
			
			if(fromList!=null && fromList.size()>0) {
				if(fromList.size()==1) {
					YieldLossRatioMasterEntity duplicateEntity=fromList.get(0);
					if(entity.getYlrId() != duplicateEntity.getYlrId() ) {
						return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered From Range Already Exists.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
					}
				} else {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered From Range Already Exists.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} 
			toList = yieldLossRatioMasterRepository.validateRange(entity.getPartyId(), entity.getProcessId(), entity.getLossRatioPercentageTo());
			if(toList!=null && toList.size()>0) {
				if(toList.size()==1) {
					YieldLossRatioMasterEntity duplicateEntity=toList.get(0);
					if(entity.getYlrId() != duplicateEntity.getYlrId() ) {
						return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered To Range Already Exists.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
					}
				} else {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered To Range Already Exists.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		}
				
		yieldLossRatioMasterRepository.saveAll(list);
		response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Yield Loss Ratio details saved successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		return response;
	}


	@Override
	public ResponseEntity<Object> delete(DeleteRequest deleteRequest) {
		log.info("In customerDelete page ");
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		
		try {
			yieldLossRatioMasterRepository.deleteData(deleteRequest.getIds(), deleteRequest.getUserId());
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Selected Yield Loss Ratio has been deleted successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Error Occurred\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	@Override
	public Page<Object[]> getAll(YieldLossRatioSearchRequest request) {
		Pageable pageable = PageRequest.of((request.getPageNo() - 1), request.getPageSize());
		Page<Object[]> list = yieldLossRatioMasterRepository.findAll(request.getPartyId(), pageable);
		return list;
	}

}
