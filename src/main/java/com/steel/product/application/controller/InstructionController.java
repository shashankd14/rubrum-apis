package com.steel.product.application.controller;

import com.steel.product.application.dto.instruction.InstructionFinishDto;
import com.steel.product.application.dto.instruction.InstructionRequestDto;
import com.steel.product.application.dto.instruction.InstructionResponseDto;
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
import java.util.Optional;
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
    @Autowired
    private CompanyDetailsService companyDetailsService;

    @Resource
    private PacketClassificationService packetClassificationService;

    @GetMapping("/list")
    public ResponseEntity<Object> getAll() {

        try {

            List<InstructionResponseDto> instructionList = instructionService.getAll().stream().map(i -> Instruction.valueOf(i))
                    .collect(Collectors.toList());

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
            return new ResponseEntity<Object>(Instruction.valueOf(instruction), HttpStatus.OK);

        } catch (Exception e) {

            return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Object> save(@RequestBody List<InstructionRequestDto> instructionDTOs) {
        return instructionService.addInstruction(instructionDTOs);

    }

    @PutMapping("/update")
    public ResponseEntity<Object> update(@RequestBody InstructionFinishDto instructionFinishDto) {

       return instructionService.updateInstruction(instructionFinishDto);
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
        return new ResponseEntity<>(instructionService.saveUnprocessedForDelivery(inwardId), HttpStatus.OK);
    }


}
