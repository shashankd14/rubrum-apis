package com.steel.product.application.service;

import com.steel.product.application.entity.PacketClassification;

import java.util.List;

public interface PacketClassificationService {

    List<PacketClassification> getAllPacketClassification();

    PacketClassification getPacketClassificationById(int paramInt);

    List<PacketClassification> findAllByPacketClassificationIdIn(List<Integer> packetClassificationIds);
}
