package com.steel.product.application.dto.pdf;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.steel.product.application.dto.BaseReq;

@Getter
@Setter
public class PartDto extends BaseReq {

    private String partDetailsId;
    private List<Integer> groupIds;

}
