package com.steel.product.application.controller;

import com.steel.product.application.entity.Status;
import com.steel.product.application.service.StatusService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping({"/api/status"})
public class StatusController {
  private StatusService statusSvc;
  
  @Autowired
  public StatusController(StatusService theStatusSvc) {
    this.statusSvc = theStatusSvc;
  }
  
  @PostMapping({"/addNew"})
  public Status saveStatus(@RequestBody Status status) {
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
