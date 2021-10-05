package com.steel.product.application.service;

import com.steel.product.application.dao.InstructionRepository;
import com.steel.product.application.dao.InwardEntryRepository;
import com.steel.product.application.dto.instruction.InstructionRequestDto;
import com.steel.product.application.dto.instruction.InstructionFinishDto;
import com.steel.product.application.dto.instruction.InstructionResponseDto;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InstructionServiceImpl implements InstructionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstructionServiceImpl.class);

    private InstructionRepository instructionRepository;

    private InwardEntryRepository inwardEntryRepository;

    private InwardEntryService inwardService;

    private ProcessService processService;

    private StatusService statusService;

    private PacketClassificationService packetClassificationService;

    @Autowired
    public InstructionServiceImpl(InstructionRepository instructionRepository, InwardEntryRepository inwardEntryRepository, InwardEntryService inwardService, ProcessService processService, StatusService statusService, PacketClassificationService packetClassificationService) {
        this.instructionRepository = instructionRepository;
        this.inwardEntryRepository = inwardEntryRepository;
        this.inwardService = inwardService;
        this.processService = processService;
        this.statusService = statusService;
        this.packetClassificationService = packetClassificationService;
    }

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
    @Transactional
    public ResponseEntity<Object> addInstruction(List<InstructionRequestDto> instructionRequestDtos) {
        LOGGER.info("inside save instruction method");
        Integer inProgressStatusId = 2;
        float incomingWeight = instructionRequestDtos.stream().filter(dto -> dto.getPlannedWeight() != null).reduce(0f, (sum, dto) -> sum + dto.getPlannedWeight(), Float::sum);
        float availableWeight = 0f;
        float existingWeight = 0f;
        boolean fromInward = false, fromParentInstruction = false, fromGroup = false;
        InwardEntry inwardEntry;
        Instruction parentInstruction = null;
        InstructionRequestDto instructionRequestDto = instructionRequestDtos.get(0);
        Integer inwardId = instructionRequestDto.getInwardId();
        Integer groupId = instructionRequestDto.getGroupId();
        Integer parentInstructionId = instructionRequestDto.getParentInstructionId();
        LOGGER.info("incoming weight " + incomingWeight);

        if (inwardId != null && groupId != null) {
            LOGGER.info("adding instructions from inward "+ inwardId +", group id " + groupId);
            inwardEntry = inwardService.getByInwardEntryId(inwardId);
            availableWeight = this.sumOfPlannedWeightOfInstructionsHavingGroupId(groupId);
            LOGGER.info("available weight for instructions with group id "+groupId+ " is "+availableWeight);
            fromGroup = true;
        } else if (inwardId != null) {
            LOGGER.info("adding instructions from inward id " + inwardId);
            inwardEntry = inwardService.getByInwardEntryId(inwardId);
            availableWeight = inwardEntry.getFpresent();
            LOGGER.info("available weight of inward "+inwardEntry.getInwardEntryId()+" is "+availableWeight);
            fromInward = true;
        } else if (parentInstructionId != null) {
                LOGGER.info("adding instructions from parent instruction id " + parentInstructionId);
                existingWeight = this.sumOfPlannedWeightOfInstructionHavingParentInstructionId(parentInstructionId);
                LOGGER.info("existing weight of instructions with parent instruction id is "+existingWeight);
                parentInstruction = this.getById(parentInstructionId);
                inwardEntry = parentInstruction.getInwardId();
                availableWeight = parentInstruction.getPlannedWeight();
                fromParentInstruction = true;
            }
        else {
                return new ResponseEntity<Object>("Invalid request.", HttpStatus.BAD_REQUEST);
            }
        if (inwardEntry.getFpresent() < 0) {
            LOGGER.error("inward has negative fpresent value "+inwardEntry.getFpresent());
            return new ResponseEntity<Object>("Inward with id " + inwardId + " has invalid fpresent value " + inwardEntry.getFpresent(), HttpStatus.BAD_REQUEST);
        }
        Float remainingWeight = availableWeight - existingWeight - incomingWeight;
        LOGGER.info("remaining weight is "+remainingWeight);
        if (fromGroup && Math.abs(remainingWeight) > 1f) {
            return new ResponseEntity<Object>("Input instructions total weight must be equal to instructions with group id " + groupId, HttpStatus.BAD_REQUEST);
        } else if (!fromGroup && remainingWeight < 0f) {
            LOGGER.error("remaining weight is invalid " + remainingWeight);
            return new ResponseEntity<Object>("inward " + inwardId + " has no available weight for processing.", HttpStatus.BAD_REQUEST);
        }
        if (fromInward) {
            LOGGER.info("setting fPresent for inward " + inwardEntry.getInwardEntryId() + " to " + remainingWeight);
            inwardEntry.setFpresent(remainingWeight);
        }
        Process process = processService.getById(instructionRequestDto.getProcessId());
        Status inProgressStatus = statusService.getStatusById(inProgressStatusId);
        inwardEntry.setStatus(inProgressStatus);
        List<Instruction> savedInstructionList = new ArrayList<Instruction>();
        try {
            for (InstructionRequestDto requestDto : instructionRequestDtos) {

                Instruction instruction = new Instruction();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                instruction.setProcess(process);
                instruction.setInstructionDate(requestDto.getInstructionDate());

                if (requestDto.getPlannedLength() != null)
                    instruction.setPlannedLength(requestDto.getPlannedLength());

                if (requestDto.getPlannedWidth() != null)
                    instruction.setPlannedWidth(requestDto.getPlannedWidth());

                if (requestDto.getPlannedWeight() != null)
                    instruction.setPlannedWeight(requestDto.getPlannedWeight());

                if (requestDto.getPlannedNoOfPieces() != null)
                    instruction.setPlannedNoOfPieces(requestDto.getPlannedNoOfPieces());

                instruction.setStatus(inProgressStatus);
                instruction.setSlitAndCut(requestDto.getSlitAndCut() != null ? requestDto.getSlitAndCut() : false);

                if (requestDto.getWastage() != null)
                    instruction.setWastage(requestDto.getWastage());

                if (requestDto.getDamage() != null)
                    instruction.setDamage(requestDto.getDamage());

                if (requestDto.getPackingWeight() != null)
                    instruction.setPackingWeight(requestDto.getPackingWeight());

                instruction.setCreatedBy(requestDto.getCreatedBy());
                instruction.setUpdatedBy(requestDto.getUpdatedBy());
                instruction.setCreatedOn(timestamp);
                instruction.setUpdatedOn(timestamp);
                instruction.setIsDeleted(false);

                if (fromParentInstruction) {
                    parentInstruction.addChildInstruction(instruction);
                } else if (fromInward) {
                    inwardEntry.addInstruction(instruction);
                } else {
                    inwardEntry.addInstruction(instruction);
                    instruction.setParentGroupId(requestDto.getGroupId());
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

            instruction.setStatus(readyToDeliverStatus);


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
                groupInstructions.forEach(in -> in.setStatus(readyToDeliverStatus));
                instructionRepository.saveAll(groupInstructions);
            }
            LOGGER.info("group instructions with group id " + parentGroupId + " is in progress");
        } else if (inwardEntry != null) {
            LOGGER.info("instruction has inward " + savedInstruction.getInwardId().getInwardEntryId());
            isAnyInstructionInProgress = inwardEntry.getInstructions().stream().anyMatch(cin -> cin.getStatus().equals(inProgressStatus));
            if (!isAnyInstructionInProgress) {
                LOGGER.info("inward " + inwardEntry + " ready to deliver");
                inwardEntry.setStatus(readyToDeliverStatus);
                inwardService.saveEntry(inwardEntry);
            }
            LOGGER.info("inward " + inwardEntry + " is in progress");
        } else if (parentInstruction != null) {
            LOGGER.info("instruction has parent instruction " + savedInstruction.getParentInstruction().getInstructionId());
            Set<Instruction> childrenInstructions = parentInstruction.getChildInstructions();
            LOGGER.info("parent instruction has children "+childrenInstructions.size());
            isAnyInstructionInProgress = childrenInstructions.stream().anyMatch(cin -> cin.getStatus().equals(inProgressStatus));
            if (!isAnyInstructionInProgress) {
                LOGGER.info("parent instruction " + parentInstruction.getInstructionId() + " ready to deliver");
                Float parentActualWeight = childrenInstructions.stream().map(in -> in.getActualWeight()).reduce(0f, Float::sum);
                LOGGER.info("parent actual weight "+parentActualWeight);
                parentInstruction.setActualWeight(parentActualWeight);
                parentInstruction.setStatus(readyToDeliverStatus);
                LOGGER.info("saving parent instruction id " + parentInstruction.getInstructionId());
                instructionRepository.save(parentInstruction);
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
    public Float sumOfPlannedWeightOfInstructionsHavingGroupId(Integer groupId) {
        return instructionRepository.sumOfPlannedWeightOfInstructionsHavingGroupId(groupId);
    }

    @Override
    public Float sumOfPlannedWeightOfInstructionHavingParentInstructionId(Integer parentInstructionId) {
        return instructionRepository.sumOfPlannedWeightOfInstructionHavingParentInstructionId(parentInstructionId);
    }

    @Override
    public List<Instruction> getAllByInstructionIdIn(List<Integer> instructionIds) {
        return instructionRepository.getAllByInstructionIdIn(instructionIds);
    }

    @Override
    public Map<Instruction, List<Double>> findInstructionsByInwardIdGroupedByPlannedLengthAndWeight(Integer inwardId) {
        List<Object[]> result = instructionRepository.findInstructionsByInwardIdGroupedByPlannedLengthAndWeight(171);
        Map<Instruction, List<Double>> map = null;
        if (result != null && !result.isEmpty()) {
            map = new HashMap<>();
            for (Object[] object : result) {
                map.put(((Instruction) object[0]), Arrays.asList((Double) object[1], (Double) object[2]));
            }
        }
        return map;
    }


    @Override
    public List<Instruction> findInstructionsWithDeliveryDetails(List<Integer> instructionIds) {
        return instructionRepository.findInstructionsWithDeliveryDetails(instructionIds);
    }

    @Override
    public List<Instruction> findSlitAndCutInstructionByInwardId(Integer inwardId) {
        return instructionRepository.findSlitAndCutInstructionByInwardId(inwardId);
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

    @Override
    public InstructionResponseDto saveUnprocessedForDelivery(Integer inwardId) {
        LOGGER.info("inside saveUnprocessedForDelivery method");
        Date date = new Date();
        Instruction unprocessedInstruction = new Instruction();
        InwardEntry inward = inwardService.getByInwardEntryId(inwardId);
        LOGGER.info("inward with id " + inwardId + " has fPresent " + inward.getFpresent());
        Integer handlingProcessId = 7, readyToDeliverStatusId = 3;
        Process handlingProcess = processService.getById(handlingProcessId);
        Status readyToDeliverStatus = statusService.getStatusById(readyToDeliverStatusId);

        float denominator = inward.getfThickness() * (inward.getfWidth() / 1000f) * 7.85f;
        float lengthUnprocessed = inward.getFpresent() / denominator;
        LOGGER.info("calculated length of instruction " + lengthUnprocessed);

        unprocessedInstruction.setPlannedLength(lengthUnprocessed);
        unprocessedInstruction.setPlannedWidth(inward.getfWidth());
        unprocessedInstruction.setPlannedWeight(inward.getFpresent());
        unprocessedInstruction.setProcess(handlingProcess);
        unprocessedInstruction.setStatus(readyToDeliverStatus);
        unprocessedInstruction.setInstructionDate(date);
        unprocessedInstruction.setCreatedBy(1);
        unprocessedInstruction.setUpdatedBy(1);
        unprocessedInstruction.setCreatedOn(date);
        unprocessedInstruction.setUpdatedOn(date);
        unprocessedInstruction.setIsDeleted(false);
        unprocessedInstruction.setSlitAndCut(false);

        inward.setFpresent(0f);
        inward.addInstruction(unprocessedInstruction);
        inward = inwardService.saveEntry(inward);
        Optional<InstructionResponseDto> savedInstruction = inward.getInstructions().stream().filter(ins -> ins.getProcess().getProcessId() == 7)
                .map(ins -> Instruction.valueOf(ins)).findFirst();

        return savedInstruction.orElse(null);

    }


}
