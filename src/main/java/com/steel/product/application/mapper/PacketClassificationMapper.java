package com.steel.product.application.mapper;

import com.steel.product.application.entity.PacketClassification;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PacketClassificationMapper {

    PacketClassification toEntity(String classificationName);
    List<PacketClassification> toEntities(List<String> classificationNames);
}
