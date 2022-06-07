package com.steel.product.application.dto.inward;

import com.steel.product.application.dto.instruction.InstructionResponseDto;
import com.steel.product.application.dto.material.MaterialResponseDto;
import com.steel.product.application.dto.materialGradeDto.MaterialGradeDto;
import com.steel.product.application.dto.party.PartyDto;
import com.steel.product.application.entity.*;

import java.util.Date;
import java.util.List;

public class InwardEntryResponseDto {

    private Integer inwardEntryId;

    private PartyDto party;

    private String coilNumber;

    private String batchNumber;

    private Date dReceivedDate;

    private Date dBillDate;

    private String vLorryNo;

    private Date dInvoiceDate;

    private String customerCoilId;

    private String customerInvoiceNo;

    private String customerBatchId;

    private String purposeType;

    private String testCertificateNumber;

    private String testCertificateFileUrl;

    private String vInvoiceNo;

    private MaterialResponseDto material;

    private MaterialGradeDto materialGrade;

    private float fWidth;

    private float fThickness;

    private float fLength;

    private float fQuantity;

    private float grossWeight;

    private Status status;

    private String vProcess;

    private Float fpresent;

    private Float valueOfGoods;

    private float billedweight;

    private String parentCoilNumber;

    private int vParentBundleNumber;

    private String remarks;

    private int createdBy;

    private int updatedBy;

    private Date createdOn;

    private Date updatedOn;

    private Boolean isDeleted;

    private List<InwardDoc> docs;

    private List<InstructionResponseDto> instruction;

    private Float inStockWeight;

    private Float availableLength;

    public Integer getInwardEntryId() {
        return inwardEntryId;
    }

    public void setInwardEntryId(Integer inwardEntryId) {
        this.inwardEntryId = inwardEntryId;
    }

    public PartyDto getParty() {
        return party;
    }

    public void setParty(PartyDto party) {
        this.party = party;
    }

    public String getCoilNumber() {
        return coilNumber;
    }

    public void setCoilNumber(String coilNumber) {
        this.coilNumber = coilNumber;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Date getdReceivedDate() {
        return dReceivedDate;
    }

    public void setdReceivedDate(Date dReceivedDate) {
        this.dReceivedDate = dReceivedDate;
    }

    public Date getdBillDate() {
        return dBillDate;
    }

    public void setdBillDate(Date dBillDate) {
        this.dBillDate = dBillDate;
    }

    public String getvLorryNo() {
        return vLorryNo;
    }

    public void setvLorryNo(String vLorryNo) {
        this.vLorryNo = vLorryNo;
    }

    public Date getdInvoiceDate() {
        return dInvoiceDate;
    }

    public void setdInvoiceDate(Date dInvoiceDate) {
        this.dInvoiceDate = dInvoiceDate;
    }

    public String getCustomerCoilId() {
        return customerCoilId;
    }

    public void setCustomerCoilId(String customerCoilId) {
        this.customerCoilId = customerCoilId;
    }

    public String getCustomerInvoiceNo() {
        return customerInvoiceNo;
    }

    public void setCustomerInvoiceNo(String customerInvoiceNo) {
        this.customerInvoiceNo = customerInvoiceNo;
    }

    public String getCustomerBatchId() {
        return customerBatchId;
    }

    public void setCustomerBatchId(String customerBatchId) {
        this.customerBatchId = customerBatchId;
    }

    public String getPurposeType() {
        return purposeType;
    }

    public void setPurposeType(String purposeType) {
        this.purposeType = purposeType;
    }

    public String getTestCertificateNumber() {
        return testCertificateNumber;
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

    public String getvInvoiceNo() {
        return vInvoiceNo;
    }

    public void setvInvoiceNo(String vInvoiceNo) {
        this.vInvoiceNo = vInvoiceNo;
    }

    public MaterialResponseDto getMaterial() {
        return material;
    }

    public void setMaterial(MaterialResponseDto material) {
        this.material = material;
    }

    public MaterialGradeDto getMaterialGrade() {
        return materialGrade;
    }

    public void setMaterialGrade(MaterialGradeDto materialGrade) {
        this.materialGrade = materialGrade;
    }

    public float getfWidth() {
        return fWidth;
    }

    public void setfWidth(float fWidth) {
        this.fWidth = fWidth;
    }

    public float getfThickness() {
        return fThickness;
    }

    public void setfThickness(float fThickness) {
        this.fThickness = fThickness;
    }

    public float getfLength() {
        return fLength;
    }

    public void setfLength(float fLength) {
        this.fLength = fLength;
    }

    public float getfQuantity() {
        return fQuantity;
    }

    public void setfQuantity(float fQuantity) {
        this.fQuantity = fQuantity;
    }

    public float getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(float grossWeight) {
        this.grossWeight = grossWeight;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getvProcess() {
        return vProcess;
    }

    public void setvProcess(String vProcess) {
        this.vProcess = vProcess;
    }

    public Float getFpresent() {
        return fpresent;
    }

    public void setFpresent(Float fpresent) {
        this.fpresent = fpresent;
    }

    public Float getValueOfGoods() {
        return valueOfGoods;
    }

    public void setValueOfGoods(Float valueOfGoods) {
        this.valueOfGoods = valueOfGoods;
    }

    public float getBilledweight() {
        return billedweight;
    }

    public void setBilledweight(float billedweight) {
        this.billedweight = billedweight;
    }

    public String getParentCoilNumber() {
        return parentCoilNumber;
    }

    public void setParentCoilNumber(String parentCoilNumber) {
        this.parentCoilNumber = parentCoilNumber;
    }

    public int getvParentBundleNumber() {
        return vParentBundleNumber;
    }

    public void setvParentBundleNumber(int vParentBundleNumber) {
        this.vParentBundleNumber = vParentBundleNumber;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public List<InwardDoc> getDocs() {
        return docs;
    }

    public void setDocs(List<InwardDoc> docs) {
        this.docs = docs;
    }

    public List<InstructionResponseDto> getInstruction() {
        return instruction;
    }

    public void setInstruction(List<InstructionResponseDto> instruction) {
        this.instruction = instruction;
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
}
