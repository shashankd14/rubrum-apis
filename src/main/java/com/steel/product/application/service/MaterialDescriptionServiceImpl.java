package com.steel.product.application.service;

import com.steel.product.application.dao.MaterialDescriptionRepository;
import com.steel.product.application.dao.MaterialGradeRepository;
import com.steel.product.application.dto.material.MaterialRequestDto;
import com.steel.product.application.dto.material.MaterialResponseDetailsDto;
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

	private MaterialDescriptionRepository matDescRepo;

	private MaterialGradeRepository materialGradeRepository;

	@Autowired
	public MaterialDescriptionServiceImpl(MaterialDescriptionRepository matDescRepo,
			MaterialGradeRepository materialGradeRepository) {
		this.matDescRepo = matDescRepo;
		this.materialGradeRepository = materialGradeRepository;
	}

	@Override
	public Material saveMatDesc(MaterialRequestDto materialRequestDto, int userId) {

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Material material = new Material();
		Material savedMaterial = new Material();
		if (materialRequestDto.getMatId() != 0) {
			material = getMatById(materialRequestDto.getMatId());
		}
		material.setDescription(materialRequestDto.getMaterial());
		material.setCreatedBy(userId);
		material.setUpdatedBy(userId);
		material.setCreatedOn(timestamp);
		material.setUpdatedOn(timestamp);
		material.setIsDeleted(false);
		material.setHsnCode(materialRequestDto.getHsnCode());
		material.setMaterialCode(materialRequestDto.getMaterialCode());

		savedMaterial = matDescRepo.save(material);

		//if (materialRequestDto.getMatId() != 0) {
			//materialGradeRepository.deleteGradesByMaterialId(materialRequestDto.getMatId());
		//}
		List<MaterialGrade> materialGradeList = materialGradeRepository.getGradesByMaterialId( materialRequestDto.getMatId());
		for (MaterialGrade materialGradeEntity : materialGradeList) {
			if(materialGradeEntity.getInwardEntry().size() ==0) {
				materialGradeRepository.deleteById( materialGradeEntity.getGradeId());
			}
		}
		
		for (String grade : materialRequestDto.getGrade()) {
			List<MaterialGrade> materialGradeList1 = materialGradeRepository.getGradesByMaterialIdName( materialRequestDto.getMatId(), grade);
			MaterialGrade materialGrade = null;
			if(materialGradeList1!=null && materialGradeList1.size()>0) {
				materialGrade = materialGradeList1.get(0);
			}
			if(materialGrade!=null && materialGrade.getGradeId()>0) {
				materialGrade.setParentMaterial(savedMaterial);
				materialGrade.setGradeName(grade);
			} else {
				materialGrade = new MaterialGrade();
				materialGrade.setParentMaterial(savedMaterial);
				materialGrade.setGradeName(grade);
			}
			materialGradeRepository.save(materialGrade);
		}
		return material;

	}

	public List<MaterialResponseDetailsDto> getAllMatDesc() {
		List<Material> materials = matDescRepo.findAll();
		return materials.stream().map(m -> Material.valueOfMat(m)).collect(Collectors.toList());
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

	@Override
	public Material findByDesc(String desc) {
		return matDescRepo.findByDescription(desc);
	}
}
