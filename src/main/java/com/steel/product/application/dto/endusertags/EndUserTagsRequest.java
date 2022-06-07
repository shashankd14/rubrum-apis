package com.steel.product.application.dto.endusertags;

import com.steel.product.application.dto.BaseReq;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EndUserTagsRequest extends BaseReq {

    private Integer tagId;

    private String tagName;
}
