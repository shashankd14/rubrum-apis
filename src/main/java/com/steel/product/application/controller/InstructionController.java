package com.steel.product.application.controller;

import com.lowagie.text.DocumentException;
import com.steel.product.application.dao.InstructionRepository;
import com.steel.product.application.dto.instruction.*;
import com.steel.product.application.dto.partDetails.PartDetailsResponse;
import com.steel.product.application.dto.pdf.InwardEntryPdfDto;
import com.steel.product.application.dto.pdf.PdfResponseDto;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.entity.PartDetails;
import com.steel.product.application.mapper.InstructionMapper;
import com.steel.product.application.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
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


    @Autowired
    public InstructionController(InstructionService instructionService, InwardEntryService inwardService, StatusService statusService, ProcessService processService, CompanyDetailsService companyDetailsService, PacketClassificationService packetClassificationService, PdfService pdfService) {
        this.instructionService = instructionService;
        this.inwardService = inwardService;
        this.statusService = statusService;
        this.processService = processService;
        this.companyDetailsService = companyDetailsService;
        this.packetClassificationService = packetClassificationService;
        this.pdfService = pdfService;
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

    @PostMapping("/save")
    public ResponseEntity<Object> save(@RequestBody List<InstructionRequestDto> instructionDTOs) {
        return instructionService.addInstruction(instructionDTOs);

    }

    @PostMapping("/save/slit")
    public ResponseEntity<Object> saveSlitInstruction(@RequestBody List<SlitInstructionSaveRequestDto> slitInstructionSaveRequestDtos) {
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

    @PostMapping("/slit/pdf/{partDetailsId}")
    public ResponseEntity<PdfResponseDto> downloadInwardPDF(@PathVariable("partDetailsId") String partDetailsId) {
        Path file = null;
        byte[] bytes = null;
        StringBuilder builder = new StringBuilder();
        try {
            file = Paths.get(pdfService.generatePdf(partDetailsId).getAbsolutePath());
            bytes = Files.readAllBytes(file);
            builder.append(Base64.getEncoder().encodeToString(bytes));
        } catch (IOException | DocumentException | org.dom4j.DocumentException ex) {
            ex.printStackTrace();
        }
        String encodedFile = builder.toString();

        return new ResponseEntity<PdfResponseDto>(new PdfResponseDto(encodedFile), HttpStatus.OK);
    }


}
