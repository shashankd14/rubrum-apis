package com.steel.product.trading.service;

import com.steel.product.trading.dto.EQPChildResponse;
import com.steel.product.trading.dto.EQPResponse;
import com.steel.product.trading.entity.EQPChildEntity;
import com.steel.product.trading.entity.EQPEntity;
import com.steel.product.trading.repository.EQPChildRepository;
import com.steel.product.trading.repository.EQPRepository;
import com.steel.product.trading.request.EQPChildRequest;
import com.steel.product.trading.request.EQPRequest;
import com.steel.product.trading.request.EQPSearchRequest;

import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import org.springframework.beans.BeanUtils;
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
public class EQPServiceImpl implements EQPService {

	@Autowired
	EQPRepository eqpRepository;

	@Autowired
	EQPChildRepository childRepository;

	@Override
	public ResponseEntity<Object> save(EQPRequest request) {
		log.info("In EQPServiceImpl page ");
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		String message = "EQP details saved successfully..! ";
		try {
			EQPEntity eqpEntity = new EQPEntity();
			BeanUtils.copyProperties(request, eqpEntity);
			eqpEntity.setIsDeleted( false);
			
			Map<Integer, Integer> oldChildIdsMap = new HashMap<>();
			List<Integer> missedChildIds = new ArrayList<>();
			
			if (request.getEnquiryId() != null && request.getEnquiryId() > 0) {
				EQPEntity oldEntity = null;

				Optional<EQPEntity> kk = eqpRepository.findById(eqpEntity.getEnquiryId());
				if (kk.isPresent()) {
					oldEntity = kk.get();
				}
				if (oldEntity != null && oldEntity.getEnquiryId() > 0) {

					for (EQPChildEntity childEntity : oldEntity.getItemsList()) {
						oldChildIdsMap.put(childEntity.getEnquiryChildId(), childEntity.getEnquiryChildId());
					}
					eqpEntity.setItemsList(oldEntity.getItemsList());
					eqpEntity.setUpdatedBy(request.getUserId());
					eqpEntity.setUpdatedOn(new Date());
					eqpEntity.setCreatedBy(oldEntity.getCreatedBy());
					eqpEntity.setCreatedOn(oldEntity.getCreatedOn());
					eqpEntity.setStatus(oldEntity.getStatus());
					message = "EQP details updated successfully..! ";
				} else {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Please enter valid data\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
				}

				// INWARD UPDATE LOGIC
				eqpRepository.save(eqpEntity);
				List<EQPChildEntity> itemsList = new ArrayList<>();
				for (EQPChildRequest childReq : request.getItemsList()) {
					EQPChildEntity childEntity = new EQPChildEntity();
					BeanUtils.copyProperties(childReq, childEntity);
					childEntity.setIsDeleted(false);
					childEntity.setEnquiryId(eqpEntity);
					childEntity.setUpdatedBy(request.getUserId());
					childEntity.setUpdatedOn(new Date());
					childEntity.setCreatedBy(request.getUserId());
					childEntity.setCreatedOn(new Date());
					childEntity.setStatus("ENQUIRY");
					itemsList.add(childEntity);
					if (childReq.getEnquiryChildId() != null && childReq.getEnquiryChildId() > 0) {
						oldChildIdsMap.remove(childReq.getEnquiryChildId());
					}
				}

				if (oldChildIdsMap != null && oldChildIdsMap.size() > 0) {
					missedChildIds = new ArrayList<Integer>(oldChildIdsMap.values());
				}
				// System.out.println("hi getItemsList().size - "+itemsList.size());
				// System.out.println("hi missedChildIds size - "+missedChildIds.size());
				childRepository.saveAll(itemsList);
				childRepository.deleteData(missedChildIds, request.getUserId());
			} else {
				eqpEntity.setCreatedBy(request.getUserId());
				eqpEntity.setCreatedOn(new Date());
				eqpEntity.setStatus("ENQUIRY");

				System.out.println("request.getItemsList().size ==  " + request.getItemsList().size());
				for (EQPChildRequest childReq : request.getItemsList()) {
					EQPChildEntity childEntity = new EQPChildEntity();
					BeanUtils.copyProperties(childReq, childEntity);
					childEntity.setIsDeleted(false);
					childEntity.setEnquiryId(eqpEntity);
					childEntity.setCreatedBy(request.getUserId());
					childEntity.setCreatedOn(new Date());
					childEntity.setStatus("ENQUIRY");
					eqpEntity.addItem(childEntity);
				}
				System.out.println("eqpEntity.getItemsList ==  " + eqpEntity.getItemsList().size());
				eqpRepository.save(eqpEntity);
			}
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \""+message+" \"}",	new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("error is ==" + e.getMessage());
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Error Occurred\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public Map<String, Object> getEQPList(EQPSearchRequest searchPageRequest) {
		log.info("In getEQPList page ");
		Map<String, Object> response = new HashMap<>();
		Pageable pageable = PageRequest.of((searchPageRequest.getPageNo() - 1), searchPageRequest.getPageSize());
		
		Page<Object[]> pageResult = eqpRepository.findAllInwardsWithSearchText(searchPageRequest.getEnquiryId(),searchPageRequest.getStatus(), searchPageRequest.getCustomerId(), searchPageRequest.getSearchText(),pageable);
		List<Integer> inwardIdsList = new ArrayList<>();
		for (Object[] result : pageResult) {
			inwardIdsList.add(result[0] != null ? (Integer) result[0] : null);
		}
		System.out.println("inwardIdsList == "+inwardIdsList.size());

		Map<Integer, List<EQPChildResponse>> map = new LinkedHashMap<>();
		Map<Integer, EQPResponse> inwardMap = new LinkedHashMap<>();
		List<EQPResponse> responseList = new ArrayList<>();
		List<Object[]> pageResultChild = eqpRepository.findAllInwardsWiseData(inwardIdsList, searchPageRequest.getStatus());
		for (Object[] result : pageResultChild) {
			EQPResponse dto = new EQPResponse();
			EQPChildResponse childDTO = new EQPChildResponse();
			dto.setEnquiryId( result[0] != null ? (Integer) result[0] : null);
			dto.setEnqCustomerId( result[1] != null ? (Integer) result[1] : null);
			dto.setEnqCustomerName( result[2] != null ? (String) result[2] : null);
			dto.setEnqEnquiryFrom( result[3] != null ? (String) result[3] : null);
			dto.setEnqEnquiryDate( result[4] != null ? (String) result[4] : null);
			dto.setEnqQty( result[5] != null ? (Integer) result[5] : null);
			dto.setEnqValue( result[6] != null ? (BigDecimal) result[6] : null);
			dto.setQuoteEnquiryFrom( result[7] != null ? (String) result[7] : null);
			dto.setQuoteEnquiryDate( result[8] != null ? (String) result[8] : null);
			dto.setQuoteQty( result[9] != null ? (Integer) result[9] : null);
			dto.setQuoteValue( result[10] != null ? (BigDecimal) result[10] : null);
			dto.setStatus( result[11] != null ? (String) result[11] : null);
			dto.setQuoteCustomerName( result[12] != null ? (String) result[12] : null);
			 
			// child items
			childDTO.setEnquiryChildId( result[13] != null ? (Integer) result[13] : null);
			childDTO.setItemId(result[14] != null ? (Integer) result[14] : null);
			childDTO.setItemSpecs( result[15] != null ? (String) result[15] : null);
			childDTO.setMake( result[16] != null ? (String) result[16] : null);
			childDTO.setAltMake( result[17] != null ? (String) result[17] : null);
			childDTO.setQty1( result[18] != null ? (Integer) result[18] : null);
			childDTO.setUnit1( result[19] != null ? (String) result[19] : null);
			childDTO.setQty2(result[20] != null ? (Integer) result[20] : null);
			childDTO.setUnit2(result[21] != null ? (String) result[21] : null);
			childDTO.setEstimateDeliveryDate( result[22] != null ? (String) result[22] : null);
			childDTO.setRemarks( result[23] != null ? (String) result[23] : null);
			childDTO.setStatus( result[24] != null ? (String) result[24] : null);
			childDTO.setLocationId( result[25] != null ? (Integer) result[25] : null);
			childDTO.setItemName( result[26] != null ? (String) result[26] : null);
			childDTO.setLocationName( result[27] != null ? (String) result[27] : null);

			if (map.get(dto.getEnquiryId() ) != null) {
				List<EQPChildResponse> dummyList = map.get(dto.getEnquiryId());
				dummyList.add(childDTO);
				map.put(dto.getEnquiryId(), dummyList);
			} else {
				List<EQPChildResponse> dummyList = new ArrayList<>();
				dummyList.add(childDTO);
				map.put(dto.getEnquiryId(), dummyList);
			}
			inwardMap.put(dto.getEnquiryId(), dto);
		}
		for (Entry<Integer, List<EQPChildResponse>> entry : map.entrySet()) {
			EQPResponse dummyEntity = inwardMap.get(entry.getKey());
			dummyEntity.setItemsList(entry.getValue());
			responseList.add(dummyEntity);
		}

		response.put("content", responseList);
		response.put("currentPage", pageResult.getNumber());
		response.put("totalItems", pageResult.getTotalElements());
		response.put("totalPages", pageResult.getTotalPages());

		return response;
	}


}
