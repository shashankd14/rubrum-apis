package com.steel.product.trading.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "trading_inward")
public class InwardTradingEntity {

	@Column(name = "qc_certificate_no", columnDefinition = "VARCHAR(100)")
	private String qcCertificateNo;
	public String getQcCertificateNo() { return qcCertificateNo; }
	public void setQcCertificateNo(String value) { this.qcCertificateNo = value; }

	@Column(name = "qc_status", columnDefinition = "VARCHAR(20)")
	private String qcStatus;
	public String getQcStatus() { return qcStatus; }
	public void setQcStatus(String value) { this.qcStatus = value; }

	@Column(name = "qc_yield_strength", columnDefinition = "DECIMAL(19,6)")
	private BigDecimal qcYieldStrength;
	public BigDecimal getQcYieldStrength() { return qcYieldStrength; }
	public void setQcYieldStrength(BigDecimal value) { this.qcYieldStrength = value; }

	@Column(name = "qc_tensile_strength", columnDefinition = "DECIMAL(19,6)")
	private BigDecimal qcTensileStrength;
	public BigDecimal getQcTensileStrength() { return qcTensileStrength; }
	public void setQcTensileStrength(BigDecimal value) { this.qcTensileStrength = value; }

	@Column(name = "qc_elongation", columnDefinition = "DECIMAL(9,6)")
	private BigDecimal qcElongation;
	public BigDecimal getQcElongation() { return qcElongation; }
	public void setQcElongation(BigDecimal value) { this.qcElongation = value; }

	@Column(name = "qc_remark", columnDefinition = "TEXT")
	private String qcRemark;
	public String getQcRemark() { return qcRemark; }
	public void setQcRemark(String value) { this.qcRemark = value; }

	@Column(name = "qc_certificates_json", columnDefinition = "LONGTEXT")
	private String qcCertificatesJson;
	public String getQcCertificatesJson() { return qcCertificatesJson; }
	public void setQcCertificatesJson(String value) { this.qcCertificatesJson = value; }


	@Column(name = "invoice_value", columnDefinition = "DECIMAL(19,2)")
	private BigDecimal invoiceValue;

	public BigDecimal getInvoiceValue() { return invoiceValue; }

	public void setInvoiceValue(BigDecimal invoiceValue) { this.invoiceValue = invoiceValue; }

	@Column(name = "received_date", columnDefinition = "DATETIME")
	private Date receivedDate;

	public Date getReceivedDate() { return receivedDate; }

	public void setReceivedDate(Date receivedDate) { this.receivedDate = receivedDate; }

	@Column(name = "documents_json", columnDefinition = "LONGTEXT")
	private String documentsJson;

	public String getDocumentsJson() { return documentsJson; }

