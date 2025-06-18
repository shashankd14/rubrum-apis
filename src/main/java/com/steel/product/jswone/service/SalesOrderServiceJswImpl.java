package com.steel.product.jswone.service;

import com.steel.product.application.dao.InstructionRepository;
import com.steel.product.application.dao.InwardEntryRepository;
import com.steel.product.application.dto.quality.ListPageSearchRequest;
import com.steel.product.application.entity.AdminUserEntity;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.entity.UserPartyMap;
import com.steel.product.application.util.CommonUtil;
import com.steel.product.jswone.entity.SalesOrderJswEntity;
import com.steel.product.jswone.entity.SalesOrderPacketsJswEntity;
import com.steel.product.jswone.entity.StatusType;
import com.steel.product.jswone.repository.SalesOrderChildJswRepository;
import com.steel.product.jswone.repository.SalesOrderJswRepository;
import com.steel.product.jswone.request.SalesOrderChildRequest;
import com.steel.product.jswone.request.SalesOrderMainRequest;

import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.beanutils.BeanUtils;
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
public class SalesOrderServiceJswImpl implements SalesOrderJswService {

	private SalesOrderJswRepository salesOrderRepository;

	private SalesOrderChildJswRepository childRepository;

	private CommonUtil commonUtil;
	
    private InstructionRepository instructionRepository;
	
    private InwardEntryRepository inwardEntryRepository;

	@Autowired
	public SalesOrderServiceJswImpl(SalesOrderJswRepository salesOrderRepository, CommonUtil commonUtil,
			SalesOrderChildJswRepository childRepository, InstructionRepository instructionRepository,
			InwardEntryRepository inwardEntryRepository) {
		this.childRepository = childRepository;
		this.salesOrderRepository = salesOrderRepository;
		this.commonUtil = commonUtil;
		this.instructionRepository = instructionRepository;
		this.inwardEntryRepository = inwardEntryRepository;
	}

