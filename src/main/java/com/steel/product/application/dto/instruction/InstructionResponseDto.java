package com.steel.product.application.dto.instruction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.steel.product.application.dto.delivery.DeliveryResponseDto;
import com.steel.product.application.dto.process.ProcessDto;
import com.steel.product.application.dto.status.StatusDto;
import com.steel.product.application.entity.PacketClassification;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class InstructionResponseDto {
    private Integer instructionId ;

    private Integer inwardEntryId;

    private ProcessDto process;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
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

    private Integer groupId ;

    private Integer parentGroupId ;

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

}
