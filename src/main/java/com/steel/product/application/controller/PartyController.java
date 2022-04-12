package com.steel.product.application.controller;

import com.steel.product.application.dto.party.PartyDto;
import com.steel.product.application.dto.party.PartyResponse;
import com.steel.product.application.entity.Party;
import com.steel.product.application.mapper.PartyMapper;
import com.steel.product.application.service.PartyDetailsService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin
@Tag(name = "Party", description = "Party")
@RequestMapping({ "/party" })
public class PartyController {

	private PartyDetailsService partySvc;

	private PartyMapper partyMapper;

	public PartyController(PartyDetailsService thePartySvc, PartyMapper partyMapper) {
		this.partySvc = thePartySvc;
		this.partyMapper = partyMapper;
	}

	@PostMapping({ "/save" })
	public ResponseEntity<Object> saveParty(@RequestBody PartyDto partyDto) {
		try {
			
			boolean stts = partySvc.checkPartyName(partyDto);
			if(stts) {
				Party party = partySvc.saveParty(partyDto);
				return new ResponseEntity<>("Party updated successfully!!!", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Entered Party Name already Exists, " + partyDto.getPartyName(), HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping({ "/update" })
	public ResponseEntity<Object> updateParty(@RequestBody PartyDto partyDto) {
		try {
			boolean stts = partySvc.checkPartyName(partyDto);
			if(stts) {
				Party party = partySvc.saveParty(partyDto);
				return new ResponseEntity<>("Party updated successfully!!!", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Entered Party Name already Exists, " + partyDto.getPartyName(), HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping({ "/list/{pageNo}/{pageSize}" })
	public ResponseEntity<Object> findAllWithPagination(@PathVariable int pageNo, @PathVariable int pageSize) {

		Map<String, Object> response = new HashMap<>();
		Page<Party> pageResult = partySvc.findAllWithPagination(pageNo, pageSize);
		List<Party> partyList = pageResult.getContent();
		List<PartyResponse> responseList =  partyMapper.toResponseList(partyList);
		response.put("content", responseList);
		response.put("currentPage", pageResult.getNumber());
		response.put("totalItems", pageResult.getTotalElements());
		response.put("totalPages", pageResult.getTotalPages());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@GetMapping({ "/list" })
	public List<PartyResponse> getAllParties() {
		return partySvc.findAllParties();
	}

	@GetMapping({ "/getById/{partyId}" })
	public PartyDto getPartyById(@PathVariable int partyId) {
		return Party.valueOf(this.partySvc.getPartyById(partyId));
	}
}
