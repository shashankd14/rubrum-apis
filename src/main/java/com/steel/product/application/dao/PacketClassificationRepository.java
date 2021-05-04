package com.steel.product.application.dao;


import com.steel.product.application.entity.PacketClassification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacketClassificationRepository extends JpaRepository<PacketClassification, Integer>  {
}
