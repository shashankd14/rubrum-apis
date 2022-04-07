package com.steel.product.application.controller;

import com.steel.product.application.dto.party.PartyDto;
import com.steel.product.application.dto.party.PartyResponse;
import com.steel.product.application.entity.Party;
import com.steel.product.application.service.PartyDetailsService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin(origins = {"http://localhost:3001"})
//@CrossOrigin(origins = {"http://rubrum-frontend.s3-website.ap-south-1.amazonaws.com"})
@CrossOrigin
@Tag(name = "Party", description = "Party")
@RequestMapping({ "/party" })
public class PartyController {

	private PartyDetailsService partySvc;

	public PartyController(PartyDetailsService thePartySvc) {
		this.partySvc = thePartySvc;
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

	@GetMapping({ "/list" })
	public List<PartyResponse> getAllParties() {
		return partySvc.findAllParties();
	}

	@GetMapping({ "/getById/{partyId}" })
	public PartyDto getPartyById(@PathVariable int partyId) {
		return Party.valueOf(this.partySvc.getPartyById(partyId));
	}
}
