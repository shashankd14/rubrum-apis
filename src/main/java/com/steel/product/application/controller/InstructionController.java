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
import java.util.List;

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

        for (InstructionDto instructionDTO : instructionDTOs) {

            //InwardEntry inward = new InwardEntry();
            try {

                Instruction instruction = new Instruction();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                instruction.setInstructionId(0);

                if (instructionDTO.getInwardId() != null) {

                    InwardEntry inward = inwardService.getByEntryId(instructionDTO.getInwardId());
                    inward.setStatus(statusService.getStatusById(2));
                    instruction.setInwardId(inward);
                }

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

                instruction.setActualLength(null);
                instruction.setActualWeight(null);
                instruction.setActualWidth(null);
                instruction.setActualNoOfPieces(0);

                instruction.setCreatedBy(instructionDTO.getCreatedBy());
                instruction.setUpdatedBy(instructionDTO.getUpdatedBy());
                instruction.setCreatedOn(timestamp);
                instruction.setUpdatedOn(timestamp);
                instruction.setIsDeleted(false);

                instructionService.save(instruction);

                // return new ResponseEntity<Object>("instruction saved successfully!",
                // HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }


        }

        return new ResponseEntity<Object>("instruction saved successfully!", HttpStatus.OK);

    }

    @PutMapping("/update")
    public ResponseEntity<Object> update(@RequestBody InstructionFinishDto instructionFinishDto) {

        List<InstructionDto> instructionDTOs = instructionFinishDto.getInstructionDtos();
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


                if (instructionFinishDto.getIsFinishTask())
                    instruction.setStatus(statusService.getStatusById(3));

                else
                    instruction.setStatus(statusService.getStatusById(2));
                //instruction.setStatus(statusService.getStatusById(instructionDTO.getStatus()));

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

                instructionService.save(instruction);

                //	return new ResponseEntity<Object>("update success!!", HttpStatus.OK);
            } catch (Exception e) {

                return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Object>("update success!!", HttpStatus.OK);
    }

    public ResponseEntity<Object> deleteById(int id) {

        try {

            instructionService.deleteById(id);
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
