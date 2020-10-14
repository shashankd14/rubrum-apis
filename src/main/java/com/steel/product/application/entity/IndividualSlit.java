package com.steel.product.application.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "product_slittinginstruction")
public class IndividualSlit {

	  @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	  @Column(name = "slittinginstructionid")
	  private int slittingInstructionId;
	 
	  @Column(name = "parentcoilid")
	  private int parentCoilId;
	  
	  @Column(name = "noofpieces")
	  private int noOfPieces;
	  
	  @Column(name = "weight")
	  private float weight;
	  
	  @Column(name = "wastage")
	  private float wastage;
	  
	  @Column(name = "damages")
	  private float damages;
	  
	  @Column(name = "createdby")
	  private int createdBy;
	
	  @Column(name = "updatedby")
	  private int updatedBy;
	
	  @Temporal(TemporalType.TIMESTAMP)
	  @Column(name = "createdon")
	  private Date createdOn;
	
	  @Temporal(TemporalType.TIMESTAMP)
	  @Column(name = "updatedon")
      private Date updatedOn;
	
	  @Column(name = "isdeleted", columnDefinition = "BIT")
	  private Boolean isDeleted;

	public int getSlittingInstructionId() {
		return slittingInstructionId;
	}

	public void setSlittingInstructionId(int slittingInstructionId) {
		this.slittingInstructionId = slittingInstructionId;
	}

	public int getParentCoilId() {
		return parentCoilId;
	}

	public void setParentCoilId(int parentCoilId) {
		this.parentCoilId = parentCoilId;
	}

	public int getNoOfPieces() {
		return noOfPieces;
	}

	public void setNoOfPieces(int noOfPieces) {
		this.noOfPieces = noOfPieces;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public float getWastage() {
		return wastage;
	}

	public void setWastage(float wastage) {
		this.wastage = wastage;
	}

	public float getDamages() {
		return damages;
	}

	public void setDamages(float damages) {
		this.damages = damages;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public int getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(int updatedBy) {
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
	  
	  
}
