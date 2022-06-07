package com.steel.product.application.dto.partDetails;

import com.steel.product.application.dto.BaseReq;

import lombok.*;


@Getter
@Setter
public class partDetailsRequest extends BaseReq {

    private Float targetWeight;
    private Float length;
    private Integer createdBy;
    private Integer updatedBy;
    private Boolean isDeleted;

}
