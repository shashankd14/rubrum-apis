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
@Table(name = "product_cuts")
public class CuttingInstruction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "cuttinginstructionid")
  private int cuttingInstructionId;
  
  @Column(name = "inwardentryid")
  private int inwardEntryId;
  
  @Temporal(TemporalType.DATE)
  @Column(name = "cuttinginstructiondate")
  private Date cuttingInstructionDate;
  
  @Column(name = "nsno")
  private int nSno;
  
  @Column(name = "nlength")
  private float nLength;
  
  @Column(name = "nnoofpieces")
  private String nNoOfPieces;
  
  @Column(name = "nbundleweight")
  private float nBundleWeight;
  
  @Column(name = "vstatus")
  private int vStatus;
  
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
  
  public int getInwardEntryId() {
    return this.inwardEntryId;
  }
  
  public void setInwardEntryId(int inwardEntryId) {
    this.inwardEntryId = inwardEntryId;
  }
  
  public int getCuttingInstructionId() {
    return this.cuttingInstructionId;
  }
  
  public void setCuttingInstructionId(int cuttingInstructionId) {
    this.cuttingInstructionId = cuttingInstructionId;
  }
  
  public int getnSno() {
    return this.nSno;
  }
  
  public void setnSno(int nSno) {
    this.nSno = nSno;
  }
  
  public float getnLength() {
    return this.nLength;
  }
  
  public void setnLength(float nLength) {
    this.nLength = nLength;
  }
  
  public String getnNoOfPieces() {
    return this.nNoOfPieces;
  }
  
  public void setnNoOfPieces(String nNoOfPieces) {
    this.nNoOfPieces = nNoOfPieces;
  }
  
  public float getnBundleWeight() {
    return this.nBundleWeight;
  }
  
  public void setnBundleWeight(float nBundleWeight) {
    this.nBundleWeight = nBundleWeight;
  }
  
  public int getvStatus() {
    return this.vStatus;
  }
  
  public void setvStatus(int vStatus) {
    this.vStatus = vStatus;
  }
  
  public int getCreatedBy() {
    return this.createdBy;
  }
  
  public void setCreatedBy(int createdBy) {
    this.createdBy = createdBy;
  }
  
  public int getUpdatedBy() {
    return this.updatedBy;
  }
  
  public void setUpdatedBy(int updatedBy) {
    this.updatedBy = updatedBy;
  }
  
  public Date getCreatedOn() {
    return this.createdOn;
  }
  
  public void setCreatedOn(Date createdOn) {
    this.createdOn = createdOn;
  }
  
  public Date getUpdatedOn() {
    return this.updatedOn;
  }
  
  public Date getCuttingInstructionDate() {
    return this.cuttingInstructionDate;
  }
  
  public void setCuttingInstructionDate(Date cuttingInstructionDate) {
    this.cuttingInstructionDate = cuttingInstructionDate;
  }
  
  public void setUpdatedOn(Date updatedOn) {
    this.updatedOn = updatedOn;
  }
  
  public Boolean getIsDeleted() {
    return this.isDeleted;
  }
  
  public void setIsDeleted(Boolean isDeleted) {
    this.isDeleted = isDeleted;
  }
}
