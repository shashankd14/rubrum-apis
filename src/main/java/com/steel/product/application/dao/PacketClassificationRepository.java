package com.steel.product.application.dao;


import com.steel.product.application.entity.PacketClassification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PacketClassificationRepository extends JpaRepository<PacketClassification, Integer>  {
    List<PacketClassification> findAllByClassificationIdIn(List<Integer> packetClassificationIds);
    Set<PacketClassification> findAllByClassificationNameIn(List<String> classificationNames);
}
