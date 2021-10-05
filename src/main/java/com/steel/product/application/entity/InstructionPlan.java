package com.steel.product.application.entity;

import com.steel.product.application.dto.instructionPlan.InstructionPlanDto;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "PRODUCT_INSTRUCTION_PLAN")
public class InstructionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "INSTRUCTION_PLAN_ID", nullable = false)
    private String planId;

    @Column(name = "TARGET_WEIGHT", nullable = false)
    private Float targetWeight;

    @Column(name = "NO_OF_PARTS", nullable = false)
    private Integer noOfParts;

    @Column(name = "IS_EQUAL", columnDefinition = "boolean default false", nullable = false)
    private Boolean isEqual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCESS_ID", nullable = false)
    private Process process;

    @Column(name = "CREATED_BY")
    private Integer createdBy;

    @Column(name = "UPDATED_BY")
    private Integer updatedBy;

    @CreationTimestamp
    @Column(name = "CREATED_ON", updatable = false)
    private Date createdOn;

    @UpdateTimestamp
    @Column(name = "UPDATED_ON")
    private Date updatedOn;

    @Column(name = "IS_DELETED", columnDefinition = "boolean default false")
    private Boolean isDeleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public Float getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(Float targetWeight) {
        this.targetWeight = targetWeight;
    }

    public Integer getNoOfParts() {
        return noOfParts;
    }

    public void setNoOfParts(Integer noOfParts) {
        this.noOfParts = noOfParts;
    }

    public Boolean getEqual() {
        return isEqual;
    }

    public void setEqual(Boolean equal) {
        isEqual = equal;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
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

    public InstructionPlan entityOf(InstructionPlanDto instructionPlanDto, Process process) {
        InstructionPlan instructionPlan = new InstructionPlan();
        instructionPlan.setTargetWeight(instructionPlanDto.getTargetWeight());
        instructionPlan.setNoOfParts(instructionPlanDto.getNoOfParts());
        instructionPlan.setEqual(instructionPlanDto.getEqual());
        instructionPlan.setProcess(process);
        return instructionPlan;
    }
}
