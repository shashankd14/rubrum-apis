package com.steel.product.application.service;

import com.steel.product.application.dao.DeliveryDetailsRepository;
import com.steel.product.application.dto.delivery.DeliveryDto;
import com.steel.product.application.dto.delivery.DeliveryItemDetails;
import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.entity.Instruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryDetailsServiceImpl implements DeliveryDetailsService{

    @Autowired
    private DeliveryDetailsRepository deliveryDetailsRepo;

    @Autowired
    private InstructionService instructionService;

    @Override
    public List<Instruction> getAll() {
        return deliveryDetailsRepo.deliveredItems();
    }

    @Override
    public List<Instruction> getInstructionsByDeliveryId(int deliveryId) {
        return deliveryDetailsRepo.deliveredItemsById(deliveryId);
    }

    @Override
    public DeliveryDetails getById(int theId) {

        Optional<DeliveryDetails> result = deliveryDetailsRepo.findById(theId);

        DeliveryDetails delivery = null;

        if (result.isPresent()) {
            delivery = result.get();
        }
        else {
            // we didn't find the employee
            throw new RuntimeException("Did not find delivery id - " + theId);
        }

        return delivery;
    }

    @Override
    public DeliveryDetails save(DeliveryDto deliveryDto) {

        DeliveryDetails savedDelivery = new DeliveryDetails();
        try {

            DeliveryDetails delivery = new DeliveryDetails();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            delivery.setDeliveryId(0);
            delivery.setCreatedBy(1);
            delivery.setUpdatedBy(1);
            delivery.setCreatedOn(timestamp);
            delivery.setUpdatedOn(timestamp);
            delivery.setDeleted(false);
            delivery.setVehicleNo(deliveryDto.getVehicleNo());

            float totalWeight = 0f;

            savedDelivery = deliveryDetailsRepo.save(delivery);

            List<DeliveryItemDetails> deliveryItemDetails = deliveryDto.getDeliveryItemDetails();
            for (DeliveryItemDetails itemDetails : deliveryItemDetails) {
                instructionService.updateInstructionWithDeliveryRemarks(savedDelivery.getDeliveryId(),
                        itemDetails.getRemarks(), itemDetails.getInstructionId());
                totalWeight = totalWeight + itemDetails.getWeight();

            }
            savedDelivery.setTotalWeight(totalWeight);
            deliveryDetailsRepo.save(savedDelivery);
        }catch (Exception e) {
            return null;
        }
        return savedDelivery;
    }

    @Override
    public void deleteById(int id) {
        deliveryDetailsRepo.deleteById(id);
    }

    @Override
    public List<DeliveryDetails> deliveryList() {
        return deliveryDetailsRepo.findAll();
    }
}
