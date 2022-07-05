package com.steel.product.application.service;

import com.steel.product.application.dao.PriceMasterRepository;
import com.steel.product.application.dto.pricemaster.PriceMasterResponse;
import com.steel.product.application.dto.pricemaster.PriceMasterRequest;
import com.steel.product.application.entity.PriceMasterEntity;

import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
	public ResponseEntity<Object> save(List<PriceMasterRequest> priceMasterRequestList, int userId) {

		ResponseEntity<Object> response = null;
		List<PriceMasterEntity> list=new ArrayList<>();
		
		for (PriceMasterRequest priceMasterRequest : priceMasterRequestList) {

			for (Integer partyId : priceMasterRequest.getPartyId()) {

				for (Integer matGradeId : priceMasterRequest.getMatGradeId()) {
					PriceMasterEntity priceMasterEntity = new PriceMasterEntity();
					if (priceMasterRequest.getId() != null && priceMasterRequest.getId() > 0) {
						priceMasterEntity.setId(priceMasterRequest.getId());
					}
					priceMasterEntity.setPartyId(partyId);
					priceMasterEntity.setProcessId(priceMasterRequest.getProcessId());
					priceMasterEntity.setMatGradeId(matGradeId);
					priceMasterEntity.setPrice(priceMasterRequest.getPrice());
					priceMasterEntity.setThicknessFrom(priceMasterRequest.getThicknessFrom());
					priceMasterEntity.setThicknessTo(priceMasterRequest.getThicknessTo());
					priceMasterEntity.setCreatedBy(userId);
					priceMasterEntity.setUpdatedBy(userId);
					priceMasterEntity.setCreatedOn(new Date());
					priceMasterEntity.setUpdatedOn(new Date());
					list.add(priceMasterEntity);
				}
			}
		}
		
		for (PriceMasterEntity entity : list) {
			
			List<PriceMasterEntity> fromList = priceMasterRepository.validateRange(entity.getPartyId(),
					entity.getProcessId(), entity.getMatGradeId(), entity.getThicknessFrom());
			
			if(fromList!=null && fromList.size()>0) {
				if(fromList.size()==1) {
					PriceMasterEntity duplicateEntity=fromList.get(0);
					if(entity.getId() != duplicateEntity.getId() ) {
						return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered From Range Already Exists.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
					}
				} else {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered From Range Already Exists.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} 
			List<PriceMasterEntity> toList = priceMasterRepository.validateRange(entity.getPartyId(),
					entity.getProcessId(), entity.getMatGradeId(), entity.getThicknessTo());
			if(toList!=null && toList.size()>0) {
				if(toList.size()==1) {
					PriceMasterEntity duplicateEntity=toList.get(0);
					if(entity.getId() != duplicateEntity.getId() ) {
						return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered To Range Already Exists.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
					}
				} else {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered To Range Already Exists.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		}
				
		priceMasterRepository.saveAll(list);
		response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Price Master details saved successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
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

		List<Object[]> list = priceMasterRepository.findById1(id);
		return prepareData(list);
	}
	
	@Override
	public List<PriceMasterResponse> getAllPriceDetails() {

		List<Object[]> list = priceMasterRepository.findAll1();
		return prepareDataList(list);
	}
	
	/*
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
	*/
	
	private List<PriceMasterResponse> prepareDataList(List<Object[]> results) {
		List<PriceMasterResponse> list = new ArrayList<>();
		try {
			if (results != null && !results.isEmpty()) {
				for (Object[] result : results) {
					PriceMasterResponse priceMasterResponse = new PriceMasterResponse();
					priceMasterResponse.setId(result[0] != null ? (Integer) result[0] : null);
					priceMasterResponse.setPartyId(result[1] != null ? (Integer) result[1] : null);
					priceMasterResponse.setProcessId(result[2] != null ? (Integer) result[2] : null);
					priceMasterResponse.setMatGradeId(result[3] != null ? (Integer) result[3] : null);
					priceMasterResponse.setThicknessFrom(result[4] != null ? (BigDecimal) result[4] : null);
					priceMasterResponse.setThicknessTo(result[5] != null ? (BigDecimal) result[5] : null);
					priceMasterResponse.setPrice(result[6] != null ? (BigDecimal) result[6] : null);
					priceMasterResponse.setCreatedBy(result[7] != null ? (Integer) result[7] : null);
					priceMasterResponse.setUpdatedBy(result[8] != null ? (Integer) result[8] : null);
					priceMasterResponse.setCreatedOn(result[9] != null ? (Date) result[9] : null);
					priceMasterResponse.setUpdatedOn(result[10] != null ? (Date) result[10] : null);
					priceMasterResponse.setPartyName(result[11] != null ? (String) result[11] : null);
					priceMasterResponse.setProcessName(result[12] != null ? (String) result[12] : null);
					priceMasterResponse.setMatGradeName(result[13] != null ? (String) result[13] : null);
					priceMasterResponse.setMaterialDescription( result[14] != null ? (String) result[14] : null);
					priceMasterResponse.setMatId(result[15] != null ? (Integer) result[15] : null);
					list.add(priceMasterResponse);
				}
			}
		} catch (Exception e) {
			log.error("Unable to get services due to :: ", e);
		}
		return list;
	}
	
	private PriceMasterResponse prepareData (List<Object[]> results) {
		PriceMasterResponse priceMasterResponse = null;
		try {
			if (results != null && !results.isEmpty()) {
				for (Object[] result : results) {
					priceMasterResponse = new PriceMasterResponse();
					priceMasterResponse.setId(result[0] != null ? (Integer) result[0] : null);
					priceMasterResponse.setPartyId(result[1] != null ? (Integer) result[1] : null);
					priceMasterResponse.setProcessId(result[2] != null ? (Integer) result[2] : null);
					priceMasterResponse.setMatGradeId(result[3] != null ? (Integer) result[3] : null);
					priceMasterResponse.setThicknessFrom(result[4] != null ? (BigDecimal) result[4] : null);
					priceMasterResponse.setThicknessTo(result[5] != null ? (BigDecimal) result[5] : null);
					priceMasterResponse.setPrice(result[6] != null ? (BigDecimal) result[6] : null);
					priceMasterResponse.setCreatedBy(result[7] != null ? (Integer) result[7] : null);
					priceMasterResponse.setUpdatedBy(result[8] != null ? (Integer) result[8] : null);
					priceMasterResponse.setCreatedOn(result[9] != null ? (Date) result[9] : null);
					priceMasterResponse.setUpdatedOn(result[10] != null ? (Date) result[10] : null);
					priceMasterResponse.setPartyName(result[11] != null ? (String) result[11] : null);
					priceMasterResponse.setProcessName(result[12] != null ? (String) result[12] : null);
					priceMasterResponse.setMatGradeName(result[13] != null ? (String) result[13] : null);
					priceMasterResponse.setMaterialDescription( result[14] != null ? (String) result[14] : null);
					priceMasterResponse.setMatId(result[15] != null ? (Integer) result[15] : null);
				}
			}
		} catch (Exception e) {
			log.error("Unable to get services due to :: ", e);
		}
		return priceMasterResponse;
	}

}
