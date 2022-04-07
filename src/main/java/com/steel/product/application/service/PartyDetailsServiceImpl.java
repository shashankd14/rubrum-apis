package com.steel.product.application.service;

import com.steel.product.application.dao.PartyDetailsRepository;
import com.steel.product.application.dto.party.PartyDto;
import com.steel.product.application.dto.party.PartyResponse;
import com.steel.product.application.entity.PacketClassification;
import com.steel.product.application.entity.Party;
import com.steel.product.application.mapper.PacketClassificationMapper;
import com.steel.product.application.mapper.PartyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PartyDetailsServiceImpl implements PartyDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PartyDetailsServiceImpl.class);

	private final PartyDetailsRepository partyRepo;

	private final AddressService addressService;

	private final PacketClassificationService packetClassificationService;

	private final PartyMapper partyMapper;

	//private final PacketClassificationMapper packetClassificationMapper;

	@Autowired
	public PartyDetailsServiceImpl(PartyDetailsRepository partyRepo, AddressService addressService,
			PacketClassificationService packetClassificationService, PartyMapper partyMapper,
			PacketClassificationMapper packetClassificationMapper) {
		this.partyRepo = partyRepo;
		this.addressService = addressService;
		this.packetClassificationService = packetClassificationService;
		this.partyMapper = partyMapper;
		//this.packetClassificationMapper = packetClassificationMapper;
	}

	public boolean checkPartyName(PartyDto partyDto) {
		LOGGER.info("inside saveParty method");
		boolean stts = true;
		Party party = partyMapper.toEntity(partyDto);
		if(partyDto.getPartyId() !=null && partyDto.getPartyId().length()>0) {
			party.setnPartyId(Integer.parseInt( partyDto.getPartyId()));
		}
		
		List<Party> partyList = partyRepo.findByPartyName(party.getPartyName());
		if (partyList != null && partyList.size() >0 ) {
			Party partyEntity = partyList.get(0);

			if (partyEntity.getnPartyId() > 0) {
				if (partyEntity.getnPartyId() != party.getnPartyId()
						&& partyEntity.getPartyName().equalsIgnoreCase(partyEntity.getPartyName())) {
					stts = false;
				}
			} else {
				if (partyEntity.getPartyName().equalsIgnoreCase(partyEntity.getPartyName())) {
					stts = false;
				}
			}
		}
		return stts;
	}

	public Party saveParty(PartyDto partyDto) {
		LOGGER.info("inside saveParty method");
		Party party = partyMapper.toEntity(partyDto);
		if(partyDto.getPartyId() !=null && partyDto.getPartyId().length()>0) {
			party.setnPartyId(Integer.parseInt( partyDto.getPartyId()));
		}
		/*
	    Map<String,PacketClassification> savedPacketClassifications = packetClassificationService
	            .findAllByPacketClassificationIdIn(partyDto.getTags().stream().map(tag -> tag.getClassificationId()).collect(Collectors.toList()))
	            .stream().collect(Collectors.toMap(pc -> pc.getClassificationName(),pc -> pc));
	    List<PacketClassification> packetClassifications = packetClassificationMapper.toEntities(partyDto.getTags());
	    packetClassifications.forEach(pc -> {
	      if(savedPacketClassifications.containsKey(pc.getClassificationName())){
	        party.addPacketClassification(savedPacketClassifications.get(pc.getClassificationName()));
	      }else {
	        party.addPacketClassification(pc);
	      }
	    });
	    */
    
		List<PacketClassification> savedPacketClassifications = packetClassificationService.findAllByPacketClassificationIdIn(
						partyDto.getTags().stream().map(tag -> tag.getClassificationId()).collect(Collectors.toList()));
		
		savedPacketClassifications.forEach(pc -> party.addPacketClassification(pc));
		if (party.getAddress1() != null) {
			addressService.saveAddress(party.getAddress1());
		}
		if (party.getAddress2() != null) {
			addressService.saveAddress(party.getAddress2());
		}

		party.setCreatedBy(1);
		party.setUpdatedBy(1);

		Party savedParty = partyRepo.save(party);

		LOGGER.info("party saved ok ! " + savedParty.getPartyName());
		return savedParty;
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

	@Override
	public List<PartyResponse> findAllParties() {
		List<Party> parties = partyRepo.findAllParties();
		return partyMapper.toResponseList(parties);
	}

}
