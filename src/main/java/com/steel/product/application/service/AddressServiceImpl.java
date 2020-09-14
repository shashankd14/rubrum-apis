package com.steel.product.application.service;

import com.steel.product.application.dao.AddressRepository;
import com.steel.product.application.entity.Address;
import com.steel.product.application.service.AddressService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl implements AddressService {
  private AddressRepository addressRepo;
  
  @Autowired
  public AddressServiceImpl(AddressRepository theAddressRepo) {
    this.addressRepo = theAddressRepo;
  }
  
  public Address saveAddress(Address address) {
    return (Address)this.addressRepo.save(address);
  }
  
  public List<Address> getAllAddress() {
    return this.addressRepo.findAll();
  }
  
  public Address getAddressById(int addressId) {
    Optional<Address> result = this.addressRepo.findById(Integer.valueOf(addressId));
    Address theAddress = null;
    if (result.isPresent()) {
      theAddress = result.get();
    } else {
      throw new RuntimeException("Did not find address id - " + addressId);
    } 
    return theAddress;
  }
}
