package com.steel.product.application.dto.inward;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
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

	private String poId;

	private String invoiceNumber;

	private int materialId;

	private String mmId;
	
	private int materialGradeId;

	private float width;

	private float thickness;

	private float length;
	
	private float grossWeight;

	private int statusId;

	private String process;

	private String tdcNo;

	private float presentWeight;

	private BigDecimal valueOfGoods;

	private ArrayList<MultipartFile> inwardFiles;
	
	private String testCertificateNumber;
	
	private MultipartFile testCertificateFile;
	
	private String remarks;

	private String parentCoilNumber;

	private Float ys;

	private Float uts;

	private Float el;
	
	private MultipartFile invoiceCopy;
	
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

	public BigDecimal getValueOfGoods() {
		return valueOfGoods;
	}

	public void setValueOfGoods(BigDecimal valueOfGoods) {
		this.valueOfGoods = valueOfGoods;
	}

	public String getParentCoilNumber() {
		return parentCoilNumber;
	}

	public void setParentCoilNumber(String parentCoilNumber) {
		this.parentCoilNumber = parentCoilNumber;
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

	public MultipartFile getInvoiceCopy() {
		return invoiceCopy;
	}

	public void setInvoiceCopy(MultipartFile invoiceCopy) {
		this.invoiceCopy = invoiceCopy;
	}

	public String getPoId() {
		return poId;
	}

	public void setPoId(String poId) {
		this.poId = poId;
	}
	
	
}
