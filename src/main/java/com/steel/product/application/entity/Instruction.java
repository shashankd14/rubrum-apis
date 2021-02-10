package com.steel.product.application.entity;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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

	@JsonManagedReference
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
	@JoinColumn(name = "processid")
	private Process process;
	 
	@Column(name = "instructiondate")
	private Date  instructionDate;
	
	@Column(name = "length")
	private Float length;

	@Column(name = "actuallength")
	private Float actualLength;
	
	@Column(name = "width")
	private Float width;

	@Column(name = "actualwidth")
	private Float actualWidth;
	
	@Column(name = "weight")
	private Float weight;

	@Column(name = "actualweight")
	private Float actualWeight;
	
	@Column(name = "noofpieces")
	private Integer noOfPieces;

	@Column(name = "actualnoofpieces")
	private Integer actualNoOfPieces;
	
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


	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "rateid")
	@JsonManagedReference
	private Rates rates;

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

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
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

	public Float getActualLength() {
		return actualLength;
	}

	public void setActualLength(Float actualLength) {
		this.actualLength = actualLength;
	}

	public Float getActualWidth() {
		return actualWidth;
	}

	public void setActualWidth(Float actualWidth) {
		this.actualWidth = actualWidth;
	}

	public Float getActualWeight() {
		return actualWeight;
	}

	public void setActualWeight(Float actualWeight) {
		this.actualWeight = actualWeight;
	}

	public Integer getActualNoOfPieces() {
		return actualNoOfPieces;
	}

	public void setActualNoOfPieces(Integer actualNoOfPieces) {
		this.actualNoOfPieces = actualNoOfPieces;
	}

}
