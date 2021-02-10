package com.steel.product.application.controller;

import com.steel.product.application.dto.inward.InwardDto;
import com.steel.product.application.entity.InwardDoc;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;

@RestController
//@CrossOrigin(origins = {"http://rubrum-frontend.s3-website.ap-south-1.amazonaws.com"})
@CrossOrigin
@RequestMapping({"/api/inwardEntry"})
public class InwardEntryController {
  @Autowired
  private InwardEntryService inwdEntrySvc;
  
  @Autowired
  private PartyDetailsService partyDetailsService;
  
  @Autowired
  private StatusService statusService;
  
  @Autowired
  private MaterialDescriptionService matDescService;
  
  @Autowired
  private MaterialGradeService matGradeService;
  
  @Autowired
  private UserService userSerive;
  
  @Autowired
  private AWSS3Service awsS3Service;
  
  @Autowired
  private InwardDocService inwardDocService;
  
  private Timestamp timestamp = new Timestamp(
      
      System.currentTimeMillis());
  
  public InwardEntryController(InwardEntryService theInwdEntrySvc) {
    this.inwdEntrySvc = theInwdEntrySvc;
  }
  
  @PostMapping("/addNew")
	public ResponseEntity<Object> saveInwardEntry(@ModelAttribute InwardDto inward) {
		InwardEntry inwardEntry = new InwardEntry();
		// InwardDoc inwardDoc = new InwardDoc();
		System.out.println("DTO details " + inward);
		try {
			inwardEntry.setInwardEntryId(0);
			inwardEntry.setPurposeType(inward.getPurposeType());
			inwardEntry.setParty(this.partyDetailsService.getPartyById(inward.getPartyId()));
			inwardEntry.setCoilNumber(inward.getCoilNumber());
			inwardEntry.setBatchNumber(inward.getBatchNumber());
			inwardEntry.setdReceivedDate(Timestamp.valueOf(inward.getInwardDate()));
			
			if(inward.getBillDate()!=null)
			inwardEntry.setdBillDate(Timestamp.valueOf(inward.getBillDate()));

			inwardEntry.setvLorryNo(inward.getVehicleNumber());
			inwardEntry.setvInvoiceNo(inward.getInvoiceNumber());
			inwardEntry.setdInvoiceDate(Timestamp.valueOf(inward.getInvoiceDate()));

			inwardEntry.setCustomerCoilId(inward.getCustomerCoilId());
			inwardEntry.setCustomerInvoiceNo(inward.getCustomerInvoiceNo());
			inwardEntry.setCustomerBatchId(inward.getCustomerBatchId());
			
			
			//inwardEntry.setCustomerInvoiceDate(Timestamp.valueOf(inward.getCustomerInvoiceDate()));

			inwardEntry.setMaterial(this.matDescService.getMatById(inward.getMaterialId()));
			inwardEntry.setMaterialGrade(matGradeService.getById(inward.getMaterialGradeId()));

			inwardEntry.setfWidth(inward.getWidth());
			inwardEntry.setfThickness(inward.getThickness());
			inwardEntry.setfLength(inward.getLength());
			inwardEntry.setfQuantity(inward.getPresentWeight());
			inwardEntry.setGrossWeight(inward.getGrossWeight());

			//inwardEntry.setStatus(this.statusService.getStatusById(inward.getStatusId()));
			inwardEntry.setStatus(this.statusService.getStatusById(1));
			
			inwardEntry.setvProcess(inward.getProcess());
			inwardEntry.setFpresent(inward.getPresentWeight());

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
			inwdEntrySvc.saveEntry(inwardEntry);

			if (inward.getInwardFiles() != null) {

				for (MultipartFile file : inward.getInwardFiles()) {

					InwardDoc inwardDoc = new InwardDoc();
					inwardDoc.setInwardEntry(inwardEntry);
					String str = awsS3Service.uploadFile(file);
					inwardDoc.setDocUrl(str);
					
					System.out.println("inwardDoc: "+inwardDoc);
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
  
  @PutMapping({"/update"})
	public ResponseEntity<Object> updateEntry(@ModelAttribute InwardDto inward) {
		InwardEntry inwardEntry = new InwardEntry();
		System.out.println("DTO details " + inward);
		try {
			inwardEntry.setInwardEntryId(inward.getInwardId());

			inwardEntry.setPurposeType(inward.getPurposeType());
			inwardEntry.setParty(this.partyDetailsService.getPartyById(inward.getPartyId()));
			inwardEntry.setCoilNumber(inward.getCoilNumber());
			inwardEntry.setBatchNumber(inward.getBatchNumber());
			inwardEntry.setdReceivedDate(Timestamp.valueOf(inward.getInwardDate()));
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
			inwardEntry.setCreatedOn(this.timestamp);
			inwardEntry.setUpdatedOn(this.timestamp);

			inwardEntry.setCreatedBy(this.userSerive.getUserById(inward.getCreatedBy()));
			inwardEntry.setUpdatedBy(this.userSerive.getUserById(inward.getUpdatedBy()));

			if (inward.getTestCertificateFile() != null) {

				String fileUrl = awsS3Service.uploadFile(inward.getTestCertificateFile());
				inwardEntry.setTestCertificateFileUrl(fileUrl);
			}
			inwdEntrySvc.saveEntry(inwardEntry);

			if (inward.getInwardFiles() != null) {

				for (MultipartFile file : inward.getInwardFiles()) {

					InwardDoc inwardDoc = new InwardDoc();
					inwardDoc.setInwardEntry(inwardEntry);
					String str = awsS3Service.uploadFile(file);
					inwardDoc.setDocUrl(str);
					
					System.out.println("inwardDoc: "+inwardDoc);
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
  
  @GetMapping({"/list"})
  public ResponseEntity<Object> findAll() {
    try {
      List<InwardEntry> inwardEntries = inwdEntrySvc.getAllEntries();
     // inwardEntries = this.inwdEntrySvc.getAllEntries();
     // System.out.println("Inward "+inwardEntries.toString());
      return new ResponseEntity<Object>(inwardEntries, HttpStatus.OK);
    } catch (Exception e) {
    	e.printStackTrace();
      return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } 
  }
  
  @GetMapping({"/getById/{inwardEntryId}"})
  public ResponseEntity<Object> getById(@PathVariable int inwardEntryId) {
    try {
      InwardEntry entry = this.inwdEntrySvc.getByEntryId(inwardEntryId);
      if (entry == null)
        throw new RuntimeException("Entry id not found - " + inwardEntryId); 
      return new ResponseEntity<Object>(entry, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } 
  }
  
  @GetMapping({"/getByCoilId/{coilNumber}"})
  public ResponseEntity<Object> getById(@PathVariable String coilNumber) {
    try {
      InwardEntry entry = this.inwdEntrySvc.getByCoilNumber(coilNumber);
      if (entry == null)
        throw new RuntimeException("Entry coilNumber id not found - " + coilNumber); 
      return new ResponseEntity<Object>(entry, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } 
  }
  
  @GetMapping({"/getByPartyId/{partyId}"})
  public ResponseEntity<Object> getInwardEntriesByPartyId(@PathVariable int partyId) {
    try {
      ResponseEntity<Object> entry = this.inwdEntrySvc.getInwardEntriesByPartyId(partyId);
      return new ResponseEntity<Object>(entry, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } 
  }
  
  @DeleteMapping({"/deleteById"})
	public ResponseEntity<Object> deleteById(@RequestParam int[] ids) {
		try {

			for (int id : ids) {

				InwardEntry entry = this.inwdEntrySvc.getByEntryId(id);
				System.out.println("entry " + entry);
				if (entry == null)
					throw new RuntimeException("InwardEntry id not found - " + id);
				this.inwdEntrySvc.deleteById(id);
				//this.inwdEntrySvc.deleteEntity(entry);
			}

			return new ResponseEntity<Object>("delete success!", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
  
  @GetMapping({"/isCoilPresent"})
  public ResponseEntity<Object> isCoilNumberPresent(@RequestParam String coilNumber) {
    try {
      boolean isPresent = this.inwdEntrySvc.isCoilNumberPresent(coilNumber);
      return new ResponseEntity<Object>(Boolean.valueOf(isPresent), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } 
  }
}
