package com.steel.product.application.controller;

import com.steel.product.application.entity.CuttingInstruction;
import com.steel.product.application.service.CuttingInstructionService;
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
@RequestMapping({"api/cuts"})
public class CuttingInstructionController {
  @Autowired
  private CuttingInstructionService cutInstrSvc;
  
  @PostMapping({"/addNew"})
  public CuttingInstruction saveCuttingInstruction(@RequestBody CuttingInstruction cuttingInstruction) {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    cuttingInstruction.setCuttingInstructionId(0);
    cuttingInstruction.setCreatedBy(1);
    cuttingInstruction.setUpdatedBy(1);
    cuttingInstruction.setIsDeleted(Boolean.valueOf(false));
    cuttingInstruction.setCreatedOn(timestamp);
    cuttingInstruction.setUpdatedOn(timestamp);
    return this.cutInstrSvc.saveCuttingInstruction(cuttingInstruction);
  }
  
  @GetMapping({"/list"})
  public List<CuttingInstruction> getCuttingInstruction() {
    return this.cutInstrSvc.getAllCuttingInstructions();
  }
  
  @GetMapping({"/getById/{cuttingInstructionId}"})
  public CuttingInstruction getCuttingInstructionById(@PathVariable int cuttingInstructionId) {
    return this.cutInstrSvc.getCuttingInstructionById(cuttingInstructionId);
  }
}
