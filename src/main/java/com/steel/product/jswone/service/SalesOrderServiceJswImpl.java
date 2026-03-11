package com.steel.product.jswone.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.DocumentException;
import com.steel.product.application.dao.InstructionRepository;
import com.steel.product.application.dao.InwardEntryRepository;
import com.steel.product.application.dto.quality.ListPageSearchRequest;
import com.steel.product.application.dto.salesorder.SalesOrderListDTO;
import com.steel.product.application.dto.salesorder.SalesOrderListResponse;
import com.steel.product.application.entity.AdminUserEntity;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.entity.UserPartyMap;
import com.steel.product.application.util.CommonUtil;
import com.steel.product.jswone.entity.JswoneAuditTrailEntity;
import com.steel.product.jswone.entity.SalesOrderAllocationEntity;
import com.steel.product.jswone.entity.SalesOrderJswEntity;
import com.steel.product.jswone.entity.SalesOrderPacketsJswEntity;
import com.steel.product.jswone.entity.StatusType;
import com.steel.product.jswone.repository.JswoneAuditTrailRepository;
import com.steel.product.jswone.repository.SalesOrderAllocationJswRepository;
import com.steel.product.jswone.repository.SalesOrderChildJswRepository;
import com.steel.product.jswone.repository.SalesOrderJswRepository;
import com.steel.product.jswone.request.CPSplitRequest;
import com.steel.product.jswone.request.SalesOrderBulkRequest;
import com.steel.product.jswone.request.SalesOrderChildRequest;
import com.steel.product.jswone.request.SalesOrderCustomFields;
import com.steel.product.jswone.request.SalesOrderDetails;
import com.steel.product.jswone.request.SalesOrderExternalRequest;
import com.steel.product.jswone.request.SalesOrderLineItem;
import com.steel.product.jswone.request.SalesOrderMainRequest;
import com.steel.product.jswone.response.SalesOrderChildAllocationResponse;

