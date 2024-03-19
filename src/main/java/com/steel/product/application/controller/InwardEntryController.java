package com.steel.product.application.controller;

import com.steel.product.application.dto.delivery.DeliveryPDFRequestDTO;
import com.steel.product.application.dto.inward.InwardDto;
import com.steel.product.application.dto.inward.InwardEntryResponseDto;
import com.steel.product.application.dto.inward.SearchListPageRequest;
import com.steel.product.application.entity.InwardDoc;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.service.*;
import com.steel.product.application.util.CommonUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.minidev.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@Tag(name = "Inward Entry", description = "Inward Entry")
@RequestMapping({ "/inwardEntry" })
public class InwardEntryController {
	
	private InwardEntryService inwdEntrySvc;

	private PartyDetailsService partyDetailsService;

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
			InwardDocService inwardDocService, CommonUtil commonUtil ) {
		this.inwdEntrySvc = inwdEntrySvc;
		this.partyDetailsService = partyDetailsService;
		this.statusService = statusService;
		this.matDescService = matDescService;
		this.matGradeService = matGradeService;
		this.awsS3Service = awsS3Service;
		this.inwardDocService = inwardDocService;
		this.commonUtil = commonUtil;
	}

	@PostMapping("/addNew")
	public ResponseEntity<Object> saveInwardEntry(@ModelAttribute InwardDto inward, HttpServletRequest request) {
		InwardEntry inwardEntry = new InwardEntry();
		System.out.println("DTO details " + inward);
		try {
			int userId = commonUtil.getUserId();
			inwardEntry.setInwardEntryId(0);
			inwardEntry.setPurposeType(inward.getPurposeType());
			inwardEntry.setParty(this.partyDetailsService.getPartyById(inward.getPartyId()));
			inwardEntry.setCoilNumber(inward.getCoilNumber());
			inwardEntry.setBatchNumber(inward.getBatchNumber());
			inwardEntry.setdReceivedDate(Timestamp.valueOf(inward.getInwardDate()));
			if (inward.getPresentWeight() <= 0) {
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

			inwardEntry.setMaterial(this.matDescService.getMatById(inward.getMaterialId()));
			inwardEntry.setMaterialGrade(matGradeService.getById(inward.getMaterialGradeId()));

			inwardEntry.setfWidth(inward.getWidth());
			inwardEntry.setfThickness(inward.getThickness());
			inwardEntry.setfLength(inward.getLength());
			inwardEntry.setAvailableLength(inward.getLength());
			inwardEntry.setfQuantity(inward.getPresentWeight());
			inwardEntry.setGrossWeight(inward.getGrossWeight());

			// inwardEntry.setStatus(this.statusService.getStatusById(inward.getStatusId()));
			inwardEntry.setStatus(this.statusService.getStatusById(1));

			inwardEntry.setvProcess(inward.getProcess());
			inwardEntry.setFpresent(inward.getPresentWeight());
			inwardEntry.setValueOfGoods(inward.getValueOfGoods());

			inwardEntry.setBilledweight(0);
			inwardEntry.setParentCoilNumber(null);
			inwardEntry.setvParentBundleNumber(0);

			inwardEntry.setRemarks(inward.getRemarks());

			inwardEntry.setIsDeleted(Boolean.valueOf(false));
			inwardEntry.setCreatedOn(this.timestamp);
			inwardEntry.setUpdatedOn(this.timestamp);
			inwardEntry.setCreatedBy(userId);
			inwardEntry.setUpdatedBy(userId);

			if (inward.getTestCertificateFile() != null) {

				String fileUrl = awsS3Service.uploadFile(inward.getTestCertificateFile());
				inwardEntry.setTestCertificateFileUrl(fileUrl);
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
			e.printStackTrace();
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping({ "/update" })
	public ResponseEntity<Object> updateEntry(@RequestBody InwardDto inward, HttpServletRequest request) {
		InwardEntry inwardEntry = new InwardEntry();
		System.out.println("DTO details " + inward);
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

			inwardEntry.setMaterial(this.matDescService.getMatById(inward.getMaterialId()));
			inwardEntry.setMaterialGrade(matGradeService.getById(inward.getMaterialGradeId()));

			inwardEntry.setfWidth(inward.getWidth());
			inwardEntry.setfThickness(inward.getThickness());
			inwardEntry.setfLength(inward.getLength());
			inwardEntry.setfQuantity(inward.getPresentWeight());
			inwardEntry.setGrossWeight(inward.getGrossWeight());

			inwardEntry.setStatus(this.statusService.getStatusById(inward.getStatusId()));
			inwardEntry.setvProcess(inward.getProcess());
			inwardEntry.setFpresent(inward.getPresentWeight());

			inwardEntry.setBilledweight(0);
			inwardEntry.setParentCoilNumber(null);
			inwardEntry.setvParentBundleNumber(0);
			inwardEntry.setIsDeleted(Boolean.valueOf(false));
			inwardEntry.setUpdatedBy( userId );

			if (inward.getTestCertificateFile() != null) {

				String fileUrl = awsS3Service.uploadFile(inward.getTestCertificateFile());
				inwardEntry.setTestCertificateFileUrl(fileUrl);
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

	@PostMapping({ "/inwardlist" })
	public ResponseEntity<Object> inwardList(@RequestBody SearchListPageRequest searchListPageRequest) {
		Map<String, Object> response = new HashMap<>();
		Page<InwardEntry> pageResult = inwdEntrySvc.inwardList(searchListPageRequest);
		List<Object> inwardList = pageResult.stream().map(inw -> InwardEntry.valueOfResponse(inw)).collect(Collectors.toList());
		response.put("content", inwardList);
		response.put("currentPage", pageResult.getNumber());
		response.put("totalItems", pageResult.getTotalElements());
		response.put("totalPages", pageResult.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	@PostMapping({ "/partywiselist" })
	public ResponseEntity<Object> partywiselist(@RequestBody SearchListPageRequest searchListPageRequest) {
		Map<String, Object> response = new HashMap<>();
		Page<InwardEntry> pageResult = inwdEntrySvc.partywiselist(searchListPageRequest);
		List<Object> inwardList = pageResult.stream().map(inw -> InwardEntry.valueOfResponse(inw)).collect(Collectors.toList());
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
	}

	@GetMapping({ "/partywise/{pageNo}/{pageSize}" })
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
	}

	@GetMapping({ "/wiplist/{pageNo}/{pageSize}" })
	public ResponseEntity<Object> findAllWIPlistWithPagination(@PathVariable int pageNo, @PathVariable int pageSize,
			@RequestParam(required = false, name = "searchText") String searchText,
			@RequestParam(required = false, name = "partyId") String partyId) {

		Map<String, Object> response = new HashMap<>();
		Page<InwardEntry> pageResult = inwdEntrySvc.findAllWIPlistWithPagination(pageNo, pageSize, searchText, partyId);
		List<Object> inwardList = pageResult.stream().map(inw -> InwardEntry.valueOfResponse(inw)).collect(Collectors.toList());
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