	@Override
	public ResponseEntity<Object> save(SalesOrderMainRequest salesOrderMainRequest, String option) {
		log.info("SalesOrderServiceImpl.Save ");
		ResponseEntity<Object> responseEntity = null;
		String message = "Sales Order created successfully..!";
		try {
			BigDecimal totalqty = new BigDecimal("0.00");
			SalesOrderJswEntity salesOrderEntity = new SalesOrderJswEntity();
			BeanUtils.copyProperties(salesOrderEntity, salesOrderMainRequest);
			salesOrderEntity.setCreatedBy(commonUtil.getUserId());
			salesOrderEntity.setAllocatedStts("PENDING");
			salesOrderEntity.setIsDeleted(false);
			salesOrderEntity.setSoStatus(StatusType.SO_CREATED.getType());
			if("approve".equals(option)){
				salesOrderEntity.setSoStatus(StatusType.SO_APPROVED.getType());
				salesOrderEntity.setApprovedDate(new Date());
				message = "Sales Order approved successfully..!";
			}
			if("update".equals(option)){
				salesOrderEntity.setSoStatus(StatusType.SO_APPROVED.getType());
				salesOrderEntity.setUpdatedOn( new Date());
				salesOrderEntity.setUpdatedBy(commonUtil.getUserId());
				message = "Sales Order updated successfully..!";
			}
			for (SalesOrderChildRequest dto : salesOrderMainRequest.getItemsList()) {
				SalesOrderPacketsJswEntity childEntity = new SalesOrderPacketsJswEntity();
				BeanUtils.copyProperties(childEntity, dto);
				childEntity.setCreatedBy(commonUtil.getUserId());
				childEntity.setItemStatus(StatusType.SO_CREATED.getType());
				if("approve".equals(option)){
					childEntity.setItemStatus(StatusType.SO_APPROVED.getType());
					childEntity.setApprovedDate(new Date());
				}
				if("update".equals(option)){
					childEntity.setUpdatedOn( new Date());
					childEntity.setUpdatedBy(commonUtil.getUserId());
					message = "Sales Order updated successfully..!";
				}
				childEntity.setAllocatedStts("PENDING");
				childEntity.setIsDeleted(false);
				salesOrderEntity.addItem(childEntity);
				totalqty = totalqty.add(childEntity.getSoqty());
			}
			salesOrderEntity.setTotalSoqty( totalqty);
			salesOrderRepository.save(salesOrderEntity);
		} catch (Exception e) {
			e.printStackTrace();
			responseEntity = new ResponseEntity<>("{\"status\": \"failure\", \"message\": \"" + e.getMessage() + "\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		responseEntity = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"" + message + "\"}", new HttpHeaders(), HttpStatus.OK);
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
		Page<Object[]> packetsList = salesOrderRepository.listAllSOIDs(listPageSearchRequest.getSearchText(), listPageSearchRequest.getSoId(), pageable);
		return packetsList;
	}

	@Override
	public List<Object[]> listAllSOs(List<Integer> soIDsList) {
		List<Object[]> packetsList = salesOrderRepository.listIdWisedetails(soIDsList);
		return packetsList;
	}
	
	@Override
	public ResponseEntity<Object> consolidatePlanner(List<SalesOrderChildRequest> salesOrderPacketsListNew) {
		log.info("inside consolidatePlanner ");
		ResponseEntity<Object> responseEntity = null;
		String message = "Consolidate planner created successfully..!";
		try {
			for (SalesOrderChildRequest request : salesOrderPacketsListNew) {
				BigDecimal balanceQtyRequired = new BigDecimal("0.00");
				BigDecimal totalAllocatedQty = new BigDecimal("0.00");
				
				SalesOrderPacketsJswEntity oldEntity = childRepository.findBySoChildId(request.getSoChildId());
				BigDecimal allocatedQty = (oldEntity.getAllocatedSoqty() == null? BigDecimal.ZERO : oldEntity.getAllocatedSoqty());

				if (request.getAllocatedSoqty() == null) {
					return new ResponseEntity<>("{\"status\": \"failure\", \"message\": \"Please entered valid value in allocation quantity\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
				if (request.getAllocatedSoqty().compareTo(BigDecimal.ZERO) <= 0) {
					return new ResponseEntity<>("{\"status\": \"failure\", \"message\": \"Please entered valid value in allocation quantity\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
				if (request.getAllocatedSoqty().compareTo(oldEntity.getSoqty()) > 0) {
					return new ResponseEntity<>("{\"status\": \"failure\", \"message\": \"Entered quantity should be less than required quantity\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
				
				balanceQtyRequired = oldEntity.getSoqty().subtract(allocatedQty);
				totalAllocatedQty = request.getAllocatedSoqty().add(allocatedQty);

				if (balanceQtyRequired.compareTo(BigDecimal.ZERO) == 0 && "COMPLETED".equals(oldEntity.getAllocatedStts())) {
					return new ResponseEntity<>("{\"status\": \"failure\", \"message\": \"This item has already been allocated.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
				if (balanceQtyRequired.compareTo(request.getAllocatedSoqty()) < 0) {
					return new ResponseEntity<>("{\"status\": \"failure\", \"message\": \"Enter the required quantity only ("+balanceQtyRequired+") }", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
				
				String allocationStts = "PENDING";
				if (balanceQtyRequired.compareTo(request.getAllocatedSoqty()) == 0) {
					allocationStts = "COMPLETED";
				}
				childRepository.consolidatePlanner(request.getSoChildId(), 
						totalAllocatedQty,
						allocationStts,
						request.getInstructionId(), 
						request.getInwardEntryId(), 
						commonUtil.getUserId());
				
				if (request.getInstructionId() != null && request.getInstructionId() > 0 && request.getInwardEntryId() != null && request.getInwardEntryId() > 0 ) {
					Optional<Instruction> instructionList = instructionRepository.findInstructionById(request.getInstructionId());
					if (instructionList != null && instructionList.isPresent()) {
						Instruction instruction = instructionList.get();
						float instructionAllocatedQty = (instruction.getAllocatedSoqty() == null? 0.0f : instruction.getAllocatedSoqty());
						Float totalAllocatedItemQty = instructionAllocatedQty + request.getAllocatedSoqty().floatValue();
						instructionRepository.consolidatePlanner(request.getInstructionId(), totalAllocatedItemQty);
					}
				} else {
					Optional<InwardEntry> inwardList = inwardEntryRepository.findById(request.getInwardEntryId());
					if (inwardList != null && inwardList.isPresent()) {
						InwardEntry inwardEntry = inwardList.get();
						float inwardAllocatedQty = (inwardEntry.getAllocatedSoqty() == null? 0.0f : inwardEntry.getAllocatedSoqty());

						Float totalAllocatedItemQty = inwardAllocatedQty + request.getAllocatedSoqty().floatValue();
						inwardEntryRepository.consolidatePlanner(request.getInwardEntryId(), totalAllocatedItemQty);
					}
				}
			}
			responseEntity = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"" + message + "\"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			responseEntity = new ResponseEntity<>("{\"status\": \"failure\", \"message\": \"" + e.getMessage() + "\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}


}
