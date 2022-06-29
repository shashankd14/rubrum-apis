package com.steel.product.application.service;

import com.steel.product.application.dao.PartyDetailsRepository;
import com.steel.product.application.dto.party.PartyDto;
import com.steel.product.application.dto.party.PartyResponse;
import com.steel.product.application.entity.EndUserTagsEntity;
import com.steel.product.application.entity.PacketClassification;
import com.steel.product.application.entity.Party;
import com.steel.product.application.mapper.PartyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PartyDetailsServiceImpl implements PartyDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PartyDetailsServiceImpl.class);

	private final PartyDetailsRepository partyRepo;

	private final AddressService addressService;

	private final PacketClassificationService packetClassificationService;

	private final EndUserTagsService endUserTagsService;

	private final PartyMapper partyMapper;

	@Autowired
	public PartyDetailsServiceImpl(PartyDetailsRepository partyRepo, AddressService addressService,
			PacketClassificationService packetClassificationService, PartyMapper partyMapper,
			EndUserTagsService endUserTagsService) {
		this.partyRepo = partyRepo;
		this.addressService = addressService;
		this.packetClassificationService = packetClassificationService;
		this.partyMapper = partyMapper;
		this.endUserTagsService = endUserTagsService;
	}

	public boolean checkPartyName(PartyDto partyDto) {
		LOGGER.info("inside saveParty method");
		boolean stts = true;
		Party party = partyMapper.toEntity(partyDto);
		if(partyDto.getNPartyId() !=null && partyDto.getNPartyId().length()>0) {
			party.setnPartyId(Integer.parseInt( partyDto.getNPartyId()));
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

	public Party saveParty(PartyDto partyDto, int userId) {
		LOGGER.info("inside saveParty method");
		Party party = partyMapper.toEntity(partyDto);
		if(partyDto.getNPartyId() !=null && partyDto.getNPartyId().length()>0) {
			party.setnPartyId(Integer.parseInt( partyDto.getNPartyId()));
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

		List<EndUserTagsEntity> savedEndUserTags = endUserTagsService.findAllByTagIdIn(
				partyDto.getEndUserTags().stream().map(tag -> tag.getTagId()).collect(Collectors.toList()));

		savedPacketClassifications.forEach(pc -> party.addPacketClassification(pc));
		savedEndUserTags.forEach(pc -> party.addEndUserTags(pc));

		if (party.getAddress1() != null) {
			addressService.saveAddress(party.getAddress1());
		}
		if (party.getAddress2() != null) {
			addressService.saveAddress(party.getAddress2());
		}

		party.setCreatedBy(userId);
		party.setUpdatedBy(userId);

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
	public Page<Party> findAllWithPagination(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Party> pageResult = partyRepo.findAllParties(pageable);
		return pageResult;
	}

	@Override
	public List<PartyResponse> findAllParties() {
		List<Party> parties = partyRepo.findAllParties();
		return partyMapper.toResponseList(parties);
	}

}
