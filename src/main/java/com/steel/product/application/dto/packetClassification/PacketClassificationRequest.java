package com.steel.product.application.dto.packetClassification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.steel.product.application.dto.BaseReq;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PacketClassificationRequest extends BaseReq {

	@JsonProperty("tagId")
    private Integer classificationId;

	@JsonProperty("tagName")
    private String classificationName;
}
