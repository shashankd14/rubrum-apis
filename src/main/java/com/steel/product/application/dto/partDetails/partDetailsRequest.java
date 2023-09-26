package com.steel.product.application.dto.partDetails;

import lombok.*;

@Getter
@Setter
public class partDetailsRequest {

    private Float targetWeight;
    private Float length;
    private Integer createdBy;
    private Integer updatedBy;
    private Boolean isDeleted;

}
