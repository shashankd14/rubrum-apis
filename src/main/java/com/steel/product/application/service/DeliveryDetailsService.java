package com.steel.product.application.service;

import com.steel.product.application.dto.delivery.DeliveryDto;
import com.steel.product.application.dto.delivery.DeliveryPacketsDto;
import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.entity.Instruction;

import java.util.List;

public interface DeliveryDetailsService {

    public List<Instruction> getAll();

    public List<Instruction> getInstructionsByDeliveryId(int deliveryId);

    public DeliveryDetails getById(int theId);

    public DeliveryDetails save(DeliveryDto deliveryDto);

    public void deleteById(int id);

    public List<DeliveryDetails> deliveryList();


    public Float findInstructionByInwardIdAndInstructionId(Integer inwardId,Integer instructionId);
}
