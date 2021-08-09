package com.steel.product.application.service;

import com.steel.product.application.dao.PartyDetailsRepository;
import com.steel.product.application.dto.party.PartyDto;
import com.steel.product.application.entity.Address;
import com.steel.product.application.entity.Party;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class PartyDetailsServiceImpl implements PartyDetailsService {

  @Autowired
  private PartyDetailsRepository partyRepo;

  @Autowired
  private AddressService addressService;
  
  public PartyDetailsServiceImpl(PartyDetailsRepository thePartyRepo) {
    this.partyRepo = thePartyRepo;
  }
  
  public Party saveParty(PartyDto partyDto) {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    Party party = new Party();
    try{
      party.setnPartyId(partyDto.getPartyId());
      party.setPartyName(partyDto.getPartyName());
      party.setPartyNickname(partyDto.getPartyNickname());
      party.setContactName(partyDto.getContactName());
      party.setContactNumber(partyDto.getContactNumber());
      party.setGstNumber(partyDto.getGstNumber());
      party.setPanNumber(partyDto.getPanNumber());
      party.setTanNumber(partyDto.getTanNumber());
      party.setEmail1(partyDto.getEmail1());
      party.setEmail2(partyDto.getEmail2());
      party.setPhone1(partyDto.getPhone1());
      party.setPhone2(partyDto.getPhone2());

      Address address1 = new Address();
      Address address2 = new Address();

      if(partyDto.getAddress1() != null) {
        if (partyDto.getAddress1().getAddressId() == 0) {
          address1 = saveAddress(address1.toEntity(partyDto.getAddress1()));
        } else {
          address1 = addressService.saveAddress(address2.toEntity(partyDto.getAddress1()));
        }
        party.setAddress1(address1);
      }
      if(partyDto.getAddress2() != null) {
        if (partyDto.getAddress2().getAddressId() == 0) {
          address2 = saveAddress(address2.toEntity(partyDto.getAddress2()));
        } else {
          address2 = addressService.saveAddress(address2.toEntity(partyDto.getAddress2()));
        }
        party.setAddress2(address2);
      }

      party.setCreatedBy(1);
      party.setUpdatedBy(1);
      party.setCreatedOn(timestamp);
      party.setUpdatedOn(timestamp);
      party.setDeleted(false);

      return partyRepo.save(party);

    }catch (Exception e){
      e.printStackTrace();
      return null;
    }
  }
  
  public List<Party> getAllParties() {
    return this.partyRepo.findAll();
  }
  
  public Party getPartyById(int partyId) {
    Optional<Party> result = this.partyRepo.findById(Integer.valueOf(partyId));
    Party party = null;
    if (result.isPresent()) {
      party = result.get();
    } else {
      throw new RuntimeException("Did not find party id - " + partyId);
    } 
    return party;
  }

  public Address saveAddress(Address address){

    if(address != null){
      Address savedAddress = addressService.saveAddress(address);
      return savedAddress;
    }
    return null;
  }
}
