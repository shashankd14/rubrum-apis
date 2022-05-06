package com.steel.product.application.controller;

import com.steel.product.application.dto.instruction.*;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.service.*;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@Tag(name = "Instruction Details", description = "Instruction Details")
@RequestMapping("/instruction")
public class InstructionController {

	private InstructionService instructionService;

	@Autowired
	public InstructionController(InstructionService instructionService, InwardEntryService inwardService,
			StatusService statusService, ProcessService processService, CompanyDetailsService companyDetailsService,
			PacketClassificationService packetClassificationService, PdfService pdfService) {
		this.instructionService = instructionService;
	}

	@GetMapping("/list")
	public ResponseEntity<Object> getAll() {

		try {

			List<InstructionResponseDto> instructionList = instructionService.getAll().stream()
					.map(i -> Instruction.valueOf(i)).collect(Collectors.toList());

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
	public ResponseEntity<Object> saveInstruction(
			@RequestBody List<InstructionSaveRequestDto> instructionSaveRequestDtos) {
		return instructionService.addInstruction(instructionSaveRequestDtos);

	}

	@PutMapping("/update")
	public ResponseEntity<Object> update(@RequestBody InstructionFinishDto instructionFinishDto) {

		return instructionService.updateInstruction(instructionFinishDto);
	}

	@PostMapping("{instructionId}")
	public ResponseEntity<Object> deleteById(@PathVariable Integer instructionId) {

		try {
			instructionService.deleteById(instructionId);
			return new ResponseEntity<Object>("delete success!", HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/saveUnprocessedForDelivery/{inwardId}")
	public ResponseEntity<Object> saveUnprocessedForDelivery(@PathVariable int inwardId,
			@RequestParam(required = false, name = "taskType") String taskType) {
		return new ResponseEntity<>(instructionService.saveUnprocessedForDelivery(inwardId, taskType), HttpStatus.OK);
	}
	
	@PostMapping("/cut")
	public ResponseEntity<Object> deleteCut(@RequestBody CutInstructionDeleteRequest cutInstructionDeleteRequest) {
		return instructionService.deleteCut(cutInstructionDeleteRequest);
	}

	@PostMapping("/slit")
	public ResponseEntity<Object> deleteSlit(@RequestBody SlitInstructionDeleteRequest slitInstructionDeleteRequest) {
		return instructionService.deleteSlit(slitInstructionDeleteRequest);
	}

}
