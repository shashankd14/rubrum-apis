package com.steel.product.application.dto.delivery;

import com.steel.product.application.dto.instruction.InstructionDto;
import com.steel.product.application.dto.material.MaterialDto;
import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.Material;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DeliveryPacketsDto {

    private DeliveryDetails deliveryDetails;

    private List<InstructionDto> instructions;

    private String partyName;

    private String customerBatchId;

    private String coilNumber;

    private Float fThickness;

    private MaterialDto materialDto;

    public DeliveryPacketsDto() {
    }

    public DeliveryPacketsDto(DeliveryDetails deliveryDetails, List<Instruction> instructions) {
        this.deliveryDetails = deliveryDetails;
        this.instructions = instructions.size() > 0 ? instructions.stream().map(i -> Instruction.valueOf(i)).collect(Collectors.toList()) : null;
        this.partyName = instructions.size() > 0 ? instructions.get(0).getInwardId().getParty().getPartyName() : "";
        this.customerBatchId = instructions.size() > 0 ? instructions.get(0).getInwardId().getCustomerBatchId() : "";
        this.coilNumber = instructions.size() > 0 ? instructions.get(0).getInwardId().getCoilNumber(): "";
        this.fThickness = instructions.size() > 0 ? instructions.get(0).getInwardId().getfThickness(): null;
        this.materialDto = instructions.size() > 0 ? Material.valueOf(instructions.get(0).getInwardId().getMaterial(),instructions.get(0).getInwardId()):null;
    }

    public DeliveryDetails getDeliveryDetails() {
        return deliveryDetails;
    }

    public void setDeliveryDetails(DeliveryDetails deliveryDetails) {
        this.deliveryDetails = deliveryDetails;
    }

    public List<InstructionDto> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<InstructionDto> instructions) {
        this.instructions = instructions;
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

    public MaterialDto getMaterialDto() {
        return materialDto;
    }

    public void setMaterialDto(MaterialDto materialDto) {
        this.materialDto = materialDto;
    }
}
