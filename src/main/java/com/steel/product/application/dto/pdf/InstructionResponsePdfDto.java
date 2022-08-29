package com.steel.product.application.dto.pdf;

import com.steel.product.application.dto.delivery.DeliveryResponseDto;
import com.steel.product.application.dto.process.ProcessDto;
import com.steel.product.application.entity.EndUserTagsEntity;
import com.steel.product.application.entity.PacketClassification;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstructionResponsePdfDto {
    private Integer instructionId ;

    private Integer inwardEntryId;

    private ProcessDto process;

    private Float plannedLength;

    private Float plannedWidth;

    private Float plannedWeight;

    private Integer plannedNoOfPieces;

    private Float actualLength;

    private Float actualWidth;

    private Float actualWeight;

    private Integer actualNoOfPieces;

    private String remarks;

    private PacketClassification packetClassification;

    private EndUserTagsEntity endUserTagsEntity;

    private DeliveryResponseDto deliveryDetails;

    private Float valueOfGoods;

    private Long countOfWeight;
    
    private String baseTotalPrice;

    private String additionalTotalPrice;

    private String packingRate;
    
    private String totalPrice;

}
