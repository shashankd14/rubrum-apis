package com.steel.product.application.controller;

import com.steel.product.application.entity.Process;
import com.steel.product.application.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/process")
public class ProcessController {

    @Autowired
    private ProcessService processService;

    @GetMapping("/getById/{processId}")
    public ResponseEntity<Object> getById(@PathVariable int processId){
        try {
            Process process = processService.getById(processId);
            return new ResponseEntity(process, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/list")
    public ResponseEntity<Object> getAll(){
        try {
            List<Process> processList = processService.getAll();
            return new ResponseEntity(processList, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
