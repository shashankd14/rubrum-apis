package com.steel.product.application.controller;

import com.steel.product.application.dto.inward.InwardDto;
import com.steel.product.application.dto.inward.InwardEntryResponseDto;
import com.steel.product.application.entity.InwardDoc;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.service.*;

import io.swagger.v3.oas.annotations.tags.Tag;

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

	private UserService userSerive;

	private AWSS3Service awsS3Service;

	private InwardDocService inwardDocService;

	private Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	@Autowired
	public InwardEntryController(InwardEntryService inwdEntrySvc, PartyDetailsService partyDetailsService,
			StatusService statusService, MaterialDescriptionService matDescService,
			MaterialGradeService matGradeService, UserService userSerive, AWSS3Service awsS3Service,
			InwardDocService inwardDocService) {
		this.inwdEntrySvc = inwdEntrySvc;
		this.partyDetailsService = partyDetailsService;
		this.statusService = statusService;
		this.matDescService = matDescService;
		this.matGradeService = matGradeService;
		this.userSerive = userSerive;
		this.awsS3Service = awsS3Service;
		this.inwardDocService = inwardDocService;
	}

	@PostMapping("/addNew")
	public ResponseEntity<Object> saveInwardEntry(@ModelAttribute InwardDto inward) {
		InwardEntry inwardEntry = new InwardEntry();
		System.out.println("DTO details " + inward);
		try {
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

			// inwardEntry.setCustomerInvoiceDate(Timestamp.valueOf(inward.getCustomerInvoiceDate()));

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

			inwardEntry.setCreatedBy(this.userSerive.getUserById(inward.getCreatedBy()));
			inwardEntry.setUpdatedBy(this.userSerive.getUserById(inward.getUpdatedBy()));

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
	public ResponseEntity<Object> updateEntry(@RequestBody InwardDto inward) {
		InwardEntry inwardEntry = new InwardEntry();
		System.out.println("DTO details " + inward);
		try {
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

			inwardEntry.setCreatedBy(this.userSerive.getUserById(inward.getCreatedBy()));
			inwardEntry.setUpdatedBy(this.userSerive.getUserById(inward.getUpdatedBy()));

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

	@GetMapping({ "/list/{pageNo}/{pageSize}" })
	public ResponseEntity<Object> findAllWithPagination(@PathVariable int pageNo, @PathVariable int pageSize,
			@RequestParam(required = false, name = "coilNumber") String coilNumber) {

		Map<String, Object> response = new HashMap<>();
		Page<InwardEntry> pageResult = inwdEntrySvc.findAllWithPagination(pageNo, pageSize, coilNumber);
		List<Object> inwardList = pageResult.stream().map(inw -> InwardEntry.valueOfResponse(inw)).collect(Collectors.toList());
		response.put("content", inwardList);
		response.put("currentPage", pageResult.getNumber());
		response.put("totalItems", pageResult.getTotalElements());
		response.put("totalPages", pageResult.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@GetMapping({ "/list" })
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
}
