package com.steel.product.application.service;

import com.steel.product.application.dto.party.PartyDto;
import com.steel.product.application.entity.Party;
import java.util.List;

public interface PartyDetailsService {
  Party saveParty(PartyDto partyDto);
  
  List<Party> getAllParties();
  
  Party getPartyById(int paramInt);
}
