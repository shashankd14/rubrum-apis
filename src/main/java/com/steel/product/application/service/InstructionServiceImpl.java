package com.steel.product.application.service;

import com.steel.product.application.dao.InstructionRepository;
import com.steel.product.application.dao.InwardEntryRepository;
import com.steel.product.application.dto.instruction.*;
import com.steel.product.application.dto.partDetails.PartDetailsResponse;
import com.steel.product.application.dto.partDetails.partDetailsRequest;
import com.steel.product.application.dto.pdf.InstructionResponsePdfDto;
import com.steel.product.application.dto.pdf.InwardEntryPdfDto;
import com.steel.product.application.dto.pdf.PartDetailsPdfResponse;
import com.steel.product.application.entity.*;
import com.steel.product.application.entity.Process;
import com.steel.product.application.mapper.InstructionMapper;
import com.steel.product.application.mapper.PartDetailsMapper;
import com.steel.product.application.mapper.TotalLengthAndWeight;
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

    Integer slitProcessId = 2;
    Integer cutProcessId = 1;
    Integer slitAndCutProcessId = 3;
    Integer inProgressStatusId = 2;

    private static final Logger LOGGER = LoggerFactory.getLogger(InstructionServiceImpl.class);

    private InstructionRepository instructionRepository;

    private InwardEntryRepository inwardEntryRepository;

    private InwardEntryService inwardService;

    private ProcessService processService;

    private StatusService statusService;

    private PacketClassificationService packetClassificationService;

    private PartDetailsService partDetailsService;

    private PartDetailsMapper partDetailsMapper;

    private InstructionMapper instructionMapper;

    @Autowired
    public InstructionServiceImpl(InstructionRepository instructionRepository, InwardEntryRepository inwardEntryRepository, InwardEntryService inwardService, ProcessService processService, StatusService statusService, PacketClassificationService packetClassificationService, PartDetailsService partDetailsService, PartDetailsMapper partDetailsMapper, InstructionMapper instructionMapper) {
        this.instructionRepository = instructionRepository;
        this.inwardEntryRepository = inwardEntryRepository;
        this.inwardService = inwardService;
        this.processService = processService;
        this.statusService = statusService;
        this.packetClassificationService = packetClassificationService;
        this.partDetailsService = partDetailsService;
        this.partDetailsMapper = partDetailsMapper;
        this.instructionMapper = instructionMapper;
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
    public ResponseEntity<Object> addCutInstruction(List<InstructionSaveRequestDto> cutInstructionSaveRequestDtos) {
        LOGGER.info("inside save cut instruction method");
        LOGGER.info("no of requests " + cutInstructionSaveRequestDtos.size());
        Map<PartDetails, List<InstructionRequestDto>> instructionPlanAndListMap = new HashMap<>();
        partDetailsRequest partDetailsRequest;
        InstructionRequestDto instructionRequestDto = cutInstructionSaveRequestDtos.get(0).getInstructionRequestDTOs().get(0);
        Integer inwardId = instructionRequestDto.getInwardId();
        Integer parentInstructionId = instructionRequestDto.getParentInstructionId();
        Integer groupId = instructionRequestDto.getGroupId();
        boolean fromInward = false, fromParentInstruction = false, fromGroup = false;

        InwardEntry inwardEntry;
        Instruction parentInstruction = null;
        PartDetails partDetails = null;
        String partDetailsId;
        Process process;
        double incomingWeight = 0f, availableWeight = 0f, existingWeight = 0f, remainingWeight = 0f;
        double incomingLength = 0f, availableLength = 0f, existingLength = 0f, remainingLength = 0f;

        if (inwardId != null && groupId != null) {
            LOGGER.info("adding instructions from inward "+ inwardId +", group id " + groupId);
            inwardEntry = inwardService.getByInwardEntryId(inwardId);
            TotalLengthAndWeight totalLengthAndWeight = this.sumOfPlannedLengthAndWeightOfInstructionsHavingGroupId(groupId);
            availableWeight = totalLengthAndWeight.getTotalWeight();
            availableLength = totalLengthAndWeight.getTotalLength();
            LOGGER.info("available length,weight for instructions with group id "+groupId+ " is "+availableLength+", "+availableWeight);
            process = processService.getById(this.slitAndCutProcessId);
            partDetailsId = null;
            fromGroup = true;
        } else if (inwardId != null) {
            LOGGER.info("adding instructions from inward id " + inwardId);
            inwardEntry = inwardService.getByInwardEntryId(inwardId);
            availableWeight = inwardEntry.getFpresent();
            availableLength = inwardEntry.getAvailableLength();
            LOGGER.info("available length,weight of inward "+inwardEntry.getInwardEntryId()+" is "+availableLength+", "+availableWeight);
            process = processService.getById(cutProcessId);
            partDetailsId = "DOC_" + System.nanoTime();
            fromInward = true;
        } else if (parentInstructionId != null) {
                LOGGER.info("adding instructions from parent instruction id " + parentInstructionId);
                TotalLengthAndWeight totalLengthAndWeight = this.sumOfPlannedLengthAndWeightOfInstructionsHavingParentInstructionId(parentInstructionId);
                existingLength = totalLengthAndWeight.getTotalLength();
                existingWeight = totalLengthAndWeight.getTotalWeight();
                LOGGER.info("existing length,weight is " +existingLength+", " +existingWeight);
                parentInstruction = this.findInstructionById(parentInstructionId);
                inwardEntry = parentInstruction.getInwardId();
                availableWeight = parentInstruction.getPlannedWeight();
                LOGGER.info("available length,weight is" + availableLength+", "+availableWeight);
                process = processService.getById(cutProcessId);
                partDetailsId = "DOC_" + System.nanoTime();
                fromParentInstruction = true;
            }
        else {
                return new ResponseEntity<Object>("Invalid request.", HttpStatus.BAD_REQUEST);
            }
        if (inwardEntry.getFpresent() < 0) {
            LOGGER.error("inward has negative fPresent value "+inwardEntry.getFpresent());
            return new ResponseEntity<Object>("Inward with id " + inwardId + " has invalid fpresent value " + inwardEntry.getFpresent(), HttpStatus.BAD_REQUEST);
        }

        for (InstructionSaveRequestDto cutInstructionDto : cutInstructionSaveRequestDtos) {
            incomingWeight += cutInstructionDto.getInstructionRequestDTOs().stream().filter(dto -> dto.getPlannedWeight() != null).reduce(0f, (sum, dto) -> sum + dto.getPlannedWeight(), Float::sum);
            incomingLength += cutInstructionDto.getInstructionRequestDTOs().stream().filter(dto -> dto.getPlannedWeight() != null).reduce(0f, (sum, dto) -> sum + dto.getPlannedLength(), Float::sum);
            partDetailsRequest = cutInstructionDto.getPartDetailsRequest();
            if(!fromGroup) {
                PartDetails partDetails1 = partDetailsMapper.toEntityForCut(partDetailsRequest);
                partDetails1.setPartDetailsId(partDetailsId);
                instructionPlanAndListMap.put(partDetails1, cutInstructionDto.getInstructionRequestDTOs());
            }else{
                instructionPlanAndListMap.put(null, cutInstructionDto.getInstructionRequestDTOs());
            }

        }

        remainingWeight = availableWeight - existingWeight - incomingWeight;
        remainingLength = availableLength - existingLength - incomingLength;
        LOGGER.info("remaining length,weight is "+remainingLength+", "+remainingWeight);
        if (fromGroup && Math.abs(remainingWeight) > 1f) {
            return new ResponseEntity<Object>("Input instructions total weight must be equal to instructions with group id " + groupId, HttpStatus.BAD_REQUEST);
        } else if (!fromGroup && remainingWeight < 0f) {
            LOGGER.error("remaining weight is invalid " + remainingWeight);
            return new ResponseEntity<Object>("inward " + inwardId + " has no available weight for processing.", HttpStatus.BAD_REQUEST);
        }
        if(remainingLength < 0f){
            LOGGER.error("remaining length is invalid "+ remainingLength);
            return new ResponseEntity<Object>("inward " + inwardId + " has no available length for processing.", HttpStatus.BAD_REQUEST);
        }
        if (fromInward) {
            LOGGER.info("setting fPresent for inward " + inwardEntry.getInwardEntryId() + " to " + remainingWeight);
            inwardEntry.setFpresent((float) remainingWeight);
            inwardEntry.setAvailableLength((float)remainingLength);
        }
        Status inProgressStatus = statusService.getStatusById(inProgressStatusId);
        inwardEntry.setStatus(inProgressStatus);
        List<Instruction> savedInstructionList = new ArrayList<Instruction>();
        for (PartDetails pd : instructionPlanAndListMap.keySet()) {
            for (InstructionRequestDto requestDto : instructionPlanAndListMap.get(pd)) {
                Instruction instruction = instructionMapper.toEntity(requestDto);
                instruction.setProcess(process);
                instruction.setStatus(inProgressStatus);
                if (fromParentInstruction) {
                    parentInstruction.addChildInstruction(instruction);
                } else if (fromInward) {
                    inwardEntry.addInstruction(instruction);
                } else {
                    inwardEntry.addInstruction(instruction);
                    instruction.setParentGroupId(groupId);
                }
                if(pd != null) {
                    pd.addInstruction(instruction);
                }
                savedInstructionList.add(instruction);
            }
        }
        LOGGER.info("saving " + instructionPlanAndListMap.keySet().size() + " part details objects");
        if(fromGroup) {
            savedInstructionList = saveAll(savedInstructionList);
            return new ResponseEntity<>(savedInstructionList.stream().map(i -> Instruction.valueOf(i)), HttpStatus.OK);
        }
        List<PartDetails> partDetailsList = partDetailsService.saveAll(instructionPlanAndListMap.keySet());
        List<PartDetailsResponse> partDetailsResponseList = partDetailsMapper.toResponseDto(partDetailsList);

        if (fromParentInstruction) {
            instructionRepository.save(parentInstruction);
        } else if (fromInward) {
            inwardService.saveEntry(inwardEntry);
        }
        return new ResponseEntity<Object>(partDetailsResponseList, HttpStatus.CREATED);
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
    public Instruction findInstructionById(Integer instructionId) {
        Optional<Instruction> instructionOptional = instructionRepository.findInstructionById(instructionId);
        if (!instructionOptional.isPresent()) {
            throw new RuntimeException("instruction with id " + instructionId + " is not found");
        }
        return instructionOptional.get();
    }

    @Override
    public List<Instruction> saveAll(List<Instruction> instructions) {
        return instructionRepository.saveAll(instructions);
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
    public Float sumOfPlannedLengthOfInstructionHavingParentInstructionId(Integer parentInstructionId) {
        return instructionRepository.sumOfPlannedLengthOfInstructionHavingParentInstructionId(parentInstructionId);
    }

    @Override
    public TotalLengthAndWeight sumOfPlannedLengthAndWeightOfInstructionsHavingParentInstructionId(Integer parentInstructionId) {
        return instructionRepository.sumOfPlannedLengthAndWeightOfInstructionsHavingParentInstructionId(parentInstructionId);
    }

    @Override
    public TotalLengthAndWeight sumOfPlannedLengthAndWeightOfInstructionsHavingGroupId(Integer groupId) {
        return instructionRepository.sumOfPlannedLengthAndWeightOfInstructionsHavingGroupId(groupId);
    }

    @Override
    public List<Instruction> getAllByInstructionIdIn(List<Integer> instructionIds) {
        return instructionRepository.getAllByInstructionIdIn(instructionIds);
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
        unprocessedInstruction.setIsSlitAndCut(false);

        inward.setFpresent(0f);
        inward.addInstruction(unprocessedInstruction);
        inward = inwardService.saveEntry(inward);
        Optional<InstructionResponseDto> savedInstruction = inward.getInstructions().stream().filter(ins -> ins.getProcess().getProcessId() == 7)
                .map(ins -> Instruction.valueOf(ins)).findFirst();

        return savedInstruction.orElse(null);

    }

    @Override
    @Transactional
    public ResponseEntity<Object> addSlitInstruction(List<InstructionSaveRequestDto> slitInstructionSaveRequestDtos) {

        LOGGER.info("inside save slit instruction method");
        LOGGER.info("no of requests " + slitInstructionSaveRequestDtos.size());
        Map<PartDetails, List<InstructionRequestDto>> instructionPlanAndListMap = new HashMap<>();
        partDetailsRequest partDetailsRequest;
        InstructionRequestDto instructionRequestDto = slitInstructionSaveRequestDtos.get(0).getInstructionRequestDTOs().get(0);
        Integer inwardId = instructionRequestDto.getInwardId();
        Integer parentInstructionId = instructionRequestDto.getParentInstructionId();
        boolean fromInward = false, fromParentInstruction = false;

        InwardEntry inwardEntry;
        Instruction parentInstruction = null;
        double incomingWeight = 0f, availableWeight = 0f, existingWeight = 0f, remainingWeight = 0f;
        double incomingLength = 0f, availableLength = 0f, existingLength = 0f, remainingLength = 0f;
        if (parentInstructionId != null) {
            LOGGER.info("adding instructions from parent instruction id " + parentInstructionId);
            parentInstruction = this.findInstructionById(parentInstructionId);
            inwardEntry = parentInstruction.getInwardId();
            LOGGER.info("parent instruction has inward " + inwardEntry.getInwardEntryId());
            TotalLengthAndWeight totalLengthAndWeight = this.sumOfPlannedLengthAndWeightOfInstructionsHavingParentInstructionId(parentInstructionId);
            existingLength = totalLengthAndWeight.getTotalLength();
            existingWeight = totalLengthAndWeight.getTotalWeight();
            LOGGER.info("existing weight of instructions with parent instruction id is " + existingWeight);
            availableWeight = parentInstruction.getPlannedWeight();
            availableLength = parentInstruction.getPlannedLength();
            LOGGER.info("available weight of parent instruction with id " + parentInstructionId + " is " + availableWeight);
            fromParentInstruction = true;
        } else if (inwardId != null) {
            LOGGER.info("adding instructions from inward id " + inwardId);
            inwardEntry = inwardService.getByInwardEntryId(inwardId);
            availableWeight = inwardEntry.getFpresent();
            availableLength = inwardEntry.getAvailableLength();
            LOGGER.info("available weight of inward " + inwardEntry.getInwardEntryId() + " is " + availableWeight);
            fromInward = true;
        } else {
            return new ResponseEntity<Object>("Invalid request", HttpStatus.BAD_REQUEST);
        }
        String partDetailsId = "DOC_" + System.nanoTime();

        for (InstructionSaveRequestDto slitInstructionDto : slitInstructionSaveRequestDtos) {
            incomingWeight += slitInstructionDto.getInstructionRequestDTOs().stream().filter(dto -> dto.getPlannedWeight() != null).reduce(0f, (sum, dto) -> sum + dto.getPlannedWeight(), Float::sum);
            incomingLength += slitInstructionDto.getPartDetailsRequest().getLength();
            partDetailsRequest = slitInstructionDto.getPartDetailsRequest();
            PartDetails partDetails = partDetailsMapper.toEntityForSlit(partDetailsRequest);
            partDetails.setPartDetailsId(partDetailsId);
            instructionPlanAndListMap.put(partDetails, slitInstructionDto.getInstructionRequestDTOs());
        }
        LOGGER.info("incoming weight is " + incomingWeight);
        remainingWeight = (availableWeight - existingWeight - incomingWeight);
        remainingLength = (availableLength - existingLength - incomingLength);
        LOGGER.info("remaining length is "+remainingLength+" remaining weight is " + remainingWeight);
        if (remainingWeight < 0f) {
            LOGGER.error("incoming weight is " + incomingWeight + " greater than available weight " + availableWeight);
            throw new RuntimeException(inwardId != null ? "inward " + inwardId + " has no available weight for processing" :
                    "parent instruction " + parentInstructionId + " has no available weight for processing");
        }
        if (remainingLength < 0f) {
            LOGGER.error("incoming length is " + incomingLength + " greater than available length " + availableLength);
            throw new RuntimeException(inwardId != null ? "inward " + inwardId + " has no available length for processing" :
                    "parent instruction " + parentInstructionId + " has no available length for processing");
        }
        if (fromInward) {
            LOGGER.info("setting fPresent for inward " + inwardEntry.getInwardEntryId() + " to " + remainingWeight);
            inwardEntry.setFpresent((float) remainingWeight);
            inwardEntry.setAvailableLength((float)remainingLength);
        }
        Process slitProcess = processService.getById(this.slitProcessId);
        Status inProgressStatus = statusService.getStatusById(this.inProgressStatusId);
        inwardEntry.setStatus(inProgressStatus);
        for (PartDetails partDetails : instructionPlanAndListMap.keySet()) {
            for (InstructionRequestDto requestDto : instructionPlanAndListMap.get(partDetails)) {
                Instruction instruction = instructionMapper.toEntity(requestDto);
                instruction.setProcess(slitProcess);
                instruction.setStatus(inProgressStatus);

                if (fromParentInstruction) {
                    parentInstruction.addChildInstruction(instruction);
                } else if (fromInward) {
                    inwardEntry.addInstruction(instruction);
                }
                partDetails.addInstruction(instruction);
            }
        }
        LOGGER.info("saving " + instructionPlanAndListMap.keySet().size() + " part details objects");
        List<PartDetails> partDetails = partDetailsService.saveAll(instructionPlanAndListMap.keySet());
        if (fromParentInstruction) {
            LOGGER.info("saving instructions from parent instruction " + parentInstructionId);
            instructionRepository.save(parentInstruction);
        } else if (fromInward) {
            LOGGER.info("saving instructions from inward " + inwardId);
            inwardService.saveEntry(inwardEntry);
        }
        List<PartDetailsResponse> partDetailsResponseList = partDetailsMapper.toResponseDto(partDetails);
        return new ResponseEntity<Object>(partDetailsResponseList, HttpStatus.CREATED);

    }

    @Override
    public InwardEntryPdfDto findInwardJoinFetchInstructionsAndPartDetails(String partDetailsId) {
        List<Object[]> objects = instructionRepository.findPartDetailsJoinFetchInstructions(partDetailsId);
        Map<PartDetailsPdfResponse, List<InstructionResponsePdfDto>> partDetailsMap = new HashMap<>();
        Integer inwardId = null;
        Integer processId = null;
        float totalWeight = 0f;
        for (Object[] obj : objects) {
            PartDetails partDetails = (PartDetails) obj[0];
            Instruction instruction = (Instruction) obj[1];
            if (inwardId == null && processId == null) {
                inwardId = instruction.getInwardId().getInwardEntryId();
                processId = instruction.getProcess().getProcessId();
            }
            Long count = (Long) obj[2];
            PartDetailsPdfResponse partDetailsPdfResponse = partDetailsMapper.toPartDetailsPdfResponse(partDetails);
            InstructionResponsePdfDto instructionResponsePdfDto = instructionMapper.toResponsePdfDto(instruction);
            instructionResponsePdfDto.setCountOfWeight(count);
            if(processId == 1){
                totalWeight += instruction.getPlannedWeight()*count;
            }
            if (partDetailsMap.isEmpty() || !partDetailsMap.containsKey(partDetailsPdfResponse)) {
                List<InstructionResponsePdfDto> list = new ArrayList<>();
                list.add(instructionResponsePdfDto);
                partDetailsMap.put(partDetailsPdfResponse, list);
                if(processId == 2) {
                    totalWeight += partDetailsPdfResponse.getTargetWeight();
                }
            } else {
                List<InstructionResponsePdfDto> list = partDetailsMap.get(partDetailsPdfResponse);
                list.add(instructionResponsePdfDto);
                partDetailsMap.put(partDetailsPdfResponse, list);
            }

        }
        InwardEntry inwardEntry = inwardService.getByEntryId(inwardId);
        InwardEntryPdfDto inwardEntryPdfDto = InwardEntry.valueOf(inwardEntry, null);
        inwardEntryPdfDto.setPartDetailsMap(partDetailsMap);
        inwardEntryPdfDto.setVProcess(String.valueOf(processId));
        inwardEntryPdfDto.setTotalWeight(totalWeight);
        return inwardEntryPdfDto;
    }


}
