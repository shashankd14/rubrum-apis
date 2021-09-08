package com.steel.product.application.service;

import com.steel.product.application.dao.PacketClassificationRepository;
import com.steel.product.application.entity.PacketClassification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PacketClassificationServiceImpl implements PacketClassificationService{

    @Autowired
    private PacketClassificationRepository packetClassificationRepository;

    @Override
    public List<PacketClassification> getAllPacketClassification() {
        return packetClassificationRepository.findAll();
    }

    @Override
    public PacketClassification getPacketClassificationById(int packetClassificationById) {

        Optional<PacketClassification> result = packetClassificationRepository.findById(Integer.valueOf(packetClassificationById));
        PacketClassification thePacketClassification = null;
        if (result.isPresent()) {
            thePacketClassification = result.get();
        } else {
            throw new RuntimeException("Did not find PacketClassification id - " + packetClassificationById);
        }
        return thePacketClassification;
    }

    @Override
    public List<PacketClassification> findAllByPacketClassificationIdIn(List<Integer> packetClassificationIds) {
        return packetClassificationRepository.findAllByClassificationIdIn(packetClassificationIds);
    }
}
