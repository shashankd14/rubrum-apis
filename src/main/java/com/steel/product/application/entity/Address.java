package com.steel.product.application.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.steel.product.application.dto.address.AddressDto;

import javax.persistence.*;
import java.util.Set;

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

  @JsonIgnore
  @OneToMany(mappedBy = "address1")
  private Set<Party> parties1;

  @JsonIgnore
  @OneToMany(mappedBy = "address2")
  private Set<Party> parties2;

  public Set<Party> getParties1() {
    return parties1;
  }

  public void setParties1(Set<Party> parties1) {
    this.parties1 = parties1;
  }

  public Set<Party> getParties2() {
    return parties2;
  }

  public void setParties2(Set<Party> parties2) {
    this.parties2 = parties2;
  }

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

  public static AddressDto valueOf(Address address){
    AddressDto addressDto = new AddressDto();
    addressDto.setAddressId(address.getAddressId());
    addressDto.setCity(address.getCity());
    addressDto.setPincode(address.getPincode());
    addressDto.setState(address.getState());
    addressDto.setDetails(address.getDetails());
    return addressDto;
  }

  public Address toEntity(AddressDto addressDto){
    Address address = new Address();
    address.setCity(addressDto.getCity());
    address.setDetails(addressDto.getDetails());
    address.setPincode(addressDto.getPincode());
    address.setState(addressDto.getState());
    return address;
  }

  @Override
  public String toString() {
    return this.details+", "+this.city+", "+", "+this.state+" - "+this.pincode;
  }
}
