package com.steel.product.application.dto.inward;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

public class InwardDto {
	
	private int inwardId;

	private int partyId;

	private String purposeType;

	private String coilNumber;
	
	private String batchNumber;

	private String inwardDate;
	
	private String billDate;

	private String vehicleNumber;

	private String invoiceDate;
	
	private String customerCoilId;
	
	private String customerInvoiceNo;
	
	private String customerBatchId;

	private String invoiceNumber;

	private int materialId;
	
	private int materialGradeId;

	private float width;

	private float thickness;

	private float length;
	
	private float grossWeight;

	private int statusId;

	private String process;

	private float presentWeight;

	private int createdBy;

	private int updatedBy;

	private ArrayList<MultipartFile> inwardFiles;
	
	private String testCertificateNumber;
	
	private MultipartFile testCertificateFile;
	
	private String remarks;

	public int getInwardId() {
		return this.inwardId;
	}

	public void setInwardId(int inwardId) {
		this.inwardId = inwardId;
	}

	public int getPartyId() {
		return this.partyId;
	}

	public void setPartyId(int partyId) {
		this.partyId = partyId;
	}

	public String getPurposeType() {
		return this.purposeType;
	}

	public void setPurposeType(String purposeType) {
		this.purposeType = purposeType;
	}

	public String getCoilNumber() {
		return this.coilNumber;
	}

	public void setCoilNumber(String coilNumber) {
		this.coilNumber = coilNumber;
	}

	public String getInwardDate() {
		return this.inwardDate;
	}

	public void setInwardDate(String inwardDate) {
		this.inwardDate = inwardDate;
	}

	public String getVehicleNumber() {
		return this.vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getInvoiceDate() {
		return this.invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getInvoiceNumber() {
		return this.invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public int getMaterialId() {
		return this.materialId;
	}

	public void setMaterialId(int materialId) {
		this.materialId = materialId;
	}

	public float getWidth() {
		return this.width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getThickness() {
		return this.thickness;
	}

	public void setThickness(float thickness) {
		this.thickness = thickness;
	}

	public float getLength() {
		return this.length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public int getStatusId() {
		return this.statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public String getProcess() {
		return this.process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public float getPresentWeight() {
		return this.presentWeight;
	}

	public void setPresentWeight(float presentWeight) {
		this.presentWeight = presentWeight;
	}

	
	public int getMaterialGradeId() {
		return this.materialGradeId;
	}

	public void setMaterialGradeId(int materialGradeId) {
		this.materialGradeId = materialGradeId;
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

	public ArrayList<MultipartFile> getInwardFiles() {
		return inwardFiles;
	}

	public void setInwardFiles(ArrayList<MultipartFile> inwardFiles) {
		this.inwardFiles = inwardFiles;
	}

	public MultipartFile getTestCertificateFile() {
		return testCertificateFile;
	}

	public void setTestCertificateFile(MultipartFile testCertificateFile) {
		this.testCertificateFile = testCertificateFile;
	}
	
	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	public String getBillDate() {
		return billDate;
	}

	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}

	public String getCustomerCoilId() {
		return customerCoilId;
	}

	public void setCustomerCoilId(String customerCoilId) {
		this.customerCoilId = customerCoilId;
	}

	public String getCustomerBatchId() {
		return customerBatchId;
	}

	public void setCustomerBatchId(String customerBatchId) {
		this.customerBatchId = customerBatchId;
	}

	public float getGrossWeight() {
		return grossWeight;
	}

	public void setGrossWeight(float grossWeight) {
		this.grossWeight = grossWeight;
	}

	public String getTestCertificateNumber() {
		return testCertificateNumber;
	}

	public void setTestCertificateNumber(String testCertificateNumber) {
		this.testCertificateNumber = testCertificateNumber;
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

	public String toString() {
		return "InwardDto [partyId=" + this.partyId + ", coilNumber=" + this.coilNumber + ", inwardDate="
				+ this.inwardDate + ", vehicleNumber=" + this.vehicleNumber + ", invoiceDate=" + this.invoiceDate
				+ ", invoiceNumber=" + this.invoiceNumber + ", materialId=" + this.materialId + ", width=" + this.width
				+ ", thickness=" + this.thickness + ", length=" + this.length + ", statusId=" + this.statusId
				+ ", heatnumber=" +  ", plantname=" +  ", process=" + this.process
				+ ", presentWeight=" + this.presentWeight + ", cast="  + ", materialGradeId="
				+ this.materialGradeId + ", createdBy=" + this.createdBy + ", updatedBy=" + this.updatedBy + "]";
	}

}
