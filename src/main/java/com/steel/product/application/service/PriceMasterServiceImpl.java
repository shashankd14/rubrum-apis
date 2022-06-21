package com.steel.product.application.service;

import com.steel.product.application.dao.PriceMasterRepository;
import com.steel.product.application.dto.pricemaster.PriceMasterRequest;
import com.steel.product.application.dto.pricemaster.PriceMasterResponse;
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
public class PriceMasterServiceImpl implements PriceMasterService {

	@Autowired
	PriceMasterRepository priceMasterRepository;

	@Override
	public ResponseEntity<Object> save(List<PriceMasterRequest> priceMasterRequestList) {

		ResponseEntity<Object> response = null;
		
		for (PriceMasterRequest priceMasterRequest : priceMasterRequestList) {

			PriceMasterEntity priceMasterEntity = new PriceMasterEntity();
			if(priceMasterRequest.getId()!=null && priceMasterRequest.getId()>0) {
				priceMasterEntity.setId( priceMasterRequest.getId());
			}
			priceMasterEntity.setPartyId(priceMasterRequest.getPartyId());
			priceMasterEntity.setProcessId(priceMasterRequest.getProcessId());
			priceMasterEntity.setMatGradeId( priceMasterRequest.getMatGradeId());
			priceMasterEntity.setPrice(priceMasterRequest.getPrice());
			priceMasterEntity.setThicknessFrom(priceMasterRequest.getThicknessFrom());
			priceMasterEntity.setThicknessTo(priceMasterRequest.getThicknessTo());
			priceMasterEntity.setCreatedBy(priceMasterRequest.getUserId());
			priceMasterEntity.setUpdatedBy(priceMasterRequest.getUserId());
			priceMasterEntity.setCreatedOn(new Date());
			priceMasterEntity.setUpdatedOn(new Date());
		
			List<PriceMasterEntity> fromList = priceMasterRepository.validateRange(priceMasterRequest.getPartyId(),
					priceMasterRequest.getProcessId(), priceMasterRequest.getMatGradeId(), 
					priceMasterRequest.getThicknessFrom());
			
			if(fromList!=null && fromList.size()>0) {
				if(fromList.size()==1) {
					PriceMasterEntity duplicateEntity=fromList.get(0);
					if(priceMasterRequest.getId() != duplicateEntity.getId() ) {
						return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered From Range Already Exists.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
					}
				} else {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered From Range Already Exists.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}else {
				List<PriceMasterEntity> toList = priceMasterRepository.validateRange(priceMasterRequest.getPartyId(),
						priceMasterRequest.getProcessId(), priceMasterRequest.getMatGradeId(),
						priceMasterRequest.getThicknessTo());
				if(toList!=null && toList.size()>0) {
					if(toList.size()==1) {
						PriceMasterEntity duplicateEntity=toList.get(0);
						if(priceMasterRequest.getId() != duplicateEntity.getId() ) {
							return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered To Range Already Exists.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
						}
					} else {
						return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered To Range Already Exists.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}
			}
		}
		for (PriceMasterRequest priceMasterRequest : priceMasterRequestList) {

			PriceMasterEntity priceMasterEntity = new PriceMasterEntity();
			if(priceMasterRequest.getId()!=null && priceMasterRequest.getId()>0) {
				priceMasterEntity.setId( priceMasterRequest.getId());
			}
			priceMasterEntity.setPartyId(priceMasterRequest.getPartyId());
			priceMasterEntity.setProcessId(priceMasterRequest.getProcessId());
			priceMasterEntity.setMatGradeId( priceMasterRequest.getMatGradeId());
			priceMasterEntity.setPrice(priceMasterRequest.getPrice());
			priceMasterEntity.setThicknessFrom(priceMasterRequest.getThicknessFrom());
			priceMasterEntity.setThicknessTo(priceMasterRequest.getThicknessTo());
			priceMasterEntity.setCreatedBy(priceMasterRequest.getUserId());
			priceMasterEntity.setUpdatedBy(priceMasterRequest.getUserId());
			priceMasterEntity.setCreatedOn(new Date());
			priceMasterEntity.setUpdatedOn(new Date());
			try {
				
				priceMasterRepository.save(priceMasterEntity);
	
				response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Price Master details saved successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
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
			priceMasterRepository.deleteById(id);
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Price Master details deleted successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public PriceMasterResponse getById(int id) {
		
		PriceMasterResponse priceMasterResponse=new PriceMasterResponse();
		Optional<PriceMasterEntity> list = priceMasterRepository.findById(id);
		if(list.isPresent()){
			PriceMasterEntity priceMasterEntity=list.get();
			priceMasterResponse = PriceMasterEntity.valueOf(priceMasterEntity);
		}
		return priceMasterResponse;
	}

	@Override
	public List<PriceMasterResponse> getCustProcessMaterialId(int partyId, int processId, int matGradeId) {

		List<PriceMasterResponse> list = priceMasterRepository
				.findByPartyIdAndProcessIdAndMatGradeId(partyId, processId, matGradeId).stream()
				.map(i -> PriceMasterEntity.valueOf(i)).collect(Collectors.toList());

		return list;
	}

	@Override
	public List<PriceMasterResponse> getCustProcess(int partyId, int processId) {

		List<PriceMasterResponse> list = priceMasterRepository.findByPartyIdAndProcessId(partyId, processId).stream()
				.map(i -> PriceMasterEntity.valueOf(i)).collect(Collectors.toList());

		return list;
	}

	@Override
	public List<PriceMasterResponse> getAllPriceDetails() {

		List<PriceMasterResponse> list = priceMasterRepository.findAll().stream().map(i -> PriceMasterEntity.valueOf(i))
				.collect(Collectors.toList());

		return list;
	}

	@Override
	public List<PriceMasterEntity> copyCustomerDetails(PriceMasterRequest priceMasterRequest) {

		log.info("getPartyId  " + priceMasterRequest.getPartyId() + " == ==   " + priceMasterRequest.getToPartyId());
		List<PriceMasterEntity> list = priceMasterRepository.findByPartyId(priceMasterRequest.getPartyId());
		for (PriceMasterEntity ins : list) {
			PriceMasterEntity kk = new PriceMasterEntity();
			kk.setPartyId(priceMasterRequest.getToPartyId());
			kk.setProcessId(ins.getProcessId());
			kk.setMatGradeId(ins.getMatGradeId());
			kk.setThicknessFrom(ins.getThicknessFrom());
			kk.setThicknessTo(ins.getThicknessTo());
			kk.setPrice(ins.getPrice());
			kk.setCreatedBy(ins.getCreatedBy());
			kk.setUpdatedBy(ins.getUpdatedBy());
			kk.setCreatedOn(ins.getCreatedOn());
			kk.setUpdatedOn(ins.getUpdatedOn());
			priceMasterRepository.save(kk);
		}
		log.info("copyCustomerDetails ");

		return list;
	}
	
	@Override
	public List<PriceMasterEntity> copyCustProcessDetails(PriceMasterRequest priceMasterRequest) {

		log.info("getPartyId  " + priceMasterRequest.getPartyId() + " == ==   " + priceMasterRequest.getToProcessId());
		List<PriceMasterEntity> list = priceMasterRepository.findByPartyIdAndProcessId(priceMasterRequest.getPartyId(), priceMasterRequest.getProcessId());
		for (PriceMasterEntity ins : list) {
			PriceMasterEntity kk = new PriceMasterEntity();
			kk.setPartyId(ins.getPartyId());
			kk.setProcessId(priceMasterRequest.getToProcessId());
			kk.setMatGradeId(ins.getMatGradeId());
			kk.setThicknessFrom(ins.getThicknessFrom());
			kk.setThicknessTo(ins.getThicknessTo());
			kk.setPrice(ins.getPrice());
			kk.setCreatedBy(ins.getCreatedBy());
			kk.setUpdatedBy(ins.getUpdatedBy());
			kk.setCreatedOn(ins.getCreatedOn());
			kk.setUpdatedOn(ins.getUpdatedOn());
			priceMasterRepository.save(kk);
		}
		log.info("copyCustProcessDetails ");

		return list;
	}

	@Override
	public List<PriceMasterEntity> copyMatGradeDetails(PriceMasterRequest priceMasterRequest) {

		log.info("getPartyId  " + priceMasterRequest.getPartyId() + " == ==   " + priceMasterRequest.getToMatGradeId());
		List<PriceMasterEntity> list = priceMasterRepository.findByPartyIdAndProcessIdAndMatGradeId(
				priceMasterRequest.getPartyId(), priceMasterRequest.getProcessId(), priceMasterRequest.getMatGradeId());
		for (PriceMasterEntity ins : list) {
			PriceMasterEntity kk = new PriceMasterEntity();
			kk.setPartyId(ins.getPartyId());
			kk.setProcessId(ins.getProcessId());
			kk.setMatGradeId(priceMasterRequest.getToMatGradeId());
			kk.setThicknessFrom(ins.getThicknessFrom());
			kk.setThicknessTo(ins.getThicknessTo());
			kk.setPrice(ins.getPrice());
			kk.setCreatedBy(ins.getCreatedBy());
			kk.setUpdatedBy(ins.getUpdatedBy());
			kk.setCreatedOn(ins.getCreatedOn());
			kk.setUpdatedOn(ins.getUpdatedOn());
			priceMasterRepository.save(kk);
		}
		log.info("copyMatGradeDetails ");

		return list;
	}

	@Override
	public List<PriceMasterResponse> getCustPriceDetails(int partyId) {

		List<PriceMasterResponse> list = priceMasterRepository.findByPartyId(partyId).stream()
				.map(i -> PriceMasterEntity.valueOf(i)).collect(Collectors.toList());

		return list;
	}

	@Override
	public List<PriceMasterResponse> getProcessPriceDetails(int processId) {

		List<PriceMasterResponse> list = priceMasterRepository.findByProcessId(processId).stream()
				.map(i -> PriceMasterEntity.valueOf(i)).collect(Collectors.toList());

		return list;
	}

}
