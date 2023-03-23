package com.steel.product.application.service;

import com.steel.product.application.dao.DeliveryDetailsRepository;
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
import com.steel.product.application.exception.MockException;
import com.steel.product.application.mapper.InstructionMapper;
import com.steel.product.application.mapper.PartDetailsMapper;
import com.steel.product.application.mapper.TotalLengthAndWeight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private DeliveryDetailsRepository deliveryDetailsRepository;
    
    private InwardEntryService inwardService;

    private ProcessService processService;

    private StatusService statusService;

    private PacketClassificationService packetClassificationService;

    private EndUserTagsService endUserTagsService;

    private PartDetailsService partDetailsService;

    private PartDetailsMapper partDetailsMapper;

    private InstructionMapper instructionMapper;

	@Autowired
	public InstructionServiceImpl(InstructionRepository instructionRepository,
			InwardEntryRepository inwardEntryRepository, InwardEntryService inwardService,
			ProcessService processService, StatusService statusService,
			PacketClassificationService packetClassificationService, EndUserTagsService endUserTagsService,
			PartDetailsService partDetailsService, PartDetailsMapper partDetailsMapper,
			InstructionMapper instructionMapper, DeliveryDetailsRepository deliveryDetailsRepository) {
        this.instructionRepository = instructionRepository;
        this.inwardEntryRepository = inwardEntryRepository;
        this.deliveryDetailsRepository = deliveryDetailsRepository;
        this.inwardService = inwardService;
        this.processService = processService;
        this.statusService = statusService;
        this.packetClassificationService = packetClassificationService;
        this.endUserTagsService = endUserTagsService;
        this.partDetailsService = partDetailsService;
        this.partDetailsMapper = partDetailsMapper;
        this.instructionMapper = instructionMapper;
    }

    @Override
    public List<Instruction> getAll() {
        return instructionRepository.getAll();
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

//    @Override
//    @Transactional
//    public ResponseEntity<Object> addCutInstruction(List<InstructionSaveRequestDto> cutInstructionSaveRequestDtos) {
//        LOGGER.info("inside save cut instruction method");
//        LOGGER.info("no of requests " + cutInstructionSaveRequestDtos.size());
//        Map<PartDetails, List<InstructionRequestDto>> instructionPlanAndListMap = new HashMap<>();
//        partDetailsRequest partDetailsRequest;
//        InstructionRequestDto instructionRequestDto = cutInstructionSaveRequestDtos.get(0).getInstructionRequestDTOs().get(0);
//        Integer inwardId = instructionRequestDto.getInwardId();
//        Integer parentInstructionId = instructionRequestDto.getParentInstructionId();
//        Integer groupId = instructionRequestDto.getGroupId();
//        boolean fromInward = false, fromParentInstruction = false, fromGroup = false;
//
//        InwardEntry inwardEntry;
//        Instruction parentInstruction = null;
//        PartDetails partDetails = null;
//        String partDetailsId;
//        Process process;
//        double incomingWeight = 0f, availableWeight = 0f, existingWeight = 0f, remainingWeight = 0f;
//        double incomingLength = 0f, availableLength = 0f, existingLength = 0f, remainingLength = 0f;
//
//        if (inwardId != null && groupId != null) {
//            LOGGER.info("adding instructions from inward "+ inwardId +", group id " + groupId);
//            inwardEntry = inwardService.getByInwardEntryId(inwardId);
//            TotalLengthAndWeight totalLengthAndWeight = this.sumOfPlannedLengthAndWeightOfInstructionsHavingGroupId(groupId);
//            availableWeight = totalLengthAndWeight.getTotalWeight();
//            availableLength = totalLengthAndWeight.getTotalLength();
//            LOGGER.info("available length,weight for instructions with group id "+groupId+ " is "+availableLength+", "+availableWeight);
//            process = processService.getById(this.slitAndCutProcessId);
//            partDetailsId = null;
//            fromGroup = true;
//        } else if (inwardId != null) {
//            LOGGER.info("adding instructions from inward id " + inwardId);
//            inwardEntry = inwardService.getByInwardEntryId(inwardId);
//            availableWeight = inwardEntry.getFpresent();
//            availableLength = inwardEntry.getAvailableLength();
//            LOGGER.info("available length,weight of inward "+inwardEntry.getInwardEntryId()+" is "+availableLength+", "+availableWeight);
//            process = processService.getById(cutProcessId);
//            partDetailsId = "DOC_" + System.nanoTime();
//            fromInward = true;
//        } else if (parentInstructionId != null) {
//                LOGGER.info("adding instructions from parent instruction id " + parentInstructionId);
//                TotalLengthAndWeight totalLengthAndWeight = this.sumOfPlannedLengthAndWeightOfInstructionsHavingParentInstructionId(parentInstructionId);
//                existingLength = totalLengthAndWeight.getTotalLength();
//                existingWeight = totalLengthAndWeight.getTotalWeight();
//                LOGGER.info("existing length,weight is " +existingLength+", " +existingWeight);
//                parentInstruction = this.findInstructionById(parentInstructionId);
//                inwardEntry = parentInstruction.getInwardId();
//                availableWeight = parentInstruction.getPlannedWeight();
//                LOGGER.info("available length,weight is" + availableLength+", "+availableWeight);
//                process = processService.getById(cutProcessId);
//                partDetailsId = "DOC_" + System.nanoTime();
//                fromParentInstruction = true;
//            }
//        else {
//                return new ResponseEntity<Object>("Invalid request.", HttpStatus.BAD_REQUEST);
//            }
//        if (inwardEntry.getFpresent() < 0) {
//            LOGGER.error("inward has negative fPresent value "+inwardEntry.getFpresent());
//            return new ResponseEntity<Object>("Inward with id " + inwardId + " has invalid fpresent value " + inwardEntry.getFpresent(), HttpStatus.BAD_REQUEST);
//        }
//
//        for (InstructionSaveRequestDto cutInstructionDto : cutInstructionSaveRequestDtos) {
//            incomingWeight += cutInstructionDto.getInstructionRequestDTOs().stream().filter(dto -> dto.getPlannedWeight() != null).reduce(0f, (sum, dto) -> sum + dto.getPlannedWeight(), Float::sum);
//            incomingLength += cutInstructionDto.getInstructionRequestDTOs().stream().filter(dto -> dto.getPlannedWeight() != null).reduce(0f, (sum, dto) -> sum + dto.getPlannedLength(), Float::sum);
//            partDetailsRequest = cutInstructionDto.getPartDetailsRequest();
//            if(!fromGroup) {
//                PartDetails partDetails1 = partDetailsMapper.toEntityForCut(partDetailsRequest);
//                partDetails1.setPartDetailsId(partDetailsId);
//                instructionPlanAndListMap.put(partDetails1, cutInstructionDto.getInstructionRequestDTOs());
//            }else{
//                instructionPlanAndListMap.put(null, cutInstructionDto.getInstructionRequestDTOs());
//            }
//
//        }
//
//        remainingWeight = availableWeight - existingWeight - incomingWeight;
//        remainingLength = availableLength - existingLength - incomingLength;
//        LOGGER.info("remaining length,weight is "+remainingLength+", "+remainingWeight);
//        if (fromGroup && Math.abs(remainingWeight) > 1f) {
//            return new ResponseEntity<Object>("Input instructions total weight must be equal to instructions with group id " + groupId, HttpStatus.BAD_REQUEST);
//        } else if (!fromGroup && remainingWeight < 0f) {
//            LOGGER.error("remaining weight is invalid " + remainingWeight);
//            return new ResponseEntity<Object>("inward " + inwardId + " has no available weight for processing.", HttpStatus.BAD_REQUEST);
//        }
//        if(remainingLength < 0f){
//            LOGGER.error("remaining length is invalid "+ remainingLength);
//            return new ResponseEntity<Object>("inward " + inwardId + " has no available length for processing.", HttpStatus.BAD_REQUEST);
//        }
//        if (fromInward) {
//            LOGGER.info("setting fPresent for inward " + inwardEntry.getInwardEntryId() + " to " + remainingWeight);
//            inwardEntry.setFpresent((float) remainingWeight);
//            inwardEntry.setAvailableLength((float)remainingLength);
//        }
//        Status inProgressStatus = statusService.getStatusById(inProgressStatusId);
//        inwardEntry.setStatus(inProgressStatus);
//        List<Instruction> savedInstructionList = new ArrayList<Instruction>();
//        for (PartDetails pd : instructionPlanAndListMap.keySet()) {
//            for (InstructionRequestDto requestDto : instructionPlanAndListMap.get(pd)) {
//                Instruction instruction = instructionMapper.toEntity(requestDto);
//                instruction.setProcess(process);
//                instruction.setStatus(inProgressStatus);
//                if (fromParentInstruction) {
//                    parentInstruction.addChildInstruction(instruction);
//                } else if (fromInward) {
//                    inwardEntry.addInstruction(instruction);
//                } else {
//                    inwardEntry.addInstruction(instruction);
//                    instruction.setParentGroupId(groupId);
//                }
//                if(pd != null) {
//                    pd.addInstruction(instruction);
//                }
//                savedInstructionList.add(instruction);
//            }
//        }
//        LOGGER.info("saving " + instructionPlanAndListMap.keySet().size() + " part details objects");
//        if(fromGroup) {
//            savedInstructionList = saveAll(savedInstructionList);
//            return new ResponseEntity<>(savedInstructionList.stream().map(i -> Instruction.valueOf(i)), HttpStatus.OK);
//        }
//        List<PartDetails> partDetailsList = partDetailsService.saveAll(instructionPlanAndListMap.keySet());
//        List<PartDetailsResponse> partDetailsResponseList = partDetailsMapper.toResponseDto(partDetailsList);
//
//        if (fromParentInstruction) {
//            instructionRepository.save(parentInstruction);
//        } else if (fromInward) {
//            inwardService.saveEntry(inwardEntry);
//        }
//        return new ResponseEntity<Object>(partDetailsResponseList, HttpStatus.CREATED);
//    }

    @Override
    @Transactional
    public void deleteById(Integer instructionId) {
        LOGGER.info("inside delete instruction method");
        Instruction deleteInstruction = instructionRepository.getOne(instructionId);
        Integer inProgressStatusId = 2, readyToDeliverStatusId = 3, receivedStatusId = 1, despatchedStatusId = 4, statusId = 0;

//        if(deleteInstruction.getInwardId() != null && deleteInstruction.getParentGroupId() != null)
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
            
            
            InwardEntry inwardEntity  = inwardService.getByInwardEntryId(deleteInstruction.getInwardId().getInwardEntryId());
            boolean checkWIPStatus = inwardEntity.getInstructions().stream().anyMatch(cin -> cin.getStatus().getStatusId()== inProgressStatusId );

    		if (checkWIPStatus) {
    			inwardEntryRepository.updateInwardStatus(deleteInstruction.getInwardId().getInwardEntryId(), inProgressStatusId);
    		} else {
    			boolean checkReadyToDeliverStatus = inwardEntity.getInstructions().stream().anyMatch(cin -> cin.getStatus().getStatusId()==readyToDeliverStatusId);
    			if (checkReadyToDeliverStatus) {
    				inwardEntryRepository.updateInwardStatus(deleteInstruction.getInwardId().getInwardEntryId(), readyToDeliverStatusId);
    			}else {
    				if(inwardEntity.getFpresent()>0) {
    					inwardEntryRepository.updateInwardStatus(deleteInstruction.getInwardId().getInwardEntryId(), receivedStatusId);
    				} else {
    					boolean despatchedStatus = inwardEntity.getInstructions().stream().anyMatch(cin -> cin.getStatus().getStatusId()==despatchedStatusId);
    					if (despatchedStatus) {
    						inwardEntryRepository.updateInwardStatus(deleteInstruction.getInwardId().getInwardEntryId(), despatchedStatusId);
    					}
    				}
    			}
    		}
    		
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
    public ResponseEntity<Object> updateInstruction(InstructionFinishDto instructionFinishDto, int userId) {
        LOGGER.info("in finish instruction method");
        List<InstructionRequestDto> InstructionRequestDtos = instructionFinishDto.getInstructionDtos();
        List<Instruction> updatedInstructionList = new ArrayList<Instruction>();
        Instruction instruction;
        Integer inProgressStatusId = 2, readyToDeliverStatusId = 3, receivedStatusId = 1, despatchedStatusId = 4, statusId = 0;
        Status inProgressStatus = statusService.getStatusById(inProgressStatusId);
        Status readyToDeliverStatus = statusService.getStatusById(readyToDeliverStatusId);
        Status currentStatus;
        Float scrapWeight=0f;
    	
        if("WIPtoFG".equalsIgnoreCase(instructionFinishDto.getTaskType())) {    // WIPtoFG
        	statusId = inProgressStatusId;
        	currentStatus= readyToDeliverStatus;
        } else if("FGtoWIP".equalsIgnoreCase(instructionFinishDto.getTaskType())) {    // FGtoWIP   .... cancel finish
        	statusId = readyToDeliverStatusId;
        	currentStatus = inProgressStatus;
        } else {             // FGtoFG   .... edit finish
        	statusId = readyToDeliverStatusId;
        	currentStatus = readyToDeliverStatus;
        }
        List<Instruction> instructions = this.findAllByInstructionIdInAndStatus(InstructionRequestDtos.stream()
                .map(ins -> ins.getInstructionId()).collect(Collectors.toList()), statusId);
        
        Map<Integer, Instruction> instructionsMap = instructions.stream().collect(Collectors.toMap(ins -> ins.getInstructionId(), ins -> ins));
        if(instructionsMap.isEmpty()){
        	LOGGER.error("no instructions found in progress status");
			return new ResponseEntity<Object>("{\"status\": \"failure\", \"message\":\"All Instructions were already finished\"}", new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
        Map<Integer, PacketClassification> packetClassificationMap = packetClassificationService.findAllByPacketClassificationIdIn(InstructionRequestDtos.stream()
                .map(ins -> ins.getPacketClassificationId()).collect(Collectors.toList())).stream().collect(Collectors.toMap(p -> p.getClassificationId(), p -> p));

        Map<Integer, EndUserTagsEntity> endUserTagsEntityMap = endUserTagsService.findAllByTagIdIn(InstructionRequestDtos.stream()
                .map(ins -> ins.getEndUserTagId()).collect(Collectors.toList())).stream().collect(Collectors.toMap(p -> p.getTagId(), p -> p));

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
          	scrapWeight = scrapWeight + (ins.getPlannedWeight() - ins.getActualWeight());
            instruction.setActualNoOfPieces(ins.getActualNoOfPieces());
            if(ins.getPacketClassificationId()!=null && ins.getPacketClassificationId() >0 ) {
                PacketClassification packetClassificationEntity=packetClassificationMap.get(ins.getPacketClassificationId());
                if("WIP".equals(packetClassificationEntity.getClassificationName())) {
                	currentStatus = inProgressStatus;
                }
                instruction.setPacketClassification(packetClassificationMap.get(ins.getPacketClassificationId()));
            } else {
                instruction.setPacketClassification(null);
            }
            instruction.setEndUserTagsEntity(endUserTagsEntityMap.get(ins.getEndUserTagId()));
            instruction.setStatus(currentStatus);
            instruction.setUpdatedBy(userId);
            updatedInstructionList.add(instruction);
        }
        instructionRepository.saveAll(updatedInstructionList);
        LOGGER.info("saved all instructions");
        boolean isAnyInstructionInProgress = false;
        Instruction savedInstruction = updatedInstructionList.get(0);
        InwardEntry inwardEntry = savedInstruction.getInwardId();
        Instruction parentInstruction = savedInstruction.getParentInstruction();
		if (scrapWeight != 0) {
			if (inwardEntry.getScrapWeight() != null && inwardEntry.getScrapWeight() != 0) {
				Float totalScrapWeight = scrapWeight + inwardEntry.getScrapWeight();
				if (totalScrapWeight > 0) {
					inwardEntry.setScrapWeight(totalScrapWeight);
				} else {
					inwardEntry.setScrapWeight(0.0f);
				}
			} else {
				if (scrapWeight > 0) {
					inwardEntry.setScrapWeight(scrapWeight);
				} else {
					inwardEntry.setScrapWeight(0.0f);
				}
			}
		}
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

        InwardEntry inwardEntity  = inwardService.getByInwardEntryId(savedInstruction.getInwardId().getInwardEntryId());
        boolean checkWIPStatus = inwardEntity.getInstructions().stream().anyMatch(cin -> cin.getStatus().getStatusId()== inProgressStatusId );

		if (checkWIPStatus) {
			inwardEntryRepository.updateInwardStatus(savedInstruction.getInwardId().getInwardEntryId(), inProgressStatusId);
		} else {
			boolean checkReadyToDeliverStatus = inwardEntity.getInstructions().stream().anyMatch(cin -> cin.getStatus().getStatusId()==readyToDeliverStatusId);
			if (checkReadyToDeliverStatus) {
				inwardEntryRepository.updateInwardStatus(savedInstruction.getInwardId().getInwardEntryId(), readyToDeliverStatusId);
			}else {
				if(inwardEntity.getFpresent()>0) {
					inwardEntryRepository.updateInwardStatus(savedInstruction.getInwardId().getInwardEntryId(), receivedStatusId);
				} else {
					boolean despatchedStatus = inwardEntity.getInstructions().stream().anyMatch(cin -> cin.getStatus().getStatusId()==despatchedStatusId);
					if (despatchedStatus) {
						inwardEntryRepository.updateInwardStatus(savedInstruction.getInwardId().getInwardEntryId(), despatchedStatusId);
					}
				}
			}
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
    public TotalLengthAndWeight sumOfPlannedLengthAndWeightOfInstructionsHavingGroupId(List<Integer> groupIds) {
        return instructionRepository.sumOfPlannedLengthAndWeightOfInstructionsHavingGroupId(groupIds);
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
	public List<Instruction> findSlitAndCutInstructionByInwardId1(Integer inwardId) {

		List<Instruction> instructions = instructionRepository.findSlitAndCutInstructionByInwardId1(inwardId);

		for (Instruction ins : instructions) {
			List<Instruction> childInstructions = instructionRepository.findSlitAndCutInstructionByGroupId(ins.getInstructionId());

			if (childInstructions == null || childInstructions.size() == 0) {
				deleteById(ins.getInstructionId());
			}

		}

		return instructionRepository.findSlitAndCutInstructionByInwardId1(inwardId);
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
    public List<InstructionResponseDto>  saveFullHandlingDispatch(List<Integer> inwardList, int userId) throws MockException {
        LOGGER.info("inside saveUnprocessedForDelivery method");

        List<InstructionResponseDto> totalList=new ArrayList<>(); 
        		
        for (Integer inwardId : inwardList) {
        
        Date date = new Date();
        Instruction unprocessedInstruction = new Instruction();
        InwardEntry inward = inwardService.getByInwardEntryId(inwardId);
        LOGGER.info("inward with id " + inwardId + " has fPresent " + inward.getFpresent());
        Integer handlingProcessId = 8, readyToDeliverStatusId = 4;
        Process handlingProcess = processService.getById(handlingProcessId);
        Status readyToDeliverStatus = statusService.getStatusById(readyToDeliverStatusId);
        
        List<Instruction> instructions = instructionRepository.findByStatus(inwardId, inProgressStatusId);
        
        if(instructions!=null && instructions.size()>0){
			List<String> errors = new ArrayList<>();
			errors.add("we can't do FULL HANDLING since some of the children instructions were in WIP status");
			throw new MockException("MSG-0007", errors);
        }

        float denominator = inward.getfThickness() * (inward.getfWidth() / 1000f) * 7.85f;
        float lengthUnprocessed = inward.getFpresent() / denominator;
        LOGGER.info("calculated length of instruction " + lengthUnprocessed);

        unprocessedInstruction.setPlannedLength(lengthUnprocessed);
        unprocessedInstruction.setPlannedWidth(inward.getfWidth());
        unprocessedInstruction.setPlannedWeight(inward.getFpresent());
        unprocessedInstruction.setActualLength(lengthUnprocessed);
        unprocessedInstruction.setActualWidth(inward.getfWidth());
        unprocessedInstruction.setActualWeight(inward.getFpresent());
        unprocessedInstruction.setProcess(handlingProcess);
        unprocessedInstruction.setStatus(readyToDeliverStatus);
        unprocessedInstruction.setInstructionDate(date);
        unprocessedInstruction.setCreatedBy(userId);
        unprocessedInstruction.setUpdatedBy(userId);
        unprocessedInstruction.setCreatedOn(date);
        unprocessedInstruction.setUpdatedOn(date);
        unprocessedInstruction.setIsDeleted(false);
        unprocessedInstruction.setIsSlitAndCut(false);

        inward.setFpresent(0f);
        inward.setStatus(readyToDeliverStatus);
        inward.addInstruction(unprocessedInstruction);
        inward = inwardService.saveEntry(inward);
        Optional<InstructionResponseDto> savedInstruction = inward.getInstructions().stream().filter(ins -> ins.getProcess().getProcessId() == 8)
                .map(ins -> Instruction.valueOf(ins)).findFirst();
        totalList.add(savedInstruction.get());
        }
        return totalList;
    }

    @Override
    public InstructionResponseDto saveUnprocessedForDelivery(Integer inwardId, int userId) {
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
        unprocessedInstruction.setActualLength(lengthUnprocessed);
        unprocessedInstruction.setActualWidth(inward.getfWidth());
        unprocessedInstruction.setActualWeight(inward.getFpresent());
        unprocessedInstruction.setProcess(handlingProcess);
        unprocessedInstruction.setStatus(readyToDeliverStatus);
        unprocessedInstruction.setInstructionDate(date);
        unprocessedInstruction.setCreatedBy(userId);
        unprocessedInstruction.setUpdatedBy(userId);
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
    public InwardEntryPdfDto findInwardJoinFetchInstructionsAndPartDetails(String partDetailsId,List<Integer> groupIds) {
        List<Object[]> objects = null;
        Integer processId = null;
        Integer cutProcessId = 1;
        if(partDetailsId !=null && groupIds != null) {
            LOGGER.info("partDetailsid: "+partDetailsId+", groupIds not null");
            objects = instructionRepository.findPartDetailsJoinFetchInstructionsByPartDetailsIdOrGroupIds(partDetailsId,groupIds);
        }else if(partDetailsId != null) {
            LOGGER.info("partDetailsId: "+partDetailsId);
            objects = instructionRepository.findPartDetailsJoinFetchInstructions(partDetailsId);
        }else {
            LOGGER.info("total groupIds "+groupIds.size());
            objects = instructionRepository.findPartDetailsJoinFetchInstructionsAndGroupIds(groupIds);
        }
        Integer inwardId = null;
        float totalWeightSlit = 0f;
        float totalWeightCut = 0f;
        String cutPartDetailsId = null;
        Map<PartDetailsPdfResponse, List<InstructionResponsePdfDto>> partDetailsSlitMap = null, partDetailsCutMap = null;
        for (Object[] obj : objects) {
            PartDetails partDetails = (PartDetails) obj[0];
            Instruction instruction = (Instruction) obj[1];
            if (inwardId == null) {
                try {
					inwardId = instruction.getInwardId().getInwardEntryId();
				} catch (Exception e) {
					inwardId = instruction.getParentInstruction().getInwardId().getInwardEntryId();
				}
            }
            Long count = (Long) obj[2];
            PartDetailsPdfResponse partDetailsPdfResponse = partDetailsMapper.toPartDetailsPdfResponse(partDetails);
            InstructionResponsePdfDto instructionResponsePdfDto = instructionMapper.toResponsePdfDto(instruction);
            instructionResponsePdfDto.setCountOfWeight(count);
            cutPartDetailsId = partDetailsPdfResponse.getPartDetailsId();//added for new cuts created from existing slits
            processId = partDetailsId == null ? cutProcessId : instruction.getProcess().getProcessId();
            if(processId == 1 || processId == 3){
                totalWeightCut += instruction.getPlannedWeight()*count;
                if(partDetailsCutMap == null) {
                    partDetailsCutMap = new HashMap<>();
                }
                partDetailsCutMap = addInstructionToPartDetailsMap(partDetailsCutMap,partDetailsPdfResponse,instructionResponsePdfDto);

            }else{//slit process
                totalWeightSlit += instruction.getPlannedWeight()*count;
                if(partDetailsSlitMap == null) {
                    partDetailsSlitMap = new HashMap<>();
                }
                partDetailsSlitMap = addInstructionToPartDetailsMap(partDetailsSlitMap,partDetailsPdfResponse,instructionResponsePdfDto);
            }
        }
        InwardEntry inwardEntry;
        InwardEntryPdfDto inwardEntryPdfDto;

        inwardEntry = inwardService.getByEntryId(inwardId);
        inwardEntryPdfDto = InwardEntry.valueOf(inwardEntry, null);
        inwardEntryPdfDto.setPartDetailsCutMap(partDetailsCutMap);
        inwardEntryPdfDto.setPartDetailsSlitMap(partDetailsSlitMap);
        inwardEntryPdfDto.setTotalWeightCut(partDetailsSlitMap == null ? totalWeightCut : 0f);
        inwardEntryPdfDto.setTotalWeightSlit(totalWeightSlit);
        inwardEntryPdfDto.setPartDetailsId(partDetailsId != null ? partDetailsId : cutPartDetailsId);
        inwardEntryPdfDto.setVProcess(String.valueOf(processId));
        return inwardEntryPdfDto;
    }

    @Override
    public ResponseEntity<Object> deleteCut(CutInstructionDeleteRequest cutInstructionDeleteRequest) {
        LOGGER.info("inside delete cut method for instruction id "+ cutInstructionDeleteRequest.getInstructionId());
        Instruction instruction = this.findInstructionById(cutInstructionDeleteRequest.getInstructionId());
        if(instruction.getIsDeleted()){
            LOGGER.error("instruction with id "+instruction.getInstructionId()+" already deleted");
            return new ResponseEntity<>("instruction with id "+instruction.getInstructionId()+" is already deleted",HttpStatus.BAD_REQUEST);
        }
        if(!instruction.getStatus().getStatusName().equals("IN PROGRESS")){
            LOGGER.error("instruction is not in in progress status");
            throw new RuntimeException("Instruction cannot be deleted as it is not in progress status");
        }
        if(instruction.getParentGroupId() != null){
            LOGGER.info("instruction has parent group id "+ instruction.getParentGroupId());
            List<Instruction> slitAndCutInstructions = this.findAllByGroupIdOrParentGroupId(instruction.getParentGroupId(),instruction.getParentGroupId());
            LOGGER.info("no of slit-and-cut instructions "+slitAndCutInstructions.size());
            for(Instruction ins:slitAndCutInstructions){
                if(ins.getGroupId() != null){
                    ins.setGroupId(null);
                }else{
                    ins.setIsDeleted(true);
                }
            }
            LOGGER.info("updating slit-and-cut instructions after removing group id");
            instructionRepository.saveAll(slitAndCutInstructions);
        }else if(instruction.getParentInstruction() != null){
            Instruction parentInstruction = instruction.getParentInstruction();
            LOGGER.info("instruction has parent instruction id "+parentInstruction.getInstructionId());
            Set<Instruction> childrenInstructions = parentInstruction.getChildInstructions();
            LOGGER.info("total children instructions are "+childrenInstructions.size());
            for(Instruction ins:childrenInstructions){
                parentInstruction.removeChildInstruction(ins);
                ins.setIsDeleted(true);
            }
            instructionRepository.saveAll(childrenInstructions);
        }else {
            InwardEntry inwardEntry = instruction.getInwardId();
            LOGGER.info("instruction created from inward "+inwardEntry.getInwardEntryId());
            Float availableLength = inwardEntry.getAvailableLength();
            Float fPresent = inwardEntry.getFpresent();
            LOGGER.info("inward available length,fPresent " + availableLength + ", " + fPresent);
            LOGGER.info("instruction length,noOfCuts,weight: "+instruction.getPlannedLength()+", "+instruction.getPlannedNoOfPieces()+", "+instruction.getPlannedWeight());
            availableLength += (instruction.getPlannedLength()*instruction.getPlannedNoOfPieces());
            fPresent += instruction.getPlannedWeight();
            LOGGER.info("setting inward available length,fPresent after delete to " + availableLength + ", " + fPresent);
            inwardEntry.setAvailableLength(availableLength);
            inwardEntry.setFpresent(fPresent);
            instruction.setIsDeleted(true);
            LOGGER.info("updating inward "+inwardEntry.getInwardEntryId());
            inwardService.saveEntry(inwardEntry);
        }
        
        InwardEntry inwardEntity1  = inwardService.getByInwardEntryId(instruction.getInwardId().getInwardEntryId());
        boolean deletedStatus = inwardEntity1.getInstructions().stream().anyMatch(cin -> cin.getIsDeleted()== false);

		if (!deletedStatus) {
			inwardEntryRepository.updateInwardStatus(instruction.getInwardId().getInwardEntryId(), 1);
		}
        
        return new ResponseEntity<>("Delete Success ..!",HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> deleteSlit(SlitInstructionDeleteRequest slitInstructionDeleteRequest) {
        LOGGER.info("inside delete slit method for part id "+ slitInstructionDeleteRequest.getPartId());
        Integer slitProcessId = 2;
        List<Instruction> instructions = instructionRepository.findInstructionsByPartIdAndProcessId(slitInstructionDeleteRequest.getPartId(),slitProcessId);
        LOGGER.info("no of slit instructions to be deleted "+instructions.size());
        PartDetails partDetails = instructions.get(0).getPartDetails();
        LOGGER.info("part id "+partDetails.getId()+" length,weight "+partDetails.getLength()+", "+partDetails.getTargetWeight());
        InwardEntry inwardEntry = null;
        Integer groupId = null;
        for(Instruction instruction: instructions){
            if(!instruction.getStatus().getStatusName().equals("IN PROGRESS")){
                LOGGER.error("instruction is not in in progress status");
                throw new RuntimeException("Instruction cannot be deleted as it is not in progress status");
            }
            if(groupId == null) {
                groupId = instruction.getGroupId();
            }
            if(instruction.getIsDeleted()){
                LOGGER.error("instruction with id "+instruction.getInstructionId()+" already deleted");
                return new ResponseEntity<>("instruction with id "+instruction.getInstructionId()+" is already deleted",HttpStatus.BAD_REQUEST);
            }
            instruction.setIsDeleted(true);
            if(inwardEntry == null) {
                inwardEntry = instruction.getInwardId();
            }
        }
        if(groupId != null){
            LOGGER.info("slit Instructions have group id "+groupId);
            List<Instruction> cutInstructions = this.findAllByParentGroupId(groupId);
            LOGGER.info("no of cut cutInstructions "+cutInstructions.size());
            for(Instruction ins:cutInstructions){
                if(!ins.getIsDeleted()){
                    LOGGER.error("cut instruction with id "+ins.getInstructionId()+" is not deleted");
                    return new ResponseEntity<>("part cannot be deleted as it has "+cutInstructions.size()+" parent group instructions(cut)",HttpStatus.BAD_REQUEST);
                }
            }
        }
        Float availableLength = inwardEntry.getAvailableLength();
        Float fPresent = inwardEntry.getFpresent();
        LOGGER.info("inward available length,fPresent "+availableLength+", "+fPresent);
        availableLength += partDetails.getLength();
        fPresent += partDetails.getTargetWeight();
        LOGGER.info("setting inward available length,fPresent after delete to "+availableLength+", "+fPresent);
        inwardEntry.setAvailableLength(availableLength);
        inwardEntry.setFpresent(fPresent);
        partDetails.setIsDeleted(true);
        LOGGER.info("updating part "+partDetails.getId());
        partDetailsService.save(partDetails);
        LOGGER.info("updating inward "+inwardEntry.getInwardEntryId());
        inwardService.saveEntry(inwardEntry);

        InwardEntry inwardEntity1  = inwardService.getByInwardEntryId(inwardEntry.getInwardEntryId());
        boolean deletedStatus = inwardEntity1.getInstructions().stream().anyMatch(cin -> cin.getIsDeleted()== false);
		if (!deletedStatus) {
			inwardEntry.setStatus(this.statusService.getStatusById(1));
		}
        return new ResponseEntity<>("Delete Success ..!",HttpStatus.OK);
    }

    @Override
    public List<Instruction> findAllByGroupIdOrParentGroupId(Integer groupId,Integer parentGroupId) {
        return instructionRepository.findAllByGroupIdOrParentGroupId(groupId,parentGroupId);
    }

    @Override
    public Instruction findFirstByGroupIdAndIsDeletedFalse(Integer groupId) {
        Optional<Instruction> instructionOptional = instructionRepository.findFirstByGroupIdAndIsDeletedFalse(groupId);
        if(!instructionOptional.isPresent()){
            throw new RuntimeException("instruction with group id "+groupId+" is either deleted or not found");
        }
        return instructionOptional.get();
    }

    @Override
    public HashMap<Integer,Double> findSumOfPlannedWeightAndActualWeightForUnprocessed() {
        List<Object[]> unprocessedWeights = instructionRepository.findSumOfPlannedWeightAndActualWeightForUnprocessed();
        HashMap<Integer,Double> inwardUnprocessedWeights = new HashMap<>();
        for(Object[] obj:unprocessedWeights){
            inwardUnprocessedWeights.put((Integer)obj[0],(double)obj[1]);
        }
        return inwardUnprocessedWeights;
    }

    private Map<PartDetailsPdfResponse, List<InstructionResponsePdfDto>> addInstructionToPartDetailsMap(Map<PartDetailsPdfResponse, List<InstructionResponsePdfDto>> partDetailsMap, PartDetailsPdfResponse partDetailsPdfResponse, InstructionResponsePdfDto instructionResponsePdfDto) {
        if (partDetailsMap.isEmpty() || !partDetailsMap.containsKey(partDetailsPdfResponse)) {
            List<InstructionResponsePdfDto> list = new ArrayList<>();
            list.add(instructionResponsePdfDto);
            partDetailsMap.put(partDetailsPdfResponse, list);
        } else {
            List<InstructionResponsePdfDto> list = partDetailsMap.get(partDetailsPdfResponse);
            list.add(instructionResponsePdfDto);
            partDetailsMap.put(partDetailsPdfResponse, list);
        }
        return partDetailsMap;
    }

    @Override
    @Transactional
    public ResponseEntity<Object> addInstruction(List<InstructionSaveRequestDto> instructionSaveRequestDtos, int userId) {
            LOGGER.info("no of requests " + instructionSaveRequestDtos.size());
            Map<PartDetails, List<InstructionRequestDto>> instructionPlanAndListMap = new HashMap<>();
            LOGGER.info("inside save instruction method");
            partDetailsRequest partDetailsRequest;
            PartDetails slitPartDetails = null;
            InstructionRequestDto instructionRequestDto = instructionSaveRequestDtos.get(0).getInstructionRequestDTOs().get(0);
            Integer inwardId = instructionRequestDto.getInwardId();
            Integer processId = instructionRequestDto.getProcessId();
            LOGGER.info("saving instructions for process id "+processId);
            Integer parentInstructionId = instructionRequestDto.getParentInstructionId();
            Integer groupId = instructionRequestDto.getGroupId();
            List<Integer> groupIds = null;
            boolean fromInward = false, fromParentInstruction = false, fromGroup = false;

            InwardEntry inwardEntry;
            Instruction parentInstruction = null;
            String partDetailsId;
            double incomingWeight = 0f, availableWeight = 0f, existingWeight = 0f, remainingWeight = 0f;
            double incomingLength = 0f, availableLength = 0f, existingLength = 0f, remainingLength = 0f;

            if (inwardId != null && groupId != null) {
                LOGGER.info("adding instructions from inward "+ inwardId +", group id " + groupId);
                inwardEntry = inwardService.getByInwardEntryId(inwardId);
                groupIds = instructionSaveRequestDtos.stream().flatMap(dto -> dto.getInstructionRequestDTOs().stream())
                        .map(ins -> ins.getGroupId()).distinct().collect(Collectors.toList());
                LOGGER.info("no of groups are "+groupIds.size());
                TotalLengthAndWeight totalLengthAndWeight = sumOfPlannedLengthAndWeightOfInstructionsHavingGroupId(groupIds);
                availableWeight = totalLengthAndWeight.getTotalWeight();
                availableLength = inwardEntry.getfLength();
                Instruction groupInstruction = this.findFirstByGroupIdAndIsDeletedFalse(groupIds.get(0));
                slitPartDetails = groupInstruction.getPartDetails();
                partDetailsId = slitPartDetails.getPartDetailsId();
                LOGGER.info("available length,weight for instructions with group id "+groupIds.stream().map(g -> g+", ").collect(Collectors.joining())+" is "+availableLength+", "+availableWeight);
                fromGroup = true;
            } else if (inwardId != null && parentInstructionId != null) {
                LOGGER.info("adding instructions from parent instruction id " + parentInstructionId);
                parentInstruction = this.findInstructionById(parentInstructionId);
                inwardEntry = parentInstruction.getInwardId();
                existingLength = inwardEntry.getAvailableLength();
                existingWeight = this.sumOfPlannedWeightOfInstructionHavingParentInstructionId(parentInstructionId);
                LOGGER.info("existing length,weight is " +existingLength+", " +existingWeight);
                availableWeight = parentInstruction.getPlannedWeight();
                availableLength = parentInstruction.getPlannedLength();
                LOGGER.info("available length,weight is" + availableLength+", "+availableWeight);
                //partDetailsId = null;
                partDetailsId = "DOC_" + System.nanoTime(); // added by Kanakadri for parent child instruction
                fromParentInstruction = true;
            } else if (inwardId != null) {
                LOGGER.info("adding instructions from inward id " + inwardId);
                inwardEntry = inwardService.getByInwardEntryId(inwardId);
                availableWeight = inwardEntry.getFpresent();
                availableLength = inwardEntry.getAvailableLength();
                LOGGER.info("available length,weight of inward "+inwardEntry.getInwardEntryId()+" is "+availableLength+", "+availableWeight);
                partDetailsId = "DOC_" + System.nanoTime();
                fromInward = true;
            } 
            else {
                return new ResponseEntity<Object>("Invalid request", HttpStatus.BAD_REQUEST);
            }
            List<Integer> packetClassificationIds = new ArrayList<>();
            List<Integer> endUserTagIds = new ArrayList<>();
            for (InstructionSaveRequestDto instructionSaveRequestDto : instructionSaveRequestDtos) {
                if(processId == 1 || processId == 3) {//for cut
                    incomingWeight += instructionSaveRequestDto.getInstructionRequestDTOs().stream().reduce(0f, (sum, dto) -> sum + dto.getPlannedWeight(), Float::sum);
                    incomingLength += instructionSaveRequestDto.getInstructionRequestDTOs().stream().reduce(0f, (sum, dto) -> sum + dto.getPlannedLength()*dto.getPlannedNoOfPieces(), Float::sum);
                    partDetailsRequest = instructionSaveRequestDto.getPartDetailsRequest();
                    PartDetails partDetails = partDetailsMapper.toEntityForCut(partDetailsRequest);
                    partDetails.setPartDetailsId(partDetailsId);
                    instructionPlanAndListMap.put(partDetails, instructionSaveRequestDto.getInstructionRequestDTOs());

                }else {
                    incomingWeight += instructionSaveRequestDto.getInstructionRequestDTOs().stream().reduce(0f, (sum, dto) -> sum + dto.getPlannedWeight(), Float::sum);
                    incomingLength += instructionSaveRequestDto.getPartDetailsRequest().getLength();
                    partDetailsRequest = instructionSaveRequestDto.getPartDetailsRequest();
                    PartDetails partDetails = partDetailsMapper.toEntityForSlit(partDetailsRequest);
                    partDetails.setPartDetailsId(partDetailsId);
                    instructionPlanAndListMap.put(partDetails, instructionSaveRequestDto.getInstructionRequestDTOs());
                }
                instructionSaveRequestDto.getInstructionRequestDTOs().forEach(in -> packetClassificationIds.add(in.getPacketClassificationId()));
                instructionSaveRequestDto.getInstructionRequestDTOs().forEach(in -> endUserTagIds.add(in.getEndUserTagId()));
            }

            Float scrapWeight = 0.0f;
            if(inwardEntry.getScrapWeight()!=null ) {
            	scrapWeight = inwardEntry.getScrapWeight();
            }
            String isScrapWeightUsed ="N";
			for (InstructionSaveRequestDto instructionSaveRequestDto : instructionSaveRequestDtos) {
				for (InstructionRequestDto instructionRequestChildDto : instructionSaveRequestDto.getInstructionRequestDTOs()) {
					if (instructionRequestChildDto.getIsScrapWeightUsed()!=null && instructionRequestChildDto.getIsScrapWeightUsed()) {
			            Float plannedWeight = 0.0f;
						if(instructionRequestChildDto.getPlannedWeight() != null && instructionRequestChildDto.getPlannedWeight() >0  ) {
							plannedWeight = instructionRequestChildDto.getPlannedWeight();
						} else if(instructionRequestChildDto.getActualWeight() != null && instructionRequestChildDto.getActualWeight() > 0 ) {
							plannedWeight = instructionRequestChildDto.getActualWeight();
						}

						scrapWeight = scrapWeight - plannedWeight;
		                availableWeight = availableWeight+plannedWeight;
						isScrapWeightUsed ="Y";
					}
				}
			}
           
            LOGGER.info("incoming length,weight "+incomingLength+","+incomingWeight);
            remainingWeight = availableWeight - existingWeight - Math.floor(incomingWeight);
            remainingLength = availableLength - existingLength - incomingLength;

            LOGGER.info("remaining length,weight is "+remainingLength+", "+remainingWeight);
            if (fromGroup && Math.abs(remainingWeight) > 1f) {
                LOGGER.error("remaining weight exceeds available weight");
                return new ResponseEntity<Object>("Cut instructions total weight must be equal to slit instructions with group ids " + groupIds.stream().map(g -> g + ", ").collect(Collectors.joining()), HttpStatus.BAD_REQUEST);
            }else if(!fromGroup && remainingWeight < 0f){
                LOGGER.error("remaining weight exceeds available weight");
                return new ResponseEntity<Object>("no available weight left for processing", HttpStatus.BAD_REQUEST);
            }
            if(!fromGroup && remainingLength < 0f){
                LOGGER.error("remaining length exceeds available length "+ remainingLength);
                return new ResponseEntity<Object>("inward " + inwardId + " has no available length for processing.", HttpStatus.BAD_REQUEST);
            }
            if (fromInward) {
                LOGGER.info("setting fPresent for inward " + inwardId + " to " + remainingWeight);
                inwardEntry.setFpresent((float) remainingWeight);
//                if (remainingWeight <= 1f){
//                    LOGGER.info("setting available length to 0");
//                    remainingLength = 0f;
//            }
                LOGGER.info("setting available length for inward " + inwardId + " to " + remainingLength);
                inwardEntry.setAvailableLength((float)remainingLength);
            }
            Process process = processService.getById(processId);
            Status inProgressStatus = statusService.getStatusById(inProgressStatusId);
            inwardEntry.setStatus(inProgressStatus);
            Map<Integer,PacketClassification> savedPacketClassifications = packetClassificationService
                    .findAllByPacketClassificationIdIn(packetClassificationIds)
                    .stream().collect(Collectors.toMap(pc -> pc.getClassificationId(),pc -> pc));
                
            Map<Integer, EndUserTagsEntity> savedEndUserTagsEntities = endUserTagsService
                    .findAllByTagIdIn( endUserTagIds)
                    .stream().collect(Collectors.toMap(pc -> pc.getTagId(),pc -> pc));
                
                for (PartDetails pd : instructionPlanAndListMap.keySet()) {
                List<InstructionRequestDto> list = instructionPlanAndListMap.get(pd);
                for (InstructionRequestDto requestDto : list) {
                    Instruction instruction = instructionMapper.toEntity(requestDto);
                    if(requestDto.getPacketClassificationId()!=null && requestDto.getPacketClassificationId() >0 ) {
                        instruction.setPacketClassification(savedPacketClassifications.get(requestDto.getPacketClassificationId()));
                    } else {
                        instruction.setPacketClassification(null);
                    }
                    instruction.setEndUserTagsEntity(savedEndUserTagsEntities.get(requestDto.getEndUserTagId()));
                    instruction.setProcess(process);
                    instruction.setStatus(inProgressStatus);
                    instruction.setCreatedBy(userId);
                    instruction.setUpdatedBy(userId);
                    if (fromParentInstruction) {
                    	//instruction.setInwardId(inwardEntry);
                        parentInstruction.addChildInstruction(instruction);
                    } else if (fromInward) {
                        inwardEntry.addInstruction(instruction);
                    } else {
                        inwardEntry.addInstruction(instruction);
                        instruction.setParentGroupId(requestDto.getGroupId());
                    }
                    if(pd != null) {
                        pd.addInstruction(instruction);
                    }
                }
            }
            LOGGER.info("saving " + instructionPlanAndListMap.keySet().size() + " part details objects");
            List<PartDetails> partDetailsList = partDetailsService.saveAll(instructionPlanAndListMap.keySet());
            List<PartDetailsResponse> partDetailsResponseList = partDetailsMapper.toResponseDto(partDetailsList);

            if (fromParentInstruction) {
            	parentInstruction.setUpdatedBy(userId);
                instructionRepository.save(parentInstruction);
            } else if (fromInward) {
            	if("Y".equals(isScrapWeightUsed)) {
            		if (scrapWeight > 0) {
        				inwardEntry.setScrapWeight(scrapWeight);
    				} else {
    					inwardEntry.setScrapWeight(0.0f);
    				}
    			}
            	inwardEntry.setUpdatedBy(userId);
                inwardService.saveEntry(inwardEntry);
            }
            return new ResponseEntity<Object>(partDetailsResponseList, HttpStatus.CREATED);
    }

	@Override
	public int getPartCount(Long theId) {

		int result = instructionRepository.getPartCount(theId);
		return result;
	}

	@Override
	public void updateS3PlanPDF(String partDetailsId, String url) {
		instructionRepository.updateS3PlanPDF(partDetailsId, url);
	}

	@Override
	public void updateS3InwardPDF(Integer inwardId, String url) {
		inwardEntryRepository.updateS3InwardPDF(inwardId, url);
	}

	@Override
	public void updateS3DCPDF(Integer inwardId, String url) {
		deliveryDetailsRepository.updateS3DCPDF(inwardId, url);
	}

	@Override
	public List<Instruction> findAllByInstructionIdInAndStatus(List<Integer> instructionIds, List<Integer> statusId) {
		return instructionRepository.findAllByInstructionIdInAndStatus(instructionIds, statusId);
	}
}
