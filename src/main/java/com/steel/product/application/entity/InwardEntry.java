package com.steel.product.application.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "product_tblinwardentry")
public class InwardEntry {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "inwardentryid")
	private int inwardEntryId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "npartyid")
	@JsonManagedReference(value = "party-inward")
	private Party party;

	@Column(name = "coilnumber")
	private String coilNumber;

	@Column(name = "batchnumber")
	private String batchNumber;

	@Column(name = "dreceiveddate")
	private Date dReceivedDate;

	@Column(name = "dbilldate")
	private Date dBillDate;

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

	@Column(name = "testcertificatenumber")
	private String testCertificateNumber;

	@Column(name = "testcertificatefileurl")
	private String testCertificateFileUrl;

	@Column(name = "vinvoiceno")
	private String vInvoiceNo;

	@JsonManagedReference
	@ManyToOne
	@JoinColumn(name = "nmatid")
	private Material material;
	
	@JsonManagedReference
	@ManyToOne
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

	@JsonManagedReference
	@ManyToOne
	@JoinColumn(name = "vstatus")
	private Status status;

	@Column(name = "vprocess")
	private String vProcess;

	@Column(name = "fpresent")
	private Float fpresent;

	@Column(name = "valueofgoods")
	private Float valueOfGoods;

	@Column(name = "billedweight")
	private float billedweight;

	@Column(name = "parentcoilnumber")
	private String parentCoilNumber;

	@Column(name = "vparentbundlenumber")
	private int vParentBundleNumber;

	@Column(name = "remarks")
	private String remarks;

	@ManyToOne
	@JoinColumn(name = "createdby")
	private User createdBy;

	@ManyToOne
	@JoinColumn(name = "updatedby")
	private User updatedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createdon", nullable = true)
	private Date createdOn;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updatedon")
	private Date updatedOn;

	@Column(name = "isdeleted", columnDefinition = "BIT")
	private Boolean isDeleted;

	@JsonBackReference
	@OneToMany(mappedBy = "inwardEntry", fetch = FetchType.EAGER)
	private List<InwardDoc> docs;
	
	@OneToMany(mappedBy = "inwardId", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	private Set<Instruction> instruction;

	@Column(name = "inStockWeight")
	private Float inStockWeight;

	public void addInstruction(Instruction instruction){
		this.getInstruction().add(instruction);
		instruction.setInwardId(this);
	}

	public void removeInstruction(Instruction instruction){
		this.getInstruction().remove(instruction);
		instruction.setInwardId(null);
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

	public User getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public User getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
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

	public Set<Instruction> getInstruction() {
		return instruction;
	}

	public void setInstruction(Set<Instruction> instruction) {
		this.instruction = instruction;
	}

	public Float getValueOfGoods() {
		return valueOfGoods;
	}

	public void setValueOfGoods(Float valueOfGoods) {
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
}
