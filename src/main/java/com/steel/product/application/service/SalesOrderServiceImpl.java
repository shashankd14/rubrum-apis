package com.steel.product.application.service;

import com.steel.product.application.dao.SalesOrderChildRepository;
import com.steel.product.application.dao.SalesOrderRepository;
import com.steel.product.application.dto.delivery.DeliveryItemDetails;
import com.steel.product.application.dto.quality.ListPageSearchRequest;
import com.steel.product.application.dto.salesorder.SalesOrderCreateDTO;
import com.steel.product.application.dto.salesorder.SalesOrderListDTO;
import com.steel.product.application.entity.*;
import com.steel.product.application.util.CommonUtil;
import com.steel.product.trading.request.DeleteRequest;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Log4j2
public class SalesOrderServiceImpl implements SalesOrderService {

	private SalesOrderRepository salesOrderRepository;

	private SalesOrderChildRepository childRepository;

	private CommonUtil commonUtil;

	private StatusService statusService;

	@Autowired
	public SalesOrderServiceImpl(SalesOrderRepository salesOrderRepository, CommonUtil commonUtil,
			StatusService statusService, SalesOrderChildRepository childRepository) {
		this.childRepository = childRepository;
		this.salesOrderRepository = salesOrderRepository;
		this.commonUtil = commonUtil;
		this.statusService = statusService;
	}

	@Override
	public Page<Object[]> listAllPackets(ListPageSearchRequest searchListPageRequest) {

		Pageable pageable = null;
		if (searchListPageRequest.getSortColumn() != null && searchListPageRequest.getSortColumn().length() > 0
				&& searchListPageRequest.getSortOrder() != null && searchListPageRequest.getSortOrder().length() > 0
				&& "ASC".equalsIgnoreCase(searchListPageRequest.getSortOrder())) {
			pageable = PageRequest.of((searchListPageRequest.getPageNo()-1), searchListPageRequest.getPageSize(), Sort.by(searchListPageRequest.getSortColumn()).ascending());
		}else if (searchListPageRequest.getSortColumn() != null && searchListPageRequest.getSortColumn().length() > 0
				&& searchListPageRequest.getSortOrder() != null && searchListPageRequest.getSortOrder().length() > 0
				&& "DESC".equalsIgnoreCase(searchListPageRequest.getSortOrder())) {
			pageable = PageRequest.of((searchListPageRequest.getPageNo()-1), searchListPageRequest.getPageSize(), Sort.by(searchListPageRequest.getSortColumn()).descending());
		} else {
			pageable = PageRequest.of((searchListPageRequest.getPageNo()-1), searchListPageRequest.getPageSize(), Sort.by("packet_id").descending());
		}		
		
		List<Integer> partyIds = new ArrayList<>();
		boolean partyIdsFlag = false;
		if (searchListPageRequest.getPartyId() != null && searchListPageRequest.getPartyId() > 0) {
			partyIds.add(searchListPageRequest.getPartyId());
			partyIdsFlag = true;
		} else {
			AdminUserEntity adminUserEntity = commonUtil.getUserDetails();
			if (adminUserEntity.getUserPartyMap() != null && adminUserEntity.getUserPartyMap().size() > 0) {
				partyIds = new ArrayList<>();
				for (UserPartyMap userPartyMap : adminUserEntity.getUserPartyMap()) {
					partyIds.add(userPartyMap.getPartyId());
					partyIdsFlag = true;
				}
				log.info("In partyIds === " + partyIds);
			} else {
				partyIdsFlag = false;
				partyIds = new ArrayList<>();
			}
		}
		Page<Object[]> packetsList = salesOrderRepository.listAllPackets(searchListPageRequest.getSearchText(), partyIds, partyIdsFlag, commonUtil.getLoginWiseMappedUserIds(), pageable);
		return packetsList;
	}

