package com.steel.product.application.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.steel.product.application.dto.delivery.DeliveryResponseDto;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "product_tbl_delivery_details")
public class DeliveryDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deliveryid")
    private Integer deliveryId;

    @Column(name = "vehicleno")
    private String vehicleNo;

    @Column(name = "totalweight")
    private Float totalWeight;

    @Column(name = "createdby")
    private Integer createdBy;

    @Column(name = "updatedby")
    private Integer updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdon")
    private Date createdOn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updatedon")
    private Date updatedOn;

    @Column(name = "isdeleted", columnDefinition = "BIT")
    private Boolean isDeleted;

    @Column(name = "customerInvoiceNo")
    private String customerInvoiceNo;

    @Column(name = "customerInvoiceDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date customerInvoiceDate;

    @OneToMany(mappedBy = "deliveryDetails", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH,
            CascadeType.REFRESH })
    private Set<Instruction> instructions;

    public void addInstruction(Instruction instruction){
        if(this.instructions == null){
            this.instructions = new HashSet<>();
        }
        this.getInstructions().add(instruction);
        instruction.setDeliveryDetails(this);
    }

    public void removeInstruction(Instruction instruction){
        this.getInstructions().remove(instruction);
        instruction.setDeliveryDetails(null);
    }

    public Integer getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Integer deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public Float getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Float totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
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

    public Set<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(Set<Instruction> instruction) {
        this.instructions = instruction;
    }

    public static DeliveryResponseDto valueOf(DeliveryDetails deliveryDetails){
        DeliveryResponseDto deliveryResponseDto = new DeliveryResponseDto();
        deliveryResponseDto.setDeliveryId(deliveryDetails.getDeliveryId());
        deliveryResponseDto.setCustomerInvoiceDate(deliveryDetails.getCustomerInvoiceDate());
        deliveryResponseDto.setCustomerInvoiceNo(deliveryDetails.getCustomerInvoiceNo());
        deliveryResponseDto.setTotalWeight(deliveryDetails.getTotalWeight());
        deliveryResponseDto.setCreatedBy(deliveryDetails.getCreatedBy());
        deliveryResponseDto.setDeleted(deliveryDetails.getDeleted());
        deliveryResponseDto.setCreatedOn(deliveryDetails.getCreatedOn());
        deliveryResponseDto.setUpdatedBy(deliveryDetails.getUpdatedBy());
        deliveryResponseDto.setUpdatedOn(deliveryDetails.getUpdatedOn());
        deliveryResponseDto.setVehicleNo(deliveryDetails.getVehicleNo());
//        deliveryResponseDto.setInstruction(deliveryDetails.getInstruction().stream().map(i -> Instruction.valueOf(i)).collect(Collectors.toList()));
        return deliveryResponseDto;
    }
}