import lombok.extern.log4j.Log4j2;

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

	@Autowired
	private JswoneAuditTrailRepository jswoneAuditTrailRepository;
	
	@Autowired
	private SpringTemplateEngine templateEngine;

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
				salesOrderEntity.setCpStatus(StatusType.CP_PLAN_DRAFT.getType());
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
				childEntity.setItemSoStatus(StatusType.SO_CREATED.getType());
				if("approve".equals(option)){
					childEntity.setItemSoStatus(StatusType.SO_APPROVED.getType());
					childEntity.setItemCpStatus(StatusType.CP_PLAN_DRAFT.getType());
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
	public Page<Object[]> listAllSOIDsCP(ListPageSearchRequest listPageSearchRequest) {

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
		Page<Object[]> packetsList = salesOrderRepository.listAllSOIDsCP(listPageSearchRequest.getSearchText(),
				listPageSearchRequest.getSoId(), listPageSearchRequest.getStatus(), pageable);
		return packetsList;
	}

	@Override
	public List<Object[]> listAllSOsCP(List<Integer> soIDsList) {
		List<Object[]> packetsList = salesOrderRepository.listIdWisedetailsCP(soIDsList);
		return packetsList;
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
				
				SalesOrderPacketsJswEntity childEntity = childRepository.findBySoChildId(request.getSoChildId());
				//BigDecimal allocatedQty = (childEntity.getAllocatedSoqty() == null? BigDecimal.ZERO : childEntity.getAllocatedSoqty());

				BigDecimal allocatedQty = Optional.ofNullable(soAllocationRepository.getTotalAllocatedQtyBySoChildId(request.getSoChildId())).orElse(BigDecimal.ZERO);
				
				if (request.getAllocatedSoqty() == null) {
					return new ResponseEntity<>("{\"status\": \"failure\", \"message\": \"Please enter valid value in allocation quantity\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
				if (request.getAllocatedSoqty().compareTo(BigDecimal.ZERO) <= 0) {
					return new ResponseEntity<>("{\"status\": \"failure\", \"message\": \"Please enter valid value in allocation quantity\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
				if (request.getAllocatedSoqty().compareTo(childEntity.getSoqty()) > 0) {
					return new ResponseEntity<>("{\"status\": \"failure\", \"message\": \"Entered quantity should be less than required quantity\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
				
				balanceQtyRequired = childEntity.getSoqty().subtract(allocatedQty);
				totalAllocatedQty = request.getAllocatedSoqty().add(allocatedQty);

				if (balanceQtyRequired.compareTo(BigDecimal.ZERO) == 0 && "COMPLETED".equals(childEntity.getAllocatedStts())) {
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
						SalesOrderJswEntity soEntity  = salesOrderRepository.findBySoId(request.getSoId());
						Instruction instruction = instructionList.get();
						float instructionAllocatedQty = (instruction.getAllocatedSoqty() == null? 0.0f : instruction.getAllocatedSoqty());
						Float totalAllocatedItemQty = instructionAllocatedQty + request.getAllocatedSoqty().floatValue();
						instructionRepository.consolidatePlanner(request.getInstructionId(), totalAllocatedItemQty, childEntity.getMmId(), soEntity.getSoNumber());
					}
				} else {
					Optional<InwardEntry> inwardList = inwardEntryRepository.findById(request.getInwardEntryId());
					if (inwardList != null && inwardList.isPresent()) {
						SalesOrderJswEntity soEntity  = salesOrderRepository.findBySoId(request.getSoId());
						InwardEntry inwardEntry = inwardList.get();
						float inwardAllocatedQty = (inwardEntry.getAllocatedSoqty() == null? 0.0f : inwardEntry.getAllocatedSoqty());

						Float totalAllocatedItemQty = inwardAllocatedQty + request.getAllocatedSoqty().floatValue();
						inwardEntryRepository.consolidatePlanner(request.getInwardEntryId(), totalAllocatedItemQty, childEntity.getMmId(), soEntity.getSoNumber());
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
	public Page<Object[]> findInventory(ListPageSearchRequest request) {
		Pageable pageable = null;
		if (request.getSortColumn() != null && request.getSortColumn().length() > 0
				&& request.getSortOrder() != null && request.getSortOrder().length() > 0
				&& "ASC".equalsIgnoreCase(request.getSortOrder())) {
			pageable = PageRequest.of((request.getPageNo()-1), request.getPageSize(), Sort.by(request.getSortColumn()).ascending());
		}else if (request.getSortColumn() != null && request.getSortColumn().length() > 0
				&& request.getSortOrder() != null && request.getSortOrder().length() > 0
				&& "DESC".equalsIgnoreCase(request.getSortOrder())) {
			pageable = PageRequest.of((request.getPageNo()-1), request.getPageSize(), Sort.by(request.getSortColumn()).descending());
		} else {
			pageable = PageRequest.of((request.getPageNo()-1), request.getPageSize(), Sort.by("inwardid").descending());
		}		
				
		List<Integer> partyIds = new ArrayList<>();
		boolean partyIdsFlag = false;
		if (request.getPartyId() != null && request.getPartyId() > 0) {
			partyIds.add(request.getPartyId());
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
		int packetStatus =0;
		
		if ("COIL".equals(request.getAllocationType()) || "INWARDSHEET".equals(request.getAllocationType())) {
			return salesOrderRepository.findCoilInventory(request.getSearchText(), request.getAllocationType(),partyIds, partyIdsFlag, pageable);
		} else if ("INWARDSHEET_PACKETS".equals(request.getAllocationType())) {
			if("FG".equals(request.getInventoryType())) {
				packetStatus=3;	
			}
			if("INPROGRESS".equals(request.getInventoryType())) {
				packetStatus=2;	
			} 
			return salesOrderRepository.findInventory(request.getSearchText(), partyIds, partyIdsFlag, packetStatus, pageable);
		} else {
			if("FG".equals(request.getInventoryType())) {
				packetStatus=3;	
			}
			if("INPROGRESS".equals(request.getInventoryType())) {
				packetStatus=2;	
			} 
			return salesOrderRepository.findInventory(request.getSearchText(), partyIds, partyIdsFlag, packetStatus, pageable);
		}
	}
	
	@Override
	@Transactional
	public ResponseEntity<Object> post(SalesOrderExternalRequest req, String option) {

		JswoneAuditTrailEntity audit = new JswoneAuditTrailEntity();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		ResponseEntity<Object> finalResponse = null;

		try {
			// ----------------------- AUDIT INIT -----------------------
			audit.setProcessType("SO_POST");
			audit.setCreatedOn(new Date());

			String rfNumber = null;
			if (req != null && req.getSalesOrder_Details() != null) {
				rfNumber = req.getSalesOrder_Details().getReference_number();
			}
			audit.setPoId(rfNumber);
			audit.setRequestObj(req == null ? "" : mapper.writeValueAsString(req));


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
				item.setMaterialName( li.getName());
				item.setItemSoStatus(StatusType.SO_CREATED.getType());
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
			finalResponse = ResponseEntity.ok(
					"{\"code\":\"success\", \"message\":\"External Sales Order processed successfully\"}"
			);
			return finalResponse;

		} catch (Exception ex) {
			ex.printStackTrace();
			finalResponse = new ResponseEntity<>(
					"{\"code\":\"failure\", \"message\": \"" + safeMsg(ex.getMessage()) + "\"}",
					HttpStatus.INTERNAL_SERVER_ERROR
			);
			return finalResponse;
		}
		finally {
			try {
				if (finalResponse != null) {
					audit.setStatusCode(String.valueOf(finalResponse.getStatusCode().value()));
					Object respBody = finalResponse.getBody();
					if (respBody == null) {
						audit.setSourceRespone("");
					} else if (respBody instanceof String) {
						audit.setSourceRespone((String) respBody);
					} else {
						audit.setSourceRespone(mapper.writeValueAsString(respBody));
					}
				} else {
					audit.setStatusCode("500");
					audit.setSourceRespone("{\"code\":\"failure\",\"message\":\"Unknown error\"}");
				}
				if (audit.getDestinationResponse() == null) {
					audit.setDestinationResponse("");
				}
			} catch (Exception ignore) {
				audit.setStatusCode("500");
				audit.setSourceRespone("{\"code\":\"failure\",\"message\":\"Audit serialization failed\"}");
			}
			try {
				jswoneAuditTrailRepository.save(audit);
			} catch (Exception saveEx) {
				log.error("SO_POST audit save failed", saveEx);
			}
		}
	}

	private String safeMsg(String msg) {
		if (msg == null) return "";
		return msg.replace("\"", "'");
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

		String option = request.getSalesOrder_Details().getStatus(); // CREATED / APPROVED / ON_HOLD / REJECTED

		log.info("Updating Sales Order with status: {}", option);

		if (option == null) {
			return new ResponseEntity<>("{\"status\":\"failure\",\"message\":\"Status is required\"}", HttpStatus.BAD_REQUEST);
		}

		switch (option.toUpperCase()) {

		case "APPROVED":
			return approveSalesOrder(request);

		case "ON_HOLD":
			return holdSalesOrder(request);

		case "REJECTED":
			return rejectSalesOrder(request);

		default:
			return new ResponseEntity<>("{\"status\":\"failure\",\"message\":\"Invalid status value\"}",
					HttpStatus.BAD_REQUEST);
		}
	}

	private ResponseEntity<Object> approveSalesOrder(SalesOrderExternalRequest req) {

		SalesOrderDetails d = req.getSalesOrder_Details();

		SalesOrderJswEntity so = salesOrderRepository.findBySoNumberIgnoreCase(d.getSalesorder_number())
				.orElseThrow(() -> new RuntimeException("SO not found"));

		Date standardDate = convertToDate(d.getStandard_material_date());
		Date likelyDate = convertToDate(d.getLikely_material_date());

		so.setSoStatus(StatusType.SO_APPROVED.getType());
		so.setCpStatus(StatusType.CP_PLAN_DRAFT.getType());
		so.setApprovedDate(new Date());
		so.setUpdatedBy(commonUtil.getUserId());
		so.setStandardMaterialDate(standardDate);
		so.setLikelyMaterialDate(likelyDate);
		so.setRemarks(d.getRemarks());
		so.setCamCode(d.getCam_code());

		// approve child items also
		for (SalesOrderPacketsJswEntity item : so.getItemslist()) {
			item.setItemSoStatus(StatusType.SO_APPROVED.getType());
			item.setItemCpStatus(StatusType.CP_PLAN_DRAFT.getType());
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
			so.setCpStatus(StatusType.CP_PLAN_DRAFT.getType());
			so.setApprovedDate(now);
			so.setUpdatedOn(now);
			so.setUpdatedBy(commonUtil.getUserId());

			for (SalesOrderPacketsJswEntity item : so.getItemslist()) {
				item.setItemSoStatus(StatusType.SO_APPROVED.getType());
				item.setItemCpStatus(StatusType.CP_PLAN_DRAFT.getType());
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
	
	@Override
	@Transactional
	public ResponseEntity<Object> consolidateSplit(CPSplitRequest request) {
		log.info("inside consolidateSplit ");
		ResponseEntity<Object> responseEntity = null;
		String message = "Consolidate Plan split processed successfully";
		try {

			if (request.getInwardEntryId() > 0 && request.getInstructionId() > 0) {
				Instruction copy = splitInstruction(request.getInstructionId(), request.getSplitQty());

				if (copy != null && copy.getInstructionId() > 0) {
					responseEntity = ResponseEntity.ok("{\"status\":\"success\",\"message\": \"" + message + "\"}");
				} else {
					responseEntity = new ResponseEntity<>("{\"status\": \"failure\", \"message\": \"Failed to split\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else if (request.getInwardEntryId() > 0) {
				InwardEntry copy = splitInward( request.getInwardEntryId(), request.getSplitQty(), request.getSoNumber());

				if (copy != null && copy.getInwardEntryId() > 0) {
					responseEntity = ResponseEntity.ok("{\"status\":\"success\",\"message\": \"" + message + "\"}");
				} else {
					responseEntity = new ResponseEntity<>("{\"status\": \"failure\", \"message\": \"Failed to split\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			responseEntity = new ResponseEntity<>("{\"status\": \"failure\", \"message\": \"" + e.getMessage() + "\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}
	
	@Transactional
	public Instruction splitInstruction(Integer id, BigDecimal splitQty) {

	    Instruction original = instructionRepository.findById(id).orElseThrow(() -> new RuntimeException("Instruction not found"));

	    Instruction copy = original.duplicateForSplit();

	    float split = splitQty.floatValue();

	    if (original.getActualWeight() != null && original.getActualWeight() > 0f) {
	        copy.setActualWeight(split);
	        copy.setPlannedWeight(split);
	        copy.setDeliveryDetails(null);

	        float balance = original.getActualWeight() - split;
	        original.setActualWeight(balance);
	        original.setPlannedWeight(balance);
	    }
	    else {
	        copy.setPlannedWeight(split);

	        float balance = original.getPlannedWeight() - split;
	        original.setPlannedWeight(balance);
	    }

	    instructionRepository.save(copy);
	    instructionRepository.save(original);

	    return copy;
	}

	@Transactional
	public InwardEntry splitInward(Integer id, BigDecimal splitQty, String soNumber) {

		InwardEntry original = inwardEntryRepository.findById(id).orElseThrow(() -> new RuntimeException("Inward not found"));

		InwardEntry copy = original.duplicateForSplit();

		float split = splitQty.floatValue();

		if (original.getFpresent() != null && original.getFpresent() > 0f) {
			copy.setfQuantity(split);
			copy.setGrossWeight(split);
			copy.setFpresent(split);
			copy.setInStockWeight(0f);
			String newCOilNumber = copy.getCoilNumber() + "_" + soNumber;
			copy.setCoilNumber(newCOilNumber);

			original.setFpresent(original.getFpresent() - split);
			original.setGrossWeight(original.getGrossWeight() - split);
			original.setfQuantity(original.getfQuantity() - split);
			inwardEntryRepository.save(copy);
			inwardEntryRepository.save(original);
		} else {
			copy=null;
		}
		return copy;
	}

	@Transactional
	@Override
	public ResponseEntity<Object> unAllocate(SalesOrderChildAllocationResponse req) {
		ResponseEntity<Object> responseEntity = null;
		Optional<SalesOrderAllocationEntity> original = soAllocationRepository.findById(req.getSoAllocationId());
		if (original.isPresent()) {

			SalesOrderAllocationEntity alloObj = original.get();
			if (alloObj.getInstructionId() != null && alloObj.getInstructionId() > 0) {
				instructionRepository.unAllocatCP(alloObj.getInstructionId());
			} else {
				if (alloObj.getInwardEntryId() != null && alloObj.getInwardEntryId() > 0) {
					inwardEntryRepository.unAllocatCP(alloObj.getInwardEntryId());
				}
			}
			soAllocationRepository.deleteById(req.getSoAllocationId());
			
			BigDecimal total = Optional.ofNullable(soAllocationRepository.getTotalAllocatedQtyBySoChildId(alloObj.getSoChildId())).orElse(BigDecimal.ZERO);
			
			if (total != null && total.compareTo(BigDecimal.ZERO) > 0) {
				childRepository.consolidatePlanner(alloObj.getSoChildId(), total, "PENDING", "", commonUtil.getUserId());
			} else {
				childRepository.consolidatePlanner(alloObj.getSoChildId(), BigDecimal.ZERO, "PENDING", "", commonUtil.getUserId());			
			}
			responseEntity = ResponseEntity.ok("{\"status\":\"success\",\"message\": \"Item has been unallocated successfully.\"}");
		} else {
			responseEntity = new ResponseEntity<>("{\"status\": \"failure\", \"message\": \"Please enter valid value\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}

	@Override
	public File generatePdf(ListPageSearchRequest request) throws IOException, DocumentException {
		Context context = getSODetailsContext(request);
		String html = loadAndFillSOTemplate(context);
		return renderPdfInstruction(html, "so", "" + request.getSoId(), "SO_PDF");
	}
	
	private Context getSODetailsContext(ListPageSearchRequest request) {
		Context context = new Context();
		List<Object[]> packetsList = salesOrderRepository.soDetailsBySoId(request.getSoId());

		SalesOrderListResponse resp = new SalesOrderListResponse();
		Float dweightTotal=0f;

		for (Object[] result : packetsList) {
			SalesOrderListDTO child = new SalesOrderListDTO();

			resp.setPartyId(result[8] != null ? Integer.parseInt(result[8].toString()) : null);
			resp.setPartyName(result[9] != null ? (String) result[9] : null);
			resp.setSoStatus(result[12] != null ? (String) result[12] : null);
			resp.setSoNumber(result[14] != null ? (String) result[14] : null);
			resp.setSoId(result[15] != null ? Integer.parseInt(result[15].toString()) : null);
			resp.setCustomerCode(result[18] != null ? (String) result[18] : null);
			resp.setOrderDate(result[19] != null ? (String) result[19] : null);
			resp.setCagtegoryName(result[20] != null ? (String) result[20] : null);
			resp.setProcessCenter( resp.getPartyName());

			child.setInstructionId(result[0] != null ? (Integer) result[0] : null);
			child.setInwardEntryId(result[1] != null ? (Integer) result[1] : null);
			child.setPlannedNoofPieces( result[21] != null ? (Integer) result[21] : null);
			child.setCoilNo(result[2] != null ? (String) result[2] : null);
			
			child.setCustomerBatchNo(result[3] != null ? (String) result[3] : null);
			child.setMaterialGrade(result[4] != null ? (String) result[4] : null);
			child.setMaterialDesc(result[5] != null ? (String) result[5] : null);
			child.setFthickness(result[6] != null ? (Float) result[6] : null);
			child.setProcessing("CTL");
			child.setPackingMode("Loose Bundle");
			child.setSpecilaInstructions("Loose Bundle");
			child.setDiagonal("Max. 3.00");
			child.setEdgeBurr("Max 3% of Thick");

			try {
				Float dweight;
				Float dwidth;
				Float dlength;
				dweight = (result[7] != null ? (Float) result[7] : null);
				dwidth = (result[10] != null ? (Float) result[10] : null);
				dlength = (result[11] != null ? (Float) result[11] : null);
				child.setFwidth(dwidth.floatValue());
				child.setFweight(dweight.floatValue());
				child.setFlenghth(dlength.floatValue());
			} catch (ClassCastException e) {
				Double dweight1 = (result[7] != null ? (Double) result[7] : null);
				Double dwidth1 = (result[10] != null ? (Double) result[10] : null);
				Double dlength1 = (result[11] != null ? (Double) result[11] : null);
				child.setFwidth(dwidth1.floatValue());
				child.setFweight(dweight1.floatValue());
				child.setFlenghth(dlength1.floatValue());
			}
			String formName = (result[22] != null ? (String) result[22] : null);
			String coilSKU		= formName+" "+child.getMaterialGrade()+ " "+child.getFthickness() + " X  "+ child.getFwidth() + " X  "+ child.getFlenghth();
			String packetSKU	= "Sheet"+" "+child.getMaterialGrade()+ " "+child.getFthickness() + " X  "+ child.getFwidth()  + " X  "+ child.getFlenghth();

			child.setCoilSKU(coilSKU);
			child.setFinalProcessingSKU(packetSKU);

			dweightTotal=dweightTotal+child.getFweight();
			child.setPacketStatus(result[13] != null ? (String) result[13] : null);
			resp.getChildListResp().add(child);
		}
		resp.setFweightTotal(dweightTotal.floatValue());
		context.setVariable("soDetails", resp);
		return context;
	}

	private String loadAndFillSOTemplate(Context context) {
		return templateEngine.process("sonew_view", context);
	}

	private File renderPdfInstruction(String html, String filename, String id, String processType)
			throws IOException, DocumentException {
		File file = File.createTempFile("aspen-steel-" + filename, ".pdf");
		File labelFile = File.createTempFile("so_details_" + id, ".pdf");
		OutputStream outputStream = new FileOutputStream(file);
		ITextRenderer renderer = new ITextRenderer(20f * 4f / 3f, 20);
		renderer.setDocumentFromString(html, new ClassPathResource("/").getURL().toExternalForm());
		renderer.layout();
		renderer.createPDF(outputStream);

		outputStream.close();
		file.deleteOnExit();
		labelFile.deleteOnExit();
		return file;
	}
	
	
	
	
	
	
	
}
