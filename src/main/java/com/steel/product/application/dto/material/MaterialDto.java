package com.steel.product.application.dto.material;

import com.steel.product.application.dto.materialGradeDto.MaterialGradeDto;

import java.util.List;

public class MaterialDto {

    private int materialId;
    private String material;
    private List<String> grade;
    private MaterialGradeDto materialGradeDto;

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public List<String> getGrade() {
        return grade;
    }

    public void setGrade(List<String> grade) {
        this.grade = grade;
    }

    public MaterialGradeDto getMaterialGradeDto() {
        return materialGradeDto;
    }

    public void setMaterialGradeDto(MaterialGradeDto materialGradeDto) {
        this.materialGradeDto = materialGradeDto;
    }
}
