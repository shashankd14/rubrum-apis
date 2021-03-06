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
	
	@Column(name = "plannedlength")
	private Float plannedLength;

	@Column(name = "actuallength")
	private Float actualLength;
	
	@Column(name = "plannedwidth")
	private Float plannedWidth;

	@Column(name = "actualwidth")
	private Float actualWidth;
	
	@Column(name = "plannedweight")
	private Float plannedWeight;

	@Column(name = "actualweight")
	private Float actualWeight;
	
	@Column(name = "plannednoofpieces")
	private Integer plannedNoOfPieces;

	@Column(name = "actualnoofpieces")
	private Integer actualNoOfPieces;
	
	@JsonManagedReference
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
	@JoinColumn(name = "status")
	private Status status;

	@JsonManagedReference
	@ManyToOne
	@JoinColumn(name = "packetClassificationId")
	private PacketClassification packetClassification;
	
	@Column(name = "groupid")
	private Integer groupId ;

	@Column(name = "parentgroupid")
	private Integer parentGroupId ;
	
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
	@JsonManagedReference(value = "instruction-rates")
	private Rates rates;

	@JsonManagedReference
	@ManyToOne
	@JoinColumn(name = "deliveryid")
	private DeliveryDetails deliveryDetails;

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

	public Float getPlannedLength() {
		return plannedLength;
	}

	public void setPlannedLength(Float plannedLength) {
		this.plannedLength = plannedLength;
	}

	public Float getPlannedWidth() {
		return plannedWidth;
	}

	public void setPlannedWidth(Float plannedWidth) {
		this.plannedWidth = plannedWidth;
	}

	public Float getPlannedWeight() {
		return plannedWeight;
	}

	public void setPlannedWeight(Float plannedWeight) {
		this.plannedWeight = plannedWeight;
	}

	public Integer getPlannedNoOfPieces() {
		return plannedNoOfPieces;
	}

	public void setPlannedNoOfPieces(Integer plannedNoOfPieces) {
		this.plannedNoOfPieces = plannedNoOfPieces;
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

	public DeliveryDetails getDeliveryDetails() {
		return deliveryDetails;
	}

	public void setDeliveryDetails(DeliveryDetails deliveryDetails) {
		this.deliveryDetails = deliveryDetails;
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

	public Rates getRates() {
		return rates;
	}

	public void setRates(Rates rates) {
		this.rates = rates;
	}

	public PacketClassification getPacketClassification() {
		return packetClassification;
	}

	public void setPacketClassification(PacketClassification packetClassification) {
		this.packetClassification = packetClassification;
	}

	public Integer getParentGroupId() {
		return parentGroupId;
	}

	public void setParentGroupId(Integer parentGroupId) {
		this.parentGroupId = parentGroupId;
	}
}
