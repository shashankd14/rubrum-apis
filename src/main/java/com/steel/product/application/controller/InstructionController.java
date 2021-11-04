package com.steel.product.application.controller;

import com.steel.product.application.dao.InstructionRepository;
import com.steel.product.application.dto.instruction.*;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.mapper.InstructionMapper;
import com.steel.product.application.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/instruction")
public class InstructionController {

    private InstructionService instructionService;
    private InwardEntryService inwardService;
    private StatusService statusService;
    private ProcessService processService;
    private CompanyDetailsService companyDetailsService;
    private PacketClassificationService packetClassificationService;
    private PdfService pdfService;
    private InstructionRepository instructionRepository;
    private InstructionMapper instructionMapper;

    @Autowired
    public InstructionController(InstructionService instructionService, InwardEntryService inwardService, StatusService statusService, ProcessService processService, CompanyDetailsService companyDetailsService, PacketClassificationService packetClassificationService, PdfService pdfService, InstructionRepository instructionRepository, InstructionMapper instructionMapper) {
        this.instructionService = instructionService;
        this.inwardService = inwardService;
        this.statusService = statusService;
        this.processService = processService;
        this.companyDetailsService = companyDetailsService;
        this.packetClassificationService = packetClassificationService;
        this.pdfService = pdfService;
        this.instructionRepository = instructionRepository;
        this.instructionMapper = instructionMapper;
    }

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

    @PostMapping("/save/cut")
    public ResponseEntity<Object> saveCutInstruction(@RequestBody List<InstructionSaveRequestDto> cutInstructionSaveRequestDtos) {
        return instructionService.addCutInstruction(cutInstructionSaveRequestDtos);

    }

    @PostMapping("/save/slit")
    public ResponseEntity<Object> saveSlitInstruction(@RequestBody List<InstructionSaveRequestDto> slitInstructionSaveRequestDtos) {
        return instructionService.addSlitInstruction(slitInstructionSaveRequestDtos);

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

//    @GetMapping("/test/{parentInstructionId}")
//    public void test(@PathVariable("parentInstructionId") Integer parentInstructionId){
//        TotalLengthAndWeight t = instructionRepository.sumOfPlannedLengthAndWeightOfInstructionsHavingParentInstructionId(parentInstructionId);
//        System.out.println(t.getTotalLength()+" "+t.getTotalWeight());
//        objects.forEach(obj -> System.out.println((Float)obj[0]+" "+(Float)obj[1]));
//    }


}
