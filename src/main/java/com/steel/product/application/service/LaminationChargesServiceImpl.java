package com.steel.product.application.service;

import com.steel.product.application.dao.LaminationChargesRepository;
import com.steel.product.application.dao.LaminationStaticDataRepository;
import com.steel.product.application.dto.lamination.LaminationChargesRequest;
import com.steel.product.application.dto.lamination.LaminationChargesResponse;
import com.steel.product.application.entity.LaminationChargesEntity;
import com.steel.product.application.entity.LaminationStaticDataEntity;
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
public class LaminationChargesServiceImpl implements LaminationChargesService {

	@Autowired
	LaminationChargesRepository laminationRepository;
	
	@Autowired
	LaminationStaticDataRepository laminationStaticDataRepository;

	@Override
	public ResponseEntity<Object> save(List<LaminationChargesRequest> laminationRequestList, int userId) {

		ResponseEntity<Object> response = null;
		List<LaminationChargesEntity> dataList=new ArrayList<>();
		for (LaminationChargesRequest laminationRequest : laminationRequestList) {
			LaminationChargesEntity packingItemEntity = new LaminationChargesEntity();
			
			if (laminationRequest.getLaminationId() != null && laminationRequest.getLaminationId() > 0) {
				packingItemEntity.setLaminationId(laminationRequest.getLaminationId());
			}
			
			List<LaminationChargesEntity> list = laminationRepository.findByPartyIdAndLaminationDetailsId(laminationRequest.getPartyId(), laminationRequest.getLaminationDetailsId());
			LaminationChargesEntity checkLaminationEntity = null;
	
			if (list != null && list.size() > 0) {
				checkLaminationEntity = list.get(0);
				packingItemEntity.setLaminationId(checkLaminationEntity.getLaminationId());
			} 
			packingItemEntity.setLaminationDetailsId(laminationRequest.getLaminationDetailsId());
			packingItemEntity.setCharges(laminationRequest.getCharges());
			packingItemEntity.setPartyId(laminationRequest.getPartyId());
			packingItemEntity.setCreatedBy(userId);
			packingItemEntity.setUpdatedBy(userId);
			packingItemEntity.setCreatedOn(new Date());
			packingItemEntity.setUpdatedOn(new Date());
			dataList.add(packingItemEntity);
		}
		laminationRepository.saveAll(dataList);
		response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Lamination charges details saved successfully..! \"}",new HttpHeaders(), HttpStatus.OK);
		return response;
	}

	@Override
	public ResponseEntity<Object> delete(int id) {
		ResponseEntity<Object> response = null;
		try {
			laminationRepository.deleteById(id);
			response = new ResponseEntity<>(
					"{\"status\": \"success\", \"message\": \"Lamination charges deleted successfully..! \"}",
					new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}",
					new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public LaminationChargesResponse getById(Integer laminationId) {

		List<Object[]> list = laminationRepository.findByLaminationId( laminationId);
		return prepareDataById(list);
	}

	@Override
	public List<LaminationChargesResponse> getByPartyId(Integer partyId) {


		List<Object[]> list = laminationRepository.findByPartyId(partyId);
		return prepareDataList(list);
	}
	
	@Override
	public List<LaminationChargesResponse> getAllLaminationDetails( ) {

		List<Object[]> list = laminationRepository.findAll1();
		return prepareDataList(list);
	}
	
	private List<LaminationChargesResponse> prepareDataList(List<Object[]> results) {
		List<LaminationChargesResponse> list = new ArrayList<>();
		try {
			if (results != null && !results.isEmpty()) {
				for (Object[] result : results) {
					LaminationChargesResponse priceMasterResponse = new LaminationChargesResponse();
					priceMasterResponse.setLaminationId( result[0] != null ? (Integer) result[0] : null);
					priceMasterResponse.setPartyId(result[1] != null ? (Integer) result[1] : null);
					priceMasterResponse.setLaminationDetailsId(result[2] != null ? (Integer) result[2] : null);
					priceMasterResponse.setCharges(result[3] != null ? (BigDecimal) result[3] : null);
					priceMasterResponse.setPartyName(result[4] != null ? (String) result[4] : null);
					priceMasterResponse.setLaminationDetailsDesc(result[5] != null ? (String) result[5] : null);
					priceMasterResponse.setCreatedBy(result[6] != null ? (Integer) result[6] : null);
					priceMasterResponse.setUpdatedBy(result[7] != null ? (Integer) result[7] : null);
					priceMasterResponse.setCreatedOn(result[8] != null ? (Date) result[8] : null);
					priceMasterResponse.setUpdatedOn(result[9] != null ? (Date) result[9] : null);
					
					list.add(priceMasterResponse);
				}
			}
		} catch (Exception e) {
			log.error("Unable to get services due to :: ", e);
		}
		return list;
	}
	
	private LaminationChargesResponse prepareDataById(List<Object[]> results) {
		LaminationChargesResponse priceMasterResponse = new LaminationChargesResponse();
		try {
			if (results != null && !results.isEmpty()) {
				for (Object[] result : results) {
					priceMasterResponse.setLaminationId(result[0] != null ? (Integer) result[0] : null);
					priceMasterResponse.setPartyId(result[1] != null ? (Integer) result[1] : null);
					priceMasterResponse.setLaminationDetailsId(result[2] != null ? (Integer) result[2] : null);
					priceMasterResponse.setCharges(result[3] != null ? (BigDecimal) result[3] : null);
					priceMasterResponse.setPartyName(result[4] != null ? (String) result[4] : null);
					priceMasterResponse.setLaminationDetailsDesc(result[5] != null ? (String) result[5] : null);
					priceMasterResponse.setCreatedBy(result[6] != null ? (Integer) result[6] : null);
					priceMasterResponse.setUpdatedBy(result[7] != null ? (Integer) result[7] : null);
					priceMasterResponse.setCreatedOn(result[8] != null ? (Date) result[8] : null);
					priceMasterResponse.setUpdatedOn(result[9] != null ? (Date) result[9] : null);
				}
			}
		} catch (Exception e) {
			log.error("Unable to get services due to :: ", e);
		}
		return priceMasterResponse;
	}
	
	
	@Override
	public List<LaminationStaticDataEntity> getLaminationDetails() {

		List<LaminationStaticDataEntity> instructionList = laminationStaticDataRepository.findAll();

		return instructionList;
	}
	
	
}