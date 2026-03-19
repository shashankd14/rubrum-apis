package com.steel.product.jswone.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
import com.steel.product.application.dto.pdf.PdfResponseDto;
import com.steel.product.application.dto.quality.ListPageSearchRequest;
import com.steel.product.jswone.entity.JswoneAuditTrailEntity;
import com.steel.product.jswone.repository.JswoneAuditTrailRepository;
import com.steel.product.jswone.request.CPSplitRequest;
import com.steel.product.jswone.request.SalesOrderBulkRequest;
import com.steel.product.jswone.request.SalesOrderChildRequest;
import com.steel.product.jswone.request.SalesOrderExternalRequest;
import com.steel.product.jswone.request.SalesOrderMainRequest;
import com.steel.product.jswone.response.CoilAllocationDTO;
import com.steel.product.jswone.response.InwardEntryResponseDto;
import com.steel.product.jswone.response.SalesOrderCPChildResponse;
import com.steel.product.jswone.response.SalesOrderCPMainResponse;
import com.steel.product.jswone.response.SalesOrderChildAllocationResponse;
import com.steel.product.jswone.response.SalesOrderChildResponse;
import com.steel.product.jswone.response.SalesOrderMainResponse;
import com.steel.product.jswone.service.SalesOrderJswService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin
@Tag(name = "Sales Order", description = "Sales Order")
@RequestMapping({ "/salesorder" })
public class SalesOrderJswController {

	private SalesOrderJswService salesOrderService;

	@Autowired
	private JswoneAuditTrailRepository jswoneAuditTrailRepository;

	@Autowired
	public SalesOrderJswController(SalesOrderJswService salesOrderService) {
		this.salesOrderService = salesOrderService;
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
			String resp = "{\"code\":\"failure\",\"message\":\"Invalid request payload\",\"error\":\"" + safeJson(msg) + "\"}";
			audit.setSourceRespone(resp);
			audit.setDestinationResponse("");

			jswoneAuditTrailRepository.save(audit);
		} catch (Exception ignore) {
			//empty
		}

