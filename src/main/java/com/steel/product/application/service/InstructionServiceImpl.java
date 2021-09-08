package com.steel.product.application.service;

import com.steel.product.application.dao.InstructionRepository;
import com.steel.product.application.dao.InwardEntryRepository;
import com.steel.product.application.dto.instruction.InstructionRequestDto;
import com.steel.product.application.dto.instruction.InstructionFinishDto;
import com.steel.product.application.entity.*;
import com.steel.product.application.entity.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InstructionServiceImpl implements InstructionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstructionServiceImpl.class);

    @Autowired
    private InstructionRepository instructionRepository;

    @Autowired
    private InwardEntryRepository inwardEntryRepository;

    @Autowired
    private InwardEntryService inwardService;

    @Autowired
    private ProcessService processService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private PacketClassificationService packetClassificationService;

    @Override
    public List<Instruction> getAll() {
        return instructionRepository.findAll();
    }

    @Override
    public List<Instruction> getAllWIP() {
        return instructionRepository.getAllWIP();
    }

    @Override
    public List<Instruction> getAllWIPList() {
        return instructionRepository.getAllWIPList();
    }

    @Override
    public Instruction getById(int theId) {

        Optional<Instruction> result = instructionRepository.findById(theId);

        Instruction theInstruction = null;

        if (result.isPresent()) {
            theInstruction = result.get();
        } else {
            // we didn't find the employee
            throw new RuntimeException("Did not find instruction id - " + theId);
        }

        return theInstruction;
    }

    @Override
    public Instruction save(Instruction instruction) {
        Instruction savedInstruction = new Instruction();
        if (instruction.getInwardId() != null) {
            InwardEntry inwardEntry = instruction.getInwardId();
            if (instruction.getPlannedWeight() != null && inwardEntry.getFpresent() != null)
                inwardEntry.setFpresent((inwardEntry.getFpresent() - instruction.getPlannedWeight()));
            inwardEntryRepository.save(inwardEntry);
        }

        savedInstruction = instructionRepository.save(instruction);

        return savedInstruction;
    }

    @Transactional
    @Override
    public ResponseEntity<Object> addInstruction(List<InstructionRequestDto> instructionRequestDtos) {
        float incomingWeight = 0f;
        float availableWeight = 0f;
        float existingWeight = 0f;
        boolean fromInward = false, fromParentInstruction = false, fromGroup = false;
        InwardEntry inwardEntry;
        Instruction parentInstruction = null;

        if (instructionRequestDtos.get(0).getInwardId() != null && instructionRequestDtos.get(0).getGroupId() != null) {
            LOGGER.info("adding instructions from group id " + instructionRequestDtos.get(0).getInwardId());
            inwardEntry = inwardService.getByEntryId(instructionRequestDtos.get(0).getInwardId());
            List<Instruction> existingInstructions = findAllByParentGroupId(instructionRequestDtos.get(0).getGroupId());
            if (existingInstructions.size() > 0) {
                return new ResponseEntity<Object>("Instructions with parent group id " + instructionRequestDtos.get(0).getGroupId() + " already exists.", HttpStatus.BAD_REQUEST);
            }

            incomingWeight = (float) instructionRequestDtos.stream().
                    mapToDouble(i -> i.getPlannedWeight().floatValue()).sum();
            availableWeight = (float) findAllByGroupId(instructionRequestDtos.get(0).getGroupId()).stream()
                    .mapToDouble(i -> i.getPlannedWeight().floatValue()).sum();
            fromGroup = true;
        } else if (instructionRequestDtos.get(0).getInwardId() != null) {
            LOGGER.info("adding instructions from inward id " + instructionRequestDtos.get(0).getInwardId());
            incomingWeight = (float) instructionRequestDtos.stream().mapToDouble(InstructionRequestDto::getPlannedWeight).sum();
            inwardEntry = inwardService.getByEntryId(instructionRequestDtos.get(0).getInwardId());
            availableWeight = inwardEntry.getFpresent();
            fromInward = true;
        } else if (instructionRequestDtos.get(0).getParentInstructionId() != null) {
            LOGGER.info("adding instructions from parent instruction id " + instructionRequestDtos.get(0).getParentInstructionId());
            incomingWeight = (float) instructionRequestDtos.stream().
                    mapToDouble(i -> i.getPlannedWeight().floatValue()).sum();
            LOGGER.info("incoming weight " + incomingWeight);
            existingWeight = (float) findAllByParentInstructionId(instructionRequestDtos.get(0).getParentInstructionId())
                    .stream().mapToDouble(i -> i.getPlannedWeight().floatValue()).sum();
            parentInstruction = getById(instructionRequestDtos.get(0).getParentInstructionId());
            inwardEntry = parentInstruction.getInwardId();
            availableWeight = parentInstruction.getPlannedWeight().floatValue();
            fromParentInstruction = true;
        } else {
            return new ResponseEntity<Object>("Invalid request.", HttpStatus.BAD_REQUEST);
        }
        if (inwardEntry.getFpresent() < 0) {
            return new ResponseEntity<Object>("Inward with id " + inwardEntry.getInwardEntryId() + " has invalid fpresent value " + inwardEntry.getFpresent(), HttpStatus.BAD_REQUEST);
        }
        if (incomingWeight > availableWeight - existingWeight) {
            return new ResponseEntity<Object>("No available weight for processing.", HttpStatus.BAD_REQUEST);
        }
        if (fromGroup && incomingWeight != availableWeight) {
            return new ResponseEntity<Object>("Input instructions total weight must be equal to instructions with group id " + instructionRequestDtos.get(0).getGroupId(), HttpStatus.BAD_REQUEST);
        }
        Process process = processService.getById(instructionRequestDtos.get(0).getProcessId());
        Status inProgress = statusService.getStatusById(2);
        inProgress.addInwardEntry(inwardEntry);
        List<Instruction> savedInstructionList = new ArrayList<Instruction>();
        try {
            for (InstructionRequestDto instructionRequestDto : instructionRequestDtos) {

                Instruction instruction = new Instruction();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                instruction.setProcess(process);
                instruction.setInstructionDate(instructionRequestDto.getInstructionDate());

                if (instructionRequestDto.getPlannedLength() != null)
                    instruction.setPlannedLength(instructionRequestDto.getPlannedLength());

                if (instructionRequestDto.getPlannedWidth() != null)
                    instruction.setPlannedWidth(instructionRequestDto.getPlannedWidth());

                if (instructionRequestDto.getPlannedWeight() != null)
                    instruction.setPlannedWeight(instructionRequestDto.getPlannedWeight());

                if (instructionRequestDto.getPlannedNoOfPieces() != null)
                    instruction.setPlannedNoOfPieces(instructionRequestDto.getPlannedNoOfPieces());

                inProgress.addInstruction(instruction);
                instruction.setSlitAndCut(instructionRequestDto.getSlitAndCut() != null ? instructionRequestDto.getSlitAndCut() : false);

                if (instructionRequestDto.getWastage() != null)
                    instruction.setWastage(instructionRequestDto.getWastage());

                if (instructionRequestDto.getDamage() != null)
                    instruction.setDamage(instructionRequestDto.getDamage());

                if (instructionRequestDto.getPackingWeight() != null)
                    instruction.setPackingWeight(instructionRequestDto.getPackingWeight());

                instruction.setCreatedBy(instructionRequestDto.getCreatedBy());
                instruction.setUpdatedBy(instructionRequestDto.getUpdatedBy());
                instruction.setCreatedOn(timestamp);
                instruction.setUpdatedOn(timestamp);
                instruction.setIsDeleted(false);

                if (fromParentInstruction) {
                    parentInstruction.addChildInstruction(instruction);
                } else if (fromInward) {
                    inwardEntry.addInstruction(instruction);
                } else {
                    inwardEntry.addInstruction(instruction);
                    instruction.setParentGroupId(instructionRequestDto.getGroupId());
                }
                savedInstructionList.add(instruction);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        savedInstructionList = saveAll(savedInstructionList);
        if (fromParentInstruction) {
            instructionRepository.save(parentInstruction);
        } else if (fromInward) {
            inwardEntry.setFpresent(availableWeight - incomingWeight);
            inwardService.saveEntry(inwardEntry);
        }
        return new ResponseEntity<>(savedInstructionList.stream().map(i -> Instruction.valueOf(i)), HttpStatus.OK);
    }

    @Override
    @Transactional
    public void deleteById(Instruction deleteInstruction) {
        deleteInstruction.setPacketClassification(null);
        deleteInstruction.setDeliveryDetails(null);
        if (deleteInstruction.getInwardId() != null) {
            InwardEntry inwardEntry = deleteInstruction.getInwardId();
            if (deleteInstruction.getPlannedWeight() != null && inwardEntry.getFpresent() != null) {
                inwardEntry.setFpresent((inwardEntry.getFpresent() + deleteInstruction.getPlannedWeight()));
            }
            if (deleteInstruction.getDeliveryDetails() != null && deleteInstruction.getActualWeight() != null && inwardEntry.getInStockWeight() != null) {
                inwardEntry.setInStockWeight(inwardEntry.getInStockWeight() + deleteInstruction.getActualWeight());
            }
            inwardEntry.removeInstruction(deleteInstruction);
            inwardEntryRepository.save(inwardEntry);
        } else if (deleteInstruction.getParentInstruction() != null) {
            Instruction parentInstruction = deleteInstruction.getParentInstruction();
            parentInstruction.removeChildInstruction(deleteInstruction);
            instructionRepository.save(parentInstruction);
        } else {
            throw new RuntimeException("Cannot find inward or parent instruction from the instruction");
        }

    }

    @Override
    public List<Instruction> findAllByGroupId(Integer groupId) {
        return instructionRepository.findByGroupId(groupId);
    }

    @Override
    public List<Instruction> findAllByParentGroupId(Integer parentGroupId) {
        return instructionRepository.findByParentGroupId(parentGroupId);
    }

    @Override
    public List<Instruction> findAllByParentInstructionId(Integer parentInstructionId) {
        return instructionRepository.findByParentInstructionId(parentInstructionId);
    }

    @Override
    public void updateInstructionWithDeliveryRemarks(int deliveryId,
                                                     String remarks, int instructionId) {
        instructionRepository.updateInstructionWithDeliveryRemarks(instructionId, deliveryId, remarks);
    }

    @Override
    public List<Instruction> saveAll(List<Instruction> instructions) {
        return instructionRepository.saveAll(instructions);
    }

    @Override
    public List<Instruction> findInstructionsByInstructionIdInAndStatusNot(List<Integer> ids, Status status) {
        return instructionRepository.findInstructionsByInstructionIdInAndStatusNot(ids, status);
    }

    @Override
    @Transactional
    public ResponseEntity<Object> updateInstruction(InstructionFinishDto instructionFinishDto) {
        LOGGER.info("in finish instruction method");
        List<InstructionRequestDto> InstructionRequestDtos = instructionFinishDto.getInstructionDtos();
        List<Instruction> updatedInstructionList = new ArrayList<Instruction>();
        Instruction instruction;
        Integer inProgressStatusId = 2, readyToDeliverStatusId = 3;
        Status inProgressStatus = statusService.getStatusById(inProgressStatusId);
        Status readyToDeliverStatus = statusService.getStatusById(readyToDeliverStatusId);
        List<Instruction> instructions = this.findAllByInstructionIdInAndStatus(InstructionRequestDtos.stream()
                .map(ins -> ins.getInstructionId()).collect(Collectors.toList()), inProgressStatusId);
        Map<Integer, Instruction> instructionsMap = instructions.stream().collect(Collectors.toMap(ins -> ins.getInstructionId(), ins -> ins));
        if(instructionsMap.isEmpty()){
        	LOGGER.error("no instructions found in progress status");
			return new ResponseEntity<Object>("all instructions already finished", HttpStatus.BAD_REQUEST);
		}
        Map<Integer, PacketClassification> packetClassificationMap = packetClassificationService.findAllByPacketClassificationIdIn(InstructionRequestDtos.stream()
                .map(ins -> ins.getPacketClassificationId()).collect(Collectors.toList())).stream().collect(Collectors.toMap(p -> p.getClassificationId(), p -> p));

        for (InstructionRequestDto ins : InstructionRequestDtos) {
            instruction = instructionsMap.get(ins.getInstructionId());
            if (instruction == null) {
                continue;
            }
            if(instruction.getChildInstructions().stream().anyMatch(ins1 -> ins1.getStatus().equals(inProgressStatus))){
                throw new RuntimeException("instruction with id "+instruction.getInstructionId()+" has children with in progress status");
            }
            instruction.setActualLength(ins.getActualLength());
            instruction.setActualWidth(ins.getActualWidth());
            instruction.setActualWeight(ins.getActualWeight());
            instruction.setActualNoOfPieces(ins.getActualNoOfPieces());
            instruction.setPacketClassification(packetClassificationMap.get(ins.getPacketClassificationId()));

                readyToDeliverStatus.addInstruction(instruction);


            updatedInstructionList.add(instruction);
        }
        instructionRepository.saveAll(updatedInstructionList);
        LOGGER.info("saved all instructions");
        boolean isAnyInstructionInProgress = false;
        Instruction savedInstruction = updatedInstructionList.get(0);

        InwardEntry inwardEntry = savedInstruction.getInwardId();
        Instruction parentInstruction = savedInstruction.getParentInstruction();
        Integer parentGroupId = savedInstruction.getParentGroupId();
        List<Instruction> parentGroupInstructions;
        List<Instruction> groupInstructions;

        if (inwardEntry != null && parentGroupId != null) {
            LOGGER.info("instruction has group id " + parentGroupId);
            parentGroupInstructions = instructionRepository.findByParentGroupId(parentGroupId);
            isAnyInstructionInProgress = parentGroupInstructions.stream().anyMatch(gin -> gin.getStatus().equals(inProgressStatus));
            if (!isAnyInstructionInProgress) {
                LOGGER.info("group instructions with group id " + parentGroupId + " ready to deliver");
                groupInstructions = instructionRepository.findByGroupId(parentGroupId);
                groupInstructions.forEach(in -> readyToDeliverStatus.addInstruction(in));
                instructionRepository.saveAll(groupInstructions);
            }
            LOGGER.info("group instructions with group id " + parentGroupId + " is in progress");
        } else if (inwardEntry != null) {
            LOGGER.info("instruction has inward " + savedInstruction.getInwardId().getInwardEntryId());
            isAnyInstructionInProgress = inwardEntry.getInstruction().stream().anyMatch(cin -> cin.getStatus().equals(inProgressStatus));
            if (!isAnyInstructionInProgress) {
                LOGGER.info("inward " + inwardEntry + " ready to deliver");
                readyToDeliverStatus.addInwardEntry(inwardEntry);
                inwardService.saveEntry(inwardEntry);
            }
            LOGGER.info("inward " + inwardEntry + " is in progress");
        } else if (parentInstruction != null) {
            LOGGER.info("instruction has parent instruction " + savedInstruction.getParentInstruction().getInstructionId());
            List<Instruction> childrenInstructions = parentInstruction.getChildInstructions();
            LOGGER.info("parent instruction has children "+childrenInstructions.size());
            isAnyInstructionInProgress = childrenInstructions.stream().anyMatch(cin -> cin.getStatus().equals(inProgressStatus));
            if (!isAnyInstructionInProgress) {
                LOGGER.info("parent instruction " + parentInstruction.getInstructionId() + " ready to deliver");
                Float parentActualWeight = childrenInstructions.stream().map(in -> in.getActualWeight()).reduce(0f, Float::sum);
                LOGGER.info("parent actual weight "+parentActualWeight);
                parentInstruction.setActualWeight(parentActualWeight);
                readyToDeliverStatus.addInstruction(parentInstruction);
                LOGGER.info("saving parent instruction id "+parentInstruction.getInstructionId());
                this.save(parentInstruction);
            }
        } else {
            LOGGER.error("no inwardId, parentInstructionId or parentGroupId found");
            throw new RuntimeException("Invalid request");
        }

        return new ResponseEntity<Object>(updatedInstructionList.stream().map(i -> Instruction.valueOf(i)), HttpStatus.OK);
    }

    @Override
    public List<Instruction> findAllByInstructionIdInAndStatus(List<Integer> instructionIds, Integer statusId) {
        return instructionRepository.findAllByInstructionIdInAndStatus(instructionIds, statusId);
    }

    @Override
    public List<Instruction> findInstructionsWithDeliveryDetails(List<Integer> instructionIds) {
        return instructionRepository.findInstructionsWithDeliveryDetails(instructionIds);
    }


}
