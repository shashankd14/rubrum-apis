package com.steel.product.application.controller;

import com.steel.product.application.dto.delivery.DeliveryPDFRequestDTO;
import com.steel.product.application.dto.instruction.WIPChildListResponseDTO;
import com.steel.product.application.dto.inward.EndUserTagWisePacketsDTO;
import com.steel.product.application.dto.inward.InwardDto;
import com.steel.product.application.dto.inward.InwardEntryResponseDto;
import com.steel.product.application.dto.inward.SearchListPageRequest;
import com.steel.product.application.dto.inward.WIPListResponseDTO;
import com.steel.product.application.entity.InwardDoc;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.service.*;
import com.steel.product.application.util.CommonUtil;
import com.steel.product.jswone.service.MaterialMasterJswService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@Tag(name = "Inward Entry", description = "Inward Entry")
@RequestMapping({ "/inwardEntry" })
@Log4j2
public class InwardEntryController {
	
	private InwardEntryService inwdEntrySvc;

	private PartyDetailsService partyDetailsService;

	private StatusService statusService;

	private MaterialDescriptionService matDescService;

	private MaterialGradeService matGradeService;

	private AWSS3Service awsS3Service;

	private CommonUtil commonUtil;

	private InwardDocService inwardDocService;
	
	private MaterialMasterJswService materialService;

	private Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	@Autowired
	public InwardEntryController(InwardEntryService inwdEntrySvc, PartyDetailsService partyDetailsService,
			StatusService statusService, MaterialDescriptionService matDescService,
			MaterialGradeService matGradeService, UserService userSerive, AWSS3Service awsS3Service,
			InwardDocService inwardDocService, CommonUtil commonUtil, MaterialMasterJswService materialService) {
		this.inwdEntrySvc = inwdEntrySvc;
		this.partyDetailsService = partyDetailsService;
		this.statusService = statusService;
		this.matDescService = matDescService;
		this.matGradeService = matGradeService;
		this.awsS3Service = awsS3Service;
		this.inwardDocService = inwardDocService;
		this.commonUtil = commonUtil;
		this.materialService = materialService;
	}

