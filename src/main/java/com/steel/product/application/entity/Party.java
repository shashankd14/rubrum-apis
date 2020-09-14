package com.steel.product.application.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "product_tblpartydetails")
public class Party {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "npartyid")
	private int nPartyId;

	@Column(name = "npartyname")
	private String nPartyName;

	@Column(name = "identifier_receivable")
	private String identifier_receivable;

	@Column(name = "identifier_payable")
	private String identifier_payable;

	@Column(name = "phoneno")
	private String phoneNo;

	@Column(name = "head_address")
	private int head_address;

	@Column(name = "branch_address")
	private int branch_address;

	@Column(name = "vcity")
	private String vCity;

	@Column(name = "vstate")
	private String vState;

	@Column(name = "vcountry")
	private String vCountry;

	@Column(name = "npinid")
	private int nPinId;

	@Column(name = "ntinnumber")
	private String nTinNumber;

	@Column(name = "vcusrate")
	private String vCusrate;

	@Column(name = "vcusrateadd")
	private int vCusrateadd;

	@Column(name = "vcusraterm")
	private int vCusraterm;

	@Column(name = "vemailaddress")
	private String vEmailAddress;

	@Column(name = "ncstno")
	private int nCstNo;

	@Column(name = "ncgstnumber")
	private String nCgstNumber;

	@Column(name = "ninwardupdates")
	private String nInwardUpdates;

	@Column(name = "nprocessupdates")
	private String nProcessUpdates;

	@Column(name = "nbillingupdates")
	private String nBillingUpdates;

	@Column(name = "createdby")
	private int createdBy;

	@Column(name = "updatedby")
	private int updatedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createdon")
	private Date createdOn;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updatedon")
	private Date updatedOn;

	@Column(name = "isdeleted", columnDefinition = "BIT")
	private Boolean isDeleted;

	
	@JsonBackReference
	@OneToMany(mappedBy = "party")
	private List<InwardEntry> inwardEntry;

	public int getnPartyId() {
		return this.nPartyId;
	}

	public void setnPartyId(int nPartyId) {
		this.nPartyId = nPartyId;
	}

	public String getnPartyName() {
		return this.nPartyName;
	}

	public void setnPartyName(String nPartyName) {
		this.nPartyName = nPartyName;
	}

	public String getIdentifier_receivable() {
		return this.identifier_receivable;
	}

	public void setIdentifier_receivable(String identifier_receivable) {
		this.identifier_receivable = identifier_receivable;
	}

	public String getIdentifier_payable() {
		return this.identifier_payable;
	}

	public void setIdentifier_payable(String identifier_payable) {
		this.identifier_payable = identifier_payable;
	}

	public String getPhoneNo() {
		return this.phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public int getHead_address() {
		return this.head_address;
	}

	public void setHead_address(int head_address) {
		this.head_address = head_address;
	}

	public int getBranch_address() {
		return this.branch_address;
	}

	public void setBranch_address(int branch_address) {
		this.branch_address = branch_address;
	}

	public String getvCity() {
		return this.vCity;
	}

	public void setvCity(String vCity) {
		this.vCity = vCity;
	}

	public String getvState() {
		return this.vState;
	}

	public void setvState(String vState) {
		this.vState = vState;
	}

	public String getvCountry() {
		return this.vCountry;
	}

	public void setvCountry(String vCountry) {
		this.vCountry = vCountry;
	}

	public int getnPinId() {
		return this.nPinId;
	}

	public void setnPinId(int nPinId) {
		this.nPinId = nPinId;
	}

	public String getnTinNumber() {
		return this.nTinNumber;
	}

	public void setnTinNumber(String nTinNumber) {
		this.nTinNumber = nTinNumber;
	}

	public String getvCusrate() {
		return this.vCusrate;
	}

	public void setvCusrate(String vCusrate) {
		this.vCusrate = vCusrate;
	}

	public int getvCusrateadd() {
		return this.vCusrateadd;
	}

	public void setvCusrateadd(int vCusrateadd) {
		this.vCusrateadd = vCusrateadd;
	}

	public int getvCusraterm() {
		return this.vCusraterm;
	}

	public void setvCusraterm(int vCusraterm) {
		this.vCusraterm = vCusraterm;
	}

	public String getvEmailAddress() {
		return this.vEmailAddress;
	}

	public void setvEmailAddress(String vEmailAddress) {
		this.vEmailAddress = vEmailAddress;
	}

	public int getnCstNo() {
		return this.nCstNo;
	}

	public void setnCstNo(int nCstNo) {
		this.nCstNo = nCstNo;
	}

	public String getnCgstNumber() {
		return this.nCgstNumber;
	}

	public void setnCgstNumber(String nCgstNumber) {
		this.nCgstNumber = nCgstNumber;
	}

	public String getnInwardUpdates() {
		return this.nInwardUpdates;
	}

	public void setnInwardUpdates(String nInwardUpdates) {
		this.nInwardUpdates = nInwardUpdates;
	}

	public String getnProcessUpdates() {
		return this.nProcessUpdates;
	}

	public void setnProcessUpdates(String nProcessUpdates) {
		this.nProcessUpdates = nProcessUpdates;
	}

	public String getnBillingUpdates() {
		return this.nBillingUpdates;
	}

	public void setnBillingUpdates(String nBillingUpdates) {
		this.nBillingUpdates = nBillingUpdates;
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

	public List<InwardEntry> getInwardEntry() {
		return inwardEntry;
	}

	public void setInwardEntry(List<InwardEntry> inwardEntry) {
		this.inwardEntry = inwardEntry;
	}

}
