package com.steel.product.application.controller;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lowagie.text.DocumentException;
import com.steel.product.application.dto.pdf.PdfResponseDto;
import com.steel.product.application.dto.quality.ListPageSearchRequest;
import com.steel.product.application.dto.salesorder.SalesOrderCreateDTO;
import com.steel.product.application.dto.salesorder.SalesOrderListDTO;
import com.steel.product.application.dto.salesorder.SalesOrderListResponse;
import com.steel.product.application.service.SalesOrderService;
import com.steel.product.application.util.CommonUtil;
import com.steel.product.trading.request.DeleteRequest;

@RestController
@CrossOrigin
@Tag(name = "Sales Order", description = "Sales Order")
@RequestMapping({ "/so" })
public class SalesOrderController {

	private SalesOrderService salesOrderService;

	private CommonUtil commonUtil;

	@Autowired
	public SalesOrderController(SalesOrderService salesOrderService, CommonUtil commonUtil) {
		this.salesOrderService = salesOrderService;
		this.commonUtil = commonUtil;
	}

	@PostMapping(value = "/allpackets", produces = "application/json")
	public ResponseEntity<Object> listAllPackets(@RequestBody ListPageSearchRequest listPageSearchRequest) {
		Map<String, Object> response = new HashMap<>();
		Page<Object[]> packetsList = salesOrderService.listAllPackets(listPageSearchRequest);
		Map<Integer, Integer> locationsMap = new HashMap<>();
		
		for (Object[] result : packetsList) {
			SalesOrderListResponse resp = new SalesOrderListResponse();
			resp.setPartyId(result[8] != null ? Integer.parseInt(result[8].toString()) : null);
			locationsMap.put(resp.getPartyId(), resp.getPartyId());
		}		
		System.out.println("locationsMap == "+locationsMap);
		Map<Integer, List<String>> locationWiseSOMap = salesOrderService.fetchMappedSOList(new ArrayList<>(locationsMap.values()));

		Map<Integer, SalesOrderListDTO> kk = new LinkedHashMap<>();
		for (Object[] result : packetsList) {
			SalesOrderListDTO resp = new SalesOrderListDTO();
			resp.setInstructionId(result[0] != null ? (Integer) result[0] : null);
			resp.setInwardEntryId(result[1] != null ? (Integer) result[1] : null);
			resp.setCoilNo(result[2] != null ? (String) result[2] : null);
			resp.setCustomerBatchNo(result[3] != null ? (String) result[3] : null);
			resp.setMaterialGrade(result[4] != null ? (String) result[4] : null);
			resp.setMaterialDesc(result[5] != null ? (String) result[5] : null);
			resp.setFthickness(result[6] != null ? (Float) result[6] : null);
			resp.setSoNumber(result[19] != null ? (String) result[19] : null);
			if (result[20] != null) {
				BigInteger ok = result[20] != null ? (BigInteger) result[20] : null;
				resp.setCustomerCodeId(ok.intValue());
			}
			resp.setSubGrade( result[21] != null ? (String) result[21] : null);
			resp.setBrand(result[22] != null ? (String) result[22] : null);
			resp.setInstructionDate( result[23] != null ? (Date) result[23] : null);

			Float dweight;
			Float dwidth;
			Float dlength;
			try {
				dweight = (result[7] != null ? (Float) result[7] : null);
				dwidth = (result[10] != null ? (Float) result[10] : null);
				dlength = (result[11] != null ? (Float) result[11] : null);
				resp.setFwidth(dwidth.floatValue());
				resp.setFweight(dweight.floatValue());
				resp.setFlenghth(dlength.floatValue());
			} catch (ClassCastException e) {
				Double dweight1 = (result[7] != null ? (Double) result[7] : null);
				Double dwidth1 = (result[10] != null ? (Double) result[10] : null);
				Double dlength1 = (result[11] != null ? (Double) result[11] : null);
				resp.setFwidth(dwidth1.floatValue());
				resp.setFweight(dweight1.floatValue());
				resp.setFlenghth(dlength1.floatValue());
			}
			resp.setPartyId(result[8] != null ? Integer.parseInt(result[8].toString()) : null);
			resp.setPartyName(result[9] != null ? (String) result[9] : null);
			resp.setMmid( result[24] != null ? (String) result[24] : null);
			resp.setMappedSOList(locationWiseSOMap.get(resp.getPartyId()));
			
			kk.put(resp.getInstructionId(), resp);
		}
		List<SalesOrderListDTO> qirList = new ArrayList<SalesOrderListDTO>(kk.values());
		response.put("content", qirList);
		response.put("currentPage", packetsList.getNumber());
		response.put("totalItems", packetsList.getTotalElements());
		response.put("totalPages", packetsList.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/create", produces = "application/json")
	public ResponseEntity<Object> save(@RequestBody List<SalesOrderCreateDTO> salesOrderPacketsList) {
		int userId = commonUtil.getUserId();
		List<SalesOrderCreateDTO> salesOrderPacketsListNew = new ArrayList<>();
		for (SalesOrderCreateDTO req : salesOrderPacketsList) {
			req.setUserId(userId);
			salesOrderPacketsListNew.add(req);
		}
		return salesOrderService.save(salesOrderPacketsListNew);
	}
	
	@PutMapping(value = "/update", produces = "application/json")
	public ResponseEntity<Object> update(@RequestBody List<SalesOrderCreateDTO> salesOrderPacketsList) {
		int userId = commonUtil.getUserId();
		List<SalesOrderCreateDTO> salesOrderPacketsListNew = new ArrayList<>();
		for (SalesOrderCreateDTO req : salesOrderPacketsList) {
			req.setUserId(userId);
			salesOrderPacketsListNew.add(req);
		}
		return salesOrderService.save(salesOrderPacketsListNew);
	}

	@PostMapping(value = "/list", produces = "application/json")
	public ResponseEntity<Object> listAllSOs(@RequestBody ListPageSearchRequest listPageSearchRequest) {
		Map<String, Object> response = new HashMap<>();
		
		Page<Object[]> packetsList1 = salesOrderService.listAllSOIDs(listPageSearchRequest);
		
		List<String> soIDsList  = new ArrayList<>(); 
		for (Object[] result : packetsList1) {
			String soId =  (result[0] != null ? (String) result[0] : null);
			soIDsList.add(soId);
		}
		List<Object[]> packetsList = salesOrderService.listAllSOs(soIDsList);
		Map<Integer, SalesOrderListResponse> soMap = new LinkedHashMap<>();
		for (Object[] result : packetsList) {
			SalesOrderListResponse resp = new SalesOrderListResponse();
			SalesOrderListDTO child = new SalesOrderListDTO();
			
			resp.setPartyId(result[8] != null ? Integer.parseInt(result[8].toString()) : null);
			resp.setPartyName(result[9] != null ? (String) result[9] : null);
			resp.setSoStatus( result[12] != null ? (String) result[12] : null);
			resp.setSoNumber(result[14] != null ? (String) result[14] : null);
			resp.setSoId(result[15] != null ? Integer.parseInt(result[15].toString()) : null);
			resp.setCustomerCode( result[18] != null ? (String) result[18] : null);

			child.setInstructionId(result[0] != null ? (Integer) result[0] : null);
			child.setInwardEntryId(result[1] != null ? (Integer) result[1] : null);
			child.setCoilNo(result[2] != null ? (String) result[2] : null);
			child.setCustomerBatchNo(result[3] != null ? (String) result[3] : null);
			child.setMaterialGrade(result[4] != null ? (String) result[4] : null);
			child.setMaterialDesc(result[5] != null ? (String) result[5] : null);
			child.setFthickness(result[6] != null ? (Float) result[6] : null);
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
			child.setPacketStatus( result[13] != null ? (String) result[13] : null);
			child.setPlannedNoofPieces(result[19] != null ? Integer.parseInt(result[19].toString()) : 0);
			child.setMmid(result[20] != null ? (String) result[20] : null);
			resp.getChildListResp().add(child);
			
			if (soMap != null && soMap.get(resp.getSoId()) != null) {
				SalesOrderListResponse addEntity = soMap.get(resp.getSoId());
				addEntity.getChildListResp().add(child);
				soMap.put(resp.getSoId(), addEntity);
			} else {
				soMap.put(resp.getSoId(), resp);
			}
		}		
		List<SalesOrderListResponse> list = new ArrayList<>(soMap.values());
		response.put("content", list);
		response.put("currentPage", packetsList1.getNumber());
		response.put("totalItems", packetsList1.getTotalElements());
		response.put("totalPages", packetsList1.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/mmidbyso", produces = "application/json")
	public ResponseEntity<Object> mmidBySO(@RequestBody ListPageSearchRequest listPageSearchRequest) {
		List<String> packetasList = salesOrderService.mmidBySO(listPageSearchRequest.getSoNo());
		return new ResponseEntity<Object>(packetasList, HttpStatus.OK);
	}

	@PostMapping(value = "/delete", produces = "application/json")
	public ResponseEntity<Object> delete(@RequestBody DeleteRequest deleteRequest) {
		deleteRequest.setUserId(commonUtil.getUserId());
		return salesOrderService.delete(deleteRequest);
	}

	@PostMapping(value = "/deletepacket", produces = "application/json")
	public ResponseEntity<Object> deletepacket(@RequestBody DeleteRequest deleteRequest) {
		deleteRequest.setUserId(commonUtil.getUserId());
		return salesOrderService.deletePackets(deleteRequest);
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
	
}
