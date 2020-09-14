package com.steel.product.application.service;

import com.steel.product.application.entity.Address;
import java.util.List;

public interface AddressService {
  Address saveAddress(Address paramAddress);
  
  List<Address> getAllAddress();
  
  Address getAddressById(int paramInt);
}
