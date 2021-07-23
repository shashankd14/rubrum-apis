package com.steel.product.application.controller;

import com.steel.product.application.dto.instruction.InstructionDto;
import com.steel.product.application.dto.instruction.InstructionFinishDto;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/instruction")
public class InstructionController {

    @Autowired
    private InstructionService instructionService;

    @Autowired
    private InwardEntryService inwardService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private ProcessService processService;

    @Resource
    private PacketClassificationService packetClassificationService;

    @GetMapping("/list")
    public ResponseEntity<Object> getAll() {

        try {

            List<Instruction> instructionList = instructionService.getAll();

            return new ResponseEntity<Object>(instructionList, HttpStatus.OK);

        } catch (Exception e) {

            e.printStackTrace();
            return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/listWIP")
    public ResponseEntity<Object> getAllWIP() {

        try {

            List<Instruction> instructionList = instructionService.getAllWIPList();

            return new ResponseEntity<Object>(instructionList, HttpStatus.OK);

        } catch (Exception e) {

            e.printStackTrace();
            return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/getById/{instructionId}")
    public ResponseEntity<Object> getById(@PathVariable("instructionId") int theId) {

        try {

            Instruction instruction = instructionService.getById(theId);
            return new ResponseEntity<Object>(instruction, HttpStatus.OK);

        } catch (Exception e) {

            return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Object> save(@RequestBody List<InstructionDto> instructionDTOs) {

        float incomingWeight = 0f;
        float availableWeight = 0f;
        float existingWeight = 0f;
        boolean fromInward = false, fromParentInstruction = false, fromGroup = false;
        InwardEntry inwardEntry = null;
        Instruction parentInstruction = null;

        if(instructionDTOs.get(0).getInwardId() != null){
            incomingWeight = (float) instructionDTOs.stream().mapToDouble(InstructionDto::getPlannedWeight).sum();
            inwardEntry = inwardService.getByEntryId(instructionDTOs.get(0).getInwardId());
            availableWeight = inwardEntry.getFpresent();
            fromInward = true;
        }else if(instructionDTOs.get(0).getParentInstructionId() != null)
        {
            incomingWeight = (float)instructionDTOs.stream().
                    mapToDouble(i -> i.getActualWeight() != null ? i.getActualWeight() : i.getPlannedWeight()).sum();
            List<Instruction> in = instructionService.findAllByParentInstructionId(instructionDTOs.get(0).getParentInstructionId());
            existingWeight = (float)instructionService.findAllByParentInstructionId(instructionDTOs.get(0).getParentInstructionId())
                    .stream().mapToDouble(i -> i.getActualWeight() != null ? i.getActualWeight() : i.getPlannedWeight()).sum();
            parentInstruction = instructionService.getById(instructionDTOs.get(0).getParentInstructionId());
            availableWeight = parentInstruction.getActualWeight() != null ? parentInstruction.getActualWeight() : parentInstruction.getPlannedWeight();
            fromParentInstruction = true;
        }
        else{
            List<Instruction> existingInstructions = instructionService.findAllByParentGroupId(instructionDTOs.get(0).getGroupId());
            if(existingInstructions.size() > 0){
                return new ResponseEntity<Object>("Instructions with parent group id "+instructionDTOs.get(0).getGroupId()+" already exists.",HttpStatus.BAD_REQUEST);
            }
            incomingWeight = (float)instructionDTOs.stream().
                    mapToDouble(i -> i.getActualWeight() != null ? i.getActualWeight() : i.getPlannedWeight()).sum();
            availableWeight = (float)instructionService.findAllByGroupId(instructionDTOs.get(0).getGroupId()).stream()
                    .mapToDouble(i -> i.getActualWeight() != null ? i.getActualWeight() : i.getPlannedWeight()).sum();
            fromGroup = true;
        }

        if(incomingWeight > availableWeight - existingWeight){
            return new ResponseEntity<Object>("No available weight for processing.", HttpStatus.BAD_REQUEST);
        }
        if(fromGroup && incomingWeight != availableWeight){
            return new ResponseEntity<Object>("Input instructions incomingWeight must be same as instructions in group id "+instructionDTOs.get(0).getGroupId(),HttpStatus.BAD_REQUEST);
        }

        List<Instruction> savedInstructionList = new ArrayList<Instruction>();
        try {
        for (InstructionDto instructionDTO : instructionDTOs) {

                Instruction instruction = new Instruction();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                instruction.setInstructionId(0);

                instruction.setProcess(processService.getById(instructionDTO.getProcessId()));
                instruction.setInstructionDate(instructionDTO.getInstructionDate());

                if (instructionDTO.getPlannedLength() != null)
                    instruction.setPlannedLength(instructionDTO.getPlannedLength());

                if (instructionDTO.getPlannedWidth() != null)
                    instruction.setPlannedWidth(instructionDTO.getPlannedWidth());

                if (instructionDTO.getPlannedWeight() != null)
                    instruction.setPlannedWeight(instructionDTO.getPlannedWeight());

                if (instructionDTO.getPlannedNoOfPieces() != null)
                    instruction.setPlannedNoOfPieces(instructionDTO.getPlannedNoOfPieces());

                instruction.setStatus(statusService.getStatusById(2));

                if (instructionDTO.getWastage() != null)
                    instruction.setWastage(instructionDTO.getWastage());

                if (instructionDTO.getDamage() != null)
                    instruction.setDamage(instructionDTO.getDamage());

                if (instructionDTO.getPackingWeight() != null)
                    instruction.setPackingWeight(instructionDTO.getPackingWeight());

                instruction.setActualLength(null);
                instruction.setActualWeight(null);
                instruction.setActualWidth(null);
                instruction.setActualNoOfPieces(null);

                instruction.setCreatedBy(instructionDTO.getCreatedBy());
                instruction.setUpdatedBy(instructionDTO.getUpdatedBy());
                instruction.setCreatedOn(timestamp);
                instruction.setUpdatedOn(timestamp);
                instruction.setIsDeleted(false);

                if(fromParentInstruction){
                    instruction.setParentInstruction(parentInstruction);
                    parentInstruction.getChildInstructions().add(instruction);
                }else if(fromInward){
                    inwardEntry.setFpresent(availableWeight - incomingWeight);
                    instruction.setInwardId(inwardEntry);
                    inwardEntry.getInstruction().add(instruction);
                }else{
                    instruction.setParentGroupId(instructionDTO.getGroupId());
                }
                savedInstructionList.add(instruction);
        }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
            if(fromParentInstruction){
                instructionService.save(parentInstruction);
            }else if(fromInward){
                inwardService.saveEntry(inwardEntry);
            }else{
                instructionService.saveAll(savedInstructionList);
            }

        return new ResponseEntity<Object>(savedInstructionList, HttpStatus.OK);

    }

    @PutMapping("/update")
    public ResponseEntity<Object> update(@RequestBody InstructionFinishDto instructionFinishDto) {

        List<InstructionDto> instructionDTOs = instructionFinishDto.getInstructionDtos();
        List<Instruction> updatedInstructionList = new ArrayList<Instruction>();
        Instruction updatedInstruction = new Instruction();
        InwardEntry inwardEntry = new InwardEntry();

        if(instructionFinishDto.getIsCoilFinished() && instructionFinishDto.getCoilNumber()!=null){
            inwardEntry = inwardService.getByCoilNumber(instructionFinishDto.getCoilNumber());
            inwardEntry.setStatus(statusService.getStatusById(3));
            inwardService.saveEntry(inwardEntry);
        }
        for (InstructionDto instructionDTO : instructionDTOs) {
            try {

                Instruction instruction = instructionService.getById(instructionDTO.getInstructionId());
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                if (instructionDTO.getInwardId() != null) {

                    InwardEntry inward = inwardService.getByEntryId(instructionDTO.getInwardId());
                    instruction.setInwardId(inward);
                }


                instruction.setProcess(processService.getById(instructionDTO.getProcessId()));
                instruction.setInstructionDate(instructionDTO.getInstructionDate());

                if (instructionDTO.getPlannedLength() != null)
                    instruction.setPlannedLength(instructionDTO.getPlannedLength());

                if (instructionDTO.getActualLength() != null)
                    instruction.setActualLength(instructionDTO.getActualLength());

                if (instructionDTO.getPlannedWidth() != null)
                    instruction.setPlannedWidth(instructionDTO.getPlannedWidth());

                if (instructionDTO.getActualWidth() != null)
                    instruction.setActualWidth(instructionDTO.getActualWidth());

                if (instructionDTO.getActualWeight() != null)
                    instruction.setActualWeight(instructionDTO.getActualWeight());

                if (instructionDTO.getPlannedNoOfPieces() != null)
                    instruction.setPlannedNoOfPieces(instructionDTO.getPlannedNoOfPieces());

                if (instructionDTO.getActualNoOfPieces() != null)
                    instruction.setActualNoOfPieces(instructionDTO.getActualNoOfPieces());

                if (instructionDTO.getStatus() != null)
                    instruction.setStatus(statusService.getStatusById(instructionDTO.getStatus()));

                if (instructionDTO.getPacketClassificationId() != null)
                    instruction.setPacketClassification(packetClassificationService.getPacketClassificationById(instructionDTO.getPacketClassificationId()));

                if (instructionDTO.getGroupId() != null)
                    instruction.setGroupId(instructionDTO.getGroupId());

                if (instructionDTO.getParentInstructionId() != null) {

                    Instruction parentInstruction = instructionService.getById(instructionDTO.getParentInstructionId());

                    instruction.setParentInstruction(parentInstruction);
                } else
                    instruction.setParentInstruction(null);

                if (instructionDTO.getWastage() != null)
                    instruction.setWastage(instructionDTO.getWastage());

                if (instructionDTO.getDamage() != null)
                    instruction.setDamage(instructionDTO.getDamage());

                if (instructionDTO.getPackingWeight() != null)
                    instruction.setPackingWeight(instructionDTO.getPackingWeight());

                instruction.setCreatedBy(instructionDTO.getCreatedBy());
                instruction.setUpdatedBy(instructionDTO.getUpdatedBy());
                instruction.setCreatedOn(timestamp);
                instruction.setUpdatedOn(timestamp);
                instruction.setIsDeleted(false);

                updatedInstruction = instructionService.save(instruction);
                updatedInstructionList.add(updatedInstruction);

                //	return new ResponseEntity<Object>("update success!!", HttpStatus.OK);
            } catch (Exception e) {

                return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Object>(updatedInstructionList, HttpStatus.OK);
    }

    @DeleteMapping("/deleteById/{instructionId}")
    public ResponseEntity<Object> deleteById(@PathVariable int instructionId) {

        try {

            Instruction deleteInstruction = new Instruction();
            deleteInstruction = instructionService.getById(instructionId);
            instructionService.deleteById(deleteInstruction);
            return new ResponseEntity<Object>("delete success!", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/saveUnprocessedForDelivery/{inwardId}")
    public ResponseEntity<Object> saveUnprocessedForDelivery(@PathVariable int inwardId) {
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Instruction instruction = new Instruction();
            InwardEntry inward = inwardService.getByEntryId(inwardId);

            instruction.setInwardId(inward);
            instruction.setProcess(processService.getById(7));
            instruction.setPlannedWeight(inward.getFpresent());
            instruction.setStatus(statusService.getStatusById(3));

            instruction.setInstructionDate(timestamp);
            instruction.setCreatedBy(1);
            instruction.setUpdatedBy(1);
            instruction.setCreatedOn(timestamp);
            instruction.setUpdatedOn(timestamp);
            instruction.setIsDeleted(false);

            instructionService.save(instruction);

            inward.setFpresent(0f);
            inwardService.saveEntry(inward);

            return new ResponseEntity<Object>("Unprocessed Coil saved for delivery", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
