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
  
  @Column(name = "details")
  private String details;
  
  @Column(name = "city")
  private String city;

  @Column(name = "state")
  private String state;

  @Column(name = "pincode")
  private int pincode;

  public int getAddressId() {
    return addressId;
  }

  public void setAddressId(int addressId) {
    this.addressId = addressId;
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public int getPincode() {
    return pincode;
  }

  public void setPincode(int pincode) {
    this.pincode = pincode;
  }

  @Override
  public String toString() {
    return this.details+", "+this.city+", "+", "+this.state+" - "+this.pincode;
  }
}
