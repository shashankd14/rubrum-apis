package com.steel.product.application.service;

import com.steel.product.application.dao.MaterialGradeRepository;
import com.steel.product.application.entity.MaterialGrade;
import com.steel.product.application.service.MaterialGradeService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MaterialGradeServiceImpl implements MaterialGradeService {
  @Autowired
  private MaterialGradeRepository materialGradeRepo;
  
  public MaterialGrade getById(int gradeId) {
    Optional<MaterialGrade> result = this.materialGradeRepo.findById(Integer.valueOf(gradeId));
    MaterialGrade grade = null;
    if (result.isPresent()) {
      grade = result.get();
    } else {
      throw new RuntimeException("Did not find grade id - " + gradeId);
    } 
    return grade;
  }
  
  public List<MaterialGrade> getAll() {
    return this.materialGradeRepo.findAll();
  }

@Override
public List<MaterialGrade> getByMaterialId(int materialId) {
	return materialGradeRepo.getGradesByMaterialId(materialId);
}
}
