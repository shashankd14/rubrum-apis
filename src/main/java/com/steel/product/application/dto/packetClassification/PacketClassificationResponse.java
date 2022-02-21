package com.steel.product.application.dto.packetClassification;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PacketClassificationResponse {
    private Integer classificationId;

    private String classificationName;

    private String createdOn;

    private String updatedOn;
}
