package com.steel.product.application.service;

import com.steel.product.application.dao.DeliveryDetailsRepository;
import com.steel.product.application.dto.delivery.DeliveryDto;
import com.steel.product.application.dto.delivery.DeliveryItemDetails;
import com.steel.product.application.dto.delivery.DeliveryPacketsDto;
import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.entity.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeliveryDetailsServiceImpl implements DeliveryDetailsService{

    private final static Logger LOGGER = LoggerFactory.getLogger(DeliveryDetails.class);

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
                LOGGER.info("Updating delivery with id "+deliveryDto.getDeliveryId());
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
            LOGGER.info("adding new delivery with id");
            delivery = new DeliveryDetails();

//            delivery.setDeliveryId(0);
            delivery.setCreatedBy(1);
            delivery.setUpdatedBy(1);
            delivery.setCreatedOn(timestamp);
            delivery.setUpdatedOn(timestamp);
            delivery.setDeleted(false);
            delivery.setVehicleNo(deliveryDto.getVehicleNo());

            deliveryItemDetails= deliveryDto.getDeliveryItemDetails();


            float inStockWeight = 0f, weightToDeliver = 0f, parentWeight = 0f;
            Integer deliveredStatusId = 4;
            Status deliveredStatus = statusService.getStatusById(deliveredStatusId);
            Integer readyToDeliverStatusId = 3;
            Map<Integer,String> instructionRemarksMap = deliveryDto.getDeliveryItemDetails().stream().
                    collect(Collectors.toMap(d -> d.getInstructionId(),d -> d.getRemarks()));
            List<Instruction> instructions = instructionService.findAllByInstructionIdInAndStatus(deliveryItemDetails.stream()
                    .map(d -> d.getInstructionId()).collect(Collectors.toList()), readyToDeliverStatusId);
            instructions.forEach(ins -> deliveredStatus.addInstruction(ins));
            instructions = instructionService.saveAll(instructions);

            InwardEntry inwardEntry;
            Instruction parentInstruction;
            List<Instruction> groupInstructions = null;
            Set<Instruction> parentGroupInstructions = new HashSet<>();
            List<Instruction> childrenInstructions;
            Set<InwardEntry> inwardEntryList = new HashSet<>();
            Integer parentGroupId;
            boolean isAnyInstructionNotDelivered = true;
            try {
                for (Instruction instruction : instructions) {
                    inwardEntry = instruction.getInwardId();
                    parentInstruction = instruction.getParentInstruction();
                    if (inwardEntry == null) {
                        inwardEntry = parentInstruction.getInwardId();
                    }
                    parentGroupId = instruction.getParentGroupId();
                    inStockWeight = inwardEntry.getInStockWeight();
                    if (inwardEntry != null && parentGroupId != null) {
                        LOGGER.info("instruction has inward id,parentGroupId " + inwardEntry.getInwardEntryId() + " " + parentGroupId);
                        if(!parentGroupInstructions.contains(instruction)) {
                            parentGroupInstructions = new HashSet<>(instructionService.findAllByParentGroupId(parentGroupId));
                        }
                        isAnyInstructionNotDelivered = parentGroupInstructions.stream().anyMatch(ins -> !ins.getStatus().equals(deliveredStatus));
                        if (!isAnyInstructionNotDelivered) {
                            if(groupInstructions == null || (groupInstructions != null && !groupInstructions.isEmpty() &&!groupInstructions.get(0).getGroupId().equals(parentGroupId))) {
                                groupInstructions = instructionService.findAllByGroupId(parentGroupId);
                                groupInstructions.forEach(ins -> deliveredStatus.addInstruction(ins));
                            }

                            }

//                    parentWeight = groupInstructions.stream().reduce(0f,(sum,ins) -> sum + ins.getActualWeight(),Float::sum);

                        weightToDeliver = instruction.getActualWeight();
                    } else if (parentInstruction != null) {
                        LOGGER.info("instruction has parent instruction id " + instruction.getParentInstruction().getInstructionId());
//                    parentWeight = parentInstruction.getActualWeight();
                            weightToDeliver = instruction.getActualWeight();
                            childrenInstructions = parentInstruction.getChildInstructions();
                            LOGGER.info("parent instruction has " + childrenInstructions.size() + " children");
                            isAnyInstructionNotDelivered = childrenInstructions.stream().anyMatch(ins -> !ins.getStatus().equals(deliveredStatus));
                            if (!isAnyInstructionNotDelivered) {
                                LOGGER.info("setting parent instruction status delivered as all children of parent instruction " + parentInstruction.getInstructionId() + " have status delivered");
                                parentInstruction.setStatus(deliveredStatus);
                            }
                            LOGGER.info("no status change for parent instruction " + parentInstruction.getInstructionId() + " as all children not delivered");


                    } else if (inwardEntry != null) {
                        LOGGER.info("instruction has inward id " + inwardEntry.getInwardEntryId());
//                    parentWeight = inwardEntry.getInStockWeight();
                        weightToDeliver = instruction.getActualWeight();
                        childrenInstructions = instruction.getChildInstructions();
                        if (childrenInstructions != null && !childrenInstructions.isEmpty()) {
                            LOGGER.info("inward id" + inwardEntry.getInwardEntryId() + " is a parent instruction with " + childrenInstructions.size() + " children");
                            if (childrenInstructions.stream().anyMatch(ins -> !ins.getStatus().equals(deliveredStatus))) {
                                throw new RuntimeException("instruction with id " + instruction.getInstructionId() + " has undelivered children instructions");
                            }

                        }
                    } else {
                        LOGGER.error("No inward id or parent instruction id found in instruction with id " + instruction.getInstructionId());
                        throw new RuntimeException("No inward id or parent instruction id found in instruction with id " + instruction.getInstructionId());
                    }
//                if(weightToDeliver > parentWeight){
//                    LOGGER.error("weight to deliver "+weightToDeliver+" exceeds parent weight "+parentWeight);
//                    throw new RuntimeException("weight to deliver "+weightToDeliver+" exceeds parent weight "+parentWeight);
//                }
                    if (weightToDeliver > inStockWeight) {
                        LOGGER.error("weight to deliver " + weightToDeliver + " exceeds in stock weight " + inStockWeight);
                        throw new RuntimeException("weight to deliver " + weightToDeliver + " exceeds in stock weight " + inStockWeight);
                    }
                    inwardEntry.setInStockWeight(inStockWeight - weightToDeliver);
                    inwardEntryList.add(inwardEntry);
                    instruction.setRemarks(instructionRemarksMap.get(instruction.getInstructionId()));
                    delivery.addInstruction(instruction);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            float totalWeight = deliveryItemDetails.stream().reduce(0f,(sum,d) -> sum + d.getWeight().floatValue(),Float::sum);
            delivery.setTotalWeight(totalWeight);
            LOGGER.info("saving "+inwardEntryList.size()+" inward entries");
            inwardEntryService.saveAll(inwardEntryList);
            LOGGER.info("saving delivery details");
            deliveryDetailsRepo.save(delivery);
        return delivery;
    }

    @Override
    public void deleteById(int id) {
        deliveryDetailsRepo.deleteById(id);
    }

    @Override
    public List<DeliveryPacketsDto> deliveryList() {
        List<DeliveryDetails> inwardEntryList = deliveryDetailsRepo.findAllDeliveries();
        LOGGER.info("Delivery details list size "+inwardEntryList.size());
        return inwardEntryList.stream().map(inw -> new DeliveryPacketsDto(inw)).collect(Collectors.toList());

    }

    @Override
    public Float findInstructionByInwardIdAndInstructionId(Integer inwardId,Integer instructionId) {
        return deliveryDetailsRepo.findInstructionByInwardIdAndInstructionId(inwardId,instructionId);
    }


}
