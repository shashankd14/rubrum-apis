package com.steel.product.application.dto.material;

import com.steel.product.application.dto.materialGradeDto.MaterialGradeDto;

import java.util.Date;
import java.util.List;

public class MaterialDto {

    private int matId;
    private String description;
    private List<String> grade;
    private MaterialGradeDto materialGrade;
    private String hsnCode;
    private String materialCode;
    private int createdBy;
    private int updatedBy;
    private Date createdOn;
    private Date updatedOn;

    public int getMatId() {
        return matId;
    }

    public void setMaterialId(int matId) {
        this.matId = matId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getGrade() {
        return grade;
    }

    public void setGrade(List<String> grade) {
        this.grade = grade;
    }

    public MaterialGradeDto getMaterialGrade() {
        return materialGrade;
    }

    public void setMaterialGrade(MaterialGradeDto materialGrade) {
        this.materialGrade = materialGrade;
    }

    public String getHsnCode() {
        return hsnCode;
    }

    public void setHsnCode(String hsnCode) {
        this.hsnCode = hsnCode;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }
}
