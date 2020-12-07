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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "product_instruction")
public class Instruction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "instructionid")
	private Integer instructionId ;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "inwardid")
	private InwardEntry inwardId;
	
	@Column(name = "processdid")
	private Integer processdId;
	 
	@Column(name = "instructiondate")
	private Date  instructionDate;
	
	@Column(name = "length")
	private Float length;
	
	@Column(name = "width")
	private Float width;
	
	@Column(name = "weight")
	private Float weight;
	
	@Column(name = "noofpieces")
	private Integer noOfPieces;
	
	@JsonManagedReference
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
	@JoinColumn(name = "status")
	private Status status;
	
	@Column(name = "groupid")
	private Integer groupId ;
	
	@OneToMany(mappedBy = "parentInstruction", fetch = FetchType.EAGER)
	private List<Instruction> childInstructions;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "parentinstructionid", referencedColumnName = "instructionid")
	private Instruction parentInstruction;
	
	@Column(name = "wastage")
	private Float wastage;
	
	@Column(name = "damage")
	private Float damage;
	
	@Column(name = "packingweight")
	private Float packingWeight;

	@Column(name = "deliveryid")
	private Integer deliveryId;

	@Column(name = "remarks")
	private String remarks;
	
	@Column(name = "createdby")
	private Integer createdBy;

	@Column(name = "updatedby")
	private Integer updatedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createdon")
	private Date createdOn;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updatedon")
	private Date updatedOn;

	@Column(name = "isdeleted", columnDefinition = "BIT")
	private Boolean isDeleted;

	public Integer getInstructionId() {
		return instructionId;
	}

	public void setInstructionId(Integer instructionId) {
		this.instructionId = instructionId;
	}

	public InwardEntry getInwardId() {
		return inwardId;
	}

	public void setInwardId(InwardEntry inwardId) {
		this.inwardId = inwardId;
	}

	public Integer getProcessdId() {
		return processdId;
	}

	public void setProcessdId(Integer processdId) {
		this.processdId = processdId;
	}

	public Date getInstructionDate() {
		return instructionDate;
	}

	public void setInstructionDate(Date instructionDate) {
		this.instructionDate = instructionDate;
	}

	public Float getLength() {
		return length;
	}

	public void setLength(Float length) {
		this.length = length;
	}

	public Float getWidth() {
		return width;
	}

	public void setWidth(Float width) {
		this.width = width;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public Integer getNoOfPieces() {
		return noOfPieces;
	}

	public void setNoOfPieces(Integer noOfPieces) {
		this.noOfPieces = noOfPieces;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public List<Instruction> getChildInstructions() {
		return childInstructions;
	}

	public void setChildInstructions(List<Instruction> childInstructions) {
		this.childInstructions = childInstructions;
	}

	public Instruction getParentInstruction() {
		return parentInstruction;
	}

	public void setParentInstruction(Instruction parentInstruction) {
		this.parentInstruction = parentInstruction;
	}

	public Float getWastage() {
		return wastage;
	}

	public void setWastage(Float wastage) {
		this.wastage = wastage;
	}

	public Float getDamage() {
		return damage;
	}

	public void setDamage(Float damage) {
		this.damage = damage;
	}

	public Float getPackingWeight() {
		return packingWeight;
	}

	public void setPackingWeight(Float packingWeight) {
		this.packingWeight = packingWeight;
	}

	public Integer getDeliveryId() {
		return deliveryId;
	}

	public void setDeliveryId(Integer deliveryId) {
		this.deliveryId = deliveryId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

}
