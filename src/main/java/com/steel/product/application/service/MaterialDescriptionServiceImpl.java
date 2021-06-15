package com.steel.product.application.service;

import com.steel.product.application.dao.MaterialDescriptionRepository;
import com.steel.product.application.dao.MaterialGradeRepository;
import com.steel.product.application.dto.material.MaterialDto;
import com.steel.product.application.entity.Material;
import com.steel.product.application.entity.MaterialGrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class MaterialDescriptionServiceImpl implements MaterialDescriptionService {

  @Autowired
  private MaterialDescriptionRepository matDescRepo;

  @Autowired
  private MaterialGradeRepository materialGradeRepository;
  
  public MaterialDescriptionServiceImpl(MaterialDescriptionRepository theMatDesc) {
    this.matDescRepo = theMatDesc;
  }
  
  public Material saveMatDesc(Material matDesc) {
    return (Material)this.matDescRepo.save(matDesc);
  }

  @Override
  public Material saveMatDesc(MaterialDto materialDto) {

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    Material material = new Material();
    Material savedMaterial = new Material();
    if(materialDto.getMaterialId() !=0){
      material = getMatById(materialDto.getMaterialId());
    }
    material.setDescription(materialDto.getMaterial());
    material.setCreatedBy(1);
    material.setUpdatedBy(1);
    material.setCreatedOn(timestamp);
    material.setUpdatedOn(timestamp);
    material.setIsDeleted(false);

    savedMaterial = matDescRepo.save(material);

    if(materialDto.getMaterialId() != 0){
      materialGradeRepository.deleteGradesByMaterialId(materialDto.getMaterialId());
    }
    for(String grade : materialDto.getGrade()){
      MaterialGrade materialGrade = new MaterialGrade();
      materialGrade.setParentMaterial(savedMaterial);
      materialGrade.setGradeName(grade);
      materialGradeRepository.save(materialGrade);
    }

    return null;

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
