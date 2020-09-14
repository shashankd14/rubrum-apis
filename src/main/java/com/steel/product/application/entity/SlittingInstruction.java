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
@Table(name = "product_tblslittinginstruction")
public class SlittingInstruction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "slittinginstructionid")
  private int slittingInstructionId;
  
  @Column(name = "inwardentryid")
  private int inwardEntryId;
  
  @Column(name = "coilnumber")
  private String coilNumber;
  
  @Column(name = "nsno")
  private int nSno;
  
  @Column(name = "nwidth")
  private float nWidth;
  
  @Column(name = "vstatus")
  private int vStatus;
  
  @Column(name = "nlength")
  private float nLength;
  
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
}
