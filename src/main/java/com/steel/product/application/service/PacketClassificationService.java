package com.steel.product.application.service;

import com.steel.product.application.dto.packetClassification.PacketClassificationRequest;
import com.steel.product.application.dto.packetClassification.PacketClassificationResponse;
import com.steel.product.application.entity.PacketClassification;

import java.util.List;
import java.util.Set;

public interface PacketClassificationService {

    List<PacketClassificationResponse> getAllPacketClassification();

    PacketClassification getPacketClassificationById(int paramInt);

    List<PacketClassification> findAllByPacketClassificationIdIn(List<Integer> packetClassificationIds);

    Set<PacketClassification> findByClassificationName(List<String> classificationNames);

    List<PacketClassificationResponse> getAllPacketClassificationByPartyId(Integer partyId);

    String savePacketClassifications(List<PacketClassificationRequest> packetClassificationRequests);

}
