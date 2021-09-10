package com.steel.product.application.dto.delivery;

import com.steel.product.application.dto.material.MaterialResponseDto;
import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.Material;

import java.util.Iterator;

public class DeliveryPacketsDto {

    private DeliveryResponseDto deliveryDetails;

    private String partyName;

    private String customerBatchId;

    private String coilNumber;

    private Float fThickness;

    private MaterialResponseDto materialResponseDto;

    public DeliveryPacketsDto() {
    }

    public DeliveryPacketsDto(DeliveryDetails deliveryDetails) {
        this.deliveryDetails = DeliveryDetails.valueOf(deliveryDetails);
        Instruction instruction = deliveryDetails.getInstructions().iterator().next();
        this.partyName = instruction.getInwardId().getParty() != null ? instruction.getInwardId().getParty().getPartyName() : "";
        this.customerBatchId = instruction.getInwardId().getCustomerBatchId();
        this.coilNumber = instruction.getInwardId().getCoilNumber();
        this.fThickness = instruction.getInwardId().getfThickness();
        this.materialResponseDto = Material.valueOf(instruction.getInwardId().getMaterial(),instruction.getInwardId());
    }


    public DeliveryResponseDto getDeliveryDetails() {
        return deliveryDetails;
    }

    public void setDeliveryDetails(DeliveryResponseDto deliveryDetails) {
        this.deliveryDetails = deliveryDetails;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getCustomerBatchId() {
        return customerBatchId;
    }

    public void setCustomerBatchId(String customerBatchId) {
        this.customerBatchId = customerBatchId;
    }

    public String getCoilNumber() {
        return coilNumber;
    }

    public void setCoilNumber(String coilNumber) {
        this.coilNumber = coilNumber;
    }


    public Float getfThickness() {
        return fThickness;
    }

    public void setfThickness(Float fThickness) {
        this.fThickness = fThickness;
    }

    public MaterialResponseDto getMaterialResponseDto() {
        return materialResponseDto;
    }

    public void setMaterialResponseDto(MaterialResponseDto materialResponseDto) {
        this.materialResponseDto = materialResponseDto;
    }
}
