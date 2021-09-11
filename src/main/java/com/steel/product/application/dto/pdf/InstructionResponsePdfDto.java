package com.steel.product.application.dto.pdf;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.steel.product.application.dto.delivery.DeliveryResponseDto;
import com.steel.product.application.dto.instruction.InstructionResponseDto;
import com.steel.product.application.dto.process.ProcessDto;
import com.steel.product.application.dto.status.StatusDto;
import com.steel.product.application.entity.PacketClassification;

import java.util.Date;
import java.util.List;

public class InstructionResponsePdfDto {
    private Integer instructionId ;

    private Integer inwardId;

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

    private DeliveryResponseDto deliveryDetails;

    public Integer getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(Integer instructionId) {
        this.instructionId = instructionId;
    }

    public Integer getInwardId() {
        return inwardId;
    }

    public void setInwardId(Integer inwardId) {
        this.inwardId = inwardId;
    }

    public ProcessDto getProcess() {
        return process;
    }

    public void setProcess(ProcessDto process) {
        this.process = process;
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

    public PacketClassification getPacketClassification() {
        return packetClassification;
    }

    public void setPacketClassification(PacketClassification packetClassification) {
        this.packetClassification = packetClassification;
    }

    public DeliveryResponseDto getDeliveryDetails() {
        return deliveryDetails;
    }

    public void setDeliveryDetails(DeliveryResponseDto deliveryDetails) {
        this.deliveryDetails = deliveryDetails;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
