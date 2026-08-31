package com.steel.product.jswone.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import com.lowagie.text.DocumentException;
import com.steel.product.application.dto.inward.InwardEntryResponseDto;
import com.steel.product.application.dto.inward.SearchListPageRequest;
import com.steel.product.application.dto.pdf.PdfResponseDto;
import com.steel.product.application.dto.quality.ListPageSearchRequest;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.service.InwardEntryService;
import com.steel.product.jswone.entity.JswoneAuditTrailEntity;
import com.steel.product.jswone.repository.JswoneAuditTrailRepository;
import com.steel.product.jswone.request.CPSplitRequest;
import com.steel.product.jswone.request.SalesOrderBulkRequest;
import com.steel.product.jswone.request.SalesOrderChildRequest;
import com.steel.product.jswone.request.SalesOrderExternalRequest;
import com.steel.product.jswone.request.SalesOrderMainRequest;
import com.steel.product.jswone.response.CoilAllocationDTO;
import com.steel.product.jswone.response.DashboardResponse;
import com.steel.product.jswone.response.InwardEntryResponseDetails;
import com.steel.product.jswone.response.SalesOrderCPChildResponse;
import com.steel.product.jswone.response.SalesOrderCPMainResponse;
import com.steel.product.jswone.response.SalesOrderChildAllocationResponse;
import com.steel.product.jswone.response.SalesOrderChildResponse;
import com.steel.product.jswone.response.SalesOrderDashboardResponse;
import com.steel.product.jswone.response.SalesOrderMainResponse;
import com.steel.product.jswone.service.SalesOrderJswService;
import com.steel.product.jswone.service.SalesOrderPDFJswService;
import com.steel.product.jswone.service.SoPageExportService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;

@RestController
@CrossOrigin
@Tag(name = "Sales Order", description = "Sales Order")
@RequestMapping({ "/salesorder" })
@Log4j2
public class SalesOrderJswController {

	@Autowired
	private SalesOrderJswService salesOrderService;
	
	@Autowired
	private SoPageExportService soPageExportService;

	@Autowired
	private SalesOrderPDFJswService sopdfService;

	@Autowired
	private JswoneAuditTrailRepository jswoneAuditTrailRepository;

	@Autowired
	private InwardEntryService inwdEntrySvc;
	
	@PostMapping(value = "/create", produces = "application/json")
	public ResponseEntity<Object> save(@RequestBody SalesOrderMainRequest salesOrderMainRequest) {
		log.info("inside SalesOrderJswController.create");
		return salesOrderService.save(salesOrderMainRequest, "create");
	}

	@PostMapping(value = "/approve", produces = "application/json")
	public ResponseEntity<Object> approve(@RequestBody SalesOrderMainRequest salesOrderMainRequest) {
		return salesOrderService.save(salesOrderMainRequest, "approve");
	}

	@PostMapping(value = "/consolidateplanner/create", produces = "application/json")
	public ResponseEntity<Object> consolidatePlanner(@RequestBody List<SalesOrderChildRequest> salesOrderMainRequest) {
		return salesOrderService.consolidatePlanner(salesOrderMainRequest);
	}

	@PostMapping(value = "/cp/split", produces = "application/json")
	public ResponseEntity<Object> consolidateSplit(@RequestBody CPSplitRequest cpSplitRequest) {
		return salesOrderService.consolidateSplit(cpSplitRequest);
	}

