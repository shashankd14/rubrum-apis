package com.steel.product.application.service;

import com.steel.product.application.entity.CuttingInstruction;
import java.util.List;

public interface CuttingInstructionService {
  CuttingInstruction saveCuttingInstruction(CuttingInstruction paramCuttingInstruction);
  
  List<CuttingInstruction> getAllCuttingInstructions();
  
  CuttingInstruction getCuttingInstructionById(int paramInt);
}
