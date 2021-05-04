package com.steel.product.application.controller;

import com.steel.product.application.entity.PacketClassification;
import com.steel.product.application.service.PacketClassificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping({"/api/packetClassification"})
public class PacketClassificationController {

    @Resource
    private PacketClassificationService packetClassificationService;

    @GetMapping({"/list"})
    public ResponseEntity<Object> getAllAddress() {
        try {
            List<PacketClassification> packetClassificationList = new ArrayList<>();
            packetClassificationList = packetClassificationService.getAllPacketClassification();
            return new ResponseEntity(packetClassificationList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping({"/getById/{packetClassificationId}"})
    public ResponseEntity<Object> getAddressById(@PathVariable int packetClassificationId) {
        try {
            PacketClassification packetClassification = new PacketClassification();
            packetClassification = packetClassificationService.getPacketClassificationById(packetClassificationId);
            return new ResponseEntity(packetClassification, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
