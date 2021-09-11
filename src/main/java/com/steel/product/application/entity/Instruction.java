package com.steel.product.application.entity;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.steel.product.application.dto.instruction.InstructionResponseDto;
import com.steel.product.application.dto.pdf.InstructionResponsePdfDto;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "product_instruction")
public class Instruction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "instructionid")
	private Integer instructionId ;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inwardid")
	private InwardEntry inwardId;

	@ManyToOne(fetch = FetchType.EAGER)
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
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "status")
	private Status status;

	@JsonManagedReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "packet_classification_id")
	private PacketClassification packetClassification;
	
	@Column(name = "groupid")
	private Integer groupId ;

	@Column(name = "parentgroupid")
	private Integer parentGroupId ;

	@OneToMany(mappedBy = "parentInstruction", cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Instruction> childInstructions;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parentinstructionid", referencedColumnName = "instructionid")
	private Instruction parentInstruction;
	
	@Column(name = "wastage")
	private Float wastage;
	
	@Column(name = "damage")
	private Float damage;
	
	@Column(name = "packingweight")
	private Float packingWeight;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rateid")
	private Rates rates;

	@ManyToOne(fetch = FetchType.EAGER)
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

	@Column(name = "isSlitAndCut", columnDefinition = "BIT")
	private Boolean isSlitAndCut;

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

	public Boolean getDeleted() {
		return isDeleted;
	}

	public void setDeleted(Boolean deleted) {
		isDeleted = deleted;
	}

	public Boolean getSlitAndCut() {
		return isSlitAndCut;
	}

	public void setSlitAndCut(Boolean slitAndCut) {
		isSlitAndCut = slitAndCut;
	}

	public void addChildInstruction(Instruction instruction){
		this.childInstructions.add(instruction);
		instruction.setParentInstruction(this);
	}

	public void removeChildInstruction(Instruction instruction) {
		this.getChildInstructions().remove(instruction);
		instruction.setParentInstruction(null);
	}

	public static InstructionResponseDto valueOf(Instruction instruction){
		InstructionResponseDto instructionResponseDto = new InstructionResponseDto();
		instructionResponseDto.setStatus(Status.valueOf(instruction.getStatus()));
		instructionResponseDto.setParentInstructionId(instruction.getParentInstruction() != null ?
				instruction.getParentInstruction().getInstructionId() : null);
		instructionResponseDto.setPacketClassification(instruction.getPacketClassification() != null ?
				instruction.getPacketClassification(): null);
		instructionResponseDto.setInstructionDate(instruction.getInstructionDate());
		instructionResponseDto.setInstructionId(instruction.getInstructionId());
		instructionResponseDto.setProcess(instruction.getProcess() != null ? Process.valueOf(instruction.getProcess()) : null);
		instructionResponseDto.setPlannedWeight(instruction.getPlannedWeight());
		instructionResponseDto.setPlannedWidth(instruction.getPlannedWidth());
		instructionResponseDto.setPlannedLength(instruction.getPlannedLength());
		instructionResponseDto.setPlannedNoOfPieces(instruction.getPlannedNoOfPieces());
		instructionResponseDto.setActualWidth(instruction.getActualWidth());
		instructionResponseDto.setActualWeight(instruction.getActualWeight());
		instructionResponseDto.setActualLength(instruction.getActualLength());
		instructionResponseDto.setActualNoOfPieces(instruction.getActualNoOfPieces());
		instructionResponseDto.setInwardId(instruction.getInwardId() != null ? instruction.getInwardId().getInwardEntryId() : null);
		instructionResponseDto.setDeleted(instruction.getIsDeleted());
		instructionResponseDto.setGroupId(instruction.getGroupId());
		instructionResponseDto.setDamage(instruction.getDamage());
		instructionResponseDto.setCreatedOn(instruction.getCreatedOn());
		instructionResponseDto.setUpdatedOn(instruction.getUpdatedOn());
		instructionResponseDto.setUpdatedBy(instruction.updatedBy);
		instructionResponseDto.setCreatedBy(instruction.getCreatedBy());
		instructionResponseDto.setPackingWeight(instruction.getPackingWeight());
		instructionResponseDto.setWastage(instruction.getWastage());
		instructionResponseDto.setRemarks(instruction.getRemarks());
		instructionResponseDto.setParentGroupId(instruction.getParentGroupId());
		instructionResponseDto.setDeliveryDetails(instruction.getDeliveryDetails() != null ? DeliveryDetails.valueOf(instruction.getDeliveryDetails()) : null);
		instructionResponseDto.setChildInstructions((instruction.getChildInstructions() != null && instruction.getChildInstructions().size() > 0)
				? instruction.getChildInstructions().stream().map(ci -> Instruction.valueOf(ci)).collect(Collectors.toList()) : null);
		instructionResponseDto.setSlitAndCut(instruction.getSlitAndCut());
		return instructionResponseDto;
	}

	public static InstructionResponsePdfDto valueOfInstructionPdf(Instruction instruction){
		InstructionResponsePdfDto instructionResponsePdfDto = new InstructionResponsePdfDto();
		instructionResponsePdfDto.setPacketClassification(instruction.getPacketClassification() != null ?
				instruction.getPacketClassification(): null);
		instructionResponsePdfDto.setPlannedLength(instruction.getPlannedLength());
		instructionResponsePdfDto.setPlannedNoOfPieces(instruction.getPlannedNoOfPieces());
		instructionResponsePdfDto.setPlannedWeight(instruction.getPlannedWeight());
		instructionResponsePdfDto.setPlannedWidth(instruction.getPlannedWidth());
		instructionResponsePdfDto.setActualLength(instruction.getActualLength());
		instructionResponsePdfDto.setActualNoOfPieces(instruction.getActualNoOfPieces());
		instructionResponsePdfDto.setActualWeight(instruction.getActualWeight());
		instructionResponsePdfDto.setActualWidth(instruction.getActualWidth());
		instructionResponsePdfDto.setDeliveryDetails(instruction.getDeliveryDetails() != null ? DeliveryDetails.valueOf(instruction.getDeliveryDetails()) : null);
		instructionResponsePdfDto.setRemarks(instruction.getRemarks());
		return instructionResponsePdfDto;
	}
}
