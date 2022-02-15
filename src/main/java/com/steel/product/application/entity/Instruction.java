package com.steel.product.application.entity;



import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.steel.product.application.dto.instruction.InstructionResponseDto;
import com.steel.product.application.dto.pdf.InstructionResponsePdfDto;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.FetchMode;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Table(name = "product_instruction")
public class Instruction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "instructionid")
	private Integer instructionId ;

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
	private Set<Instruction> childInstructions;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deliveryid")
    private DeliveryDetails deliveryDetails;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "createdby")
    private Integer createdBy;

    @Column(name = "updatedby")
    private Integer updatedBy;

    @CreationTimestamp
	@Column(name="createdon")
    private Date createdOn;

    @UpdateTimestamp
	@Column(name="updatedon")
    private Date updatedOn;

    @Column(name = "isdeleted", columnDefinition = "BIT")
    private Boolean isDeleted;

    @Column(name = "is_slit_and_cut", columnDefinition = "BIT")
    private Boolean isSlitAndCut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_Details_id")
    private PartDetails partDetails;

    public void addChildInstruction(Instruction instruction) {
        this.getChildInstructions().add(instruction);
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
        instructionResponseDto.setInwardEntryId(instruction.getInwardId() != null ? instruction.getInwardId().getInwardEntryId() : null);
        instructionResponseDto.setIsDeleted(instruction.getIsDeleted());
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
        instructionResponseDto.setChildInstructions((instruction.getChildInstructions() != null && !instruction.getChildInstructions().isEmpty())
                ? instruction.getChildInstructions().stream().map(ci -> Instruction.valueOf(ci)).collect(Collectors.toList()) : null);
        instructionResponseDto.setIsSlitAndCut(instruction.getIsSlitAndCut());
		instructionResponseDto.setPartId(instruction.getPartDetails() != null ? instruction.getPartDetails().getId() : null);
		instructionResponseDto.setPartDetailsId(instruction.getPartDetails() != null ? instruction.getPartDetails().getPartDetailsId(): null);
        return instructionResponseDto;
    }

	public static InstructionResponsePdfDto valueOfInstructionPdf(Instruction instruction, InwardEntry inwardEntry) {
		InstructionResponsePdfDto instructionResponsePdfDto = new InstructionResponsePdfDto();
		instructionResponsePdfDto.setPacketClassification(instruction.getPacketClassification() != null ?
				instruction.getPacketClassification() : null);
		instructionResponsePdfDto.setProcess(Process.valueOf(instruction.getProcess()));
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
		if (inwardEntry != null) {
			instructionResponsePdfDto.setValueOfGoods((float) ((instruction.getActualWeight() / inwardEntry.getfQuantity()) * inwardEntry.getValueOfGoods()));
		}
		return instructionResponsePdfDto;
	}
}