	public void setDocumentsJson(String documentsJson) { this.documentsJson = documentsJson; }

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "inward_id")
	private Integer inwardId;

	@Column(name = "inward_no")
	private String inwardNo;

	@Column(name = "status")
	private String status;

	@Column(name = "purpose_type")
	private String purposeType;

	@Column(name = "purpose_id")
	private Integer purposeId;

	@Column(name = "consignment_id")
	private String consignmentId;

	@Column(name = "vendor_id")
	private Integer vendorId;

	@Column(name = "transporter_name")
	private String transporterName;

	@Column(name = "transporter_phone_no")
	private String transporterPhoneNo;

	@OneToMany(mappedBy = "inwardId", cascade = {CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval = true)
	private Set<InwardTradingChildEntity> itemsList;
	
	@Column(name = "po_number", length = 100)
	private String poNumber;

	@Column(name = "vendor_batch_no")
	private String vendorBatchNo;

	@Column(name = "location_id")
	private Integer locationId;

	@Column(name = "vehicle_no")
	private String vehicleNo;

	@Column(name = "document_no")
	private String documentNo;

	@Column(name = "document_type")
	private String documentType;

	@Column(name = "document_date")
	private Date documentDate;

	@Column(name = "eway_bill_no")
	private String ewayBillNo;

	@Column(name = "eway_bill_date")
	private Date ewayBillDate;

	@Column(name = "value_of_goods")
	private BigDecimal valueOfGoods;

	@Column(name = "currency_code")
	private String currencyCode;

	@Column(name = "extra_charges_option")
	private String extraChargesOption;

	@Column(name = "freight_charges")
	private BigDecimal freightCharges;

	@Column(name = "insurance_amount")
	private BigDecimal insuranceAmount;

	@Column(name = "loading_charges")
	private BigDecimal loadingCharges;

	@Column(name = "weightmen_charges")
	private BigDecimal weightmenCharges;

	@Column(name = "cgst")
	private BigDecimal cgst;

	@Column(name = "sgst")
	private BigDecimal sgst;

	@Column(name = "igst")
	private BigDecimal igst;

	@Column(name = "total_inward_volume")
	private BigDecimal totalInwardVolume;

	@Column(name = "total_weight")
	private BigDecimal totalWeight;

	@Column(name = "total_volume")
	private BigDecimal totalVolume;

	@Column(name = "reconciliation_remark")
	private String reconciliationRemark;

	@Column(name = "deduct_freight", columnDefinition = "BIT")
	private Boolean deductFreight;

	@Column(name = "raise_debit_note", columnDefinition = "BIT")
	private Boolean raiseDebitNote;
	
	@Column(name = "is_deleted", columnDefinition = "BIT")
	private Boolean isDeleted;
	
	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@Column(name = "created_on", updatable = false)
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "updated_on")
	@UpdateTimestamp
	private Date updatedOn;
	
	public void addItem(InwardTradingChildEntity item){
		if(this.itemsList == null){
			this.itemsList = new LinkedHashSet<>();
		}
		this.itemsList.add(item);
		item.setInwardId( this);
	}

	public Integer getInwardId() {
		return inwardId;
	}

	public void setInwardId(Integer inwardId) {
		this.inwardId = inwardId;
	}

	public String getInwardNo() {
		return inwardNo;
	}

	public void setInwardNo(String inwardNo) {
		this.inwardNo = inwardNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPurposeType() {
		return purposeType;
	}

	public void setPurposeType(String purposeType) {
		this.purposeType = purposeType;
	}

	public Integer getPurposeId() {
		return purposeId;
	}

	public void setPurposeId(Integer purposeId) {
		this.purposeId = purposeId;
	}

	public Integer getVendorId() {
		return vendorId;
	}

	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}

	public String getTransporterName() {
		return transporterName;
	}

	public void setTransporterName(String transporterName) {
		this.transporterName = transporterName;
	}

	public String getTransporterPhoneNo() {
		return transporterPhoneNo;
	}

	public void setTransporterPhoneNo(String transporterPhoneNo) {
		this.transporterPhoneNo = transporterPhoneNo;
	}

	public Set<InwardTradingChildEntity> getItemsList() {
		return itemsList;
	}

	public void setItemsList(Set<InwardTradingChildEntity> itemsList) {
		this.itemsList = itemsList;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public String getVendorBatchNo() {
		return vendorBatchNo;
	}

	public void setVendorBatchNo(String vendorBatchNo) {
		this.vendorBatchNo = vendorBatchNo;
	}

	public String getConsignmentId() {
		return consignmentId;
	}

	public void setConsignmentId(String consignmentId) {
		this.consignmentId = consignmentId;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public String getVehicleNo() {
		return vehicleNo;
	}

	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}

	public String getDocumentNo() {
		return documentNo;
	}

	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public Date getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(Date documentDate) {
		this.documentDate = documentDate;
	}

	public String getEwayBillNo() {
		return ewayBillNo;
	}

	public void setEwayBillNo(String ewayBillNo) {
		this.ewayBillNo = ewayBillNo;
	}

	public Date getEwayBillDate() {
		return ewayBillDate;
	}

	public void setEwayBillDate(Date ewayBillDate) {
		this.ewayBillDate = ewayBillDate;
	}

	public BigDecimal getValueOfGoods() {
		return valueOfGoods;
	}

	public void setValueOfGoods(BigDecimal valueOfGoods) {
		this.valueOfGoods = valueOfGoods;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getExtraChargesOption() {
		return extraChargesOption;
	}

	public void setExtraChargesOption(String extraChargesOption) {
		this.extraChargesOption = extraChargesOption;
	}

	public BigDecimal getFreightCharges() {
		return freightCharges;
	}

	public void setFreightCharges(BigDecimal freightCharges) {
		this.freightCharges = freightCharges;
	}

	public BigDecimal getInsuranceAmount() {
		return insuranceAmount;
	}

	public void setInsuranceAmount(BigDecimal insuranceAmount) {
		this.insuranceAmount = insuranceAmount;
	}

	public BigDecimal getLoadingCharges() {
		return loadingCharges;
	}

	public void setLoadingCharges(BigDecimal loadingCharges) {
		this.loadingCharges = loadingCharges;
	}

	public BigDecimal getWeightmenCharges() {
		return weightmenCharges;
	}

	public void setWeightmenCharges(BigDecimal weightmenCharges) {
		this.weightmenCharges = weightmenCharges;
	}

	public BigDecimal getCgst() {
		return cgst;
	}

	public void setCgst(BigDecimal cgst) {
		this.cgst = cgst;
	}

	public BigDecimal getSgst() {
		return sgst;
	}

	public void setSgst(BigDecimal sgst) {
		this.sgst = sgst;
	}

	public BigDecimal getIgst() {
		return igst;
	}

	public void setIgst(BigDecimal igst) {
		this.igst = igst;
	}

	public BigDecimal getTotalInwardVolume() {
		return totalInwardVolume;
	}

	public void setTotalInwardVolume(BigDecimal totalInwardVolume) {
		this.totalInwardVolume = totalInwardVolume;
	}

	public BigDecimal getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(BigDecimal totalWeight) {
		this.totalWeight = totalWeight;
	}

	public BigDecimal getTotalVolume() {
		return totalVolume;
	}

	public void setTotalVolume(BigDecimal totalVolume) {
		this.totalVolume = totalVolume;
	}

	public String getReconciliationRemark() {
		return reconciliationRemark;
	}

	public void setReconciliationRemark(String reconciliationRemark) {
		this.reconciliationRemark = reconciliationRemark;
	}

	public Boolean getDeductFreight() {
		return deductFreight;
	}

	public void setDeductFreight(Boolean deductFreight) {
		this.deductFreight = deductFreight;
	}

	public Boolean getRaiseDebitNote() {
		return raiseDebitNote;
	}

	public void setRaiseDebitNote(Boolean raiseDebitNote) {
		this.raiseDebitNote = raiseDebitNote;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
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
	

}
