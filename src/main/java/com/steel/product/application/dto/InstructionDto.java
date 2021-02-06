package com.steel.product.application.dto;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class InstructionDto {

	private int instructionId ;
	
	private Integer inwardId;
	
	private Integer processdId;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date  instructionDate;
	
	private Float length;
	
	private Float width;
	
	private Float weight;
	
	private Integer noOfPieces;

	private Float actualLength;

	private Float actualWidth;

	private Float actualWeight;

	private Integer actualNoOfPieces;
	
	private Integer status;
	
	private Integer groupId ;
	
	private Integer parentInstructionId;
	
	private Float wastage;
	
	private Float damage;
	
	private Float packingWeight;
	   
	private Integer createdBy;

	private Integer updatedBy;

	private Date createdOn;

	private Date updatedOn;

	private Boolean isDeleted;

	public int getInstructionId() {
		return instructionId;
	}

	public void setInstructionId(int instructionId) {
		this.instructionId = instructionId;
	}

	public Integer getInwardId() {
		return inwardId;
	}

	public void setInwardId(Integer inwardId) {
		this.inwardId = inwardId;
	}

	public Integer getProcessdId() {
		return processdId;
	}

	public void setProcessdId(Integer processdId) {
		this.processdId = processdId;
	}

	public Date getInstructionDate() {
		return instructionDate;
	}

	public void setInstructionDate(Date instructionDate) {
		this.instructionDate = instructionDate;
	}

	public Float getLength() {
		return length;
	}

	public void setLength(Float length) {
		this.length = length;
	}

	public Float getWidth() {
		return width;
	}

	public void setWidth(Float width) {
		this.width = width;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public Integer getNoOfPieces() {
		return noOfPieces;
	}

	public void setNoOfPieces(Integer noOfPieces) {
		this.noOfPieces = noOfPieces;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getParentInstructionId() {
		return parentInstructionId;
	}

	public void setParentInstructionId(Integer parentInstructionId) {
		this.parentInstructionId = parentInstructionId;
	}

	public Float getWastage() {
		return wastage;
	}

	public void setWastage(Float wastage) {
		this.wastage = wastage;
	}

	public Float getDamage() {
		return damage;
	}

	public void setDamage(Float damage) {
		this.damage = damage;
	}

	public Float getPackingWeight() {
		return packingWeight;
	}

	public void setPackingWeight(Float packingWeight) {
		this.packingWeight = packingWeight;
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

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
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
}
