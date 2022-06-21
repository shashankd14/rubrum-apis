package com.steel.product.application.service;

import com.steel.product.application.dao.AdditionalPriceMasterRepository;
import com.steel.product.application.dao.AdditionalPriceStaticRepository;
import com.steel.product.application.dto.additionalpricemaster.AdditionalPriceMasterRequest;
import com.steel.product.application.dto.additionalpricemaster.AdditionalPriceMasterResponse;
import com.steel.product.application.dto.pricemaster.PriceMasterResponse;
import com.steel.product.application.entity.AdditionalPriceMasterEntity;
import com.steel.product.application.entity.AdditionalPriceStaticEntity;
import com.steel.product.application.entity.PriceMasterEntity;

import lombok.extern.log4j.Log4j2;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class AdditionalPriceMasterServiceImpl implements AdditionalPriceMasterService {

	@Autowired
	AdditionalPriceMasterRepository additionalPriceMasterRepository;

	@Autowired
	AdditionalPriceStaticRepository additionalPriceStaticRepository;

	@Override
	public List<AdditionalPriceStaticEntity> additionalPriceStaticDetails(Integer id) {

		List<AdditionalPriceStaticEntity> list = additionalPriceStaticRepository.findByProcessId(id);
		return list;
	}
	
	@Override
	public ResponseEntity<Object> save(List<AdditionalPriceMasterRequest> priceMasterRequestList) {

		ResponseEntity<Object> response = null;
		
		for (AdditionalPriceMasterRequest priceMasterRequest : priceMasterRequestList) {
			List<AdditionalPriceMasterEntity> fromList = additionalPriceMasterRepository.validateRange(priceMasterRequest.getPartyId(),
					priceMasterRequest.getProcessId(), priceMasterRequest.getAdditionalPriceId(), 
					priceMasterRequest.getRangeFrom());
			
			if(fromList!=null && fromList.size()>0) {
				if(fromList.size()==1) {
					AdditionalPriceMasterEntity duplicateEntity=fromList.get(0);
					if(priceMasterRequest.getId() != duplicateEntity.getId() ) {
						return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered From Range Already Exists.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
					}
				} else {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered From Range Already Exists.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}else {
				List<AdditionalPriceMasterEntity> toList = additionalPriceMasterRepository.validateRange(priceMasterRequest.getPartyId(),
						priceMasterRequest.getProcessId(), priceMasterRequest.getAdditionalPriceId(),
						priceMasterRequest.getRangeTo());
				if(toList!=null && toList.size()>0) {
					if(toList.size()==1) {
						AdditionalPriceMasterEntity duplicateEntity=toList.get(0);
						if(priceMasterRequest.getId() != duplicateEntity.getId() ) {
							return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered To Range Already Exists.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
						}
					} else {
						return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered To Range Already Exists.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}
			}
		}
		for (AdditionalPriceMasterRequest priceMasterRequest : priceMasterRequestList) {

			AdditionalPriceMasterEntity priceMasterEntity = new AdditionalPriceMasterEntity();
			if(priceMasterRequest.getId()!=null && priceMasterRequest.getId()>0) {
				priceMasterEntity.setId( priceMasterRequest.getId() );
			}
			priceMasterEntity.setPartyId( priceMasterRequest.getPartyId() );
			priceMasterEntity.setProcessId( priceMasterRequest.getProcessId() );
			priceMasterEntity.setAdditionalPriceId( priceMasterRequest.getAdditionalPriceId() );
			priceMasterEntity.setPrice( priceMasterRequest.getPrice() );
			priceMasterEntity.setRangeFrom( priceMasterRequest.getRangeFrom() );
			priceMasterEntity.setRangeTo( priceMasterRequest.getRangeTo() );
			priceMasterEntity.setCreatedBy( priceMasterRequest.getUserId() );
			priceMasterEntity.setUpdatedBy( priceMasterRequest.getUserId() );
			priceMasterEntity.setCreatedOn( new Date() );
			priceMasterEntity.setUpdatedOn( new Date() );
			try {
				
				additionalPriceMasterRepository.save(priceMasterEntity);
	
				response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Additional Price Master details saved successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
			} catch (Exception e) {
				response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return response;
	}

	@Override
	public ResponseEntity<Object> delete(int id) {
		ResponseEntity<Object> response = null;
		try {
			additionalPriceMasterRepository.deleteById(id);
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Additional Master details deleted successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public AdditionalPriceMasterResponse getById(int id) {
		
		AdditionalPriceMasterResponse priceMasterResponse=new AdditionalPriceMasterResponse();
		Optional<AdditionalPriceMasterEntity> list = additionalPriceMasterRepository.findById(id);
		if(list.isPresent()){
			AdditionalPriceMasterEntity priceMasterEntity=list.get();
			priceMasterResponse = AdditionalPriceMasterEntity.valueOf(priceMasterEntity);
		}
		return priceMasterResponse;
	}

	@Override
	public List<AdditionalPriceMasterResponse> getCustProcessAdditionalPriceId(int partyId, int processId,
			int additionalPriceId) {

		List<AdditionalPriceMasterResponse> list = additionalPriceMasterRepository
				.findByPartyIdAndProcessIdAndAdditionalPriceId(partyId, processId, additionalPriceId).stream()
				.map(i -> AdditionalPriceMasterEntity.valueOf(i)).collect(Collectors.toList());

		return list;
	}
	
	@Override
	public List<AdditionalPriceMasterResponse> getCustProcess(int partyId, int processId) {

		List<AdditionalPriceMasterResponse> list = additionalPriceMasterRepository.findByPartyIdAndProcessId(partyId, processId).stream()
				.map(i -> AdditionalPriceMasterEntity.valueOf(i)).collect(Collectors.toList());

		return list;
	}

	@Override
	public List<AdditionalPriceMasterResponse> getCustPriceDetails(int partyId) {

		List<AdditionalPriceMasterResponse> list = additionalPriceMasterRepository.findByPartyId(partyId).stream()
				.map(i -> AdditionalPriceMasterEntity.valueOf(i)).collect(Collectors.toList());

		return list;
	}

	@Override
	public List<AdditionalPriceMasterResponse> getProcessPriceDetails(int processId) {

		List<AdditionalPriceMasterResponse> list = additionalPriceMasterRepository.findByProcessId(processId).stream()
				.map(i -> AdditionalPriceMasterEntity.valueOf(i)).collect(Collectors.toList());

		return list;
	}


	@Override
	public List<AdditionalPriceMasterResponse> getAllPriceDetails() {

		List<AdditionalPriceMasterResponse> list = additionalPriceMasterRepository.findAll().stream()
				.map(i -> AdditionalPriceMasterEntity.valueOf(i)).collect(Collectors.toList());

		return list;
	}

}
