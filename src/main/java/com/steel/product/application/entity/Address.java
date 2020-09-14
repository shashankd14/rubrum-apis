package com.steel.product.application.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "product_address")
public class Address {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "addressid")
  private int addressId;
  
  @Column(name = "line1")
  private String line1;
  
  @Column(name = "line2")
  private String line2;
  
  public int getAddressId() {
    return this.addressId;
  }
  
  public void setAddressId(int addressId) {
    this.addressId = addressId;
  }
  
  public String getLine1() {
    return this.line1;
  }
  
  public void setLine1(String line1) {
    this.line1 = line1;
  }
  
  public String getLine2() {
    return this.line2;
  }
  
  public void setLine2(String line2) {
    this.line2 = line2;
  }
}
