package com.steel.product.application.service;

import com.steel.product.application.dao.DeliveryDetailsRepository;
import com.steel.product.application.dto.delivery.DeliveryDto;
import com.steel.product.application.dto.delivery.DeliveryItemDetails;
import com.steel.product.application.dto.delivery.DeliveryPacketsDto;
import com.steel.product.application.dto.delivery.DeliveryResponseDto;
import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.entity.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeliveryDetailsServiceImpl implements DeliveryDetailsService{

    Logger logger = LoggerFactory.getLogger(DeliveryDetails.class);

    @Autowired
    private DeliveryDetailsRepository deliveryDetailsRepo;

    @Autowired
    private InstructionService instructionService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private InwardEntryService inwardEntryService;

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
    @Transactional
    public DeliveryDetails save(DeliveryDto deliveryDto) {

        List<DeliveryItemDetails> deliveryItemDetails;
        DeliveryDetails delivery;
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            if(deliveryDto.getDeliveryId() != null){
                logger.info("Updating delivery with id "+deliveryDto.getDeliveryId());
                delivery = getById(deliveryDto.getDeliveryId());
                if(deliveryDto.getCustomerInvoiceNo() != null) {
                    delivery.setCustomerInvoiceNo(deliveryDto.getCustomerInvoiceNo());
                }
                if(deliveryDto.getCustomerInvoiceDate() != null){
                    delivery.setCustomerInvoiceDate(deliveryDto.getCustomerInvoiceDate());
                }
                deliveryDetailsRepo.save(delivery);
                return delivery;
            }
            logger.info("adding new delivery with id");
            delivery = new DeliveryDetails();

//            delivery.setDeliveryId(0);
            delivery.setCreatedBy(1);
            delivery.setUpdatedBy(1);
            delivery.setCreatedOn(timestamp);
            delivery.setUpdatedOn(timestamp);
            delivery.setDeleted(false);
            delivery.setVehicleNo(deliveryDto.getVehicleNo());

            deliveryItemDetails= deliveryDto.getDeliveryItemDetails();
            InwardEntry inwardEntry = null;
            Instruction parentInstruction;
            List<Instruction> childrenInstructionsToDeliver = new ArrayList<>();
            Float inStockWeight = 0f, weightToDeliver = 0f;
            Status deliveredStatus = statusService.getStatusById(4);
            List<Instruction> instructions = instructionService.findInstructionsByInstructionIdInAndStatusNot(deliveryItemDetails.stream()
                    .map(d -> d.getInstructionId()).collect(Collectors.toList()), deliveredStatus);
            for (Instruction instruction:instructions) {
                if(instruction.getInwardId() != null) {
                    logger.info("instruction has inward id"+instruction.getInwardId().getInwardEntryId());
                    inwardEntry = instruction.getInwardId();
                    childrenInstructionsToDeliver = instruction.getChildInstructions().stream().filter(ci -> ci.getStatus().getStatusId() < 4)
                    .collect(Collectors.toList());
                    logger.info("no of children instructions present in instruction "+childrenInstructionsToDeliver.size());
                    if(childrenInstructionsToDeliver.size() > 0){
                        logger.error("instruction with id "+instruction.getInstructionId()+" has children instructions "+childrenInstructionsToDeliver.size());
//                        throw new RuntimeException("instruction with id "+instruction.getInstructionId()+" has all children instructions delivered.");
                    }
//                    childrenInstructionsToDeliver.forEach(ci -> ci.setStatus(deliveredStatus));
                    inStockWeight = inwardEntry.getInStockWeight();
                    weightToDeliver = instruction.getActualWeight();
                }else if(instruction.getParentInstruction() != null){
                    logger.info("instruction has parent instruction id "+instruction.getParentInstruction().getInstructionId());
                    parentInstruction = instruction.getParentInstruction();
                    inwardEntry = parentInstruction.getInwardId();
//                    childrenInstructionsToDeliver = parentInstruction.getChildInstructions().stream().
//                            filter(ci -> ci.getStatus().getStatusId() < 4).collect(Collectors.toList());
                    inStockWeight = inwardEntry.getInStockWeight();
//                    weightToDeliver = (float)childrenInstructionsToDeliver.stream().mapToDouble(ci -> ci.getActualWeight()).sum();
                    weightToDeliver = instruction.getActualWeight();
                }else{
                    logger.error("No inward id or parent instruction id found in instruction with id "+instruction.getInstructionId());
                    throw new RuntimeException("No inward id or parent instruction id found in instruction with id "+instruction.getInstructionId());
                }
                    instruction.setStatus(deliveredStatus);
                    delivery.addInstruction(instruction);
//                totalWeight = totalWeight + itemDetails.getWeight();
            }

            logger.info("inStock weight= "+inStockWeight+" weight to deliver= "+weightToDeliver);
            if (inStockWeight > weightToDeliver) {
                logger.info("inStock weight > weight to deliver");
                inwardEntry.setInStockWeight(inwardEntry.getInStockWeight() - weightToDeliver);
            } else {
                logger.info("inStock weight < weight to deliver");
                throw new RuntimeException("Weight to deliver exceeds inStock weight of inward with id "+inwardEntry.getInwardEntryId());
            }
            instructions.forEach(ins -> ins.setRemarks(deliveryItemDetails.stream().filter(d -> d.getInstructionId() == ins.getInstructionId())
                    .map(d -> d.getRemarks()).findFirst().get()));
            delivery.setTotalWeight((float)deliveryItemDetails.stream().mapToDouble(d -> d.getWeight()).sum());
            if(childrenInstructionsToDeliver.size() > 0){
                logger.info("setting status for children instructions");
                childrenInstructionsToDeliver.forEach(ci -> ci.setStatus(deliveredStatus));
            }
            logger.info("saving delivery");
            deliveryDetailsRepo.save(delivery);
            logger.info("updating inward");
            inwardEntryService.saveEntry(inwardEntry);

        return delivery;
    }

    @Override
    public void deleteById(int id) {
        deliveryDetailsRepo.deleteById(id);
    }

    @Override
    public List<DeliveryPacketsDto> deliveryList() {
        List<DeliveryDetails> inwardEntryList = deliveryDetailsRepo.findAllDeliveries();
        logger.info("Delivery details list size "+inwardEntryList.size());
        return inwardEntryList.stream().map(inw -> new DeliveryPacketsDto(inw)).collect(Collectors.toList());

    }

    @Override
    public Float findInstructionByInwardIdAndInstructionId(Integer inwardId,Integer instructionId) {
        return deliveryDetailsRepo.findInstructionByInwardIdAndInstructionId(inwardId,instructionId);
    }


}
