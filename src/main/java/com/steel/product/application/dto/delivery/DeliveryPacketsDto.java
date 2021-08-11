package com.steel.product.application.dto.delivery;

import com.steel.product.application.dto.material.MaterialResponseDto;
import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.entity.Material;

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
        this.partyName = deliveryDetails.getInstruction().get(0).getInwardId().getParty() != null ? deliveryDetails.getInstruction().get(0).getInwardId().getParty().getPartyName() : "";
        this.customerBatchId = deliveryDetails.getInstruction().get(0).getInwardId().getCustomerBatchId();
        this.coilNumber = deliveryDetails.getInstruction().get(0).getInwardId().getCoilNumber();
        this.fThickness = deliveryDetails.getInstruction().get(0).getInwardId().getfThickness();
        this.materialResponseDto = Material.valueOf(deliveryDetails.getInstruction().get(0).getInwardId().getMaterial(),deliveryDetails.getInstruction().get(0).getInwardId());
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
