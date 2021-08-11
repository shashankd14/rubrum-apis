package com.steel.product.application.controller;

import com.steel.product.application.dto.party.PartyDto;
import com.steel.product.application.entity.Party;
import com.steel.product.application.service.PartyDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@RestController
//@CrossOrigin(origins = {"http://localhost:3001"})
//@CrossOrigin(origins = {"http://rubrum-frontend.s3-website.ap-south-1.amazonaws.com"})
@CrossOrigin
@RequestMapping({"/api/party"})
public class PartyController {
  private PartyDetailsService partySvc;
  
  Timestamp timestamp = new Timestamp(System.currentTimeMillis());
  
  public PartyController(PartyDetailsService thePartySvc) {
    this.partySvc = thePartySvc;
  }
  
  @PostMapping({"/save"})
  public ResponseEntity<Object> saveParty(@RequestBody PartyDto partyDto) {
    try{
      partyDto.setnPartyId(0);
      Party party = partySvc.saveParty(partyDto);
      if(party==null)
        return new ResponseEntity<>("Error in saving party!!!", HttpStatus.INTERNAL_SERVER_ERROR);
      return new ResponseEntity<>("Party saved successfully!!!", HttpStatus.OK);
    }catch (Exception e){
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping({"/update"})
  public ResponseEntity<Object> updateParty(@RequestBody PartyDto partyDto) {
    try{
      Party party = partySvc.saveParty(partyDto);
      return new ResponseEntity<>("Party saved successfully!!!", HttpStatus.OK);
    }catch (Exception e){
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
  
  @GetMapping({"/list"})
  public List<PartyDto> getAllParties() {
    return this.partySvc.getAllParties().stream().map(p -> Party.valueOf(p)).collect(Collectors.toList());
  }
  
  @GetMapping({"/getById/{partyId}"})
  public PartyDto getPartyById(@PathVariable int partyId) {
    return Party.valueOf(this.partySvc.getPartyById(partyId));
  }
}
