package com.steel.product.application.controller;

import com.steel.product.application.entity.Party;
import com.steel.product.application.service.PartyDetailsService;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  
  @PostMapping({"/addNew"})
  public Party saveParty(@RequestBody Party party) {
    party.setnPartyId(0);
    party.setCreatedBy(1);
    party.setUpdatedBy(1);
    party.setCreatedOn(this.timestamp);
    party.setUpdatedOn(this.timestamp);
    party.setIsDeleted(Boolean.valueOf(false));
    return this.partySvc.saveParty(party);
  }
  
  @GetMapping({"/list"})
  public List<Party> getAllParties() {
    return this.partySvc.getAllParties();
  }
  
  @GetMapping({"/getById/{partyId}"})
  public Party getPartyById(@PathVariable int partyId) {
    return this.partySvc.getPartyById(partyId);
  }
}
