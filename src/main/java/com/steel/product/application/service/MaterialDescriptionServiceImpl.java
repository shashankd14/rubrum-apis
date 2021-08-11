package com.steel.product.application.service;

import com.steel.product.application.dao.MaterialDescriptionRepository;
import com.steel.product.application.dao.MaterialGradeRepository;
import com.steel.product.application.dto.material.MaterialInputDto;
import com.steel.product.application.dto.material.MaterialResponseDto;
import com.steel.product.application.entity.Material;
import com.steel.product.application.entity.MaterialGrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
  public Material saveMatDesc(MaterialInputDto materialInputDto) {

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    Material material = new Material();
    Material savedMaterial = new Material();
    if(materialInputDto.getMatId() !=0){
      material = getMatById(materialInputDto.getMatId());
    }
    material.setDescription(materialInputDto.getDescription());
    material.setCreatedBy(1);
    material.setUpdatedBy(1);
    material.setCreatedOn(timestamp);
    material.setUpdatedOn(timestamp);
    material.setIsDeleted(false);
    material.setHsnCode(materialInputDto.getHsnCode());
    material.setMaterialCode(materialInputDto.getMaterialCode());

    savedMaterial = matDescRepo.save(material);

    if(materialInputDto.getMatId() != 0){
      materialGradeRepository.deleteGradesByMaterialId(materialInputDto.getMatId());
    }
    for(String grade : materialInputDto.getGrade()){
      MaterialGrade materialGrade = new MaterialGrade();
      materialGrade.setParentMaterial(savedMaterial);
      materialGrade.setGradeName(grade);
      materialGradeRepository.save(materialGrade);
    }

    return null;

  }

  public List<MaterialResponseDto> getAllMatDesc() {
    List<Material> materials = matDescRepo.findAll();
    return materials.stream().map(m -> Material.valueOf(m)).collect(Collectors.toList());
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
