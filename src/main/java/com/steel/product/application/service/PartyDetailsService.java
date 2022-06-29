package com.steel.product.application.service;

import com.steel.product.application.dto.party.PartyDto;
import com.steel.product.application.dto.party.PartyResponse;
import com.steel.product.application.entity.Party;
import java.util.List;

import org.springframework.data.domain.Page;

public interface PartyDetailsService {
	
	boolean checkPartyName(PartyDto partyDto);

	Party saveParty(PartyDto partyDto, int userId);

	List<Party> getAllParties();

	Party getPartyById(int paramInt);

	List<PartyResponse> findAllParties();

	Page<Party> findAllWithPagination(int pageNo, int pageSize);
}
