package com.steel.product.application.service;

import com.lowagie.text.DocumentException;
import com.steel.product.application.dao.SalesOrderChildRepository;
import com.steel.product.application.dao.SalesOrderRepository;
import com.steel.product.application.dto.delivery.DeliveryItemDetails;
import com.steel.product.application.dto.quality.ListPageSearchRequest;
import com.steel.product.application.dto.salesorder.SalesOrderCreateDTO;
import com.steel.product.application.dto.salesorder.SalesOrderListDTO;
import com.steel.product.application.dto.salesorder.SalesOrderListResponse;
import com.steel.product.application.entity.*;
import com.steel.product.application.util.CommonUtil;
import com.steel.product.trading.request.DeleteRequest;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;

@Service
@Log4j2
public class SalesOrderServiceImpl implements SalesOrderService {

	private SalesOrderRepository salesOrderRepository;

	private SalesOrderChildRepository childRepository;

	private CommonUtil commonUtil;

	private StatusService statusService;

	private SpringTemplateEngine templateEngine;
	
	@Autowired
	public SalesOrderServiceImpl(SalesOrderRepository salesOrderRepository, CommonUtil commonUtil,
			StatusService statusService, SalesOrderChildRepository childRepository,
			SpringTemplateEngine templateEngine) {
		this.childRepository = childRepository;
		this.salesOrderRepository = salesOrderRepository;
		this.commonUtil = commonUtil;
		this.statusService = statusService;
		this.templateEngine = templateEngine;
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
		Map<Integer, SalesOrderListDTO> kk = new LinkedHashMap<>();
		for (Object[] result : packetsList) {
			cnt++;
			String sono = result[0] != null ? (String) result[0] : null;
		}
		return cnt;
	}

	@Override
	public SalesOrderListResponse getSoNoAndCustCode(List<Integer> list) {
		SalesOrderListResponse resp = new SalesOrderListResponse();
		List<Object[]> packetsList = salesOrderRepository.validateSoNoAndCustCode(list);
		for (Object[] result : packetsList) {
			resp.setSoNumber(result[0] != null ? (String) result[0] : null);
			resp.setCustomerCode(result[2] != null ? (String) result[2] : null);
		}
		return resp;
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
			resp.setProcessCenter("RAMESHWAR COIL CUTTER LLP, Ahmedabad");

			child.setInstructionId(result[0] != null ? (Integer) result[0] : null);
			child.setInwardEntryId(result[1] != null ? (Integer) result[1] : null);
			child.setPlannedNoofPieces( result[21] != null ? (Integer) result[21] : null);
			child.setCoilNo(result[2] != null ? (String) result[2] : null);
			child.setCoilSKU("MS HR Coil E250A 2x1250");
			child.setProcessing("CTL");
			child.setFinalProcessingSKU("MS HR Sheet 2062:2011 E250A 2x1250x1710");
			child.setPackingMode("Loose Bundle");
			child.setSpecilaInstructions("Loose Bundle");
			child.setDiagonal("Max. 3.00");
			child.setEdgeBurr("Max 3% of Thick");
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
			dweightTotal=dweightTotal+child.getFweight();
			child.setPacketStatus(result[13] != null ? (String) result[13] : null);
			resp.getChildListResp().add(child);
		}
		resp.setFweightTotal(dweightTotal.floatValue());
		context.setVariable("soDetails", resp);
		return context;
	}

	private String loadAndFillSOTemplate(Context context) {
		return templateEngine.process("so_view", context);
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
