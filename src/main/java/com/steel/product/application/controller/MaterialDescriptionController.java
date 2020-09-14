package com.steel.product.application.controller;

import com.steel.product.application.entity.Material;
import com.steel.product.application.service.MaterialDescriptionService;
import java.sql.Timestamp;
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
@RequestMapping({"/api/material"})
public class MaterialDescriptionController {
  @Autowired
  private MaterialDescriptionService matDescSvc;
  
  Timestamp timestamp = new Timestamp(System.currentTimeMillis());
  
  @PostMapping({"/addNew"})
  public Material saveMatDesc(@RequestBody Material matDesc) {
    matDesc.setMatId(0);
    matDesc.setCreatedBy(1);
    matDesc.setUpdatedBy(1);
    matDesc.setCreatedOn(this.timestamp);
    matDesc.setUpdatedOn(this.timestamp);
    matDesc.setIsDeleted(Boolean.valueOf(false));
    return this.matDescSvc.saveMatDesc(matDesc);
  }
  
  @GetMapping({"/list"})
  public List<Material> getAllMatDesc() {
    return this.matDescSvc.getAllMatDesc();
  }
  
  @GetMapping({"/getById/{matId}"})
  public Material getMatById(@PathVariable int matId) {
    return this.matDescSvc.getMatById(matId);
  }
}
