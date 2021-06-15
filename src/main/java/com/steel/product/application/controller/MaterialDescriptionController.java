package com.steel.product.application.controller;

import com.steel.product.application.dto.material.MaterialDto;
import com.steel.product.application.entity.Material;
import com.steel.product.application.service.MaterialDescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin(origins = {"http://localhost:3001"})
//@CrossOrigin(origins = {"http://rubrum-frontend.s3-website.ap-south-1.amazonaws.com"})
@CrossOrigin
@RequestMapping({"/api/material"})
public class MaterialDescriptionController {
  @Autowired
  private MaterialDescriptionService matDescSvc;
  

  
  @PostMapping({"/save"})
  public ResponseEntity<Object> saveMatDesc(@RequestBody MaterialDto materialDto) {
    try{
      materialDto.setMaterialId(0);
      matDescSvc.saveMatDesc(materialDto);
      return new ResponseEntity<>("Material saved successfully", HttpStatus.OK);
    }catch (Exception e){
      e.printStackTrace();
      return new ResponseEntity<>("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping({"/update"})
  public ResponseEntity<Object> updateMaterial(@RequestBody MaterialDto materialDto) {
    try{
      matDescSvc.saveMatDesc(materialDto);
      return new ResponseEntity<>("Material saved successfully", HttpStatus.OK);
    }catch (Exception e){
      e.printStackTrace();
      return new ResponseEntity<>("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
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
