package com.steel.product.jswone.service;

import com.steel.product.application.dao.InstructionRepository;
import com.steel.product.application.dao.InwardEntryRepository;
import com.steel.product.application.dto.quality.ListPageSearchRequest;
import com.steel.product.application.entity.AdminUserEntity;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.entity.UserPartyMap;
import com.steel.product.application.util.CommonUtil;
import com.steel.product.jswone.entity.SalesOrderAllocationEntity;
import com.steel.product.jswone.entity.SalesOrderJswEntity;
import com.steel.product.jswone.entity.SalesOrderPacketsJswEntity;
import com.steel.product.jswone.entity.StatusType;
import com.steel.product.jswone.repository.SalesOrderAllocationJswRepository;
import com.steel.product.jswone.repository.SalesOrderChildJswRepository;
import com.steel.product.jswone.repository.SalesOrderJswRepository;
import com.steel.product.jswone.request.*;

import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class SalesOrderServiceJswImpl implements SalesOrderJswService {

    @Autowired
	private SalesOrderJswRepository salesOrderRepository;

    @Autowired
	private SalesOrderChildJswRepository childRepository;

    @Autowired
	private SalesOrderAllocationJswRepository soAllocationRepository;

    @Autowired
	private CommonUtil commonUtil;
	
    @Autowired
    private InstructionRepository instructionRepository;
	
    @Autowired
    private InwardEntryRepository inwardEntryRepository;

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
			
			if ("create".equals(option)) {
				Optional<SalesOrderJswEntity> kk = salesOrderRepository.findBySoNumberIgnoreCase(salesOrderMainRequest.getSoNumber());
				if(kk.isPresent()) {
					return new ResponseEntity<>("{\"status\": \"failure\", \"message\": \"Entered SO Number already exists.\"}", new HttpHeaders(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			
			if("approve".equals(option)){
				salesOrderEntity.setSoStatus(StatusType.SO_APPROVED.getType());
				salesOrderEntity.setApprovedDate(new Date());
				message = "Sales Order approved successfully..!";
			}
			if("update".equals(option)){
				salesOrderEntity.setSoStatus(StatusType.SO_CREATED.getType());
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
		Page<Object[]> packetsList = salesOrderRepository.listAllSOIDs(listPageSearchRequest.getSearchText(),
				listPageSearchRequest.getSoId(), listPageSearchRequest.getStatus(), pageable);
		return packetsList;
	}

	@Override
	public List<Object[]> listAllSOs(List<Integer> soIDsList) {
		List<Object[]> packetsList = salesOrderRepository.listIdWisedetails(soIDsList);
		return packetsList;
	}
	
	@Override
	@Transactional
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
						request.getSpecialInstructions(), 
						commonUtil.getUserId());
				
				SalesOrderAllocationEntity allocation = new SalesOrderAllocationEntity();
				allocation.setInstructionId(request.getInstructionId());
				allocation.setInwardEntryId(request.getInwardEntryId());
				allocation.setSoChildId(request.getSoChildId());
				allocation.setSoId(request.getSoId());
				allocation.setAllocatedSoqty(request.getAllocatedSoqty());
				allocation.setAllocatedStts(allocationStts);
				allocation.setAllocationBy(commonUtil.getUserId());
				soAllocationRepository.save(allocation);
				
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

	@Override
	public Page<Object[]> findInventory(ListPageSearchRequest listPageSearchRequest) {
		Pageable pageable = null;
		if (listPageSearchRequest.getSortColumn() != null && listPageSearchRequest.getSortColumn().length() > 0
				&& listPageSearchRequest.getSortOrder() != null && listPageSearchRequest.getSortOrder().length() > 0
				&& "ASC".equalsIgnoreCase(listPageSearchRequest.getSortOrder())) {
			pageable = PageRequest.of((listPageSearchRequest.getPageNo()-1), listPageSearchRequest.getPageSize(), Sort.by(listPageSearchRequest.getSortColumn()).ascending());
		}else if (listPageSearchRequest.getSortColumn() != null && listPageSearchRequest.getSortColumn().length() > 0
				&& listPageSearchRequest.getSortOrder() != null && listPageSearchRequest.getSortOrder().length() > 0
				&& "DESC".equalsIgnoreCase(listPageSearchRequest.getSortOrder())) {
			pageable = PageRequest.of((listPageSearchRequest.getPageNo()-1), listPageSearchRequest.getPageSize(), Sort.by(listPageSearchRequest.getSortColumn()).descending());
		} else {
			pageable = PageRequest.of((listPageSearchRequest.getPageNo()-1), listPageSearchRequest.getPageSize(), Sort.by("inwardid").descending());
		}		
				
		List<Integer> partyIds = new ArrayList<>();
		boolean partyIdsFlag = false;
		if (listPageSearchRequest.getPartyId() != null && listPageSearchRequest.getPartyId() > 0) {
			partyIds.add(listPageSearchRequest.getPartyId());
			partyIdsFlag = true;
		} else {
			AdminUserEntity adminUserEntity = commonUtil.getUserDetails();
			if (adminUserEntity.getUserPartyMap() != null && adminUserEntity.getUserPartyMap().size() > 0) {
				partyIds = new ArrayList<>();
				//for (UserPartyMap userPartyMap : adminUserEntity.getUserPartyMap()) {
					//partyIds.add(userPartyMap.getPartyId());
					//partyIdsFlag = true;
				//}
				log.info("In partyIds === " + partyIds);
			} else {
				partyIdsFlag = false;
				partyIds = new ArrayList<>();
			}
		}
		Page<Object[]> packetsList = salesOrderRepository.findInventory(listPageSearchRequest.getSearchText(), partyIds,
				partyIdsFlag, pageable);
		return packetsList;
	}
	@Override
	@Transactional
	public ResponseEntity<Object> post(SalesOrderExternalRequest req, String option) {
		try {

			// ----------------------- Validate Mandatory Fields -----------------------
			String missingField = validateExternalRequest(req);
			if (missingField != null) {
				return new ResponseEntity<>(
						"{\"code\":\"failure\", \"message\": \"" + missingField + " is required\"}",
						HttpStatus.BAD_REQUEST
				);
			}

			// ----------------------- Extract Header Fields -----------------------
			SalesOrderDetails d = req.getSalesOrder_Details();
			SalesOrderCustomFields c = req.getCustom_fields();

			// ----------------------- FIND EXISTING SO BY SO_NUMBER -----------------------
			Optional<SalesOrderJswEntity> existingSoOpt = salesOrderRepository.findBySoNumberAndIsDeletedFalse(d.getSalesorder_number());

			SalesOrderJswEntity so;

			if (existingSoOpt.isPresent()) {
				// ======================= UPDATE FLOW =======================
				so = existingSoOpt.get();

				// Remove existing children (orphanRemoval = true will delete them)
				if (so.getItemslist() != null) {
					so.getItemslist().clear();
				}

			} else {
				// ======================= INSERT FLOW =======================
				so = new SalesOrderJswEntity();
				so.setSoNumber(d.getSalesorder_number());
				so.setCreatedBy(commonUtil.getUserId());
				so.setCreatedOn(new Date());
				so.setAllocatedStts("PENDING");
				so.setIsDeleted(false);
				so.setSoStatus(StatusType.SO_CREATED.getType());
			}

			// ----------------------- COMMON HEADER UPDATE -----------------------
			so.setSocreatedate(convertToDate(d.getDate()));
			so.setRefno(d.getReference_number());
			so.setCustomerid(d.getCustomer_id());
			so.setDeliverymethod(d.getDelivery_method());
			so.setTerms(req.getPayment_terms_label());
			so.setPaymentmode(String.valueOf(req.getPayment_terms()));
			so.setBizsegment(c.getCf_biz_segment());
			so.setSupplysource(c.getCf_supply_source());
			so.setTypeofsupply(d.getDelivery_method());
			so.setZbooksSo(d.getSalesorder_id());
			so.setUpdatedBy(commonUtil.getUserId());
			so.setUpdatedOn(new Date());
			so.setTotalSoqty(BigDecimal.valueOf(d.getTotal_quantity()));

			// ----------------------- Branch -----------------------
			if (d.getBranch_id() != null) {
				so.setBranchId(d.getBranch_id());
			}

			// ----------------------- Expected Delivery Date -----------------------
			if (d.getExpected_shipment_date() != null) {
				Date original = convertToDate(d.getExpected_shipment_date());
				so.setExpectedDeliveryDate(original);

				Calendar cal = Calendar.getInstance();
				cal.setTime(original);
				cal.add(Calendar.DAY_OF_MONTH, -2);
				so.setStandardMaterialDate(cal.getTime());
			}

			// ----------------------- LINE ITEMS (CHILD UPSERT) -----------------------
			for (SalesOrderLineItem li : req.getLine_items()) {

				SalesOrderPacketsJswEntity item = new SalesOrderPacketsJswEntity();

				item.setSoId(so);
				item.setMmId(li.getSku());
				item.setSoqty(BigDecimal.valueOf(li.getQuantity()));
				item.setTax_percentage(String.valueOf(li.getTax_percentage()));
				item.setHsn_or_sac(li.getHsn_or_sac());
				item.setItemStatus(StatusType.SO_CREATED.getType());
				item.setAllocatedStts("PENDING");
				item.setIsDeleted(false);
				item.setCreatedBy(commonUtil.getUserId());
				item.setUpdatedBy(commonUtil.getUserId());
				item.setCreatedOn(new Date());
				item.setUpdatedOn(new Date());

				if (li.getWarehouse_id() != null) {
					item.setWearhouseId(li.getWarehouse_id());
				}
				so.addItem(item);
			}
			salesOrderRepository.save(so);
			return ResponseEntity.ok(
					"{\"code\":\"success\", \"message\":\"External Sales Order processed successfully\"}"
			);

		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(
					"{\"code\":\"failure\", \"message\": \"" + ex.getMessage() + "\"}",
					HttpStatus.INTERNAL_SERVER_ERROR
			);
		}
	}
	

	private String validateExternalRequest(SalesOrderExternalRequest req) {

		// SalesOrder_Details
		if (req.getSalesOrder_Details() == null) return "SalesOrder_Details";

		SalesOrderDetails d = req.getSalesOrder_Details();
		if (isEmpty(d.getSalesorder_number())) return "SalesOrder_Details.salesorder_number";
		if (isEmpty(d.getDate())) return "SalesOrder_Details.date";
		if (isEmpty(d.getStatus())) return "SalesOrder_Details.status";
		if (isEmpty(d.getReference_number())) return "SalesOrder_Details.reference_number";
		if (isEmpty(d.getCustomer_id())) return "SalesOrder_Details.customer_id";
		if (isEmpty(d.getDelivery_method())) return "SalesOrder_Details.delivery_method";
		if (isEmpty(d.getBranch_id())) return "SalesOrder_Details.branch_id";
		if (d.getTotal_quantity() == null) return "SalesOrder_Details.total_quantity";

		// Line Items
		if (req.getLine_items() == null || req.getLine_items().isEmpty()) return "line_items";
		for (SalesOrderLineItem li : req.getLine_items()) {
			if (isEmpty(li.getSku())) return "line_items.sku";
			if (isEmpty(li.getWarehouse_id())) return "line_items.warehouse_id";

		}

		// payments
		if (req.getPayment_terms() == null) return "payment_terms";
		if (isEmpty(req.getPayment_terms_label())) return "payment_terms_label";

		// custom fields
		if (req.getCustom_fields() == null) return "custom_fields";

		SalesOrderCustomFields c = req.getCustom_fields();

		if (isEmpty(c.getCf_biz_segment())) return "custom_fields.cf_biz_segment";
		if (isEmpty(c.getCf_supply_source())) return "custom_fields.cf_supply_source";
		if (isEmpty(c.getCf_delivery_method())) return "custom_fields.cf_delivery_method";

		return null; // ALL OK
	}

	private boolean isEmpty(String s) {
		return s == null || s.trim().isEmpty();
	}

	private Date convertToDate(String dateStr) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
		} catch (Exception e) {
			return null;
		}
	}


	@Override
	public ResponseEntity<Object> update(SalesOrderExternalRequest request) {

		String option = request
				.getSalesOrder_Details()
				.getStatus();   // CREATED / APPROVED / ON_HOLD / REJECTED

		log.info("Updating Sales Order with status: {}", option);

		if (option == null) {
			return new ResponseEntity<>(
					"{\"status\":\"failure\",\"message\":\"Status is required\"}",
					HttpStatus.BAD_REQUEST
			);
		}

		switch (option.toUpperCase()) {

			case "APPROVED":
				return approveSalesOrder(request);

			case "ON_HOLD":
				return holdSalesOrder(request);

			case "REJECTED":
				return rejectSalesOrder(request);

			default:
				return new ResponseEntity<>(
						"{\"status\":\"failure\",\"message\":\"Invalid status value\"}",
						HttpStatus.BAD_REQUEST
				);
		}
	}

	private ResponseEntity<Object> approveSalesOrder(SalesOrderExternalRequest req) {

		SalesOrderDetails d = req.getSalesOrder_Details();

		SalesOrderJswEntity so =
				salesOrderRepository.findBySoNumberIgnoreCase(d.getSalesorder_number())
						.orElseThrow(() -> new RuntimeException("SO not found"));

		Date standardDate = convertToDate(d.getStandard_material_date());
		Date likelyDate = convertToDate(d.getLikely_material_date());

		so.setSoStatus(StatusType.SO_APPROVED.getType());
		so.setApprovedDate(new Date());
		so.setUpdatedBy(commonUtil.getUserId());
		so.setStandardMaterialDate(standardDate);
		so.setLikelyMaterialDate(likelyDate);
		so.setRemarks(d.getRemarks());
		so.setCamCode(d.getCam_code());

		// approve child items also
		for (SalesOrderPacketsJswEntity item : so.getItemslist()) {
			item.setItemStatus(StatusType.SO_APPROVED.getType());
			item.setApprovedDate(new Date());
		}

		salesOrderRepository.save(so);

		return ResponseEntity.ok(
				"{\"status\":\"success\",\"message\":\"Sales Order approved Successfully.\"}"
		);
	}


	private ResponseEntity<Object> holdSalesOrder(SalesOrderExternalRequest req) {

		SalesOrderDetails d = req.getSalesOrder_Details();

		SalesOrderJswEntity so =
				salesOrderRepository.findBySoNumberIgnoreCase(d.getSalesorder_number())
						.orElseThrow(() -> new RuntimeException("SO not found"));

		so.setSoStatus(StatusType.SO_HOLD.getType());
		so.setUpdatedOn(new Date());
		so.setUpdatedBy(commonUtil.getUserId());
		so.setRemarks(d.getRemarks());
		so.setCamCode(d.getCam_code());
		salesOrderRepository.save(so);
		return ResponseEntity.ok(
				"{\"status\":\"success\",\"message\":\"Sales Order put on hold Successfully.\"}"
		);
	}

	private ResponseEntity<Object> rejectSalesOrder(SalesOrderExternalRequest req) {

		SalesOrderDetails d = req.getSalesOrder_Details();

		SalesOrderJswEntity so =
				salesOrderRepository.findBySoNumberIgnoreCase(d.getSalesorder_number())
						.orElseThrow(() -> new RuntimeException("SO not found"));

		so.setSoStatus(StatusType.SO_REJECTED.getType());
		so.setRemarks(d.getRemarks());
		so.setUpdatedOn(new Date());
		so.setUpdatedBy(commonUtil.getUserId());
		so.setRemarks(d.getRemarks());
		so.setCamCode(d.getCam_code());
		salesOrderRepository.save(so);
		return ResponseEntity.ok(
				"{\"status\":\"success\",\"message\":\"Sales Order rejected Successfully.\"}"
		);
	}

	@Override
	@Transactional
	public ResponseEntity<Object> bulkUpdate(SalesOrderBulkRequest request) {

		if (request.getSoIds() == null || request.getSoIds().isEmpty()) {
			return ResponseEntity.badRequest()
					.body("{\"status\":\"failure\",\"message\":\"SO ID list is empty\"}");
		}

		List<SalesOrderJswEntity> orders = salesOrderRepository.findAllById(request.getSoIds());

		if (orders.size() != request.getSoIds().size()) {
			return ResponseEntity.badRequest()
					.body("{\"status\":\"failure\",\"message\":\"One or more SO IDs not found\"}");
		}

		Date now = new Date();
		for (SalesOrderJswEntity so : orders) {
			so.setSoStatus(StatusType.SO_APPROVED.getType());
			so.setApprovedDate(now);
			so.setUpdatedOn(now);
			so.setUpdatedBy(commonUtil.getUserId());

			for (SalesOrderPacketsJswEntity item : so.getItemslist()) {
				item.setItemStatus(StatusType.SO_APPROVED.getType());
				item.setApprovedDate(now);
				item.setUpdatedOn(now);
				item.setUpdatedBy(commonUtil.getUserId());
			}
		}

		salesOrderRepository.saveAll(orders);

		return ResponseEntity.ok(
				"{\"status\":\"success\",\"message\":\"Sales Orders approved successfully\",\"count\":"
						+ orders.size() + "}"
		);
	}
}
