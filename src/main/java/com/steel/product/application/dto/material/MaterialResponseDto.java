package com.steel.product.application.dto.material;

import com.steel.product.application.dto.materialGradeDto.MaterialGradeDto;

import java.util.Date;
import java.util.List;

public class MaterialResponseDto {

    private int matId;
    private String description;
    private List<MaterialGradeDto> materialGradeList;
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

    public void setMatId(int matId) {
        this.matId = matId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<MaterialGradeDto> getMaterialGradeList() {
        return materialGradeList;
    }

    public void setMaterialGradeList(List<MaterialGradeDto> materialGradeList) {
        this.materialGradeList = materialGradeList;
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

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public MaterialGradeDto getMaterialGrade() {
        return materialGrade;
    }

    public void setMaterialGrade(MaterialGradeDto materialGrade) {
        this.materialGrade = materialGrade;
    }
}
