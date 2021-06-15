package com.steel.product.application.controller;

import com.steel.product.application.dto.instruction.InstructionGroupDto;
import com.steel.product.application.entity.InstructionGroup;
import com.steel.product.application.service.InstructionGroupService;
import com.steel.product.application.service.InstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/instructionGroup")
public class InstructionGroupController {

    @Autowired
    private InstructionGroupService instructionGroupService;

    @Autowired
    private InstructionService instructionService;

    @PostMapping({"/save"})
    public ResponseEntity<Object> saveAddress(@RequestBody InstructionGroupDto instructionGroupDto) {


        try {

            InstructionGroup savedInstructionGroup = instructionGroupService.saveInstructionGroup(instructionGroupDto);

            return new ResponseEntity(savedInstructionGroup, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
