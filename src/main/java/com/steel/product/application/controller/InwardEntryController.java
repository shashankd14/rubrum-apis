package com.steel.product.application.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.steel.product.application.dto.delivery.DeliveryPDFRequestDTO;
import com.steel.product.application.dto.inward.EndUserTagWisePacketsDTO;
import com.steel.product.application.dto.inward.InwardDto;
import com.steel.product.application.dto.inward.SearchListPageRequest;
import com.steel.product.application.entity.InwardDoc;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.service.AWSS3Service;
import com.steel.product.application.service.EndUserTagsService;
import com.steel.product.application.service.InwardDocService;
import com.steel.product.application.service.InwardEntryService;
import com.steel.product.application.service.LocationMasterService;
import com.steel.product.application.service.MaterialDescriptionService;
import com.steel.product.application.service.MaterialGradeService;
import com.steel.product.application.service.PacketClassificationService;
import com.steel.product.application.service.PartyDetailsService;
import com.steel.product.application.service.StatusService;
import com.steel.product.application.service.UserService;
import com.steel.product.application.util.CommonUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;

@RestController
@CrossOrigin
@Tag(name = "Inward Entry", description = "Inward Entry")
@RequestMapping({ "/inwardEntry" })
@Log4j2
public class InwardEntryController {

	private InwardEntryService inwdEntrySvc;

	private PartyDetailsService partyDetailsService;

	private LocationMasterService locationMasterService;

	private PacketClassificationService packetClassificationService;

	private EndUserTagsService endUserTagsService;

	private StatusService statusService;

	private MaterialDescriptionService matDescService;

	private MaterialGradeService matGradeService;

	private AWSS3Service awsS3Service;

	private CommonUtil commonUtil;

	private InwardDocService inwardDocService;

	private Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	@Autowired
	public InwardEntryController(InwardEntryService inwdEntrySvc, PartyDetailsService partyDetailsService,
			StatusService statusService, MaterialDescriptionService matDescService,
			MaterialGradeService matGradeService, UserService userSerive, AWSS3Service awsS3Service,
			InwardDocService inwardDocService, CommonUtil commonUtil, LocationMasterService locationMasterService,
			PacketClassificationService packetClassificationService, EndUserTagsService endUserTagsService) {
		this.inwdEntrySvc = inwdEntrySvc;
		this.partyDetailsService = partyDetailsService;
		this.statusService = statusService;
		this.matDescService = matDescService;
		this.matGradeService = matGradeService;
		this.awsS3Service = awsS3Service;
		this.inwardDocService = inwardDocService;
		this.commonUtil = commonUtil;
		this.locationMasterService = locationMasterService;
		this.packetClassificationService = packetClassificationService;
		this.endUserTagsService = endUserTagsService;
	}

	@PostMapping("/addNew")
	public ResponseEntity<Object> saveInwardEntry(@Valid @ModelAttribute InwardDto inwarddto, HttpServletRequest request) {
		InwardEntry inwardEntry = new InwardEntry();
		System.out.println("DTO details " + inwarddto);
		log.info("Inside saveInwardEntry ");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			int userId = commonUtil.getUserId();
			inwardEntry.setInwardEntryId(0);
			inwardEntry.setPurposeType(inwarddto.getPurposeType());
			boolean isPresent = this.inwdEntrySvc.isCoilNumberPresent(inwarddto.getCoilNumber());
			if(isPresent) {
				log.error("duplicate coil number ");
				return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered Coil Number already exists\"}", headers, HttpStatus.BAD_REQUEST);
			}
			inwardEntry.setParty(this.partyDetailsService.getPartyById(inwarddto.getPartyId()));
			if (inwarddto != null && inwarddto.getLocationId() > 0) {
				log.info("inwarddto.getLocationId() == " + inwarddto.getLocationId());
				inwardEntry.setLocation(this.locationMasterService.getByLocationId(inwarddto.getLocationId()));
			}
			if (inwarddto != null && inwarddto.getPacketClassificationId() > 0) {
				log.info("inwarddto.getPacketClassificationId() == " + inwarddto.getPacketClassificationId());
				inwardEntry.setPacketClassification(this.packetClassificationService.getPacketClassificationById(inwarddto.getPacketClassificationId()));
			}

			if (inwarddto != null && inwarddto.getEndUserTagId() > 0) {
				log.info("inwarddto.getEndUserTagId() == " + inwarddto.getEndUserTagId());
				inwardEntry.setEndUserTagsEntity(this.endUserTagsService.getEndUserTagsById(inwarddto.getEndUserTagId()));
			}
			inwardEntry.setNoofpieces( inwarddto.getNoofpieces());
			
