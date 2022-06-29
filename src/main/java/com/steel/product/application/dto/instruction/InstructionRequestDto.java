package com.steel.product.application.dto.instruction;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class InstructionRequestDto {

    private int instructionId;

    private Integer inwardId;

    private Integer processId;

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

    private Integer status;

    private Integer packetClassificationId;

    private Integer endUserTagId;

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
}
