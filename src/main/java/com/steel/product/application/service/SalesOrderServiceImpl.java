package com.steel.product.application.service;

import com.steel.product.application.dao.InstructionRepository;
import com.steel.product.application.dao.SalesOrderRepository;
import com.steel.product.application.dto.quality.ListPageSearchRequest;
import com.steel.product.application.entity.*;
import com.steel.product.application.util.CommonUtil;

import lombok.extern.log4j.Log4j2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.text.DecimalFormat;
import java.util.*;

@Service
@Log4j2
public class SalesOrderServiceImpl implements SalesOrderService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SalesOrderServiceImpl.class);

	private static final DecimalFormat decfor = new DecimalFormat("0.00");

	private SalesOrderRepository salesOrderRepository;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	public SalesOrderServiceImpl(SalesOrderRepository salesOrderRepository) {
		this.salesOrderRepository = salesOrderRepository;
	}

	@Override
	public Page<Object[]> listAllPackets(ListPageSearchRequest listPageSearchRequest) {
		
		Pageable pageable = PageRequest.of((listPageSearchRequest.getPageNo() - 1), listPageSearchRequest.getPageSize());
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
		Page<Object[]> packetsList = salesOrderRepository.listAllPackets(listPageSearchRequest.getSearchText(), partyIds, partyIdsFlag, commonUtil.getLoginWiseMappedUserIds(), pageable);
		return packetsList;
	}

}
