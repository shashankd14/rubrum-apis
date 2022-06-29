package com.steel.product.application.dto.pdf;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PartDto {

    private String partDetailsId;
    private List<Integer> groupIds;

}