			if (inwarddto.getPresentWeight() <= 0) {
				log.error("inwarddto.getPresentWeight() is invalid");
				return new ResponseEntity<Object>("Invalid present weight entered.", headers, HttpStatus.BAD_REQUEST);
			}
			inwardEntry.setCoilNumber(inwarddto.getCoilNumber());
			inwardEntry.setBatchNumber(inwarddto.getBatchNumber());
			inwardEntry.setInwardType(inwarddto.getInwardType());
			inwardEntry.setdReceivedDate(Timestamp.valueOf(inwarddto.getInwardDate()));
			if (inwarddto.getPresentWeight() <= 0) {
				log.info("Invalid present weight entered.");
				return new ResponseEntity<Object>("Invalid present weight entered.", headers, HttpStatus.BAD_REQUEST);
			}
			inwardEntry.setInStockWeight(inwarddto.getPresentWeight());

			if (inwarddto.getBillDate() != null)
				inwardEntry.setdBillDate(Timestamp.valueOf(inwarddto.getBillDate()));

			inwardEntry.setvLorryNo(inwarddto.getVehicleNumber());
			inwardEntry.setvInvoiceNo(inwarddto.getInvoiceNumber());
			inwardEntry.setdInvoiceDate(Timestamp.valueOf(inwarddto.getInvoiceDate()));

			inwardEntry.setCustomerCoilId(inwarddto.getCustomerCoilId());
			inwardEntry.setCustomerInvoiceNo(inwarddto.getCustomerInvoiceNo());
			inwardEntry.setCustomerBatchId(inwarddto.getCustomerBatchId());

			inwardEntry.setMaterial(this.matDescService.getMatById(inwarddto.getMaterialId()));
			inwardEntry.setMaterialGrade(matGradeService.getById(inwarddto.getMaterialGradeId()));
			
			inwardEntry.setfWidth(inwarddto.getWidth());
			inwardEntry.setfThickness(inwarddto.getThickness());
			inwardEntry.setfLength(inwarddto.getLength());
			inwardEntry.setAvailableLength(inwarddto.getLength());
			inwardEntry.setfQuantity(inwarddto.getPresentWeight());
			inwardEntry.setGrossWeight(inwarddto.getGrossWeight());

			if("Coil".equals(inwarddto.getInwardType())) {
				inwardEntry.setStatus(this.statusService.getStatusById(1));
			} else {
				inwardEntry.setStatus(this.statusService.getStatusById(1));
			}

			inwardEntry.setvProcess(inwarddto.getProcess());
			inwardEntry.setTdcNo(inwarddto.getTdcNo());
			inwardEntry.setFpresent(inwarddto.getPresentWeight());
			inwardEntry.setValueOfGoods(inwarddto.getValueOfGoods());

			inwardEntry.setBilledweight(0);
			inwardEntry.setParentCoilNumber(null);
			inwardEntry.setvParentBundleNumber(0);

			inwardEntry.setRemarks(inwarddto.getRemarks());

			inwardEntry.setIsDeleted(Boolean.valueOf(false));
			inwardEntry.setCreatedOn(this.timestamp);
			inwardEntry.setUpdatedOn(this.timestamp);
			inwardEntry.setCreatedBy(userId);
			inwardEntry.setUpdatedBy(userId);

			if (inwarddto.getTestCertificateFile() != null) {

				String fileUrl = awsS3Service.uploadFile(inwarddto.getTestCertificateFile());
				inwardEntry.setTestCertificateFileUrl(fileUrl);
			}
			inwardEntry.setTestCertificateNumber(inwarddto.getTestCertificateNumber());
			InwardEntry savedInwardEntry = inwdEntrySvc.saveEntry(inwardEntry);

