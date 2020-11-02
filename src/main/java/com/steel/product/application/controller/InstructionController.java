package com.steel.product.application.controller;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.steel.product.application.constants.Values;
import com.steel.product.application.dto.InstructionDto;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.service.InstructionService;
import com.steel.product.application.service.InwardEntryService;
import com.steel.product.application.service.StatusService;

@RestController
@CrossOrigin
@RequestMapping("/api/instruction")
public class InstructionController {

	@Autowired
	private InstructionService instructionSerice;
	
	@Autowired
	private InwardEntryService inwardService;
	
	@Autowired
	private StatusService statusService;
	
	@GetMapping("/list")
	public ResponseEntity<Object> getAll(){
		
		try {
			
			List<Instruction> instructioList = instructionSerice.getAll();
			
			return new ResponseEntity<Object>(instructioList, HttpStatus.OK);
			
		}catch(Exception e) {
			
			e.printStackTrace();
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GetMapping("/getById/{instructionId}")
	public ResponseEntity<Object> getById(@PathVariable("instructionId")int theId) {
		
		try {

			Instruction instruction = instructionSerice.getById(theId);
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

				instruction.setStatus(statusService.getStatusById(instructionDTO.getStatus()));

				if (instructionDTO.getGroupId() != null)
					instruction.setGroupId(instructionDTO.getGroupId());

				if (instructionDTO.getParentInstructionId() != null) {

					Instruction parentInstruction = instructionSerice.getById(instructionDTO.getParentInstructionId());

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

				instruction.setCreatedBy(instructionDTO.getCreatedBy());
				instruction.setUpdatedBy(instructionDTO.getUpdatedBy());
				instruction.setCreatedOn(timestamp);
				instruction.setUpdatedOn(timestamp);
				instruction.setIsDeleted(false);

				instructionSerice.save(instruction);

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
			
			Instruction instruction = instructionSerice.getById(instructionDTO.getInstructionId());
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			
			if(instructionDTO.getInwardId() != null) {
				
				InwardEntry inward = inwardService.getByEntryId(instructionDTO.getInwardId());
				instruction.setInwardId(inward);
			}

			instruction.setProcessdId(instructionDTO.getProcessdId());
			instruction.setInstructionDate(instructionDTO.getInstructionDate());
			
			if(instructionDTO.getLength() != null)
			instruction.setLength(instructionDTO.getLength());
			
			if(instructionDTO.getWidth() !=null)
			instruction.setWidth(instructionDTO.getWidth());
			
			if(instructionDTO.getWeight() !=null)
			instruction.setWeight(instructionDTO.getWeight());
			
			if(instructionDTO.getNoOfPieces() !=null)
			instruction.setNoOfPieces(instructionDTO.getNoOfPieces());
			
			instruction.setStatus(statusService.getStatusById(instructionDTO.getStatus()));
			
			if(instructionDTO.getGroupId() !=null)
			instruction.setGroupId(instructionDTO.getGroupId());
			
			if(instructionDTO.getParentInstructionId() !=null) {
				
				Instruction parentInstruction = instructionSerice.getById(instructionDTO.getParentInstructionId());
				
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
			
			instructionSerice.save(instruction);
			
		//	return new ResponseEntity<Object>("update success!!", HttpStatus.OK);
		}catch(Exception e) {
			
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		  }
		}
		
		return new ResponseEntity<Object>("update success!!", HttpStatus.OK);
	}
	
	public ResponseEntity<Object> deleteById(int id) {
		
		try {

			instructionSerice.deleteById(id);
			return new ResponseEntity<Object>("delete success!", HttpStatus.OK);
			
		}catch(Exception e) {
			
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