		return new ResponseEntity<>(
				"{\"code\":\"failure\",\"message\":\"Invalid request payload\"}",
				HttpStatus.BAD_REQUEST
		);
	}

	private String safeJson(String s) {
		if (s == null) return "";
		return s.replace("\"", "'").replace("\n", " ").replace("\r", " ");
	}

	@PostMapping(value = "/create", produces = "application/json")
	public ResponseEntity<Object> save(@RequestBody SalesOrderMainRequest salesOrderMainRequest) {
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

			resp.setDeliverymethod( result[3] != null ? (String) result[3] : null);
			resp.setDestinationcode( result[4] != null ? (String) result[4] : null);
			resp.setRefno( result[5] != null ? (String) result[5] : null);
			resp.setJoplsorefno( result[6] != null ? (String) result[6] : null);
			resp.setBizsegment( result[7] != null ? (String) result[7] : null);
			resp.setEcommerce( result[8] != null ? (String) result[8] : null);
			resp.setSupplysource( result[9] != null ? (String) result[9] : null);
			resp.setTypeofsupply( result[10] != null ? (String) result[10] : null);
			resp.setIncomingpayment( result[11] != null ? (String) result[11] : null);
			resp.setPaymentmode( result[12] != null ? (String) result[12] : null);
			resp.setTerms( result[13] != null ? (String) result[13] : null);
			resp.setCustomerCode( result[14] != null ? (String) result[14] : null);
			resp.setTotalSoqty( result[15] != null ? (BigDecimal) result[15] : null);
			resp.setTotalAllocatedSoqty( result[16] != null ? (BigDecimal) result[16] : null);
			resp.setAllocatedStts( result[17] != null ? (String) result[17] : null);
			resp.setSoStatus( result[18] != null ? (String) result[18] : null);

			resp.setZbooks_so(result[19] != null ? (String) result[19] : null);
			resp.setExpected_delivery_date(formatDate(result[20]));
			resp.setLikely_material_date(formatDate(result[21]));
			resp.setStandard_material_date(formatDate(result[22]));

			child.setSoChildId(result[23] != null ? (Integer) result[23] : null);
			child.setMmId(result[24] != null ? (String) result[24] : null);
			child.setSoqty(result[27] != null ? (BigDecimal) result[27] : null);
			child.setAllocatedSoqty(result[28] != null ? (BigDecimal) result[28] : null);
			child.setAllocatedStts(result[29] != null ? (String) result[29] : null);
			child.setItemStatus(result[30] != null ? (String) result[30] : null);
			child.setWearhouse_id(result[31] != null ? (String) result[31] : null);
			child.setTax(result[32] != null ? (String) result[32] : null);

			child.setMm_description(result[33] != null ? (String) result[33] : null);
			child.setHsn(result[34] != null ? String.valueOf(result[34]) : null);
			child.setWare_house_name(result[35] != null ? (String) result[35] : null);
			resp.setBranch(result[36] != null ? (String) result[36] : null);
			resp.setCam_code(result[37] != null ? (String) result[37] : null);
			resp.setRemarks(result[38] != null ? (String) result[38] : null);
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
	public ResponseEntity<Object> findInventory(@RequestBody ListPageSearchRequest listPageSearchRequest) {
		Map<String, Object> response = new HashMap<>();
		listPageSearchRequest.setPageSize(100);
		Page<Object[]> packetsList1 = salesOrderService.findInventory(listPageSearchRequest);
		List<InwardEntryResponseDto> list = new ArrayList<>();

		for (Object[] result : packetsList1) {
			InwardEntryResponseDto resp = new InwardEntryResponseDto();
			resp.setInstructionId(result[0] != null ? Integer.parseInt(result[0].toString()) : null);
			resp.setInwardEntryId(result[1] != null ? Integer.parseInt(result[1].toString()) : null);
			resp.setCoilNumber(result[2] != null ? (String) result[2] : null);
			resp.setCustomerBatchId(result[3] != null ? (String) result[3] : null);
			resp.setMmId(result[4] != null ? (String) result[4] : null);
			resp.setMaterial(result[5] != null ? (String) result[5] : null);
			resp.setMaterialGrade(result[6] != null ? (String) result[6] : null);
			resp.setFThickness(result[7] != null ? (float) result[7] : null);
			resp.setFLength(result[8] != null ? (float) result[8] : null);
			resp.setAvailQty(result[9] != null ? BigDecimal.valueOf(((Number) result[9]).doubleValue()) : null);
			resp.setLocationName(result[10] != null ? (String) result[10] : null);
			list.add(resp);
		}
		
		if ("INWARDSHEET_PACKETS".equals(listPageSearchRequest.getAllocationType())) {
			listPageSearchRequest.setAllocationType("INWARDSHEET");
			packetsList1 = salesOrderService.findInventory(listPageSearchRequest);
			for (Object[] result : packetsList1) {
				InwardEntryResponseDto resp = new InwardEntryResponseDto();
				resp.setInstructionId(result[0] != null ? Integer.parseInt(result[0].toString()) : null);
				resp.setInwardEntryId(result[1] != null ? Integer.parseInt(result[1].toString()) : null);
				resp.setCoilNumber(result[2] != null ? (String) result[2] : null);
				resp.setCustomerBatchId(result[3] != null ? (String) result[3] : null);
				resp.setMmId(result[4] != null ? (String) result[4] : null);
				resp.setMaterial(result[5] != null ? (String) result[5] : null);
				resp.setMaterialGrade(result[6] != null ? (String) result[6] : null);
				resp.setFThickness(result[7] != null ? (float) result[7] : null);
				resp.setFLength(result[8] != null ? (float) result[8] : null);
				resp.setAvailQty(result[9] != null ? BigDecimal.valueOf(((Number) result[9]).doubleValue()) : null);
				resp.setLocationName(result[10] != null ? (String) result[10] : null);
				list.add(resp);
			}
		}
		
		response.put("content", list);
		response.put("currentPage", packetsList1.getNumber());
		response.put("totalItems", packetsList1.getTotalElements());
		response.put("totalPages", packetsList1.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/consolidateplanner/list", produces = "application/json")
	public ResponseEntity<Object> consolidateplannerListAllSOs(@RequestBody ListPageSearchRequest listPageSearchRequest) {

	    Map<String, Object> response = new HashMap<>();

	    listPageSearchRequest.getStatus().add("SO_APPROVED");
	    Page<Object[]> packetsList1 = salesOrderService.listAllSOIDsCP(listPageSearchRequest);

	    List<Integer> soIDsList = new ArrayList<>();
	    for (Object[] row : packetsList1) {
	        if (row[0] != null) {
	            soIDsList.add((Integer) row[0]);
	        }
	    }

	    List<Object[]> packetsList = salesOrderService.listAllSOsCP(soIDsList);

	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	    Map<Integer, SalesOrderCPMainResponse> soMap = new LinkedHashMap<>();
	    Map<Integer, SalesOrderCPChildResponse> soChildMap = new LinkedHashMap<>();

	    for (Object[] result : packetsList) {

	        Integer soId = result[0] != null ? Integer.parseInt(result[0].toString()) : null;

	        /* =======================	SALES ORDER (PARENT)	======================== */
	        SalesOrderCPMainResponse so =  soMap.getOrDefault(soId, new SalesOrderCPMainResponse());

			if (!soMap.containsKey(soId)) {
				so.setSoId(soId);
				so.setSoNumber(result[1] != null ? (String) result[1] : null);
				so.setExpectedDeliveryDate(result[2] != null ? sdf.format(result[2]) : null);
				so.setCustomerCode(result[3] != null ? (String) result[3] : null);
				so.setTotalQty(result[4] != null ? (BigDecimal) result[4] : null);
				so.setCpStatus(result[5] != null ? (String) result[5] : null);
				so.setBranchId( result[1] != null ? (String) result[1] : null);
				so.setBranchName( result[23] != null ? (String) result[23] : null);
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
	            child.setMaterialDescription( (String) result[14]);
	            child.setLocation( result[20] != null ? (String) result[20] : null);
	            child.setWareHouseName(result[22] != null ? (String) result[22] : null);
	            soChildMap.put(soChildId, child);
	            isNewChild = true;
	        }

			/* ======================= ALLOCATION DETAILS ======================== */
	        SalesOrderChildAllocationResponse allocation =  new SalesOrderChildAllocationResponse();

	        Integer soAllocationId = result[15] != null ? (Integer) result[15] : 0;

	        allocation.setSoAllocationId(soAllocationId);
			allocation.setInstructionId(result[7] != null ? (Integer) result[7] : null);
			allocation.setInwardId(result[9] != null ? (Integer) result[9] : null);
			allocation.setAllocatedqty((BigDecimal) result[16]);
			allocation.setCoilNumber(result[17] != null ? (String) result[17] : "");
			allocation.setNoofPieces(result[18] != null ? ((Number) result[18]).intValue() : 0);
			allocation.setPacking(result[19] != null ? (String) result[19] : "");
			allocation.setLocationName(result[20] != null ? (String) result[20] : "");
			allocation.setStatus(result[21] != null ? (String) result[21] : "");
			allocation.setSize("");

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
		return salesOrderService.unAllocate(req );
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
	public ResponseEntity<Object> soBulkApprove(
			@RequestBody SalesOrderBulkRequest request) {
		return salesOrderService.bulkUpdate(request);
	}

	@PostMapping("/pdf")
	public ResponseEntity<PdfResponseDto> downloadDeliveryPDF(@RequestBody ListPageSearchRequest request) throws DocumentException {
		Path file = null;
		byte[] bytes = null;
		StringBuilder builder = new StringBuilder();
		try {

			file = Paths.get(salesOrderService.generatePdf(request).getAbsolutePath());
			bytes = Files.readAllBytes(file);
			builder.append(Base64.getEncoder().encodeToString(bytes));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		String encodedFile = builder.toString();
		return new ResponseEntity<>(new PdfResponseDto(encodedFile), HttpStatus.OK);
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

	@PostMapping(value = "/coil/allocationdetails", produces = "application/json")
	public ResponseEntity<Object> coilAllocationDetails(@RequestBody SalesOrderChildRequest request) {
		List<CoilAllocationDTO> packetsList = salesOrderService.coilAllocationDetails(request);
		return new ResponseEntity<>(packetsList, HttpStatus.OK);
	}
}
