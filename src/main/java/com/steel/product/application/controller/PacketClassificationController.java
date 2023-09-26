package com.steel.product.application.controller;

import com.steel.product.application.dto.packetClassification.PacketClassificationRequest;
import com.steel.product.application.dto.packetClassification.PacketClassificationResponse;
import com.steel.product.application.entity.PacketClassification;
import com.steel.product.application.service.PacketClassificationService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@CrossOrigin
@Tag(name = "Packet Classification", description = "Packet Classification")
@RequestMapping({"/packetClassification"})
public class PacketClassificationController {

    @Resource
    private PacketClassificationService packetClassificationService;

    @GetMapping({"/list"})
    public ResponseEntity<Object> getAllTags() {
        try {
            return new ResponseEntity(packetClassificationService.getAllPacketClassification(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping({"/getById/{packetClassificationId}"})
    public ResponseEntity<Object> getAddressById(@PathVariable("packetClassificationId") int theId) {
        try {
            PacketClassification packetClassification = new PacketClassification();
            packetClassification = packetClassificationService.getPacketClassificationById(theId);
            return new ResponseEntity(packetClassification, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getByPartyId/{partyId}")
    public List<PacketClassificationResponse> getAddressById(@PathVariable("partyId") Integer partyId) {
        return packetClassificationService.getAllPacketClassificationByPartyId(partyId);
    }

    @PostMapping("/save")
    public String savePacketClassification(@RequestBody List<PacketClassificationRequest> packetClassificationRequests){
        return packetClassificationService.savePacketClassifications(packetClassificationRequests);
    }

}
