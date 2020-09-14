package com.steel.product.application.service;

import com.steel.product.application.dao.PartyDetailsRepository;
import com.steel.product.application.entity.Party;
import com.steel.product.application.service.PartyDetailsService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class PartyDetailsServiceImpl implements PartyDetailsService {
  private PartyDetailsRepository partyRepo;
  
  public PartyDetailsServiceImpl(PartyDetailsRepository thePartyRepo) {
    this.partyRepo = thePartyRepo;
  }
  
  public Party saveParty(Party party) {
    return (Party)this.partyRepo.save(party);
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
}