	@Override
	public ResponseEntity<Object> save(List<SalesOrderCreateDTO> salesOrderPacketsListNew) {
        ResponseEntity< Object > responseEntity = null;
		String message="Sales Order created successfully ! ";
		try {
			
			for (SalesOrderCreateDTO request : salesOrderPacketsListNew) {
				SalesOrderEntity salesOrderEntity = new SalesOrderEntity();
				if(request.getSoId() !=null && request.getSoId() > 0 ) {
					message="Sales Order details updated successfully ! ";
					salesOrderEntity.setSoId(request.getSoId());
					salesOrderEntity.setUpdatedBy(request.getUserId());
					salesOrderEntity.setUpdatedOn(new Date());
					Optional<SalesOrderEntity> dummy =salesOrderRepository.findById(request.getSoId());
					SalesOrderEntity oldEntity = null;
					if (dummy.isPresent()) {
						oldEntity = dummy.get();
						salesOrderEntity.setCreatedBy(oldEntity.getCreatedBy());
						salesOrderEntity.setCreatedOn(oldEntity.getCreatedOn());
					}
				} else if(request.getSoNumber() !=null && request.getSoNumber().length() > 0 ) {
					List<SalesOrderEntity> dummy =salesOrderRepository.findBySoNumber(request.getSoNumber());
					if (dummy != null && dummy.size()>0 ) {
						salesOrderEntity = dummy.get(0);
						salesOrderEntity.setUpdatedBy(request.getUserId());
						salesOrderEntity.setUpdatedOn(new Date());
					}else {
						salesOrderEntity.setSoNumber(request.getSoNumber());
						salesOrderEntity.setCreatedBy(request.getUserId());
						salesOrderEntity.setCreatedOn(new Date());
					}
				} else {
					salesOrderEntity.setSoNumber(request.getSoNumber());
					salesOrderEntity.setCreatedBy(request.getUserId());
					salesOrderEntity.setCreatedOn(new Date());
				}
				salesOrderEntity.setCustomerCodeId( request.getCustomerCodeId());
				salesOrderEntity.setPartyId(request.getPartyId());
				salesOrderEntity.setTotalWeight(BigDecimal.ZERO);
				salesOrderEntity.setStatus(this.statusService.getStatusById(1));
				salesOrderEntity.setIsDeleted(false);
				salesOrderRepository.save(salesOrderEntity);
				SalesOrderPacketsEntity childEntity = new SalesOrderPacketsEntity();
				SalesOrderPacketsEntity oldEntity = null;

				if(request.getSoChildId() !=null && request.getSoChildId()> 0 ) {
					Optional<SalesOrderPacketsEntity> dummy =childRepository.findById(request.getSoChildId());
					if (dummy.isPresent()) {
						oldEntity = dummy.get();
						childEntity.setCreatedBy(oldEntity.getCreatedBy());
						childEntity.setCreatedOn(oldEntity.getCreatedOn());
					}
					childEntity.setSoChildId(request.getSoChildId());
					childEntity.setUpdatedBy(request.getUserId());
					childEntity.setUpdatedOn(new Date());
				} else {
					List<SalesOrderPacketsEntity> dummy11 =childRepository.findBySoIdAndInstructionId(salesOrderEntity.getSoId(), request.getInstructionId());
					if (dummy11 != null && dummy11.size() > 0) {
						childEntity = dummy11.get(0);
						childEntity.setUpdatedBy(request.getUserId());
						childEntity.setUpdatedOn(new Date());
					} else {
						childEntity.setCreatedBy(request.getUserId());
						childEntity.setCreatedOn(new Date());
					}
				}
				childEntity.setSoId(salesOrderEntity.getSoId());
				childEntity.setCoilNo(request.getCoilNo());
				childEntity.setInstructionId(request.getInstructionId());
				childEntity.setInwardEntryId(request.getInwardEntryId());
				childEntity.setCustomerBatchNo(request.getCustomerBatchNo());
				childEntity.setFthickness(request.getFthickness());
				childEntity.setFwidth(request.getFthickness());
				childEntity.setFlength(request.getFthickness());
				childEntity.setFweight(request.getFthickness());
				childEntity.setIsDeleted(false);
				childEntity.setStatus(this.statusService.getStatusById(1));
				childRepository.save(childEntity);
			}
			//salesOrderRepository.save(salesOrderEntity);
		} catch (Exception e) {
			e.printStackTrace();
        	responseEntity = new ResponseEntity<>( "{\"status\": \"failure\", \"message\": \""+e.getMessage()+"\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    	responseEntity = new ResponseEntity<>( "{\"status\": \"success\", \"message\": \""+message+"\"}", new HttpHeaders(), HttpStatus.OK);
		return responseEntity;
	}

	@Override
	public Page<Object[]> listAllSOIDs(ListPageSearchRequest listPageSearchRequest) {

		Pageable pageable = PageRequest.of((listPageSearchRequest.getPageNo() - 1),  listPageSearchRequest.getPageSize());
		List<Integer> partyIds = new ArrayList<>();
		boolean partyIdsFlag = false;
		if (listPageSearchRequest.getPartyId() != null && listPageSearchRequest.getPartyId() > 0) {
			partyIds.add(listPageSearchRequest.getPartyId());
			partyIdsFlag = true;
		} else {
			AdminUserEntity adminUserEntity = commonUtil.getUserDetails();
			if (adminUserEntity.getUserPartyMap() != null && adminUserEntity.getUserPartyMap().size() > 0) {
				partyIds = new ArrayList<>();
				for (UserPartyMap userPartyMap : adminUserEntity.getUserPartyMap()) {
					partyIds.add(userPartyMap.getPartyId());
					partyIdsFlag = true;
				}
				log.info("In partyIds === " + partyIds);
			} else {
				partyIdsFlag = false;
				partyIds = new ArrayList<>();
			}
		}
		Page<Object[]> packetsList = salesOrderRepository.listAllSOIDs(listPageSearchRequest.getSearchText(),
				listPageSearchRequest.getSoId(), commonUtil.getLoginWiseMappedUserIds(),
				pageable);

		return packetsList;
	}
	
	@Override
	public List<Object[]> listAllSOs(List<String> soIDsList) {

		List<Object[]> packetsList = salesOrderRepository.listAllSOs(soIDsList);

		return packetsList;
	}

	@Override
	public ResponseEntity<Object> delete(DeleteRequest deleteRequest) {
		log.info("In customerDelete page ");
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		
		try {
			salesOrderRepository.deleteData(deleteRequest.getIds(), deleteRequest.getUserId());
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Selected SO has been deleted successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \""+e.getMessage()+"\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public ResponseEntity<Object> deletePackets(DeleteRequest deleteRequest) {
		log.info("In customerDelete page ");
		ResponseEntity<Object> response = null;
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		try {
			
			childRepository.deleteData(deleteRequest.getIds(), deleteRequest.getUserId());
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Selected Packet(s) has been deleted successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \""+e.getMessage()+"\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public int validateSoNoAndCustCode(List<DeliveryItemDetails> deliveryItemDetails) {
		int cnt = 0;
		List<Integer> dcIds = new ArrayList<>();
		for (DeliveryItemDetails deliveryDetails : deliveryItemDetails) {
			dcIds.add(deliveryDetails.getInstructionId());
		}
		List<Object[]> packetsList = salesOrderRepository.validateSoNoAndCustCode(dcIds);
		System.out.println("dcIds " + dcIds);

		Map<Integer, SalesOrderListDTO> kk = new LinkedHashMap<>();
		for (Object[] result : packetsList) {
			cnt++;
			String sono = result[0] != null ? (String) result[0] : null;
			//String cuatCode = result[1] != null ? (String) result[1] : null;
		}
		return cnt;
	}
}
