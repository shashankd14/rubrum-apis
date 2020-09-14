package com.steel.product.application.service;

import com.steel.product.application.dao.CuttingInstructionRepository;
import com.steel.product.application.entity.CuttingInstruction;
import com.steel.product.application.service.CuttingInstructionService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CuttingInstructionServiceImpl implements CuttingInstructionService {
  private CuttingInstructionRepository cuttingInsRepo;
  
  @Autowired
  public CuttingInstructionServiceImpl(CuttingInstructionRepository theCuttingInsRepo) {
    this.cuttingInsRepo = theCuttingInsRepo;
  }
  
  public CuttingInstruction saveCuttingInstruction(CuttingInstruction cuttingInstruction) {
    return (CuttingInstruction)this.cuttingInsRepo.save(cuttingInstruction);
  }
  
  public List<CuttingInstruction> getAllCuttingInstructions() {
    return this.cuttingInsRepo.findAll();
  }
  
  public CuttingInstruction getCuttingInstructionById(int cuttingInstructionId) {
    Optional<CuttingInstruction> result = this.cuttingInsRepo.findById(Integer.valueOf(cuttingInstructionId));
    CuttingInstruction theCuttingInstruction = null;
    if (result.isPresent()) {
      theCuttingInstruction = result.get();
    } else {
      throw new RuntimeException("Did not find cutting instruction id - " + cuttingInstructionId);
    } 
    return theCuttingInstruction;
  }
}
