package com.steel.product.trading.service;

import com.steel.product.application.dto.pricemaster.PriceMasterResponse;
import com.steel.product.application.dto.pricemaster.PriceMasterListPageRequest;
import com.steel.product.application.entity.PriceMasterEntity;
import com.steel.product.trading.entity.MaterialMasterEntity;
import com.steel.product.trading.repository.MaterialMasterRepository;
import com.steel.product.trading.request.MaterialMasterRequest;

import lombok.extern.log4j.Log4j2;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class MaterialMasterServiceImpl implements MaterialMasterService {

	@Autowired
	MaterialMasterRepository materialMasterRepository;

	@Autowired
	MaterialMasterService materialMasterService;

	@Override
	public ResponseEntity<Object> save(List<MaterialMasterRequest> priceMasterRequestList, int userId) {

		ResponseEntity<Object> response = null;
		List<PriceMasterEntity> list=new ArrayList<>();
		
		for (MaterialMasterRequest priceMasterRequest : priceMasterRequestList) {

			for (Integer partyId : priceMasterRequest.getPartyId()) {

				for (Integer matGradeId : priceMasterRequest.getMatGradeId()) {
					PriceMasterEntity priceMasterEntity = new PriceMasterEntity();
					if (priceMasterRequest.getId() != null && priceMasterRequest.getId() > 0) {
						priceMasterEntity.setId(priceMasterRequest.getId());
					}
					priceMasterEntity.setCreatedBy(userId);
					priceMasterEntity.setUpdatedBy(userId);
					priceMasterEntity.setCreatedOn(new Date());
					priceMasterEntity.setUpdatedOn(new Date());
					if(priceMasterRequest.getPrice()!=null) {
						list.add(priceMasterEntity);
					}
				}
			}
		}
		
		response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Price Master details saved successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		return response;
	}

	@Override
	public ResponseEntity<Object> delete(int id) {
		ResponseEntity<Object> response = null;
		try {
			materialMasterRepository.deleteById(id);
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Price Master details deleted successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public PriceMasterResponse getById(int id) {

		List<Object[]> list = null; //materialMasterRepository.findById1(id);
		return prepareData(list);
	}
	
	@Override
	public List<PriceMasterResponse> getAllPriceDetails(int partyId) {

		List<Object[]> list = null;//materialMasterRepository.findAllDetails(partyId);
		return prepareDataList(list);
	}
	
	@Override
	public List<PriceMasterResponse> getPartyGradeWiseDetails(int partyId, int processId, int gradeId) {

		List<Object[]> list = null;//materialMasterRepository.findByPartyIdAndProcessIdAndMatGradeIdss(partyId, processId, gradeId);
		return prepareDataList(list);
	}
	
	@Override
	public List<PriceMasterResponse> getAllPriceDetails( ) {

		List<Object[]> list = null;//materialMasterRepository.findAll1();
		return prepareDataList(list);
	}
	
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

	@Override
	public Page<MaterialMasterEntity> findAllWithPagination(PriceMasterListPageRequest request) {
		//Pageable pageable = PageRequest.of((request.getPageNo() - 1), request.getPageSize());
		//Page<PriceMasterEntity> pageResult = materialMasterRepository.findAll(request.getSearchText(), 				request.getThicknessRange(), pageable);
		return null;
	}

}
