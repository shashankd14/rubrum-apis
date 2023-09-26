package com.steel.product.application.dto.packetClassification;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PacketClassificationResponse {
	
	@JsonProperty("tagId")
	private Integer classificationId;

	@JsonProperty("tagName")
    private String classificationName;

    private String createdOn;

    private String updatedOn;
}
