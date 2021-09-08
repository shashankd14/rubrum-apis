package com.steel.product.application.dao;


import com.steel.product.application.entity.PacketClassification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PacketClassificationRepository extends JpaRepository<PacketClassification, Integer>  {
    List<PacketClassification> findAllByClassificationIdIn(List<Integer> packetClassificationIds);
}
