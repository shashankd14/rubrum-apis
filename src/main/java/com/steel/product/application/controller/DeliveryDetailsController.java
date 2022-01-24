package com.steel.product.application.controller;

import com.steel.product.application.dto.delivery.DeliveryDto;
import com.steel.product.application.dto.delivery.DeliveryPacketsDto;
import com.steel.product.application.dto.delivery.DeliveryResponseDto;
import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.service.DeliveryDetailsService;
import com.steel.product.application.service.InstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/delivery")
public class DeliveryDetailsController {

    @Autowired
    private DeliveryDetailsService deliveryDetailsService;

    @Autowired
    private InstructionService instructionService;

    @GetMapping("/list")
    public ResponseEntity<Object> getAll(){
        try{
            List<DeliveryPacketsDto> deliveryDetailsList = deliveryDetailsService.deliveryList();
            return new ResponseEntity<>(deliveryDetailsList, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getById/{deliveryId}")
    public ResponseEntity<Object> getById(@PathVariable("deliveryId") int deliveryId){
        try{
            List<Instruction> deliveredInstructionsById = deliveryDetailsService.getInstructionsByDeliveryId(deliveryId);
            return new ResponseEntity<>(deliveredInstructionsById.stream().map(ins -> Instruction.valueOf(ins)).collect(Collectors.toList()), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Object> save(@RequestBody DeliveryDto deliveryDto) {
        ResponseEntity<Object> result = null;

        DeliveryDetails deliveryDetails = new DeliveryDetails();
            try {
                deliveryDetails = deliveryDetailsService.save(deliveryDto);
                result = new ResponseEntity<>("Delivery details saved successfully!", HttpStatus.OK);
                } catch (Exception e) {
                result = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }


        return result;
    }

//    @PutMapping("/update/{deliveryId}")
//    public ResponseEntity<Object> update(@PathVariable int deliveryId){
//        try{
//            DeliveryDetails delivery = deliveryDetailsService.getById(deliveryId);
//            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//
//            delivery.setCreatedBy(1);
//            delivery.setUpdatedBy(1);
//            delivery.setCreatedOn(timestamp);
//            delivery.setUpdatedOn(timestamp);
//            delivery.setDeleted(false);
//
//            deliveryDetailsService.save(delivery);
//            return new ResponseEntity<>("Delivery details saved successfully!", HttpStatus.OK);
//        }catch (Exception e){
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @DeleteMapping("/deleteById/{deliveryId}")
    public ResponseEntity<Object> deleteById(@PathVariable("deliveryId") Integer id){
        try{
            deliveryDetailsService.deleteById(id);
            return new ResponseEntity<>("Delete successful!", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}