package com.steel.product.application.dto.delivery;

import com.steel.product.application.dto.instruction.InstructionDto;
import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.entity.Instruction;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DeliveryPacketsDto {

    private DeliveryDetails deliveryDetails;

    private List<InstructionDto> instructions;

    private String partyName;

    private String customerBatchId;

    private String coilNumber;

    private String customerInvoiceNo;

    private Date customerInvoiceDate;

    public DeliveryPacketsDto() {
    }

    public DeliveryPacketsDto(DeliveryDetails deliveryDetails, List<Instruction> instructions) {
        this.deliveryDetails = deliveryDetails;
        this.instructions = instructions.size() > 0 ? instructions.stream().map(i -> Instruction.valueOf(i)).collect(Collectors.toList()) : null;
        this.partyName = instructions.size() > 0 ? instructions.get(0).getInwardId().getParty().getPartyName() : "";
        this.customerBatchId = instructions.size() > 0 ? instructions.get(0).getInwardId().getCustomerBatchId() : "";
        this.coilNumber = instructions.size() > 0 ? instructions.get(0).getInwardId().getCoilNumber(): "";
        this.customerInvoiceNo = deliveryDetails.getCustomerInvoiceNo();
        this.customerInvoiceDate = deliveryDetails.getCustomerInvoiceDate();
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

    public String getCustomerInvoiceNo() {
        return customerInvoiceNo;
    }

    public void setCustomerInvoiceNo(String customerInvoiceNo) {
        this.customerInvoiceNo = customerInvoiceNo;
    }

    public Date getCustomerInvoiceDate() {
        return customerInvoiceDate;
    }

    public void setCustomerInvoiceDate(Date customerInvoiceDate) {
        this.customerInvoiceDate = customerInvoiceDate;
    }
}
