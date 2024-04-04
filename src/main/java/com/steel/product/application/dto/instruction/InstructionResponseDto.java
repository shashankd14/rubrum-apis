package com.steel.product.application.dto.instruction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.steel.product.application.dto.delivery.DeliveryResponseDto;
import com.steel.product.application.dto.inward.InwardEntryResponseDto;
import com.steel.product.application.dto.partDetails.PartDetailsResponse;
import com.steel.product.application.dto.process.ProcessDto;
import com.steel.product.application.dto.status.StatusDto;
import com.steel.product.application.entity.EndUserTagsEntity;
import com.steel.product.application.entity.PacketClassification;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class InstructionResponseDto {
	
	private Integer instructionId;

	private Integer inwardEntryId;

	private InwardEntryResponseDto inwardEntryResponseDto;

	private ProcessDto process;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date instructionDate;

	private Float plannedLength;

	private Float plannedWidth;

	private Float plannedWeight;

	private Integer plannedNoOfPieces;

	private Float actualLength;

	private Float actualWidth;

	private Float actualWeight;

	private Integer actualNoOfPieces;

	private StatusDto status;

	private PacketClassification packetClassification;

	private EndUserTagsEntity endUserTagsentity;

	private Integer groupId;

	private Integer parentGroupId;

	private Integer parentInstructionId;

	private Float wastage;

	private Float damage;

	private Float packingWeight;

	private Integer createdBy;

	private Integer updatedBy;

	private Date createdOn;

	private Date updatedOn;

	private Boolean isDeleted;

	private String remarks;

	private Boolean isSlitAndCut;

	private List<InstructionResponseDto> childInstructions;

	private DeliveryResponseDto deliveryDetails;

	private PartDetailsResponse partDetails;

	private Long partId;

	private String partDetailsId;

	private String pdfS3Url;

	private BigDecimal plannedYieldLossRatio;

	private BigDecimal actualYieldLossRatio;

}