	@PostMapping("/addNew")
	public ResponseEntity<Object> saveInwardEntry(@ModelAttribute InwardDto inward, HttpServletRequest request) {
		InwardEntry inwardEntry = new InwardEntry();
		log.info("in saveInwardEntry ");
		try {
			int userId = commonUtil.getUserId();
			
			boolean isPresent = this.inwdEntrySvc.isCoilNumberPresent(inward.getCoilNumber());
			if(isPresent) {
				log.error("duplicate coil number ");
				return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered Coil Number already exists\"}", new HttpHeaders(), HttpStatus.BAD_REQUEST);
			}
			log.info("Entered MMID IS  = "+inward.getMmId()+", Location is "+inward.getPartyId());
			if(!(inward.getMmId()!=null && inward.getMmId().length() >0 )) {
				log.error("Invalid INWARD ID ");
				return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Please enter valid MMID\"}", new HttpHeaders(), HttpStatus.BAD_REQUEST);
			}
			inwardEntry.setInwardEntryId(0);
			inwardEntry.setPurposeType(inward.getPurposeType());
			try {
				inwardEntry.setParty(this.partyDetailsService.getPartyById(inward.getPartyId()));
			} catch (Exception e) {
				log.error("Invalid Party Id ");
				return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Please select valid Location \"}", new HttpHeaders(), HttpStatus.BAD_REQUEST);
			}
			inwardEntry.setCoilNumber(inward.getCoilNumber());
			inwardEntry.setBatchNumber(inward.getBatchNumber());
			inwardEntry.setdReceivedDate(Timestamp.valueOf(inward.getInwardDate()));
			if (inward.getPresentWeight() <= 0) {
				log.error("inward.getPresentWeight() is invalid");
				return new ResponseEntity<Object>("Invalid present weight entered.", HttpStatus.BAD_REQUEST);
			}
			inwardEntry.setInStockWeight(inward.getPresentWeight());

			if (inward.getBillDate() != null)
				inwardEntry.setdBillDate(Timestamp.valueOf(inward.getBillDate()));

			inwardEntry.setvLorryNo(inward.getVehicleNumber());
			inwardEntry.setvInvoiceNo(inward.getInvoiceNumber());
			inwardEntry.setdInvoiceDate(Timestamp.valueOf(inward.getInvoiceDate()));

			inwardEntry.setCustomerCoilId(inward.getCustomerCoilId());
			inwardEntry.setCustomerInvoiceNo(inward.getCustomerInvoiceNo());
			inwardEntry.setCustomerBatchId(inward.getCustomerBatchId());
			inwardEntry.setPoId(inward.getPoId());
			inwardEntry.setMmId(inward.getMmId());
			//inwardEntry.setMaterial(this.matDescService.getMatById(inward.getMaterialId()));
			//inwardEntry.setMaterialGrade(matGradeService.getById(inward.getMaterialGradeId()));

			inwardEntry.setfWidth(inward.getWidth());
			inwardEntry.setfThickness(inward.getThickness());
			inwardEntry.setfLength(inward.getLength());
			inwardEntry.setAvailableLength(inward.getLength());
			inwardEntry.setfQuantity(inward.getPresentWeight());
			inwardEntry.setGrossWeight(inward.getGrossWeight());

			// inwardEntry.setStatus(this.statusService.getStatusById(inward.getStatusId()));
			inwardEntry.setStatus(this.statusService.getStatusById(1));

			inwardEntry.setvProcess(inward.getProcess());
			inwardEntry.setTdcNo(inward.getTdcNo());
			inwardEntry.setFpresent(inward.getPresentWeight());
			inwardEntry.setValueOfGoods(inward.getValueOfGoods());
			inwardEntry.setYs( inward.getYs());
			inwardEntry.setUts( inward.getUts());
			inwardEntry.setEl(inward.getEl());

			inwardEntry.setBilledweight(0);
			inwardEntry.setParentCoilNumber(null);
			inwardEntry.setvParentBundleNumber(0);

			inwardEntry.setRemarks(inward.getRemarks());

			inwardEntry.setIsDeleted(Boolean.valueOf(false));
			inwardEntry.setCreatedOn(this.timestamp);
			inwardEntry.setUpdatedOn(this.timestamp);
			inwardEntry.setCreatedBy(userId);
			inwardEntry.setCreatedBy(userId);
			inwardEntry.setManualPoFlag(inward.isManualPoFlag());

			if (inward.getTestCertificateFile() != null) {
				String fileUrl = awsS3Service.uploadFile(inward.getTestCertificateFile());
				inwardEntry.setTestCertificateFileUrl(fileUrl);
			}
			if (inward.getInvoiceCopy() != null) {
				String fileUrl = awsS3Service.uploadFile(inward.getInvoiceCopy());
				inwardEntry.setInvoicecopyFileurl( fileUrl);
			}
			inwardEntry.setTestCertificateNumber(inward.getTestCertificateNumber());
			InwardEntry savedInwardEntry = inwdEntrySvc.saveEntry(inwardEntry);

			if (inward.getInwardFiles() != null) {
				for (MultipartFile file : inward.getInwardFiles()) {
					InwardDoc inwardDoc = new InwardDoc();
					inwardDoc.setInwardEntry(inwardEntry);
					String str = awsS3Service.uploadFile(file);
					inwardDoc.setDocUrl(str);
					inwardDocService.save(inwardDoc);
				}
			}
			return new ResponseEntity<Object>(InwardEntry.valueOfResponse(inwardEntry), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error while creating the INWARD "+e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping({ "/update" })
	public ResponseEntity<Object> updateEntry(@RequestBody InwardDto inward, HttpServletRequest request) {
		InwardEntry inwardEntry = new InwardEntry();
		log.info("in updateEntry ");
		try {
			int userId = commonUtil.getUserId();
			inwardEntry = inwdEntrySvc.getByEntryId(inward.getInwardId());
			inwardEntry.setPurposeType(inward.getPurposeType());
			inwardEntry.setParty(this.partyDetailsService.getPartyById(inward.getPartyId()));
			inwardEntry.setCoilNumber(inward.getCoilNumber());
			inwardEntry.setBatchNumber(inward.getBatchNumber());
			inwardEntry.setdReceivedDate(Timestamp.valueOf(inward.getInwardDate()));
			if (inward.getBillDate() != null)
				inwardEntry.setdBillDate(Timestamp.valueOf(inward.getBillDate()));

			inwardEntry.setvLorryNo(inward.getVehicleNumber());
			inwardEntry.setvInvoiceNo(inward.getInvoiceNumber());
			inwardEntry.setdInvoiceDate(Timestamp.valueOf(inward.getInvoiceDate()));

			inwardEntry.setCustomerCoilId(inward.getCustomerCoilId());
			inwardEntry.setCustomerBatchId(inward.getCustomerBatchId());
			inwardEntry.setPoId(inward.getPoId());

			inwardEntry.setMaterial(this.matDescService.getMatById(inward.getMaterialId()));
			inwardEntry.setMaterialGrade(matGradeService.getById(inward.getMaterialGradeId()));

			inwardEntry.setfWidth(inward.getWidth());
			inwardEntry.setfThickness(inward.getThickness());
			inwardEntry.setfLength(inward.getLength());
			inwardEntry.setfQuantity(inward.getPresentWeight());
			inwardEntry.setGrossWeight(inward.getGrossWeight());

			inwardEntry.setStatus(this.statusService.getStatusById(inward.getStatusId()));
			inwardEntry.setvProcess(inward.getProcess());
			inwardEntry.setTdcNo(inward.getTdcNo());
			inwardEntry.setFpresent(inward.getPresentWeight());

			inwardEntry.setBilledweight(0);
			inwardEntry.setParentCoilNumber(null);
			inwardEntry.setvParentBundleNumber(0);
			inwardEntry.setIsDeleted(Boolean.valueOf(false));
			inwardEntry.setUpdatedBy(userId);
			inwardEntry.setYs(inward.getYs());
			inwardEntry.setUts(inward.getUts());
			inwardEntry.setEl(inward.getEl());
			
			if (inward.getTestCertificateFile() != null) {
				String fileUrl = awsS3Service.uploadFile(inward.getTestCertificateFile());
				inwardEntry.setTestCertificateFileUrl(fileUrl);
			}
			if (inward.getInvoiceCopy() != null) {
				String fileUrl = awsS3Service.uploadFile(inward.getInvoiceCopy());
				inwardEntry.setInvoicecopyFileurl( fileUrl);
			}
			inwardEntry.setTestCertificateNumber(inward.getTestCertificateNumber());
			inwdEntrySvc.saveEntry(inwardEntry);

			if (inward.getInwardFiles() != null) {

				for (MultipartFile file : inward.getInwardFiles()) {
					InwardDoc inwardDoc = new InwardDoc();
					inwardDoc.setInwardEntry(inwardEntry);
					String str = awsS3Service.uploadFile(file);
					inwardDoc.setDocUrl(str);
					System.out.println("inwardDoc: " + inwardDoc);
					inwardDocService.save(inwardDoc);
				}
			}

			return new ResponseEntity<Object>("success", HttpStatus.OK);
		} catch (Exception e) {

			System.out.println(e.toString());
			e.printStackTrace();
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/*@GetMapping({ "/partywise/{pageNo}/{pageSize}" })
	public ResponseEntity<Object> findAllPartyWiseWithPagination(@PathVariable int pageNo, @PathVariable int pageSize,
			@RequestParam(required = false, name = "searchText") String searchText,
			@RequestParam(required = false, name = "partyId") String partyId) {
		SearchListPageRequest searchListPageRequest = new SearchListPageRequest();
		searchListPageRequest.setPageNo( pageNo);
		searchListPageRequest.setPageSize(pageSize);
		searchListPageRequest.setSearchText( searchText);
		searchListPageRequest.setPartyId( partyId);
		
		Map<String, Object> response = new HashMap<>();
		Page<InwardEntry> pageResult = inwdEntrySvc.partywiselist(searchListPageRequest);
		List<Object> inwardList = pageResult.stream().map(inw -> InwardEntry.valueOfResponse(inw)).collect(Collectors.toList());
		response.put("content", inwardList);
		response.put("currentPage", pageResult.getNumber());
		response.put("totalItems", pageResult.getTotalElements());
		response.put("totalPages", pageResult.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}*/

	@PostMapping({ "/partywiselist" })
	public ResponseEntity<Object> partywiselist(@RequestBody SearchListPageRequest searchListPageRequest) {
		Map<String, Object> response = new HashMap<>();
		log.info("in partywiselist ");
		if ("ENDUSER".equals(searchListPageRequest.getLoginType())) {
			Page<Object[]> pageResult = inwdEntrySvc.partywiselistEndUserTagWise(searchListPageRequest);
			List<EndUserTagWisePacketsDTO> responseList = new ArrayList<>();
			for (Object[] result : pageResult) {
				EndUserTagWisePacketsDTO dto = new EndUserTagWisePacketsDTO();
				dto.setCoilNumber(result[0] != null ? (String) result[0] : null);
				dto.setCustomerBatchId(result[1] != null ? (String) result[1] : null);
				dto.setMaterialDesc(result[2] != null ? (String) result[2] : null);
				dto.setMaterialGrade( result[3] != null ? (String) result[3] : null);
				dto.setThickness(result[4] != null ? (Float) result[4] : null);
				dto.setWidth(result[5] != null ? (Float) result[5] : null);
				dto.setLength(result[6] != null ? (Float) result[6] : null);
				dto.setClassificationTag(result[7] != null ? (String) result[7] : null);
				dto.setEndUserTagName( result[8] != null ? (String) result[8] : null);
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
			Page<Object[]> packetsList1 = inwdEntrySvc.listAllLocationWiseInwards(searchListPageRequest);
			Map<String, String> matDescMap = new HashMap<>();

			List<Integer> inwardIdList = new ArrayList<>(); 
			for (Object[] result : packetsList1) {
				Integer inwardId =  (result[0] != null ? (Integer) result[0] : null);
				inwardIdList.add(inwardId);
				matDescMap.put((result[12] != null ? (String) result[12] : null), (result[13] != null ? (String) result[13] : null));
			}
			log.info("In inwardIdList === " + matDescMap);
			List<InwardEntry> pageResult = inwdEntrySvc.locationWiseListByInwardId(inwardIdList);
			List<InwardEntryResponseDto> inwardList = pageResult.stream().map(inw -> InwardEntry.valueOfResponsePartyWise(inw, matDescMap)).collect(Collectors.toList());
			response.put("content", inwardList);
			response.put("currentPage", packetsList1.getNumber());
			response.put("totalItems", packetsList1.getTotalElements());
			response.put("totalPages", packetsList1.getTotalPages());
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping({ "/inwardlist" })
	public ResponseEntity<Object> inwardList(@RequestBody SearchListPageRequest searchListPageRequest) {
		log.info("in inwardList ");
		Map<String, Object> response = new HashMap<>();
		Page<Object[]> packetsList1 = inwdEntrySvc.listAllLocationWiseInwards(searchListPageRequest);

		List<Integer> inwardIdList = new ArrayList<>();
		for (Object[] result : packetsList1) {
			Integer inwardId = (result[0] != null ? (Integer) result[0] : null);
			inwardIdList.add(inwardId);
		}
		log.info("In inwardIdList === " + inwardIdList);
		List<InwardEntry> pageResult = inwdEntrySvc.locationWiseListByInwardId(inwardIdList);
		List<InwardEntryResponseDto> inwardList = pageResult.stream().map(inw -> InwardEntry.valueOfResponse(inw, materialService)).collect(Collectors.toList());
		response.put("content", inwardList);
		response.put("currentPage", packetsList1.getNumber());
		response.put("totalItems", packetsList1.getTotalElements());
		response.put("totalPages", packetsList1.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	@PostMapping({ "/wiplist" })
	public ResponseEntity<Object> wiplist(@RequestBody SearchListPageRequest request) {
		log.info("in wiplist ");
		Map<String, Object> response = new HashMap<>();
		request.setStatus(2);
		Page<Object[]> packetsList1 = null;
		if (request.getSearchText() != null && request.getSearchText().length() > 0) {
			packetsList1 = inwdEntrySvc.wipInwardIdListPlanId(request);
		} else {
			packetsList1 = inwdEntrySvc.listAllLocationWiseInwards(request);
		}
 
		List<Integer> inwardIdList = new ArrayList<>();
		for (Object[] result : packetsList1) {
			Integer inwardId = (result[0] != null ? (Integer) result[0] : null);
			inwardIdList.add(inwardId);
		}
		System.out.println("inwardIdList == "+inwardIdList);
		List<Object[]> packetsList= null;
		if (request.getSearchText() != null && request.getSearchText().length() > 0) {
			packetsList = inwdEntrySvc.wipListNewQueryWithPlanId(request.getSearchText());
		} else {
			packetsList = inwdEntrySvc.wipListNewQuery(inwardIdList);
		}

		Map<Integer, WIPListResponseDTO> inwardMap = new LinkedHashMap<>();
		List<WIPChildListResponseDTO> childList = new ArrayList<WIPChildListResponseDTO>();
		for (Object[] result : packetsList) {

			WIPChildListResponseDTO child = new WIPChildListResponseDTO();
			WIPListResponseDTO parent = new WIPListResponseDTO();
			parent.setInwardEntryId(result[1] != null ? (Integer) result[1] : null);
			parent.setCoilNumber(result[2] != null ? (String) result[2] : null);
			parent.setCustomerBatchId(result[3] != null ? (String) result[3] : null);
			parent.setCoilAge( result[4] != null ? (Integer) result[4] : null);
			parent.setPartyName(result[5] != null ? (String) result[5] : null);
			parent.setInwardStatus( result[6] != null ? (String) result[6] : null);
			parent.setMaterialDesc(result[7] != null ? (String) result[7] : null);
			parent.setMaterialGrade(result[8] != null ? (String) result[8] : null);
			parent.setMaterialSubGrade(result[9] != null ? (String) result[9] : null);
			parent.setMmId(result[10] != null ? (String) result[10] : null);
			parent.setFLength(result[11] != null ? (Float) result[11] : null);
			parent.setFThickness(result[12] != null ? (Float) result[12] : null);
			parent.setFWidth(result[13] != null ? (Float) result[13] : null);
			parent.setFpresent(result[14] != null ? (Float) result[14] : null);
			parent.setGrossWeight(result[15] != null ? (Float) result[15] : null);
			child.setInstructionId(result[0] != null ? (Integer) result[0] : null);
			
			child.setActualLength(result[16] != null ? (Float) result[16] : null);
			child.setActualNoOfPieces(result[17] != null ? (Integer) result[17] : null);
			child.setActualWeight(result[18] != null ? (Float) result[18] : null);
			child.setActualWidth(result[19] != null ? (Float) result[19] : null);
			child.setInstructionDate(result[20] != null ? (Date) result[20] : null);
			child.setPlannedLength(result[22] != null ? (Float) result[22] : null); //  22
			child.setPlannedNoOfPieces(result[23] != null ? (Integer) result[23] : null);
			child.setPlannedWeight(result[24] != null ? (Float) result[24] : null);
			child.setPlannedWidth(result[25] != null ? (Float) result[25] : null); // 25
			child.setAdditionalWeight( result[26] != null ? (Float) result[26] : null);
			child.setClassificationName(result[27] != null ? (String) result[27] : null);
			child.setEndUserTagName(result[28] != null ? (String) result[28] : null);
			child.setStatusName(result[29] != null ? (String) result[29] : null);
			child.setProcessName(result[30] != null ? (String) result[30] : null);
			child.setSoNo(result[31] != null ? (String) result[31] : null);

			if (inwardMap != null && inwardMap.get(parent.getInwardEntryId()) != null) {
				childList = inwardMap.get(parent.getInwardEntryId()).getInstruction();
				childList.add(child);
				parent.setInstruction(childList);
			} else {
				childList = new ArrayList<WIPChildListResponseDTO>();
				childList.add(child);
				parent.setInstruction(childList);
			}
			inwardMap.put(parent.getInwardEntryId(), parent);
		}
		List<WIPListResponseDTO> inwardList = new ArrayList<>(inwardMap.values() );
		log.info("In wiplist === " + inwardIdList);
		//List<InwardEntry> pageResult = inwdEntrySvc.locationWiseListByInwardId(inwardIdList);
		//List<InwardEntryResponseDto> inwardList = pageResult.stream().map(inw -> InwardEntry.valueOfResponse(inw, materialService)).collect(Collectors.toList());
		response.put("content", inwardList);
		response.put("currentPage", packetsList1.getNumber());
		response.put("totalItems", packetsList1.getTotalElements());
		response.put("totalPages", packetsList1.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@GetMapping({ "/wiplist/{pageNo}/{pageSize}" })
	public ResponseEntity<Object> findAllWIPlistWithPagination(@PathVariable int pageNo, @PathVariable int pageSize,
			@RequestParam(required = false, name = "searchText") String searchText,
			@RequestParam(required = false, name = "partyId") String partyId) {

		Map<String, Object> response = new HashMap<>();
		Page<InwardEntry> pageResult = inwdEntrySvc.findAllWIPlistWithPagination(pageNo, pageSize, searchText, partyId);
		List<Object> inwardList = pageResult.stream().map(inw -> InwardEntry.valueOfResponse(inw, materialService)).collect(Collectors.toList());
		response.put("content", inwardList);
		response.put("currentPage", pageResult.getNumber());
		response.put("totalItems", pageResult.getTotalElements());
		response.put("totalPages", pageResult.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	@GetMapping({ "/list/{pageNo}/{pageSize}" })
	public ResponseEntity<Object> findAllWithPagination(@PathVariable int pageNo, @PathVariable int pageSize,
			@RequestParam(required = false, name = "searchText") String searchText,
			@RequestParam(required = false, name = "partyId") String partyId) {
		SearchListPageRequest searchListPageRequest = new SearchListPageRequest();
		searchListPageRequest.setPageNo(pageNo);
		searchListPageRequest.setPageSize(pageSize);
		searchListPageRequest.setSearchText( searchText);
		searchListPageRequest.setPartyId( partyId);
		
		Map<String, Object> response = new HashMap<>();
		Page<InwardEntry> pageResult = inwdEntrySvc.partywiselist(searchListPageRequest);
		List<Object> inwardList = pageResult.stream().map(inw -> InwardEntry.valueOfResponse(inw, materialService)).collect(Collectors.toList());
		response.put("content", inwardList);
		response.put("currentPage", pageResult.getNumber());
		response.put("totalItems", pageResult.getTotalElements());
		response.put("totalPages", pageResult.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	@GetMapping({ "/listold" })
	public ResponseEntity<Object> listold() {
		try {
			
			List<InwardEntryResponseDto> inwardEntries = inwdEntrySvc.findAllInwards();
			return new ResponseEntity<Object>(inwardEntries, HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
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
				throw new RuntimeException("Entry id not found - " );
			return new ResponseEntity<Object>(entry, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/*
	@PostMapping({ "/qrcode/inward" })
	public ResponseEntity<PdfResponseDto> qrcode(@RequestBody PdfDto pdfDto ) {
		InputStreamResource inputStreamResource = null;
		ResponseEntity<PdfResponseDto> kk = null ;
		try {

			QRCodeResponse resp = inwdEntrySvc.getQRCodeDetails(pdfDto.getInwardId());
			byte[] pngData;
			StringBuilder text = new StringBuilder();
			text.append("Coil NO : " + resp.getCoilNo());
			text.append("\nCustomer BatchNo : " + resp.getCustomerBatchNo());
			text.append("\nMaterial Type : " + resp.getMaterialDesc());
			text.append("\nMaterial Grade : " + resp.getMaterialGrade());
			text.append("\nThickness : " + resp.getFthickness());
			text.append("\nWidth : " + resp.getFwidth());
			text.append("\nNet Weight : " + resp.getFweight());
			text.append("\nGross Weight : " + resp.getFweight());
			pngData = pdfGenerator.getQRCode(text.toString(), 0, 0);
			inputStreamResource = pdfGenerator.inputStreamResource(pngData, pdfDto.getInwardId());
			byte[] sourceBytes = IOUtils.toByteArray(inputStreamResource.getInputStream());
			StringBuilder builder = new StringBuilder();
			builder.append(Base64.getEncoder().encodeToString(sourceBytes));
			String encodedFile = builder.toString();
			kk = new ResponseEntity<PdfResponseDto>(new PdfResponseDto(encodedFile), HttpStatus.OK);
		} catch (WriterException | IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return kk;
	}*/
}
