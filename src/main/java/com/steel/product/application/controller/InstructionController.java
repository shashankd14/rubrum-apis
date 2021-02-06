package com.steel.product.application.controller;

import com.steel.product.application.constants.Values;
import com.steel.product.application.dto.InstructionDto;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.service.InstructionService;
import com.steel.product.application.service.InwardEntryService;
import com.steel.product.application.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
	
	@GetMapping("/list")
	public ResponseEntity<Object> getAll(){
		
		try {
			
			List<Instruction> instructionList = instructionService.getAll();
			
			return new ResponseEntity<Object>(instructionList, HttpStatus.OK);
			
		}catch(Exception e) {
			
			e.printStackTrace();
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GetMapping("/getById/{instructionId}")
	public ResponseEntity<Object> getById(@PathVariable("instructionId")int theId) {
		
		try {

			Instruction instruction = instructionService.getById(theId);
			return new ResponseEntity<Object>(instruction, HttpStatus.OK);
			
		}catch(Exception e) {
			
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/save")
	public ResponseEntity<Object> save(@RequestBody List<InstructionDto>  instructionDTOs) {
		
		for(InstructionDto instructionDTO : instructionDTOs) {
			
			try {

				Instruction instruction = new Instruction();
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());

				instruction.setInstructionId(0);

				if (instructionDTO.getInwardId() != null) {

					InwardEntry inward = inwardService.getByEntryId(instructionDTO.getInwardId());
					instruction.setInwardId(inward);
				}

				if (instructionDTO.getProcessdId() == 1)
					instruction.setProcessdId(Values.CUTTINGINSTRUCTION_PROCESS_ID);
				else if (instructionDTO.getProcessdId() == 2)
					instruction.setProcessdId(Values.SLITTINGINSTRUCTION_PROCESS_ID);

				instruction.setInstructionDate(instructionDTO.getInstructionDate());

				if (instructionDTO.getLength() != null)
					instruction.setLength(instructionDTO.getLength());

				if (instructionDTO.getWidth() != null)
					instruction.setWidth(instructionDTO.getWidth());

				if (instructionDTO.getWeight() != null)
					instruction.setWeight(instructionDTO.getWeight());

				if (instructionDTO.getNoOfPieces() != null)
					instruction.setNoOfPieces(instructionDTO.getNoOfPieces());

				instruction.setStatus(statusService.getStatusById(2));

				if (instructionDTO.getGroupId() != null)
					instruction.setGroupId(instructionDTO.getGroupId());

				if (instructionDTO.getParentInstructionId() != null) {

					Instruction parentInstruction = instructionService.getById(instructionDTO.getParentInstructionId());

					instruction.setParentInstruction(parentInstruction);
				}

				else
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
	public ResponseEntity<Object> update(@RequestBody List<InstructionDto>  instructionDTOs) {
		
		for(InstructionDto instructionDTO : instructionDTOs) {
		try {
			
			Instruction instruction = instructionService.getById(instructionDTO.getInstructionId());
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			
			if(instructionDTO.getInwardId() != null) {
				
				InwardEntry inward = inwardService.getByEntryId(instructionDTO.getInwardId());
				instruction.setInwardId(inward);
			}

			instruction.setProcessdId(instructionDTO.getProcessdId());
			instruction.setInstructionDate(instructionDTO.getInstructionDate());
			
			if(instructionDTO.getLength() != null)
			instruction.setLength(instructionDTO.getLength());

			if(instructionDTO.getActualLength() != null)
				instruction.setActualLength(instructionDTO.getActualLength());
			
			if(instructionDTO.getWidth() !=null)
			instruction.setWidth(instructionDTO.getWidth());

			if(instructionDTO.getActualWidth() !=null)
				instruction.setActualWidth(instructionDTO.getActualWidth());
			
			if(instructionDTO.getActualWeight() !=null)
			instruction.setActualWeight(instructionDTO.getActualWeight());
			
			if(instructionDTO.getNoOfPieces() !=null)
			instruction.setNoOfPieces(instructionDTO.getNoOfPieces());

			if(instructionDTO.getActualNoOfPieces() !=null)
				instruction.setActualNoOfPieces(instructionDTO.getActualNoOfPieces());
			
			instruction.setStatus(statusService.getStatusById(instructionDTO.getStatus()));
			
			if(instructionDTO.getGroupId() !=null)
			instruction.setGroupId(instructionDTO.getGroupId());
			
			if(instructionDTO.getParentInstructionId() !=null) {
				
				Instruction parentInstruction = instructionService.getById(instructionDTO.getParentInstructionId());
				
				instruction.setParentInstruction(parentInstruction);
			}
				
			else
				instruction.setParentInstruction(null);
			
			if(instructionDTO.getWastage() !=null)
			instruction.setWastage(instructionDTO.getWastage());
			
			if(instructionDTO.getDamage() !=null)
			instruction.setDamage(instructionDTO.getDamage());
			
			if(instructionDTO.getPackingWeight() !=null)
			instruction.setPackingWeight(instructionDTO.getPackingWeight());
			
			instruction.setCreatedBy(instructionDTO.getCreatedBy());
			instruction.setUpdatedBy(instructionDTO.getUpdatedBy());
			instruction.setCreatedOn(timestamp);
			instruction.setUpdatedOn(timestamp);
			instruction.setIsDeleted(false);
			
			instructionService.save(instruction);
			
		//	return new ResponseEntity<Object>("update success!!", HttpStatus.OK);
		}catch(Exception e) {
			
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		  }
		}
		
		return new ResponseEntity<Object>("update success!!", HttpStatus.OK);
	}
	
	public ResponseEntity<Object> deleteById(int id) {
		
		try {

			instructionService.deleteById(id);
			return new ResponseEntity<Object>("delete success!", HttpStatus.OK);
			
		}catch(Exception e) {
			
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
