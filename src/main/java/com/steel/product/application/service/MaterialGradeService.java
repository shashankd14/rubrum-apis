package com.steel.product.application.service;

import com.steel.product.application.entity.MaterialGrade;
import java.util.List;

public interface MaterialGradeService {
	
	MaterialGrade getById(int materialGradeId);

	List<MaterialGrade> getAll();
	
	List<MaterialGrade> getByMaterialId(int materialId);

	MaterialGrade findByGradeName(String gradeName);

}
