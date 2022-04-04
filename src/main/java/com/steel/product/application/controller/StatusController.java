package com.steel.product.application.controller;

import com.steel.product.application.entity.Status;
import com.steel.product.application.service.StatusService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin(origins = {"http://localhost:3001"})
//@CrossOrigin(origins = {"http://rubrum-frontend.s3-website.ap-south-1.amazonaws.com"})
@CrossOrigin
@Tag(name = "Status", description = "Status")
@RequestMapping({"/status"})
public class StatusController {
	
  private StatusService statusSvc;
  
  @Autowired
  public StatusController(StatusService theStatusSvc) {
    this.statusSvc = theStatusSvc;
  }
  
  @PostMapping({"/addNew"})
  public Status saveStatus(@ModelAttribute Status status) {
    status.setStatusId(0);
    return this.statusSvc.saveStatus(status);
  }
  
  @GetMapping({"/list"})
  public List<Status> getAllStatus() {
    return this.statusSvc.getAllStatus();
  }
  
  @GetMapping({"/getById/{statusId}"})
  public Status getStatusById(@PathVariable int statusId) {
    return this.statusSvc.getStatusById(statusId);
  }
}