			if (inwarddto.getInwardFiles() != null) {
				for (MultipartFile file : inwarddto.getInwardFiles()) {
					InwardDoc inwardDoc = new InwardDoc();
					inwardDoc.setInwardEntry(inwardEntry);
					String str = awsS3Service.uploadFile(file);
					inwardDoc.setDocUrl(str);
					inwardDocService.save(inwardDoc);
				}
			}
			return new ResponseEntity<Object>(InwardEntry.valueOfResponse(inwardEntry), headers, HttpStatus.OK);
		} catch (Exception e) {
			log.info("e.getMessage() == " + e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<Object>(e.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping({ "/update" })
	public ResponseEntity<Object> updateEntry(@RequestBody InwardDto inwarddto, HttpServletRequest request) {
		InwardEntry inwardEntry = new InwardEntry();
		System.out.println("DTO details " + inwarddto);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			int userId = commonUtil.getUserId();
			inwardEntry = inwdEntrySvc.getByEntryId(inwarddto.getInwardId());
			inwardEntry.setPurposeType(inwarddto.getPurposeType());
			inwardEntry.setParty(this.partyDetailsService.getPartyById(inwarddto.getPartyId()));
			if (inwarddto != null && inwarddto.getLocationId() > 0) {
				inwardEntry.setLocation(this.locationMasterService.getByLocationId(inwarddto.getLocationId()));
			}
			if (inwarddto != null && inwarddto.getPacketClassificationId() > 0) {
				log.info("inwarddto.getPacketClassificationId() == " + inwarddto.getPacketClassificationId());
				inwardEntry.setPacketClassification(this.packetClassificationService.getPacketClassificationById(inwarddto.getPacketClassificationId()));
			}

			if (inwarddto != null && inwarddto.getEndUserTagId() > 0) {
				log.info("inwarddto.getEndUserTagId() == " + inwarddto.getEndUserTagId());
				inwardEntry.setEndUserTagsEntity(this.endUserTagsService.getEndUserTagsById(inwarddto.getEndUserTagId()));
			}
			inwardEntry.setNoofpieces( inwarddto.getNoofpieces());
			inwardEntry.setCoilNumber(inwarddto.getCoilNumber());
			inwardEntry.setBatchNumber(inwarddto.getBatchNumber());
			inwardEntry.setdReceivedDate(Timestamp.valueOf(inwarddto.getInwardDate()));
			if (inwarddto.getBillDate() != null)
				inwardEntry.setdBillDate(Timestamp.valueOf(inwarddto.getBillDate()));

			inwardEntry.setvLorryNo(inwarddto.getVehicleNumber());
			inwardEntry.setvInvoiceNo(inwarddto.getInvoiceNumber());
			inwardEntry.setdInvoiceDate(Timestamp.valueOf(inwarddto.getInvoiceDate()));
			inwardEntry.setInwardType( inwarddto.getInwardType() );
			inwardEntry.setCustomerCoilId(inwarddto.getCustomerCoilId());
			inwardEntry.setCustomerBatchId(inwarddto.getCustomerBatchId());

			inwardEntry.setMaterial(this.matDescService.getMatById(inwarddto.getMaterialId()));
			inwardEntry.setMaterialGrade(matGradeService.getById(inwarddto.getMaterialGradeId()));

			inwardEntry.setfWidth(inwarddto.getWidth());
			inwardEntry.setfThickness(inwarddto.getThickness());
			inwardEntry.setfLength(inwarddto.getLength());
			inwardEntry.setfQuantity(inwarddto.getPresentWeight());
			inwardEntry.setGrossWeight(inwarddto.getGrossWeight());
			inwardEntry.setStatus(this.statusService.getStatusById(inwarddto.getStatusId()));
			
			inwardEntry.setvProcess(inwarddto.getProcess());
			if (inwarddto.getTdcNo() != null && inwarddto.getTdcNo().length() > 0) {
				inwardEntry.setTdcNo(inwarddto.getTdcNo());
			}
			inwardEntry.setFpresent(inwarddto.getPresentWeight());
			inwardEntry.setBilledweight(0);
			inwardEntry.setParentCoilNumber(null);
			inwardEntry.setvParentBundleNumber(0);
			inwardEntry.setIsDeleted(Boolean.valueOf(false));
			inwardEntry.setUpdatedBy(userId);

			if (inwarddto.getTestCertificateFile() != null) {

				String fileUrl = awsS3Service.uploadFile(inwarddto.getTestCertificateFile());
				inwardEntry.setTestCertificateFileUrl(fileUrl);
			}
			inwardEntry.setTestCertificateNumber(inwarddto.getTestCertificateNumber());
			inwdEntrySvc.saveEntry(inwardEntry);

			if (inwarddto.getInwardFiles() != null) {

				for (MultipartFile file : inwarddto.getInwardFiles()) {

					InwardDoc inwardDoc = new InwardDoc();
					inwardDoc.setInwardEntry(inwardEntry);
					String str = awsS3Service.uploadFile(file);
					inwardDoc.setDocUrl(str);

					System.out.println("inwardDoc: " + inwardDoc);
					inwardDocService.save(inwardDoc);
				}
			}
			return new ResponseEntity<Object>("success", headers,HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(e.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//  This is for Partywise Register
	@PostMapping({ "/partywiselist" })
	public ResponseEntity<Object> partywiselist(@RequestBody SearchListPageRequest searchListPageRequest) {
		Map<String, Object> response = new HashMap<>();

		if ("ENDUSER".equals(searchListPageRequest.getLoginType())) {
			Page<Object[]> pageResult = inwdEntrySvc.partywiselistEndUserTagWise(searchListPageRequest);
			List<EndUserTagWisePacketsDTO> responseList = new ArrayList<>();
			for (Object[] result : pageResult) {
				EndUserTagWisePacketsDTO dto = new EndUserTagWisePacketsDTO();
				dto.setCoilNumber(result[0] != null ? (String) result[0] : null);
				dto.setCustomerBatchId(result[1] != null ? (String) result[1] : null);
				dto.setMaterialDesc(result[2] != null ? (String) result[2] : null);
				dto.setMaterialGrade(result[3] != null ? (String) result[3] : null);
				dto.setThickness(result[4] != null ? (Float) result[4] : null);
				dto.setWidth(result[5] != null ? (Float) result[5] : null);
				dto.setLength(result[6] != null ? (Float) result[6] : null);
				dto.setClassificationTag(result[7] != null ? (String) result[7] : null);
				dto.setEndUserTagName(result[8] != null ? (String) result[8] : null);
				dto.setInwardStatus(result[9] != null ? (String) result[9] : null);
				dto.setPacketStatus(result[10] != null ? (String) result[10] : null);
				responseList.add(dto);
			}
			response.put("content", responseList);
			response.put("currentPage", pageResult.getNumber());
			response.put("totalItems", pageResult.getTotalElements());
			response.put("totalPages", pageResult.getTotalPages());
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} else {
			Page<InwardEntry> pageResult = inwdEntrySvc.partywiselist(searchListPageRequest);
			List<Object> inwardList = pageResult.stream().map(inw -> InwardEntry.valueOfResponse(inw))
					.collect(Collectors.toList());
			response.put("content", inwardList);
			response.put("currentPage", pageResult.getNumber());
			response.put("totalItems", pageResult.getTotalElements());
			response.put("totalPages", pageResult.getTotalPages());
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	//  This is for Inward List
	@GetMapping({ "/list/{pageNo}/{pageSize}" })
	public ResponseEntity<Object> findAllWithPagination(@PathVariable int pageNo, @PathVariable int pageSize,
			@RequestParam(required = false, name = "searchText") String searchText,
			@RequestParam(required = false, name = "partyId") String partyId) {
		SearchListPageRequest searchListPageRequest = new SearchListPageRequest();
		searchListPageRequest.setPageNo(pageNo);
		searchListPageRequest.setPageSize(pageSize);
		searchListPageRequest.setSearchText(searchText);
		searchListPageRequest.setPartyId(partyId);

		Map<String, Object> response = new HashMap<>();
		Page<InwardEntry> pageResult = inwdEntrySvc.partywiselist(searchListPageRequest);
		List<Object> inwardList = pageResult.stream().map(inw -> InwardEntry.valueOfResponse(inw))
				.collect(Collectors.toList());
		response.put("content", inwardList);
		response.put("currentPage", pageResult.getNumber());
		response.put("totalItems", pageResult.getTotalElements());
		response.put("totalPages", pageResult.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	//  This is for WIP List
	@GetMapping({ "/wiplist/{pageNo}/{pageSize}" })
	public ResponseEntity<Object> findAllWIPlistWithPagination(@PathVariable int pageNo, @PathVariable int pageSize,
			@RequestParam(required = false, name = "searchText") String searchText,
			@RequestParam(required = false, name = "partyId") String partyId) {

		Map<String, Object> response = new HashMap<>();
		Page<InwardEntry> pageResult = inwdEntrySvc.findAllWIPlistWithPagination(pageNo, pageSize, searchText, partyId);
		List<Object> inwardList = pageResult.stream().map(inw -> InwardEntry.valueOfResponse(inw))
				.collect(Collectors.toList());
		response.put("content", inwardList);
		response.put("currentPage", pageResult.getNumber());
		response.put("totalItems", pageResult.getTotalElements());
		response.put("totalPages", pageResult.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
 
	@GetMapping({ "/pwr/list" })
	public ResponseEntity<Object> findAllEntriesPwr() {
		try {
			List<InwardEntry> inwardEntries = inwdEntrySvc.getAllEntriesPwr();
			return new ResponseEntity<Object>(
					inwardEntries.stream().map(inw -> InwardEntry.valueOfResponse(inw)).collect(Collectors.toList()),
					HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping({ "/getById/{inwardEntryId}" })
	public ResponseEntity<Object> getById(@PathVariable int inwardEntryId) {
		try {
			InwardEntry entry = this.inwdEntrySvc.getByEntryId(inwardEntryId);
			if (entry == null)
				throw new RuntimeException("Entry id not found - " + inwardEntryId);
			return new ResponseEntity<Object>(InwardEntry.valueOfResponse(entry), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping({ "/getByCoilId/{coilNumber}" })
	public ResponseEntity<Object> getById(@PathVariable String coilNumber) {
		try {
			InwardEntry entry = this.inwdEntrySvc.getByCoilNumber(coilNumber.replace("\n", ""));
			if (entry == null)
				throw new RuntimeException("Entry coilNumber id not found - " + coilNumber);
			return new ResponseEntity<Object>(InwardEntry.valueOfResponse(entry), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping({ "/getByPartyId/{partyId}" })
	public ResponseEntity<Object> getInwardEntriesByPartyId(@PathVariable int partyId) {
		try {
			ResponseEntity<Object> entry = this.inwdEntrySvc.getInwardEntriesByPartyId(partyId);
			return new ResponseEntity<Object>(entry, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping({ "/deleteById" })
	public ResponseEntity<Object> deleteById(@RequestParam int[] ids) {
		try {

			for (int id : ids) {

				InwardEntry entry = this.inwdEntrySvc.getByEntryId(id);
				System.out.println("entry " + entry);
				if (entry == null)
					throw new RuntimeException("InwardEntry id not found - " + id);
				this.inwdEntrySvc.deleteById(id);
				// this.inwdEntrySvc.deleteEntity(entry);
			}

			return new ResponseEntity<Object>("delete success!", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping({ "/isBatchNoPresent" })
	public ResponseEntity<Object> isBatchNoPresent(@RequestParam String batchNumber) {
		try {
			boolean isPresent = this.inwdEntrySvc.isBatchNoPresent(batchNumber);
			return new ResponseEntity<Object>(Boolean.valueOf(isPresent), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping({ "/isCoilPresent" })
	public ResponseEntity<Object> isCoilNumberPresent(@RequestParam String coilNumber) {
		try {
			boolean isPresent = this.inwdEntrySvc.isCoilNumberPresent(coilNumber);
			return new ResponseEntity<Object>(Boolean.valueOf(isPresent), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping({ "/isCustomerBatchIdPresent" })
	public ResponseEntity<Object> isCustomerBatchPresent(@RequestParam String customerBatchId) {
		try {
			boolean isPresent = this.inwdEntrySvc.isCustomerBatchIdPresent(customerBatchId);
			return new ResponseEntity<Object>(Boolean.valueOf(isPresent), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping({ "/getPlanPDFs/{inwardEntryId}" })
	public ResponseEntity<Object> getPlanPDFs(@PathVariable int inwardEntryId) {
		try {
			JSONObject entry = this.inwdEntrySvc.getPlanPDFs(inwardEntryId);
			if (entry == null)
				throw new RuntimeException("Entry id not found - " + inwardEntryId);
			return new ResponseEntity<Object>(entry, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping({ "/getLabels/{processType}/{inwardEntryId}" })
	public ResponseEntity<Object> getLabels(@PathVariable int inwardEntryId, @PathVariable String processType) {
		try {
			JSONObject entry = this.inwdEntrySvc.getLabels(inwardEntryId, processType);
			if (entry == null)
				throw new RuntimeException("Entry id not found - " + inwardEntryId);
			return new ResponseEntity<Object>(entry, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping({ "/getdcpdfs" })
	public ResponseEntity<Object> getdcpdf(@RequestBody DeliveryPDFRequestDTO req) {
		try {
			JSONObject entry = this.inwdEntrySvc.getdcpdf(req);
			if (entry == null)
				throw new RuntimeException("Entry id not found - ");
			return new ResponseEntity<Object>(entry, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<String> handleBindException(BindException ex) {
		String message = ex.getBindingResult().getFieldErrors().stream().findFirst().map(this::resolveFieldErrorMessage)
				.orElse("Invalid input");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + message + "\"}", headers,
				HttpStatus.BAD_REQUEST);
	}

	private String resolveFieldErrorMessage(FieldError error) {
		// typeMismatch errors don't carry your custom @NotNull/@Pattern message,
		// so build a friendly one manually based on the field name
		if ("typeMismatch".equals(error.getCode())) {
			return error.getField() + " must be a valid number";
		}
		return error.getDefaultMessage();
	}
}
