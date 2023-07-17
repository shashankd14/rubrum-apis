package com.steel.product.application.controller;

import com.steel.product.application.dto.instruction.*;
import com.steel.product.application.dto.pdf.InwardEntryPdfDto;
import com.steel.product.application.dto.pdf.PartDto;
import com.steel.product.application.dto.qrcode.QRCodeResponse;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.exception.MockException;
import com.steel.product.application.service.*;
import com.steel.product.application.util.CommonUtil;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@Tag(name = "Instruction Details", description = "Instruction Details")
@RequestMapping("/instruction")
public class InstructionController {

	private InstructionService instructionService;

	private CommonUtil commonUtil;
	
	private QRCodePDFGenerator pdfGenerator;

	@Autowired
	public InstructionController(InstructionService instructionService,  CommonUtil commonUtil, QRCodePDFGenerator pdfGenerator) {
		this.instructionService = instructionService;
		this.commonUtil = commonUtil;
		this.pdfGenerator = pdfGenerator;
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
			@RequestBody List<InstructionSaveRequestDto> instructionSaveRequestDtos, HttpServletRequest request) {
		int userId = commonUtil.getUserId();
		return instructionService.addInstruction(instructionSaveRequestDtos, userId);

	}

	@PutMapping("/update")
	public ResponseEntity<Object> update(@RequestBody InstructionFinishDto instructionFinishDto, HttpServletRequest request) {
		int userId = commonUtil.getUserId();
		return instructionService.updateInstruction(instructionFinishDto, userId);
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
	public ResponseEntity<Object> saveUnprocessedForDelivery(@PathVariable int inwardId, HttpServletRequest request) {
		int userId = commonUtil.getUserId();
		return new ResponseEntity<>(instructionService.saveUnprocessedForDelivery(inwardId, userId), HttpStatus.OK);
	}

	@PostMapping("/saveFullHandlingDispatch")
	public ResponseEntity<Object> saveFullHandlingDispatch(@RequestBody List<Integer> inwardList, HttpServletRequest request) throws MockException {
		int userId = commonUtil.getUserId();
		return new ResponseEntity<>(instructionService.saveFullHandlingDispatch(inwardList, userId ), HttpStatus.OK);
	}
	
	@PostMapping("/cut")
	public ResponseEntity<Object> deleteCut(@RequestBody CutInstructionDeleteRequest cutInstructionDeleteRequest) {
		return instructionService.deleteCut(cutInstructionDeleteRequest);
	}

	@PostMapping("/slit")
	public ResponseEntity<Object> deleteSlit(@RequestBody SlitInstructionDeleteRequest slitInstructionDeleteRequest) {
		return instructionService.deleteSlit(slitInstructionDeleteRequest);
	}

	@PostMapping({ "/qrcode/plan" })
	public ResponseEntity<Object> qrcode(@RequestBody PartDto partDto) {
		InputStreamResource inputStreamResource = null;
		ResponseEntity<Object> kk = null ;
		try {

	        InwardEntryPdfDto inwardEntryPdfDto = instructionService.findQRCodeInwardJoinFetchInstructionsAndPartDetails(partDto.getPartDetailsId(), partDto.getGroupIds());

	        List<QRCodeResponse> resp = instructionService.getQRCodeDetails(inwardEntryPdfDto);

			inputStreamResource = pdfGenerator.planInputStreamResource( resp, partDto);
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "inline; filename=QRCode_PLAN" + partDto.getPartDetailsId() + ".pdf");
			
			kk = ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM).body(inputStreamResource);
			
			
		} catch ( Exception e) {
			e.printStackTrace();
		}
		return kk;
	}

}