	@PostMapping(value = "/list", produces = "application/json")
	public ResponseEntity<Object> listAllSOs(@RequestBody ListPageSearchRequest listPageSearchRequest) {
		Map<String, Object> response = new HashMap<>();

		if ( !("ALL".equals(listPageSearchRequest.getFilterStatus())) && listPageSearchRequest.getFilterStatus() != null && listPageSearchRequest.getFilterStatus().length() > 0) {
			listPageSearchRequest.getStatus().add(listPageSearchRequest.getFilterStatus());
		}
		Page<Object[]> packetsList1 = salesOrderService.listAllSOIDs(listPageSearchRequest);

		List<Integer> soIDsList = new ArrayList<>();
		for (Object[] result : packetsList1) {
			Integer soId = (result[0] != null ? (Integer) result[0] : null);
			soIDsList.add(soId);
		}
		
		List<Object[]> packetsList = salesOrderService.listAllSOs(soIDsList);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Map<Integer, SalesOrderMainResponse> soMap = new LinkedHashMap<>();
		for (Object[] result : packetsList) {
			SalesOrderMainResponse resp = new SalesOrderMainResponse();
			SalesOrderChildResponse child = new SalesOrderChildResponse();

			resp.setSoId(result[0] != null ? Integer.parseInt(result[0].toString()) : null);
			resp.setSoNumber(result[1] != null ? (String) result[1] : null);
			resp.setSocreatedate(result[2] != null ? sdf.format(result[2]) : null);

			resp.setDeliverymethod(result[3] != null ? (String) result[3] : null);
			resp.setDestinationcode(result[4] != null ? (String) result[4] : null);
			resp.setRefno(result[5] != null ? (String) result[5] : null);
			resp.setJoplsorefno(result[6] != null ? (String) result[6] : null);
			resp.setBizsegment(result[7] != null ? (String) result[7] : null);
			resp.setEcommerce(result[8] != null ? (String) result[8] : null);
			resp.setSupplysource(result[9] != null ? (String) result[9] : null);
			resp.setTypeofsupply(result[10] != null ? (String) result[10] : null);
			resp.setIncomingpayment(result[11] != null ? (String) result[11] : null);
			resp.setPaymentmode(result[12] != null ? (String) result[12] : null);
			resp.setTerms(result[13] != null ? (String) result[13] : null);
			resp.setCustomerCode(result[14] != null ? (String) result[14] : null);
			resp.setTotalSoqty(result[15] != null ? (BigDecimal) result[15] : null);
			resp.setTotalAllocatedSoqty(result[16] != null ? (BigDecimal) result[16] : null);
			resp.setAllocatedStts(result[17] != null ? (String) result[17] : null);
			resp.setSoStatus(result[18] != null ? (String) result[18] : null);

			resp.setZbooks_so(result[19] != null ? (String) result[19] : null);
			resp.setExpected_delivery_date(formatDate(result[20]));
			resp.setLikely_material_date(formatDate(result[21]));
			resp.setStandard_material_date(formatDate(result[22]));
			resp.setZohoStatus(result[44] != null ? (String) result[44] : null);
			resp.setBranchId(result[45] != null ? (String) result[45] : null);

			child.setSoChildId(result[23] != null ? (Integer) result[23] : null);
			child.setMmId(result[24] != null ? (String) result[24] : null);
			child.setSoqty(result[27] != null ? (BigDecimal) result[27] : null);
			child.setTotalAllocatedItemQty(result[28] != null ? (BigDecimal) result[28] : null);
			child.setAllocatedStts(result[29] != null ? (String) result[29] : null);
			child.setItemStatus(result[30] != null ? (String) result[30] : null);
			child.setWearhouse_id(result[31] != null ? (String) result[31] : null);
			child.setTax(result[32] != null ? (String) result[32] : null);
			child.setInvoicedItemQty(toBigDecimal(result[46]));
			BigDecimal itemSOQty = child.getSoqty() != null ? child.getSoqty() : BigDecimal.ZERO;
			BigDecimal itemInvQty = child.getInvoicedItemQty() != null ? child.getInvoicedItemQty() : BigDecimal.ZERO;
			child.setBalInvoicedItemQty(itemSOQty.subtract(itemInvQty).setScale(2, RoundingMode.HALF_UP));

			resp.setInvoicedSoQty(toBigDecimal(result[47]));
			BigDecimal soQty1 = resp.getTotalSoqty() != null ? resp.getTotalSoqty() : BigDecimal.ZERO;
			BigDecimal invQty1 = resp.getInvoicedSoQty() != null ? resp.getInvoicedSoQty() : BigDecimal.ZERO;
			resp.setBalInvoicedSoQty(soQty1.subtract(invQty1).setScale(2, RoundingMode.HALF_UP));
			
			child.setMm_description(result[33] != null ? (String) result[33] : null);
			child.setHsn(result[34] != null ? String.valueOf(result[34]) : null);
			child.setWare_house_name(result[35] != null ? (String) result[35] : null);
			resp.setBranch(result[36] != null ? (String) result[36] : null);
			resp.setCam_code(result[37] != null ? (String) result[37] : null);
			resp.setRemarks(result[38] != null ? (String) result[38] : null);
			resp.setCustomer_name(result[39] != null ? (String) result[39] : null);
			resp.setCustomer_number(result[40] != null ? (String) result[40] : null);
			child.setNumberOfSheets(result[41] != null ? (String) result[41] : null);
			resp.setSpecial_delivery_instructions(result[42] != null ? (String) result[42] : null);
			if (result[43] != null) {
				SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				resp.setOrder_confirmation_time(sdfDateTime.format((Timestamp) result[43]));
			} else {
				resp.setOrder_confirmation_time(null);
			}
			resp.getItemslist().add(child);

			if (soMap != null && soMap.get(resp.getSoId()) != null) {
				SalesOrderMainResponse addEntity = soMap.get(resp.getSoId());
				addEntity.getItemslist().add(child);
				soMap.put(resp.getSoId(), addEntity);
			} else {
				soMap.put(resp.getSoId(), resp);
			}
		}

		List<SalesOrderMainResponse> list = new ArrayList<>(soMap.values());
		response.put("content", list);
		response.put("currentPage", packetsList1.getNumber());
		response.put("totalItems", packetsList1.getTotalElements());
		response.put("totalPages", packetsList1.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
 
	@PostMapping(value = "/findinventory", produces = "application/json")
	public ResponseEntity<Object> findInventory(@RequestBody ListPageSearchRequest request) {
		Map<String, Object> response = new LinkedHashMap<>();
		
		Page<Object[]> packetsList1 = salesOrderService.findInventory(request);
		List<InwardEntryResponseDetails> list = new ArrayList<>();
		Set<Integer> coilAgeSet = new LinkedHashSet<>();

		for (Object[] result : packetsList1) {
			InwardEntryResponseDetails resp = new InwardEntryResponseDetails();
			resp.setInstructionId(result[0] != null ? Integer.parseInt(result[0].toString()) : null);
			resp.setInwardEntryId(result[1] != null ? Integer.parseInt(result[1].toString()) : null);
			resp.setCoilNumber(result[2] != null ? (String) result[2] : null);
			resp.setCustomerBatchId(result[3] != null ? (String) result[3] : null);
			resp.setMmId(result[4] != null ? (String) result[4] : null);
			resp.setProduct(result[5] != null ? (String) result[5] : null);
			resp.setMaterialSubGrade(result[6] != null ? (String) result[6] : null);
			resp.setFThickness(result[7] != null ? (float) result[7] : null);
			resp.setFLength(result[8] != null ? (float) result[8] : null);
			resp.setAvailQty(result[9] != null ? BigDecimal.valueOf(((Number) result[9]).doubleValue()) : null);
			resp.setLocationName(result[10] != null ? (String) result[10] : null);
			resp.setNoofPieces(result[11] != null ? ((Number) result[11]).intValue() : 0);
			resp.setFWidth(result[12] != null ? (float) result[12] : null);
			Integer coilage = result[13] != null ? Integer.parseInt(result[13].toString()) : null;
			resp.setAllocatedQty(result[14] != null ? BigDecimal.valueOf(((Number) result[14]).doubleValue()) : null);
			resp.setGrade(result[15] != null ? (String) result[15] : null);
			resp.setBrand(result[16] != null ? (String) result[16] : null);
			resp.setWarehouseId(result[17] != null ? (String) result[17] : null);
	        resp.setCoilage(coilage);
	        if (coilage != null) {
	        	coilAgeSet.add(coilage);  
	        }
	        list.add(resp);
		}

		if ("INWARDSHEET_PACKETS".equals(request.getAllocationType())) {
			request.setAllocationType("INWARDSHEET");
			packetsList1 = salesOrderService.findInventory(request);
			for (Object[] result : packetsList1) {
				InwardEntryResponseDetails resp = new InwardEntryResponseDetails();
				resp.setInstructionId(result[0] != null ? Integer.parseInt(result[0].toString()) : null);
				resp.setInwardEntryId(result[1] != null ? Integer.parseInt(result[1].toString()) : null);
				resp.setCoilNumber(result[2] != null ? (String) result[2] : null);
				resp.setCustomerBatchId(result[3] != null ? (String) result[3] : null);
				resp.setMmId(result[4] != null ? (String) result[4] : null);
				resp.setProduct(result[5] != null ? (String) result[5] : null);
				resp.setMaterialSubGrade(result[6] != null ? (String) result[6] : null);
				resp.setFThickness(result[7] != null ? (float) result[7] : null);
				resp.setFLength(result[8] != null ? (float) result[8] : null);
				resp.setAvailQty(result[9] != null ? BigDecimal.valueOf(((Number) result[9]).doubleValue()) : null);
				resp.setLocationName(result[10] != null ? (String) result[10] : null);
				resp.setNoofPieces(result[11] != null ? ((Number) result[11]).intValue() : 0);
				resp.setFWidth(result[12] != null ? (float) result[12] : null);
				Integer coilage = result[13] != null ? Integer.parseInt(result[13].toString()) : null;
				resp.setCoilage(coilage);
				resp.setAllocatedQty(result[14] != null ? BigDecimal.valueOf(((Number) result[14]).doubleValue()) : null);
				resp.setGrade(result[15] != null ? (String) result[15] : null);
				resp.setBrand(result[16] != null ? (String) result[16] : null);
				resp.setWarehouseId(result[17] != null ? (String) result[17] : null);

				if (coilage != null) {
					coilAgeSet.add(coilage);
				}
				list.add(resp);
			}
		}
		List<Integer> coilAgeList = new ArrayList<>(coilAgeSet);
		Collections.sort(coilAgeList);
		response.put("content", list);
		response.put("currentPage", packetsList1.getNumber());
		response.put("totalItems", packetsList1.getTotalElements());
		response.put("totalPages", packetsList1.getTotalPages());
		response.put("coilAgeList", coilAgeList);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/consolidateplanner/list", produces = "application/json")
	public ResponseEntity<Object> consolidateplannerListAllSOs(
			@RequestBody ListPageSearchRequest listPageSearchRequest) {

		Map<String, Object> response = new HashMap<>();
		
		Page<Object[]> packetsList1 = salesOrderService.listAllSOIDsCP(listPageSearchRequest);

		List<Integer> soIDsList = new ArrayList<>();
		for (Object[] row : packetsList1) {
			if (row[0] != null) {
				soIDsList.add((Integer) row[0]);
			}
		}
		
		boolean warehouseFlag = false;
		if (listPageSearchRequest.getWarehouseList() != null && listPageSearchRequest.getWarehouseList().size() > 0) {
			warehouseFlag = true;
		}
		
		if(listPageSearchRequest.getSoId() !=null && listPageSearchRequest.getSoId()>0 ) {
			warehouseFlag = false;
			soIDsList = new ArrayList<>();
			soIDsList.add(listPageSearchRequest.getSoId());
		}
		List<Object[]> packetsList = salesOrderService.listAllSOsCP(soIDsList, warehouseFlag,
				listPageSearchRequest.getWarehouseList());

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		Map<Integer, SalesOrderCPMainResponse> soMap = new LinkedHashMap<>();
		Map<Integer, SalesOrderCPChildResponse> soChildMap = new LinkedHashMap<>();

		for (Object[] result : packetsList) {

			Integer soId = result[0] != null ? Integer.parseInt(result[0].toString()) : null;

			/* ======================= SALES ORDER (PARENT) ======================== */
			SalesOrderCPMainResponse so = soMap.getOrDefault(soId, new SalesOrderCPMainResponse());

			if (!soMap.containsKey(soId)) {
				so.setSoId(soId);
				so.setSoNumber(result[1] != null ? (String) result[1] : null);
				so.setRefno(result[25] != null ? (String) result[25] : null);
				so.setExpectedDeliveryDate(result[2] != null ? sdf.format(result[2]) : null);
				so.setCustomerCode(result[3] != null ? (String) result[3] : null);
				so.setTotalQty(result[4] != null ? (BigDecimal) result[4] : null);
				so.setBranchId(result[26] != null ? (String) result[26] : null);
				so.setBranchName(result[27] != null ? (String) result[27] : null);
				so.setCpStatus(result[5] != null ? (String) result[5] : null);
				so.setPdfGenerationPart(result[28] != null ? (String) result[28] : null);
				soMap.put(soId, so);
			}

			/* ======================= SALES ORDER ITEM ======================== */
			boolean isNewChild = false;
			Integer soChildId = result[6] != null ? (Integer) result[6] : null;
			SalesOrderCPChildResponse child = soChildMap.getOrDefault(soChildId, new SalesOrderCPChildResponse());

			if (!soChildMap.containsKey(soChildId)) {
				child.setSoChildId(soChildId);
				child.setMmId((String) result[8]);
				child.setItemQty((BigDecimal) result[10]);
				child.setAllocatedSoqty((BigDecimal) result[11]);
				child.setAllocatedStts((String) result[12]);
				child.setItemStatus((String) result[13]);
				child.setMaterialDescription((String) result[14]);
				// child.setLocation( result[20] != null ? (String) result[20] : null);
				child.setWareHouseName(result[23] != null ? (String) result[23] : null);
				child.setWareHouseId(result[22] != null ? (String) result[22] : null);
				child.setMmidMeasurements(result[34] != null ? (String) result[34] : "");
				child.setBrandId( result[36] != null ? (Integer) result[36] : 0);
				soChildMap.put(soChildId, child);
				isNewChild = true;
			}

			/* ======================= ALLOCATION DETAILS ======================== */
			SalesOrderChildAllocationResponse allocation = new SalesOrderChildAllocationResponse();

			Integer soAllocationId = result[15] != null ? (Integer) result[15] : 0;

			allocation.setSoAllocationId(soAllocationId);
			allocation.setInstructionId(result[7] != null ? (Integer) result[7] : null);
			allocation.setInwardId(result[9] != null ? (Integer) result[9] : null);
			int formId = (result[35] != null ? Integer.parseInt(result[35].toString()) : 0);

			if (allocation.getInwardId() != null && allocation.getInwardId() > 0) {
				allocation.setAllocationType("RM");
			}
			if ((allocation.getInstructionId() != null && allocation.getInstructionId() > 0) || (formId == 21)) {
				allocation.setAllocationType("FG");
			}
			allocation.setAllocatedqty((BigDecimal) result[16]);
			allocation.setCoilNumber(result[17] != null ? (String) result[17] : "");
			allocation.setCustomerBatchId( result[29] != null ? (String) result[29] : "");
			allocation.setNoofPieces(result[18] != null ? ((Number) result[18]).intValue() : 0);
			allocation.setPacking(result[19] != null ? (String) result[19] : "");
			allocation.setLocationName(result[20] != null ? (String) result[20] : "");
			allocation.setStatus(result[21] != null ? (String) result[21] : "");
			allocation.setSize(result[24] != null ? (String) result[24] : "");
			
			String materialDesc = (result[30] != null ? (String) result[30] : "");
			String materialGrade = (result[31] != null ? (String) result[31] : "");
			allocation.setZohoSyncRemarks(result[32] != null ? (String) result[32] : "");
			allocation.setZohoSyncStts(result[33] != null ? (String) result[33] : "");

			String coilSKU = materialGrade + materialDesc + " * " + allocation.getSize();
			allocation.setProductDetails(coilSKU);

			if (soAllocationId != null && soAllocationId > 0) {
				child.getAllocationDetails().add(allocation);
			}
			/* ======================= ADD ITEM TO SO ONLY ONCE ======================== */
			if (isNewChild) {
				so.getItemslist().add(child);
			}
		}

		response.put("content", new ArrayList<>(soMap.values()));
		response.put("currentPage", packetsList1.getNumber());
		response.put("totalItems", packetsList1.getTotalElements());
		response.put("totalPages", packetsList1.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/cp/unallocate", produces = "application/json")
	public ResponseEntity<Object> unAllocate(@RequestBody SalesOrderChildAllocationResponse req) {
		return salesOrderService.unAllocate(req);
	}

	@PostMapping(value = "/post", produces = "application/json")
	public ResponseEntity<Object> post(@RequestBody SalesOrderExternalRequest salesOrderExternalRequest) {
		return salesOrderService.post(salesOrderExternalRequest, "post");
	}

	@PostMapping(value = "/update", produces = "application/json")
	public ResponseEntity<Object> update(@RequestBody SalesOrderExternalRequest salesOrderMainRequest) {
		return salesOrderService.update(salesOrderMainRequest);
	}

	@PostMapping(value = "/bulkaUpdate", produces = "application/json")
	public ResponseEntity<Object> soBulkApprove(@RequestBody SalesOrderBulkRequest request) {
		return salesOrderService.bulkUpdate(request);
	}
	@PostMapping("/pdf")
	public ResponseEntity<PdfResponseDto> downloadSOPDF(@RequestBody ListPageSearchRequest request) throws DocumentException {

	    File pdfFile = null;
	    try {
	        pdfFile = sopdfService.generatePdf(request);

	        if (pdfFile == null || !pdfFile.exists()) {
	            log.error("PDF generation failed or file not found for soId=" + request.getSoId());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }

	        byte[] bytes = Files.readAllBytes(pdfFile.toPath()); // read into memory first
	        String encodedFile = Base64.getEncoder().encodeToString(bytes);
	        return ResponseEntity.ok(new PdfResponseDto(encodedFile));

	    } catch (IOException ex) {
	        log.error("Error reading PDF file for soId=" + request.getSoId(), ex);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

	    } finally {
	        // Always runs — deletes file after bytes are safely in memory
	        if (pdfFile != null && pdfFile.exists()) {
	            boolean deleted = pdfFile.delete();
	            log.info("PDF deleted: " + deleted + " | path: " + pdfFile.getAbsolutePath());
	        }
	    }
	}

	@PostMapping(value = "/coil/allocationdetails", produces = "application/json")
	public ResponseEntity<Object> coilAllocationDetails(@RequestBody SalesOrderChildRequest request) {
		List<CoilAllocationDTO> packetsList = salesOrderService.coilAllocationDetails(request);
		return new ResponseEntity<>(packetsList, HttpStatus.OK);
	}

	@PostMapping({ "/allocatedcoils" })
	public ResponseEntity<Object> allocatedCoils(@RequestBody SearchListPageRequest searchListPageRequest) {
		Map<String, Object> response = new HashMap<>();
		log.info("in allocatedCoils ");
		Page<Object[]> packetsList1 = inwdEntrySvc.listAllocatedCoils(searchListPageRequest);
		Map<String, String> matDescMap = new HashMap<>();
		Map<Integer, String> inwardwiseSoMap = new HashMap<>();

		List<Integer> inwardIdList = new ArrayList<>();
		for (Object[] result : packetsList1) {
			Integer inwardId = (result[0] != null ? (Integer) result[0] : null);
			inwardIdList.add(inwardId);
			matDescMap.put((result[12] != null ? (String) result[12] : null), (result[13] != null ? (String) result[13] : null));
			inwardwiseSoMap.put(inwardId, (result[14] != null ? (String) result[14] : null));
		}
		log.info("In allocatedCoils === " + matDescMap);
		List<InwardEntry> pageResult = inwdEntrySvc.locationWiseListByInwardId(inwardIdList);
		List<InwardEntryResponseDto> inwardList = pageResult.stream().map(inw -> InwardEntry.valueOfResponseAllocatedPackets(inw, matDescMap,inwardwiseSoMap)).collect(Collectors.toList());
		response.put("content", inwardList);
		response.put("currentPage", packetsList1.getNumber());
		response.put("totalItems", packetsList1.getTotalElements());
		response.put("totalPages", packetsList1.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	@PostMapping("/dashboard")
	public ResponseEntity<Object> dashboard(@RequestBody SearchListPageRequest request) {

	    List<Object[]> resultList = salesOrderService.dashboard(request);
	    Map<String, SalesOrderDashboardResponse> dashboardMap = new LinkedHashMap<>();

		if (!resultList.isEmpty()) {
			Object[] result = resultList.get(0); // single row expected
			int cnt =  ((Number) result[0]).intValue();
			BigDecimal totalWeight = result[1] != null ? (BigDecimal) result[1] : BigDecimal.ZERO;
			dashboardMap.put("totalOrders", build(cnt, totalWeight));

			int cnt1 = ((Number) result[2]).intValue();
			BigDecimal totalWeight1 = result[3] != null ? (BigDecimal) result[3] : BigDecimal.ZERO;
			dashboardMap.put("approvedOrders", build(cnt1, totalWeight1));
		}

	    DashboardResponse response = new DashboardResponse();
	    response.setSuccess(true);
	    response.setMessage("Dashboard summary fetched successfully.");
	    response.setModule("SO");
	    response.setDashboard(dashboardMap);

	    return ResponseEntity.ok(response);
	}

	/**
	 * POST /api/jswone/reports/so-page-export/download
	 *
	 * Binary attachment. Preferable for a plain browser download since it avoids
	 * the ~33% base64 overhead entirely.
	 */
	@PostMapping("/download")
	public ResponseEntity<byte[]> exportDownload(@RequestBody(required = false) ListPageSearchRequest request) {

		byte[] bytes = soPageExportService.exportAsBytes(request != null ? request : new ListPageSearchRequest());
		String fileName = SoPageExportService.buildFileName();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(SoPageExportService.XLSX_CONTENT_TYPE));
		headers.setContentDispositionFormData("attachment", fileName);
		headers.setContentLength(bytes.length);
		// Exposed so a browser fetch() can read the filename back.
		headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
		headers.setCacheControl("no-store");
		return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
	}
	
	private SalesOrderDashboardResponse build(int value, BigDecimal totalWeight) {
		SalesOrderDashboardResponse res = new SalesOrderDashboardResponse();
		res.setTotalSOCount(value);
		res.setTotalWeight(totalWeight);
		return res;
	}
	private static final int SCALE = 2;

	private static BigDecimal toBigDecimal(Object value) {
	    if (value == null) {
	        return null;
	    }
	    BigDecimal bd;
	    if (value instanceof BigDecimal) {
	        bd = (BigDecimal) value;
	    } else if (value instanceof Number) {
	        bd = new BigDecimal(value.toString());
	    } else {
	        String s = value.toString().trim();
	        if (s.isEmpty()) {
	            return null;
	        }
	        bd = new BigDecimal(s);
	    }
	    return bd.setScale(SCALE, RoundingMode.HALF_UP);
	}
	
	private String formatDate(Object value) {
		if (value == null)
			return null;

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		if (value instanceof java.sql.Timestamp) {
			return sdf.format(new Date(((Timestamp) value).getTime()));
		}
		if (value instanceof java.sql.Date) {
			return sdf.format((java.sql.Date) value);
		}
		if (value instanceof java.util.Date) {
			return sdf.format((java.util.Date) value);
		}

		return value.toString(); // fallback
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Object> handleNotReadable(HttpMessageNotReadableException ex, ServletWebRequest webRequest) {

		HttpServletRequest request = webRequest.getRequest();
		String rawBody = "";
		try {
			rawBody = request.getReader().lines().collect(Collectors.joining("\n"));
		} catch (Exception ignore) {
			rawBody = "";
		}

		JswoneAuditTrailEntity audit = new JswoneAuditTrailEntity();
		try {
			audit.setProcessType("SO_POST");
			audit.setCreatedOn(new Date());
			audit.setRequestUrl(request.getRequestURI());
			audit.setRequestObj(rawBody);
			audit.setStatusCode("400");

			String msg = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
			String resp = "{\"code\":\"failure\",\"message\":\"Invalid request payload\",\"error\":\"" + safeJson(msg)
					+ "\"}";
			audit.setSourceRespone(resp);
			audit.setDestinationResponse("");

			jswoneAuditTrailRepository.save(audit);
		} catch (Exception ignore) {
			// empty
		}

		return new ResponseEntity<>("{\"code\":\"failure\",\"message\":\"Invalid request payload\"}",
				HttpStatus.BAD_REQUEST);
	}

	private String safeJson(String s) {
		if (s == null)
			return "";
		return s.replace("\"", "'").replace("\n", " ").replace("\r", " ");
	}

	
}
