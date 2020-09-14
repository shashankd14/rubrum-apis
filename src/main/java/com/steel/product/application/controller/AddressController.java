package com.steel.product.application.controller;

import com.steel.product.application.entity.Address;
import com.steel.product.application.service.AddressService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@CrossOrigin(origins = {"http://localhost:3001"})
//@CrossOrigin(origins = {"http://rubrum-frontend.s3-website.ap-south-1.amazonaws.com"})
@CrossOrigin
@RequestMapping({"/api/address"})
public class AddressController {
  private AddressService addressSvc;
  
  public AddressController(AddressService theAddressSvc) {
    this.addressSvc = theAddressSvc;
  }
  
  @PostMapping({"/addNew"})
  public ResponseEntity<Object> saveAddress(@RequestBody Address address) {
    try {
      this.addressSvc.saveAddress(address);
      return new ResponseEntity("success", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } 
  }
  
  @GetMapping({"/list"})
  public ResponseEntity<Object> getAllAddress() {
    try {
      List<Address> addressList = new ArrayList<>();
      addressList = this.addressSvc.getAllAddress();
      return new ResponseEntity(addressList, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } 
  }
  
  @GetMapping({"/getById/{addressId}"})
  public ResponseEntity<Object> getAddressById(@PathVariable int addressId) {
    try {
      Address address = new Address();
      address = this.addressSvc.getAddressById(addressId);
      return new ResponseEntity(address, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } 
  }
}
