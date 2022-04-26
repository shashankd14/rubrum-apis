package com.steel.product.application.dto.packetClassification;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PacketClassificationRequest {

	@JsonProperty("tagId")
    private Integer classificationId;

	@JsonProperty("tagName")
    private String classificationName;
}
