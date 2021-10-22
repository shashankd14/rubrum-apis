package com.steel.product.application.dto.material;

import com.steel.product.application.dto.materialGradeDto.MaterialGradeDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class MaterialResponseDto {

    private int matId;
    private String description;
    private MaterialGradeDto materialGrade;
    private String hsnCode;
    private String materialCode;
    private int createdBy;
    private int updatedBy;
    private Date createdOn;
    private Date updatedOn;

}
