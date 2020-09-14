package com.steel.product.application.service;

import com.steel.product.application.dao.MaterialDescriptionRepository;
import com.steel.product.application.entity.Material;
import com.steel.product.application.service.MaterialDescriptionService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class MaterialDescriptionServiceImpl implements MaterialDescriptionService {
  private MaterialDescriptionRepository matDescRepo;
  
  public MaterialDescriptionServiceImpl(MaterialDescriptionRepository theMatDesc) {
    this.matDescRepo = theMatDesc;
  }
  
  public Material saveMatDesc(Material matDesc) {
    return (Material)this.matDescRepo.save(matDesc);
  }
  
  public List<Material> getAllMatDesc() {
    return this.matDescRepo.findAll();
  }
  
  public Material getMatById(int MatId) {
    Optional<Material> result = this.matDescRepo.findById(Integer.valueOf(MatId));
    Material theMatDesc = null;
    if (result.isPresent()) {
      theMatDesc = result.get();
    } else {
      throw new RuntimeException("Did not find Material id - " + MatId);
    } 
    return theMatDesc;
  }
}
