package com.steel.product.application.mapper;

import com.steel.product.application.dto.packetClassification.PacketClassificationRequest;
import com.steel.product.application.dto.packetClassification.PacketClassificationResponse;
import com.steel.product.application.entity.PacketClassification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PacketClassificationMapper {

    @Mapping(target = "createdOn",dateFormat = "dd-MM-yyyy HH:mm:ss")
    @Mapping(target = "updatedOn",dateFormat = "dd-MM-yyyy HH:mm:ss")
    PacketClassificationResponse toResponse(PacketClassification packetClassification);
    List<PacketClassificationResponse> toList(List<PacketClassification> packetClassification);

    PacketClassificationRequest toEntity(PacketClassification packetClassification);
    List<PacketClassification> requestToEntity(List<PacketClassificationRequest> packetClassificationRequest);
}
