package com.steel.product.application.entity;

import com.steel.product.application.dto.inward.InwardEntryResponseDto;
import com.steel.product.application.dto.material.MaterialResponseDto;
import com.steel.product.application.dto.pdf.InstructionResponsePdfDto;
import com.steel.product.application.dto.pdf.InwardEntryPdfDto;
import com.steel.product.jswone.service.MaterialMasterJswService;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "product_tblinwardentry")
public class InwardEntry {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "inwardentryid")
	private int inwardEntryId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "npartyid")
	private Party party;

	@Column(name = "coilnumber")
	private String coilNumber;

	@Column(name = "batchnumber")
	private String batchNumber;

	@Column(name = "dreceiveddate")
	private Date dReceivedDate;

	@Column(name = "dbilldate")
	private Date dBillDate;

	@Column(name = "po_id")
	private String poId;

	@Column(name = "vlorryno")
	private String vLorryNo;

	@Column(name = "dinvoicedate")
	private Date dInvoiceDate;

	@Column(name = "customercoilid")
	private String customerCoilId;
	
	@Column(name = "customerinvoiceno")
	private String customerInvoiceNo;

	@Column(name = "customerbatchid")
	private String customerBatchId;

	@Column(name = "purposetype")
	private String purposeType;
	
	@Column(name = "invoicecopy_fileurl")
	private String invoicecopyFileurl;
	
	@Column(name = "invoice_copy")
	private String invoiceCopy;

	@Column(name = "testcertificatenumber")
	private String testCertificateNumber;

	@Column(name = "testcertificatefileurl")
	private String testCertificateFileUrl;

	@Column(name = "vinvoiceno")
	private String vInvoiceNo;

	@JoinColumn(name = "mm_id")
	private String mmId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nmatid")
	private Material material;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "materialgradeid")
	private MaterialGrade materialGrade;

	@Column(name = "fwidth")
	private float fWidth;

	@Column(name = "fthickness")
	private float fThickness;

	@Column(name = "flength")
	private float fLength;

	@Column(name = "fquantity")
	private float fQuantity;

	@Column(name = "grossweight")
	private float grossWeight;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "vstatus")
	private Status status;

	@Column(name = "vprocess")
	private String vProcess;

	@Column(name = "tdc_no")
	private String tdcNo;

	@Column(name = "fpresent")
	private Float fpresent;

	@Column(name = "valueofgoods")
	private BigDecimal valueOfGoods;

	@Column(name = "billedweight")
	private float billedweight;

	@Column(name = "scrapWeight")
	private Float scrapWeight;

	@Column(name = "parentcoilnumber")
	private String parentCoilNumber;

	@Column(name = "vparentbundlenumber")
	private int vParentBundleNumber;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "createdby")
	private int createdBy;

	@Column(name = "updatedby")
	private int updatedBy;

	@CreationTimestamp
	@Column(name = "createdon", nullable = false,updatable = false)
	private Date createdOn;

	@UpdateTimestamp
	@Column(name = "updatedon")
	private Date updatedOn;

	@Column(name = "isdeleted", columnDefinition = "BIT")
	private Boolean isDeleted;

	@OneToMany(mappedBy = "inwardEntry")
	private List<InwardDoc> docs;
	
	@OneToMany(mappedBy = "inwardId", cascade = {CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval = true)
	private Set<Instruction> instructions;

	@Column(name = "in_stock_weight")
	private Float inStockWeight;

	@Column(name = "available_length")
	private Float availableLength;

    @Column(name = "pdf_s3_url")
    private String pdfS3Url;

    @Column(name = "allocated_soqty")
    private Float allocatedSoqty;
    
    @Column(name = "labelpdf_s3_url")
    private String labelpdfS3Url;
    
    @Column(name = "ys")
	private Float ys;

    @Column(name = "uts")
	private Float uts;

    @Column(name = "el")
	private Float el;

	@Column(name = "zoho_sync_stts")
	private String zohoSyncStts;

	@Column(name = "zoho_sync_remarks")
	private String zohoSyncRemarks;

	@Column(name = "manual_po_flag")
	private String manualPoFlag;

	@Column(name = "bill_id")
	private String billId;

	@Column(name = "zoho_docupload_stts")
	private String zohoDocuploadStts;
	
	@Column(name = "zoho_docupload_remarks")
	private String zohoDocuploadRemarks;

	@Column(name = "sono")
	private String sono;

	@Column(name = "batch_id")
	private String batchId;
	
	@Column(name = "allocated_mmid")
	private String allocatedMmid;

	public void addInstruction(Instruction instruction){
		if(this.instructions == null){
			this.instructions = new LinkedHashSet<>();
		}
		this.getInstructions().add(instruction);
		instruction.setInwardId(this);
	}

	public void removeInstruction(Instruction instruction){
		this.getInstructions().remove(instruction);
		instruction.setInwardId(null);
	}

	public String getBillId() {
		return billId;
	}

	public void setBillId(String billId) {
		this.billId = billId;
	}

	public String getZohoDocuploadStts() {
		return zohoDocuploadStts;
	}

	public void setZohoDocuploadStts(String zohoDocuploadStts) {
		this.zohoDocuploadStts = zohoDocuploadStts;
	}

	public int getInwardEntryId() {
		return this.inwardEntryId;
	}

	public void setInwardEntryId(int inwardEntryId) {
		this.inwardEntryId = inwardEntryId;
	}

	public String getCoilNumber() {
		return this.coilNumber;
	}

	public void setCoilNumber(String coilNumber) {
		this.coilNumber = coilNumber;
	}

	public Date getdReceivedDate() {
		return this.dReceivedDate;
	}

	public void setdReceivedDate(Date dReceivedDate) {
		this.dReceivedDate = dReceivedDate;
	}

	public Date getdBillDate() {
		return this.dBillDate;
	}

	public void setdBillDate(Date dBillDate) {
		this.dBillDate = dBillDate;
	}

	public String getvLorryNo() {
		return this.vLorryNo;
	}

	public void setvLorryNo(String vLorryNo) {
		this.vLorryNo = vLorryNo;
	}

	public Date getdInvoiceDate() {
		return this.dInvoiceDate;
	}

	public void setdInvoiceDate(Date dInvoiceDate) {
		this.dInvoiceDate = dInvoiceDate;
	}

	public String getvInvoiceNo() {
		return this.vInvoiceNo;
	}

	public void setvInvoiceNo(String vInvoiceNo) {
		this.vInvoiceNo = vInvoiceNo;
	}

	public float getfWidth() {
		return this.fWidth;
	}

	public void setfWidth(float fWidth) {
		this.fWidth = fWidth;
	}

	public float getfThickness() {
		return this.fThickness;
	}

	public void setfThickness(float fThickness) {
		this.fThickness = fThickness;
	}

	public float getfLength() {
		return this.fLength;
	}

	public void setfLength(float fLength) {
		this.fLength = fLength;
	}

	public float getfQuantity() {
		return this.fQuantity;
	}

	public void setfQuantity(float fQuantity) {
		this.fQuantity = fQuantity;
	}


	public String getvProcess() {
		return this.vProcess;
	}

	public void setvProcess(String vProcess) {
		this.vProcess = vProcess;
	}

	public float getBilledweight() {
		return this.billedweight;
	}

	public Float getFpresent() {
		return fpresent;
	}

	public void setFpresent(Float fpresent) {
		this.fpresent = fpresent;
	}

	public void setBilledweight(float billedweight) {
		this.billedweight = billedweight;
	}

	public String getParentCoilNumber() {
		return this.parentCoilNumber;
	}

	public void setParentCoilNumber(String parentCoilNumber) {
		this.parentCoilNumber = parentCoilNumber;
	}

	public int getvParentBundleNumber() {
		return this.vParentBundleNumber;
	}

	public void setvParentBundleNumber(int vParentBundleNumber) {
		this.vParentBundleNumber = vParentBundleNumber;
	}

	
	public Date getUpdatedOn() {
		return this.updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Boolean getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Party getParty() {
		return this.party;
	}

	public void setParty(Party party) {
		this.party = party;
	}

	public Material getMaterial() {
		return this.material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}
	
	public MaterialGrade getMaterialGrade() {
		return materialGrade;
	}

	public void setMaterialGrade(MaterialGrade materialGrade) {
		this.materialGrade = materialGrade;
	}

	public Status getStatus() {
		return this.status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public int getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getBatchNumber() {
		return this.batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	public String getCustomerCoilId() {
		return this.customerCoilId;
	}

	public void setCustomerCoilId(String customerCoilId) {
		this.customerCoilId = customerCoilId;
	}

	public String getCustomerBatchId() {
		return this.customerBatchId;
	}

	public void setCustomerBatchId(String customerBatchId) {
		this.customerBatchId = customerBatchId;
	}

	
	public String getPoId() {
		return poId;
	}

	public void setPoId(String poId) {
		this.poId = poId;
	}

	public String getPurposeType() {
		return this.purposeType;
	}

	public void setPurposeType(String purposeType) {
		this.purposeType = purposeType;
	}

	public String getTestCertificateNumber() {
		return this.testCertificateNumber;
	}
	
	public void setTestCertificateNumber(String testCertificateNumber) {
		this.testCertificateNumber = testCertificateNumber;
	}

	
	public String getTestCertificateFileUrl() {
		return testCertificateFileUrl;
	}

	public void setTestCertificateFileUrl(String testCertificateFileUrl) {
		this.testCertificateFileUrl = testCertificateFileUrl;
	}

	public float getGrossWeight() {
		return this.grossWeight;
	}

	public void setGrossWeight(float grossWeight) {
		this.grossWeight = grossWeight;
	}

	public List<InwardDoc> getDocs() {
		return docs;
	}

	public void setDocs(List<InwardDoc> docs) {
		this.docs = docs;
	}

	public String getCustomerInvoiceNo() {
		return customerInvoiceNo;
	}

	public void setCustomerInvoiceNo(String customerInvoiceNo) {
		this.customerInvoiceNo = customerInvoiceNo;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Set<Instruction> getInstructions() {
		return instructions;
	}

	public void setInstructions(Set<Instruction> instruction) {
		this.instructions = instruction;
	}

	public BigDecimal getValueOfGoods() {
		return valueOfGoods;
	}

	public void setValueOfGoods(BigDecimal valueOfGoods) {
		this.valueOfGoods = valueOfGoods;
	}

	public Boolean getDeleted() {
		return isDeleted;
	}

	public void setDeleted(Boolean deleted) {
		isDeleted = deleted;
	}

	public Float getInStockWeight() {
		return inStockWeight;
	}

	public void setInStockWeight(Float inStockWeight) {
		this.inStockWeight = inStockWeight;
	}

	public Float getAvailableLength() {
		return availableLength;
	}

	public void setAvailableLength(Float availableLength) {
		this.availableLength = availableLength;
	}

	public Float getScrapWeight() {
		return scrapWeight;
	}

	public void setScrapWeight(Float scrapWeight) {
		this.scrapWeight = scrapWeight;
	}

	public String getPdfS3Url() {
		return pdfS3Url;
	}

	public void setPdfS3Url(String pdfS3Url) {
		this.pdfS3Url = pdfS3Url;
	}

	public String getLabelpdfS3Url() {
		return labelpdfS3Url;
	}

	public void setLabelpdfS3Url(String labelpdfS3Url) {
		this.labelpdfS3Url = labelpdfS3Url;
	}

	public String getTdcNo() {
		return tdcNo;
	}

	public void setTdcNo(String tdcNo) {
		this.tdcNo = tdcNo;
	}

	public String getMmId() {
		return mmId;
	}

	public void setMmId(String mmId) {
		this.mmId = mmId;
	}

	public Float getAllocatedSoqty() {
		return allocatedSoqty;
	}

	public void setAllocatedSoqty(Float allocatedSoqty) {
		this.allocatedSoqty = allocatedSoqty;
	}
	public String getInvoiceCopy() {
		return invoiceCopy;
	}

	public void setInvoiceCopy(String invoiceCopy) {
		this.invoiceCopy = invoiceCopy;
	}

	public String getZohoSyncStts() {
		return zohoSyncStts;
	}

	public void setZohoSyncStts(String zohoSyncStts) {
		this.zohoSyncStts = zohoSyncStts;
	}

	public String getZohoSyncRemarks() {
		return zohoSyncRemarks;
	}

	public void setZohoSyncRemarks(String zohoSyncRemarks) {
		this.zohoSyncRemarks = zohoSyncRemarks;
	}

	public Float getYs() {
		return ys;
	}

	public void setYs(Float ys) {
		this.ys = ys;
	}

	public Float getUts() {
		return uts;
	}

	public void setUts(Float uts) {
		this.uts = uts;
	}

	public Float getEl() {
		return el;
	}

	public void setEl(Float el) {
		this.el = el;
	}

	public String getInvoicecopyFileurl() {
		return invoicecopyFileurl;
	}

	public void setInvoicecopyFileurl(String invoicecopyFileurl) {
		this.invoicecopyFileurl = invoicecopyFileurl;
	}

	public String getManualPoFlag() {
		return manualPoFlag;
	}

	public void setManualPoFlag(String manualPoFlag) {
		this.manualPoFlag = manualPoFlag;
	}

	public static InwardEntryPdfDto valueOf(InwardEntry inwardEntry, List<InstructionResponsePdfDto> instructionResponsePdfDtos){
        InwardEntryPdfDto inwardEntryPdfDto = new InwardEntryPdfDto();
        inwardEntryPdfDto.setInwardEntryId(inwardEntry.getInwardEntryId());
        inwardEntryPdfDto.setPartyName(inwardEntry.getParty() != null ? inwardEntry.getParty().getPartyName() : "");
        inwardEntryPdfDto.setCoilNumber(inwardEntry.getCoilNumber());
        inwardEntryPdfDto.setBatchNumber(inwardEntry.getBatchNumber());
        inwardEntryPdfDto.setCustomerBatchId(inwardEntry.getCustomerBatchId());
        inwardEntryPdfDto.setFQuantity(inwardEntry.getfQuantity());
        //inwardEntryPdfDto.setMatDescription(inwardEntry.getMaterial() != null ? inwardEntry.getMaterial().getDescription() : "");
       // inwardEntryPdfDto.setMaterialGradeName(inwardEntry.getMaterialGrade() != null ? inwardEntry.getMaterialGrade().getGradeName() : "");
        inwardEntryPdfDto.setFThickness(inwardEntry.getfThickness());
        inwardEntryPdfDto.setFWidth(inwardEntry.getfWidth());
        inwardEntryPdfDto.setGrossWeight(inwardEntry.getGrossWeight());
        inwardEntryPdfDto.setCreatedOn(inwardEntry.getCreatedOn());
        if (instructionResponsePdfDtos != null) {
            Map<Float, List<InstructionResponsePdfDto>> instructionsMap = instructionResponsePdfDtos.stream()
                    .collect(Collectors.groupingBy(InstructionResponsePdfDto::getPlannedWeight));
            inwardEntryPdfDto.setInstructionsMap(instructionsMap);
            inwardEntryPdfDto.setInstructions(instructionResponsePdfDtos);
//            inwardEntryPdfDto.setTotalWeight(instructionResponsePdfDtos.stream().
//                    map(ins -> ins.getPlannedWeight())
//                    .reduce(0f, Float::sum));
        }
        inwardEntryPdfDto.setPurposeType(inwardEntry.getPurposeType());
        inwardEntryPdfDto.setDReceivedDate(inwardEntry.getdReceivedDate());
        inwardEntryPdfDto.setVLorryNo(inwardEntry.getvLorryNo());
        inwardEntryPdfDto.setVInvoiceNo(inwardEntry.getvInvoiceNo());
        inwardEntryPdfDto.setTestCertificateNumber(inwardEntry.getTestCertificateNumber());
        inwardEntryPdfDto.setRemarks(inwardEntry.getRemarks());
        inwardEntryPdfDto.setDInvoiceDate(inwardEntry.getdInvoiceDate());
        inwardEntryPdfDto.setValueOfGoods(inwardEntry.getValueOfGoods());
        inwardEntryPdfDto.setPartyCgst(inwardEntry.getParty().getGstNumber());
        inwardEntryPdfDto.setCustomerInvoiceNo(inwardEntry.getCustomerInvoiceNo());
        inwardEntryPdfDto.setBilledWeight(inwardEntry.getBilledweight());
        inwardEntryPdfDto.setFLength(inwardEntry.getfLength());

        return inwardEntryPdfDto;
    }

	public static InwardEntryPdfDto valueOf(InwardEntry inwardEntry,
			List<InstructionResponsePdfDto> instructionResponsePdfDtos,
			MaterialMasterJswService materialService) {
        InwardEntryPdfDto inwardEntryPdfDto = new InwardEntryPdfDto();
        inwardEntryPdfDto.setInwardEntryId(inwardEntry.getInwardEntryId());
        inwardEntryPdfDto.setPartyName(inwardEntry.getParty() != null ? inwardEntry.getParty().getPartyName() : "");
        inwardEntryPdfDto.setCoilNumber(inwardEntry.getCoilNumber());
        inwardEntryPdfDto.setBatchNumber(inwardEntry.getBatchNumber());
        inwardEntryPdfDto.setCustomerBatchId(inwardEntry.getCustomerBatchId());
        inwardEntryPdfDto.setFQuantity(inwardEntry.getfQuantity());
        inwardEntryPdfDto.setMatDescription(inwardEntry.getMmId()!= null ? materialService.getProductName( inwardEntry.getMmId()).getDescription() : null);
        inwardEntryPdfDto.setMaterialGradeName(inwardEntry.getMmId()!= null ? materialService.getGradeName(inwardEntry.getMmId()).getGradeName() : null);
        try {
			inwardEntryPdfDto.setSubGradeName( inwardEntry.getMmId()!= null ? materialService.getSubGradeName(inwardEntry.getMmId()).getSubGradeName() : null);
		} catch (Exception e) {
			inwardEntryPdfDto.setSubGradeName("");
		}
        //inwardEntryPdfDto.setMatDescription(inwardEntry.getMaterial() != null ? inwardEntry.getMaterial().getDescription() : "");
       // inwardEntryPdfDto.setMaterialGradeName(inwardEntry.getMaterialGrade() != null ? inwardEntry.getMaterialGrade().getGradeName() : "");
        inwardEntryPdfDto.setFThickness(inwardEntry.getfThickness());
        inwardEntryPdfDto.setFWidth(inwardEntry.getfWidth());
        inwardEntryPdfDto.setGrossWeight(inwardEntry.getGrossWeight());
        inwardEntryPdfDto.setCreatedOn(inwardEntry.getCreatedOn());
        if (instructionResponsePdfDtos != null) {
            Map<Float, List<InstructionResponsePdfDto>> instructionsMap = instructionResponsePdfDtos.stream()
                    .collect(Collectors.groupingBy(InstructionResponsePdfDto::getPlannedWeight));
            inwardEntryPdfDto.setInstructionsMap(instructionsMap);
            inwardEntryPdfDto.setInstructions(instructionResponsePdfDtos);
//            inwardEntryPdfDto.setTotalWeight(instructionResponsePdfDtos.stream().
//                    map(ins -> ins.getPlannedWeight())
//                    .reduce(0f, Float::sum));
        }
        inwardEntryPdfDto.setPurposeType(inwardEntry.getPurposeType());
        inwardEntryPdfDto.setDReceivedDate(inwardEntry.getdReceivedDate());
        inwardEntryPdfDto.setVLorryNo(inwardEntry.getvLorryNo());
        inwardEntryPdfDto.setVInvoiceNo(inwardEntry.getvInvoiceNo());
        inwardEntryPdfDto.setTestCertificateNumber(inwardEntry.getTestCertificateNumber());
        inwardEntryPdfDto.setRemarks(inwardEntry.getRemarks());
        inwardEntryPdfDto.setDInvoiceDate(inwardEntry.getdInvoiceDate());
        inwardEntryPdfDto.setValueOfGoods(inwardEntry.getValueOfGoods());
        inwardEntryPdfDto.setPartyCgst(inwardEntry.getParty().getGstNumber());
        inwardEntryPdfDto.setCustomerInvoiceNo(inwardEntry.getCustomerInvoiceNo());
        inwardEntryPdfDto.setBilledWeight(inwardEntry.getBilledweight());
        inwardEntryPdfDto.setFLength(inwardEntry.getfLength());

        return inwardEntryPdfDto;
    }

	public static InwardEntryPdfDto valueOf(InwardEntry inwardEntry, List<InstructionResponsePdfDto> instructionsCut, List<InstructionResponsePdfDto> instructionsSlit) {
        InwardEntryPdfDto inwardEntryPdfDto = new InwardEntryPdfDto();
        inwardEntryPdfDto.setInwardEntryId(inwardEntry.getInwardEntryId());
        inwardEntryPdfDto.setPartyName(inwardEntry.getParty() != null ? inwardEntry.getParty().getPartyName() : "");
        inwardEntryPdfDto.setCoilNumber(inwardEntry.getCoilNumber());
        inwardEntryPdfDto.setBatchNumber(inwardEntry.getBatchNumber());
        inwardEntryPdfDto.setCustomerBatchId(inwardEntry.getCustomerBatchId());
        inwardEntryPdfDto.setFQuantity(inwardEntry.getfQuantity());
        inwardEntryPdfDto.setMatDescription(inwardEntry.getMaterial() != null ? inwardEntry.getMaterial().getDescription() : "");
        inwardEntryPdfDto.setMaterialGradeName(inwardEntry.getMaterialGrade() != null ? inwardEntry.getMaterialGrade().getGradeName() : "");
        inwardEntryPdfDto.setFThickness(inwardEntry.getfThickness());
        inwardEntryPdfDto.setFWidth(inwardEntry.getfWidth());
        inwardEntryPdfDto.setGrossWeight(inwardEntry.getGrossWeight());
        inwardEntryPdfDto.setCreatedOn(inwardEntry.getCreatedOn());
//        inwardEntryPdfDto.setInstructionsCutMap(instructionsCut.stream()
//                .collect(Collectors.groupingBy(InstructionResponsePdfDto::getPlannedWeight)));
        inwardEntryPdfDto.setInstructionsSlitMap(instructionsSlit.stream()
                .collect(Collectors.groupingBy(InstructionResponsePdfDto::getPlannedWeight)));
        inwardEntryPdfDto.setTotalWeightCut(instructionsCut.stream().
                map(ins -> ins.getPlannedWeight())
                .reduce(0f, Float::sum));
        inwardEntryPdfDto.setTotalWeightSlit(instructionsSlit.stream().
                map(ins -> ins.getPlannedWeight())
                .reduce(0f, Float::sum));
        inwardEntryPdfDto.setPurposeType(inwardEntry.getPurposeType());
        inwardEntryPdfDto.setDReceivedDate(inwardEntry.getdReceivedDate());
        inwardEntryPdfDto.setVLorryNo(inwardEntry.getvLorryNo());
        inwardEntryPdfDto.setVInvoiceNo(inwardEntry.getvInvoiceNo());
        inwardEntryPdfDto.setTestCertificateNumber(inwardEntry.getTestCertificateNumber());
        inwardEntryPdfDto.setRemarks(inwardEntry.getRemarks());
        inwardEntryPdfDto.setDInvoiceDate(inwardEntry.getdInvoiceDate());
        inwardEntryPdfDto.setValueOfGoods(inwardEntry.getValueOfGoods());
        inwardEntryPdfDto.setPartyCgst(inwardEntry.getParty().getGstNumber());
        inwardEntryPdfDto.setCustomerInvoiceNo(inwardEntry.getCustomerInvoiceNo());
        inwardEntryPdfDto.setBilledWeight(inwardEntry.getBilledweight());
        inwardEntryPdfDto.setFLength(inwardEntry.getfLength());
        return inwardEntryPdfDto;
    }
	
	public static InwardEntryResponseDto valueOfResponse (InwardEntry inwardEntry, MaterialMasterJswService materialService) {
		InwardEntryResponseDto inwardEntryResponseDto = new InwardEntryResponseDto();
		inwardEntryResponseDto.setInwardEntryId(inwardEntry.getInwardEntryId());
		inwardEntryResponseDto.setParty(inwardEntry.getParty() != null ? Party.valueOf(inwardEntry.getParty()) : null);
		inwardEntryResponseDto.setCoilNumber(inwardEntry.getCoilNumber());
		inwardEntryResponseDto.setTdcNo( inwardEntry.getTdcNo() );
		inwardEntryResponseDto.setBatchNumber(inwardEntry.getBatchNumber());
		inwardEntryResponseDto.setCustomerBatchId(inwardEntry.getCustomerBatchId());
		inwardEntryResponseDto.setfQuantity(inwardEntry.getfQuantity());
		inwardEntryResponseDto.setMaterial(inwardEntry.getMmId()!= null ? materialService.getProductName( inwardEntry.getMmId()) : null);
		inwardEntryResponseDto.setMaterialGrade(inwardEntry.getMmId()!= null ? materialService.getGradeName(inwardEntry.getMmId()) : null);
		//inwardEntryResponseDto.setMaterialGrade(inwardEntry.getMaterialGrade() != null ? MaterialGrade.valueOf(inwardEntry.getMaterialGrade()) : null);
		inwardEntryResponseDto.setfThickness(inwardEntry.getfThickness());
		inwardEntryResponseDto.setfWidth(inwardEntry.getfWidth());
		inwardEntryResponseDto.setGrossWeight(inwardEntry.getGrossWeight());
		inwardEntryResponseDto.setCreatedOn(inwardEntry.getCreatedOn());
		inwardEntryResponseDto.setInstruction(inwardEntry.getInstructions() != null ?
				inwardEntry.getInstructions().stream().filter(i -> !i.getIsDeleted())
						.map(i -> Instruction.valueOf(i)).collect(Collectors.toList()): null);
		if(inwardEntryResponseDto.getInstruction()!=null && inwardEntryResponseDto.getInstruction().size()>0) {
			Collections.sort(inwardEntryResponseDto.getInstruction(), new MyInstructionIdComp());
		}
		
		inwardEntryResponseDto.setPurposeType(inwardEntry.getPurposeType());
		inwardEntryResponseDto.setdReceivedDate(inwardEntry.getdReceivedDate());
		inwardEntryResponseDto.setvLorryNo(inwardEntry.getvLorryNo());
		inwardEntryResponseDto.setvInvoiceNo(inwardEntry.getvInvoiceNo());
		inwardEntryResponseDto.setTestCertificateNumber(inwardEntry.getTestCertificateNumber());
		inwardEntryResponseDto.setRemarks(inwardEntry.getRemarks());
		inwardEntryResponseDto.setdInvoiceDate(inwardEntry.getdInvoiceDate());
		inwardEntryResponseDto.setValueOfGoods(inwardEntry.getValueOfGoods());
		inwardEntryResponseDto.setCreatedBy(inwardEntry.getCreatedBy());
		inwardEntryResponseDto.setCreatedOn(inwardEntry.getCreatedOn());
		inwardEntryResponseDto.setUpdatedBy(inwardEntry.getUpdatedBy());
		inwardEntryResponseDto.setUpdatedOn(inwardEntry.getUpdatedOn());
		inwardEntryResponseDto.setStatus(inwardEntry.getStatus());
		inwardEntryResponseDto.setfQuantity(inwardEntry.getfQuantity());
		inwardEntryResponseDto.setFpresent(inwardEntry.getFpresent());
		inwardEntryResponseDto.setInStockWeight(inwardEntry.getInStockWeight());
		inwardEntryResponseDto.setDeleted(inwardEntry.getDeleted());
		inwardEntryResponseDto.setfLength(inwardEntry.getfLength());
		inwardEntryResponseDto.setAvailableLength(inwardEntry.getAvailableLength());
		inwardEntryResponseDto.setCustomerInvoiceNo(inwardEntry.getCustomerInvoiceNo());
		inwardEntryResponseDto.setParentCoilNumber(inwardEntry.getParentCoilNumber());
		inwardEntryResponseDto.setScrapWeight( inwardEntry.getScrapWeight() );
		inwardEntryResponseDto.setMmId( inwardEntry.getMmId()  );
		long daysBetween = 0;
		try {
			// Today's date
			LocalDate today = LocalDate.now();
			Calendar calendar = Calendar.getInstance();
			Date date = inwardEntry.getCreatedOn();
			if (inwardEntry.getdReceivedDate() != null) {
				date = inwardEntry.getdReceivedDate();
			}
			calendar.setTime(date); // convert Date to Calendar
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH) + 1; // 0-based, so add 1
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			LocalDate oldDate = LocalDate.of(year, month, day);
			daysBetween = ChronoUnit.DAYS.between(oldDate, today);
			inwardEntryResponseDto.setAgeing(daysBetween);
		} catch (Exception e) {
			inwardEntryResponseDto.setAgeing(daysBetween);
		}
		return inwardEntryResponseDto;
	}

	public static InwardEntryResponseDto valueOfResponsePartyWise (InwardEntry inwardEntry, Map<String, String> matDescMap) {
		InwardEntryResponseDto inwardEntryResponseDto = new InwardEntryResponseDto();
		inwardEntryResponseDto.setInwardEntryId(inwardEntry.getInwardEntryId());
		inwardEntryResponseDto.setParty(inwardEntry.getParty() != null ? Party.valueOf(inwardEntry.getParty()) : null);
		inwardEntryResponseDto.setCoilNumber(inwardEntry.getCoilNumber());
		inwardEntryResponseDto.setTdcNo( inwardEntry.getTdcNo() );
		inwardEntryResponseDto.setBatchNumber(inwardEntry.getBatchNumber());
		inwardEntryResponseDto.setCustomerBatchId(inwardEntry.getCustomerBatchId());
		inwardEntryResponseDto.setfQuantity(inwardEntry.getfQuantity());
		MaterialResponseDto kk =new MaterialResponseDto();
		kk.setMmDescConcatenated(matDescMap.get( inwardEntry.getMmId()));
		inwardEntryResponseDto.setMaterial(kk);
		//inwardEntryResponseDto.setMaterialGrade(inwardEntry.getMmId()!= null ? materialService.getGradeName(inwardEntry.getMmId()) : null);
		//inwardEntryResponseDto.setMaterialGrade(inwardEntry.getMaterialGrade() != null ? MaterialGrade.valueOf(inwardEntry.getMaterialGrade()) : null);
		inwardEntryResponseDto.setfThickness(inwardEntry.getfThickness());
		inwardEntryResponseDto.setfWidth(inwardEntry.getfWidth());
		inwardEntryResponseDto.setGrossWeight(inwardEntry.getGrossWeight());
		inwardEntryResponseDto.setCreatedOn(inwardEntry.getCreatedOn());
		inwardEntryResponseDto.setInstruction(inwardEntry.getInstructions() != null ?
				inwardEntry.getInstructions().stream().filter(i -> !i.getIsDeleted())
						.map(i -> Instruction.valueOf(i)).collect(Collectors.toList()): null);
		if(inwardEntryResponseDto.getInstruction()!=null && inwardEntryResponseDto.getInstruction().size()>0) {
			Collections.sort(inwardEntryResponseDto.getInstruction(), new MyInstructionIdComp());
		}
		
		inwardEntryResponseDto.setPurposeType(inwardEntry.getPurposeType());
		inwardEntryResponseDto.setdReceivedDate(inwardEntry.getdReceivedDate());
		inwardEntryResponseDto.setvLorryNo(inwardEntry.getvLorryNo());
		inwardEntryResponseDto.setvInvoiceNo(inwardEntry.getvInvoiceNo());
		inwardEntryResponseDto.setTestCertificateNumber(inwardEntry.getTestCertificateNumber());
		inwardEntryResponseDto.setRemarks(inwardEntry.getRemarks());
		inwardEntryResponseDto.setdInvoiceDate(inwardEntry.getdInvoiceDate());
		inwardEntryResponseDto.setValueOfGoods(inwardEntry.getValueOfGoods());
		inwardEntryResponseDto.setCreatedBy(inwardEntry.getCreatedBy());
		inwardEntryResponseDto.setCreatedOn(inwardEntry.getCreatedOn());
		inwardEntryResponseDto.setUpdatedBy(inwardEntry.getUpdatedBy());
		inwardEntryResponseDto.setUpdatedOn(inwardEntry.getUpdatedOn());
		inwardEntryResponseDto.setStatus(inwardEntry.getStatus());
		inwardEntryResponseDto.setfQuantity(inwardEntry.getfQuantity());
		inwardEntryResponseDto.setFpresent(inwardEntry.getFpresent());
		inwardEntryResponseDto.setInStockWeight(inwardEntry.getInStockWeight());
		inwardEntryResponseDto.setDeleted(inwardEntry.getDeleted());
		inwardEntryResponseDto.setfLength(inwardEntry.getfLength());
		inwardEntryResponseDto.setAvailableLength(inwardEntry.getAvailableLength());
		inwardEntryResponseDto.setCustomerInvoiceNo(inwardEntry.getCustomerInvoiceNo());
		inwardEntryResponseDto.setParentCoilNumber(inwardEntry.getParentCoilNumber());
		inwardEntryResponseDto.setScrapWeight( inwardEntry.getScrapWeight() );
		inwardEntryResponseDto.setMmId( inwardEntry.getMmId()  );
		long daysBetween = 0;
		try {
			// Today's date
			LocalDate today = LocalDate.now();
			Calendar calendar = Calendar.getInstance();
			Date date = inwardEntry.getCreatedOn();
			if (inwardEntry.getdReceivedDate() != null) {
				date = inwardEntry.getdReceivedDate();
			}
			calendar.setTime(date); // convert Date to Calendar
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH) + 1; // 0-based, so add 1
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			LocalDate oldDate = LocalDate.of(year, month, day);
			daysBetween = ChronoUnit.DAYS.between(oldDate, today);
			inwardEntryResponseDto.setAgeing(daysBetween);
		} catch (Exception e) {
			inwardEntryResponseDto.setAgeing(daysBetween);
		}
		return inwardEntryResponseDto;
	}

	public static InwardEntryResponseDto valueOfResponse(InwardEntry inwardEntry) {
		InwardEntryResponseDto inwardEntryResponseDto = new InwardEntryResponseDto();
		inwardEntryResponseDto.setInwardEntryId(inwardEntry.getInwardEntryId());
		inwardEntryResponseDto.setParty(inwardEntry.getParty() != null ? Party.valueOf(inwardEntry.getParty()) : null);
		inwardEntryResponseDto.setCoilNumber(inwardEntry.getCoilNumber());
		inwardEntryResponseDto.setTdcNo( inwardEntry.getTdcNo() );
		inwardEntryResponseDto.setBatchNumber(inwardEntry.getBatchNumber());
		inwardEntryResponseDto.setCustomerBatchId(inwardEntry.getCustomerBatchId());
		inwardEntryResponseDto.setfQuantity(inwardEntry.getfQuantity());
		inwardEntryResponseDto.setMaterial(inwardEntry.getMaterial() != null ? Material.valueOf(inwardEntry.getMaterial(), inwardEntry) : null);
		inwardEntryResponseDto.setMaterialGrade(inwardEntry.getMaterialGrade() != null ? MaterialGrade.valueOf(inwardEntry.getMaterialGrade()) : null);
		inwardEntryResponseDto.setfThickness(inwardEntry.getfThickness());
		inwardEntryResponseDto.setfWidth(inwardEntry.getfWidth());
		inwardEntryResponseDto.setGrossWeight(inwardEntry.getGrossWeight());
		inwardEntryResponseDto.setCreatedOn(inwardEntry.getCreatedOn());
		inwardEntryResponseDto.setInstruction(inwardEntry.getInstructions() != null ?
				inwardEntry.getInstructions().stream().filter(i -> !i.getIsDeleted())
						.map(i -> Instruction.valueOf(i)).collect(Collectors.toList()): null);
		if(inwardEntryResponseDto.getInstruction()!=null && inwardEntryResponseDto.getInstruction().size()>0) {
			Collections.sort(inwardEntryResponseDto.getInstruction(), new MyInstructionIdComp());
		}

		inwardEntryResponseDto.setPurposeType(inwardEntry.getPurposeType());
		inwardEntryResponseDto.setdReceivedDate(inwardEntry.getdReceivedDate());
		inwardEntryResponseDto.setvLorryNo(inwardEntry.getvLorryNo());
		inwardEntryResponseDto.setvInvoiceNo(inwardEntry.getvInvoiceNo());
		inwardEntryResponseDto.setMmId(inwardEntry.getMmId());
		inwardEntryResponseDto.setTestCertificateNumber(inwardEntry.getTestCertificateNumber());
		inwardEntryResponseDto.setRemarks(inwardEntry.getRemarks());
		inwardEntryResponseDto.setdInvoiceDate(inwardEntry.getdInvoiceDate());
		inwardEntryResponseDto.setValueOfGoods(inwardEntry.getValueOfGoods());
		inwardEntryResponseDto.setYs(inwardEntry.getYs());
		inwardEntryResponseDto.setUts(inwardEntry.getUts());
		inwardEntryResponseDto.setEl(inwardEntry.getEl());
		inwardEntryResponseDto.setTestCertificateFileUrl(inwardEntry.getTestCertificateFileUrl());
		inwardEntryResponseDto.setInvoicecopyFileurl(inwardEntry.getInvoicecopyFileurl());
		inwardEntryResponseDto.setCreatedBy(inwardEntry.getCreatedBy());
		inwardEntryResponseDto.setCreatedOn(inwardEntry.getCreatedOn());
		inwardEntryResponseDto.setUpdatedBy(inwardEntry.getUpdatedBy());
		inwardEntryResponseDto.setUpdatedOn(inwardEntry.getUpdatedOn());
		inwardEntryResponseDto.setStatus(inwardEntry.getStatus());
		inwardEntryResponseDto.setfQuantity(inwardEntry.getfQuantity());
		inwardEntryResponseDto.setFpresent(inwardEntry.getFpresent());
		inwardEntryResponseDto.setInStockWeight(inwardEntry.getInStockWeight());
		inwardEntryResponseDto.setDeleted(inwardEntry.getDeleted());
		inwardEntryResponseDto.setfLength(inwardEntry.getfLength());
		inwardEntryResponseDto.setAvailableLength(inwardEntry.getAvailableLength());
		inwardEntryResponseDto.setCustomerInvoiceNo(inwardEntry.getCustomerInvoiceNo());
		inwardEntryResponseDto.setParentCoilNumber(inwardEntry.getParentCoilNumber());
		inwardEntryResponseDto.setScrapWeight(inwardEntry.getScrapWeight());
		return inwardEntryResponseDto;
	}

	public InwardEntry duplicateForSplit() {

	    InwardEntry copy = new InwardEntry();

	    // ===== SIMPLE FIELDS =====
	    copy.setCoilNumber(this.getCoilNumber());
	    copy.setBatchNumber(this.getBatchNumber());
	    copy.setdReceivedDate(this.getdReceivedDate());
	    copy.setdBillDate(this.getdBillDate());
	    copy.setPoId(this.getPoId());
	    copy.setvLorryNo(this.getvLorryNo());
	    copy.setdInvoiceDate(this.getdInvoiceDate());
	    copy.setCustomerCoilId(this.getCustomerCoilId());
	    copy.setCustomerInvoiceNo(this.getCustomerInvoiceNo());
	    copy.setCustomerBatchId(this.getCustomerBatchId());
	    copy.setPurposeType(this.getPurposeType());
	    copy.setInvoicecopyFileurl(this.getInvoicecopyFileurl());
	    copy.setInvoiceCopy(this.getInvoiceCopy());
	    copy.setTestCertificateNumber(this.getTestCertificateNumber());
	    copy.setTestCertificateFileUrl(this.getTestCertificateFileUrl());
	    copy.setvInvoiceNo(this.getvInvoiceNo());
	    copy.setMmId(this.getMmId());

	    copy.setfWidth(this.getfWidth());
	    copy.setfThickness(this.getfThickness());
	    copy.setfLength(this.getfLength());
	    copy.setfQuantity(this.getfQuantity());
	    copy.setGrossWeight(this.getGrossWeight());
	    copy.setvProcess(this.getvProcess());
	    copy.setTdcNo(this.getTdcNo());
	    copy.setFpresent(this.getFpresent());
	    copy.setValueOfGoods(this.getValueOfGoods());
	    copy.setBilledweight(this.getBilledweight());
	    copy.setScrapWeight(this.getScrapWeight());
	    copy.setParentCoilNumber(this.getParentCoilNumber());
	    copy.setvParentBundleNumber(this.getvParentBundleNumber());
	    copy.setRemarks(this.getRemarks());
	    copy.setIsDeleted(this.getIsDeleted());
	    copy.setInStockWeight(this.getInStockWeight());
	    copy.setAvailableLength(this.getAvailableLength());
	    copy.setPdfS3Url(this.getPdfS3Url());
	    copy.setAllocatedSoqty(this.getAllocatedSoqty());
	    copy.setLabelpdfS3Url(this.getLabelpdfS3Url());
	    copy.setYs(this.getYs());
	    copy.setUts(this.getUts());
	    copy.setEl(this.getEl());
	    copy.setManualPoFlag(this.getManualPoFlag());

	    // ===== RELATION REFERENCES =====
	    copy.setParty(this.getParty());
	    copy.setMaterial(this.getMaterial());
	    copy.setMaterialGrade(this.getMaterialGrade());
	    copy.setStatus(this.getStatus());
	    return copy;
	}

	public static InwardEntryResponseDto valueOfResponseAllocatedPackets(InwardEntry inwardEntry, Map<String, String> matDescMap) {
		InwardEntryResponseDto inwardEntryResponseDto = new InwardEntryResponseDto();
		inwardEntryResponseDto.setInwardEntryId(inwardEntry.getInwardEntryId());
		inwardEntryResponseDto.setParty(inwardEntry.getParty() != null ? Party.valueOf(inwardEntry.getParty()) : null);
		inwardEntryResponseDto.setCoilNumber(inwardEntry.getCoilNumber());
		inwardEntryResponseDto.setTdcNo( inwardEntry.getTdcNo() );
		inwardEntryResponseDto.setBatchNumber(inwardEntry.getBatchNumber());
		inwardEntryResponseDto.setCustomerBatchId(inwardEntry.getCustomerBatchId());
		inwardEntryResponseDto.setfQuantity(inwardEntry.getfQuantity());
		MaterialResponseDto kk =new MaterialResponseDto();
		kk.setMmDescConcatenated(matDescMap.get( inwardEntry.getMmId()));
		inwardEntryResponseDto.setMaterial(kk);
		inwardEntryResponseDto.setfThickness(inwardEntry.getfThickness());
		inwardEntryResponseDto.setfWidth(inwardEntry.getfWidth());
		inwardEntryResponseDto.setGrossWeight(inwardEntry.getGrossWeight());
		inwardEntryResponseDto.setCreatedOn(inwardEntry.getCreatedOn());
		inwardEntryResponseDto.setInstruction(inwardEntry.getInstructions() != null ? inwardEntry.getInstructions()
				.stream().filter(i -> !i.getIsDeleted() && i.getAllocatedSoqty() !=null && i.getAllocatedSoqty() > 0 && i.getSono() != null)
				.map(i -> Instruction.valueOf(i)).collect(Collectors.toList()) : null);
		
		if (inwardEntryResponseDto.getInstruction() != null && inwardEntryResponseDto.getInstruction().size() > 0) {
			Collections.sort(inwardEntryResponseDto.getInstruction(), new MyInstructionIdComp());
		}
		
		inwardEntryResponseDto.setPurposeType(inwardEntry.getPurposeType());
		inwardEntryResponseDto.setdReceivedDate(inwardEntry.getdReceivedDate());
		inwardEntryResponseDto.setvLorryNo(inwardEntry.getvLorryNo());
		inwardEntryResponseDto.setvInvoiceNo(inwardEntry.getvInvoiceNo());
		inwardEntryResponseDto.setTestCertificateNumber(inwardEntry.getTestCertificateNumber());
		inwardEntryResponseDto.setRemarks(inwardEntry.getRemarks());
		inwardEntryResponseDto.setdInvoiceDate(inwardEntry.getdInvoiceDate());
		inwardEntryResponseDto.setValueOfGoods(inwardEntry.getValueOfGoods());
		inwardEntryResponseDto.setCreatedBy(inwardEntry.getCreatedBy());
		inwardEntryResponseDto.setCreatedOn(inwardEntry.getCreatedOn());
		inwardEntryResponseDto.setUpdatedBy(inwardEntry.getUpdatedBy());
		inwardEntryResponseDto.setUpdatedOn(inwardEntry.getUpdatedOn());
		inwardEntryResponseDto.setStatus(inwardEntry.getStatus());
		inwardEntryResponseDto.setfQuantity(inwardEntry.getfQuantity());
		inwardEntryResponseDto.setFpresent(inwardEntry.getFpresent());
		inwardEntryResponseDto.setInStockWeight(inwardEntry.getInStockWeight());
		inwardEntryResponseDto.setDeleted(inwardEntry.getDeleted());
		inwardEntryResponseDto.setfLength(inwardEntry.getfLength());
		inwardEntryResponseDto.setAvailableLength(inwardEntry.getAvailableLength());
		inwardEntryResponseDto.setCustomerInvoiceNo(inwardEntry.getCustomerInvoiceNo());
		inwardEntryResponseDto.setParentCoilNumber(inwardEntry.getParentCoilNumber());
		inwardEntryResponseDto.setScrapWeight( inwardEntry.getScrapWeight() );
		inwardEntryResponseDto.setMmId( inwardEntry.getMmId()  );
		inwardEntryResponseDto.setAllocatedMmid(inwardEntry.getAllocatedMmid());
		inwardEntryResponseDto.setSono( inwardEntry.getSono() );
		long daysBetween = 0;
		try {
			// Today's date
			LocalDate today = LocalDate.now();
			Calendar calendar = Calendar.getInstance();
			Date date = inwardEntry.getCreatedOn();
			if (inwardEntry.getdReceivedDate() != null) {
				date = inwardEntry.getdReceivedDate();
			}
			calendar.setTime(date); // convert Date to Calendar
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH) + 1; // 0-based, so add 1
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			LocalDate oldDate = LocalDate.of(year, month, day);
			daysBetween = ChronoUnit.DAYS.between(oldDate, today);
			inwardEntryResponseDto.setAgeing(daysBetween);
		} catch (Exception e) {
			inwardEntryResponseDto.setAgeing(daysBetween);
		}
		return inwardEntryResponseDto;
	}

	public String getZohoDocuploadRemarks() {
		return zohoDocuploadRemarks;
	}

	public void setZohoDocuploadRemarks(String zohoDocuploadRemarks) {
		this.zohoDocuploadRemarks = zohoDocuploadRemarks;
	}

	public String getSono() {
		return sono;
	}

	public void setSono(String sono) {
		this.sono = sono;
	}

	public String getAllocatedMmid() {
		return allocatedMmid;
	}

	public void setAllocatedMmid(String allocatedMmid) {
		this.allocatedMmid = allocatedMmid;
	}
	
	
	
}
